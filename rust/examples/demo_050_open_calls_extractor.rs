/*
 * Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.
 *
 * This code is provided for educational purposes as part of the
 * "Agentic AI & Creative Coding" workshop.
 * Unauthorized reproduction or distribution is prohibited.
 */

//! Demo 050: Open Calls Extractor
//!
//! What you will learn?
//!
//! - Context engineering:
//!   - using tools just for structured input (without execution).
//!   - we are forcing single tool use with `tool_choice`.
//! - Cognitive science: powerful vision model of Claude LLM - multimodality.
//! - Rust:
//!   - `Base64ImageSource::from_path` reads the file, base64-encodes it, and
//!     infers the media type from the extension - one call builds the image
//!     content block.
//!   - `ToolChoice::tool(name)` coerces the model into a single tool call
//!     matching our schema, so there is no free-form text to parse.
//!   - the tool is never executed; we just read `tool_use.input` and treat it
//!     as our structured extraction result.

use claudius::{
    Anthropic, Base64ImageSource, ContentBlock, ImageBlock, KnownModel, MessageCreateParams,
    MessageParam, MessageRole, Model, TextBlock, ToolChoice, ToolParam, ToolUnionParam,
};
use serde_json::json;

// The workshop images live at the repo root; resolve relative to this crate so
// the demo runs regardless of the current working directory.
const IMAGE_PATH: &str =
    concat!(env!("CARGO_MANIFEST_DIR"), "/../../../data/workshop/open-calls-creatives.jpg");

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    let client = Anthropic::new(None)?;

    let image = ImageBlock::new_with_base64(Base64ImageSource::from_path(IMAGE_PATH)?);

    // We declare a tool whose only purpose is to *shape* the model's output.
    let tools = vec![ToolUnionParam::CustomTool(
        ToolParam::new(
            "OpenCallsReceiver".to_string(),
            json!({
                "type": "object",
                "properties": {
                    "calls": {
                        "type": "array",
                        "items": {
                            "type": "object",
                            "properties": {
                                "deadline": { "type": "string", "description": "Deadline in ISO format" },
                                "title": { "type": "string", "description": "Title of the call" }
                            },
                            "required": ["deadline", "title"]
                        }
                    }
                },
                "required": ["calls"]
            }),
        )
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
            let calls = tool_use.input["calls"]
                .as_array()
                .expect("`calls` must be an array");

            // Sort by ISO-formatted deadline descending - most recent first.
            // ISO 8601 strings sort lexicographically in chronological order.
            let mut sorted: Vec<&serde_json::Value> = calls.iter().collect();
            sorted.sort_by(|a, b| b["deadline"].as_str().cmp(&a["deadline"].as_str()));

            for call in sorted {
                println!(
                    "{}: {}",
                    call["deadline"].as_str().unwrap_or(""),
                    call["title"].as_str().unwrap_or("")
                );
            }
        }
    }

    Ok(())
}
