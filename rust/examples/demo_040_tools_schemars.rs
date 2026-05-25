/*
 * Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.
 *
 * This code is provided for educational purposes as part of the
 * "Agentic AI & Creative Coding" workshop.
 * Unauthorized reproduction or distribution is prohibited.
 */

//! Demo 040 (schemars variant): Tools in the Hands of AI
//!
//! This mirrors the Kotlin `anthropic-sdk-kotlin` pattern where a tool is
//! defined as a typed data class with field descriptions, and the schema is
//! generated automatically. In Rust we get the same ergonomics with the
//! `schemars` crate (the analog of Python's Pydantic and TypeScript's Zod).
//!
//! What you will learn?
//!
//! - Context engineering: defining the tool input as a typed struct rather
//!   than a hand-written JSON Schema object.
//! - Rust:
//!   - `#[derive(JsonSchema)]` turns the struct (and its doc comments) into a
//!     JSON Schema via `schema_for!`; no hand-written schema to drift.
//!   - `#[derive(Deserialize)]` lets `serde_json::from_value` validate *and*
//!     parse the model's tool input into the typed struct in one step - no
//!     manual `["n"].as_u64()` indexing.
//!
//! Setup: the `schemars` and `serde` crates (already in `Cargo.toml`).

use claudius::{
    Anthropic, ContentBlock, KnownModel, MessageCreateParams, MessageParam, MessageRole, Model,
    ToolParam, ToolResultBlock, ToolUnionParam,
};
use schemars::JsonSchema;
use serde::Deserialize;

/// Plain Rust function - the "tool" implementation. The LLM never runs this
/// itself; it only requests that we run it on its behalf.
fn fibonacci(n: u64) -> u64 {
    let (mut a, mut b) = (0u64, 1u64);
    for _ in 0..n {
        (a, b) = (b, a + b);
    }
    a
}

/// Single source of truth: this struct drives both the JSON Schema we hand to
/// the model AND the static Rust type we deserialize the model's call into.
/// The doc comment below becomes the JSON Schema `description` for the field.
#[derive(Debug, Deserialize, JsonSchema)]
struct FibonacciInput {
    /// The Fibonacci number to calculate
    n: u64,
}

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    let client = Anthropic::new(None)?;
    let model = Model::Known(KnownModel::ClaudeOpus47);

    // `schema_for!` derives the JSON Schema from the struct; `to_value()` turns
    // it into the `serde_json::Value` the tool API expects. (schemars emits a
    // top-level `$schema`/`title` which the Anthropic API tolerates.)
    let schema = schemars::schema_for!(FibonacciInput).to_value();

    let tools = vec![ToolUnionParam::CustomTool(
        ToolParam::new("Fibonacci".to_string(), schema)
            .with_description("Calculates Fibonacci number n".to_string()),
    )];

    let mut conversation = vec![MessageParam::user("What's Fibonacci number 42")];

    // First turn: the model decides to call the Fibonacci tool instead of
    // attempting (and likely failing) to compute the value itself.
    let tool_use_response = client
        .send(
            MessageCreateParams::new(1024, conversation.clone(), model.clone())
                .with_tools(tools.clone()),
        )
        .await?;

    println!("Stop reason: {:?}", tool_use_response.stop_reason);

    let content = tool_use_response.content;

    // Execute every tool_use block. `from_value` validates the model's output
    // against the struct; if the model ever returns the wrong shape we fail
    // fast here instead of silently producing wrong math.
    let mut tool_results: Vec<ContentBlock> = Vec::new();
    for block in &content {
        if let Some(tool_use) = block.as_tool_use() {
            let input: FibonacciInput = serde_json::from_value(tool_use.input.clone())?;
            let result = fibonacci(input.n);
            tool_results.push(ContentBlock::ToolResult(
                ToolResultBlock::new(tool_use.id.clone()).with_string_content(result.to_string()),
            ));
        }
    }

    conversation.push(MessageParam::new_with_blocks(
        content,
        MessageRole::Assistant,
    ));
    conversation.push(MessageParam::new_with_blocks(
        tool_results,
        MessageRole::User,
    ));

    // Second turn: now the model has the real number and can phrase the final
    // natural-language answer.
    let final_response = client
        .send(MessageCreateParams::new(1024, conversation, model).with_tools(tools))
        .await?;

    for block in &final_response.content {
        if let Some(text) = block.as_text() {
            println!("{}", text.text);
        }
    }

    Ok(())
}
