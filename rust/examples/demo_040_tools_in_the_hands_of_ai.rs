/*
 * Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.
 *
 * This code is provided for educational purposes as part of the
 * "Agentic AI & Creative Coding" workshop.
 * Unauthorized reproduction or distribution is prohibited.
 */

//! Demo 040: Tools in the Hands of AI
//!
//! What you will learn?
//!
//! - Context engineering: tools as a basis for agentic use cases -
//!   how to define a tool input schema and connect it with Rust logic.
//! - Cognitive science: LLMs are bad at math - do math with a
//!   calculator, not with harnessed stochastic entropy.
//! - Rust:
//!   - a tool is a `ToolParam` (name + description + JSON Schema). Here
//!     the schema is hand-written with the `serde_json::json!` macro;
//!     see `demo_040_tools_schemars` for the typed-struct variant.
//!   - `ContentBlock::as_tool_use` narrows the response union to the
//!     tool call, and `tool_use.input` is a `serde_json::Value` we read
//!     with `["n"].as_u64()`.

use claudius::{
    Anthropic, ContentBlock, KnownModel, MessageCreateParams, MessageParam, MessageRole, Model,
    ToolParam, ToolResultBlock, ToolUnionParam,
};
use serde_json::json;

/// Plain Rust function - the "tool" implementation. The LLM never runs this
/// itself; it only requests that we run it on its behalf.
fn fibonacci(n: u64) -> u64 {
    let (mut a, mut b) = (0u64, 1u64);
    for _ in 0..n {
        (a, b) = (b, a + b);
    }
    a
}

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    let client = Anthropic::new(None)?;
    let model = Model::Known(KnownModel::ClaudeOpus47);

    // Tool declaration: name, description and a JSON Schema for the inputs. The
    // model reads the description to decide *when* to call the tool and the
    // schema to know *what* to pass.
    let tools = vec![ToolUnionParam::CustomTool(
        ToolParam::new(
            "Fibonacci".to_string(),
            json!({
                "type": "object",
                "properties": {
                    "n": {
                        "type": "integer",
                        "description": "The Fibonacci number to calculate"
                    }
                },
                "required": ["n"]
            }),
        )
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

    // Print any text the model wrote alongside the tool call.
    for block in &content {
        if let Some(text) = block.as_text() {
            println!("{}", text.text);
        }
    }

    // Execute every tool_use block the model emitted and collect the results as
    // `tool_result` blocks keyed by the original tool_use id.
    let mut tool_results: Vec<ContentBlock> = Vec::new();
    for block in &content {
        if let Some(tool_use) = block.as_tool_use() {
            let n = tool_use.input["n"].as_u64().expect("n must be an integer");
            let result = fibonacci(n);
            tool_results.push(ContentBlock::ToolResult(
                ToolResultBlock::new(tool_use.id.clone()).with_string_content(result.to_string()),
            ));
        }
    }

    // Record the assistant turn, then feed the tool results back as a user turn.
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
