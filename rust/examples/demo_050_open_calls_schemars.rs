/*
 * Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.
 *
 * This code is provided for educational purposes as part of the
 * "Agentic AI & Creative Coding" workshop.
 * Unauthorized reproduction or distribution is prohibited.
 */

//! Demo 050 (schemars variant): Open Calls Extractor
//!
//! The structured-extraction counterpart to `demo_040_tools_schemars`: the tool
//! is never executed - we use it purely to force the model into a schema-shaped
//! reply, then parse that reply back into typed structs.
//!
//! What you will learn?
//!
//! - Context engineering:
//!   - using tools just for structured input (without execution).
//!   - we are forcing single tool use with `tool_choice`.
//! - Cognitive science: powerful vision model of Claude LLM - multimodality.
//! - Rust:
//!   - nested `#[derive(JsonSchema)]` structs compose into a single JSON
//!     Schema; `#[derive(Deserialize)]` validates the response back into them.
//!   - by default schemars factors nested types out into `$ref`/`$defs`, which
//!     the Anthropic tool schema doesn't reliably resolve - we set
//!     `inline_subschemas` so `Call` is expanded in place (a flat struct like
//!     `demo_040_tools_schemars` needs no such tweak).
//!   - `deadline` is kept as a `String` here; for real date handling you would
//!     add a crate like `chrono`/`time` (with their schemars/serde support) -
//!     ISO 8601 strings already sort chronologically, so we sort as-is.

use claudius::{
    Anthropic, Base64ImageSource, ContentBlock, ImageBlock, KnownModel, MessageCreateParams,
    MessageParam, MessageRole, Model, TextBlock, ToolChoice, ToolParam, ToolUnionParam,
};
use schemars::JsonSchema;
use schemars::generate::SchemaSettings;
use serde::Deserialize;

const IMAGE_PATH: &str =
    concat!(env!("CARGO_MANIFEST_DIR"), "/../../../data/workshop/open-calls-creatives.jpg");

/// Nested structs compose into a single JSON Schema and validate end-to-end.
#[derive(Debug, Deserialize, JsonSchema)]
struct Call {
    /// Deadline in ISO 8601 format
    deadline: String,
    /// Title of the open call
    title: String,
}

/// Receives open call entries from the input
#[derive(Debug, Deserialize, JsonSchema)]
struct OpenCallsReceiver {
    calls: Vec<Call>,
}

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    let client = Anthropic::new(None)?;

    let image = ImageBlock::new_with_base64(Base64ImageSource::from_path(IMAGE_PATH)?);

    // Schema is derived from the struct; description comes from its doc comment.
    // `inline_subschemas` expands the nested `Call` in place instead of emitting
    // a `$ref` into `$defs`.
    let schema = SchemaSettings::default()
        .with(|s| s.inline_subschemas = true)
        .into_generator()
        .into_root_schema_for::<OpenCallsReceiver>()
        .to_value();

    let tools = vec![ToolUnionParam::CustomTool(
        ToolParam::new("OpenCallsReceiver".to_string(), schema)
            .with_description("Receives open call entries from the input".to_string()),
    )];

    let conversation = vec![MessageParam::new_with_blocks(
        vec![
            ContentBlock::Image(image),
            ContentBlock::Text(TextBlock::new("Decode open calls from supplied image")),
        ],
        MessageRole::User,
    )];

    let response = client
        .send(
            MessageCreateParams::new(1024, conversation, Model::Known(KnownModel::ClaudeOpus47))
                .with_tools(tools)
                // Forcing this specific tool guarantees the response is a
                // tool_use block matching our schema - no schema drift.
                .with_tool_choice(ToolChoice::tool("OpenCallsReceiver")),
        )
        .await?;

    for block in &response.content {
        if let Some(tool_use) = block.as_tool_use() {
            // `from_value` validates the raw JSON against the structs in one
            // step; a wrong shape fails fast here instead of later.
            let receiver: OpenCallsReceiver = serde_json::from_value(tool_use.input.clone())?;

            let mut calls = receiver.calls;
            // ISO 8601 strings sort lexicographically in chronological order.
            calls.sort_by(|a, b| b.deadline.cmp(&a.deadline));

            for call in &calls {
                println!("{}: {}", call.deadline, call.title);
            }
        }
    }

    Ok(())
}
