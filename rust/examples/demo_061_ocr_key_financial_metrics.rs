/*
 * Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.
 *
 * This code is provided for educational purposes as part of the
 * "Agentic AI & Creative Coding" workshop.
 * Unauthorized reproduction or distribution is prohibited.
 */

//! Demo 061: OCR Key Financial Metrics
//!
//! A practical application of structured extraction: we send a screenshot of an
//! income statement and ask the model to populate a typed structure with the
//! figures.
//!
//! Observations:
//!
//! - Context engineering:
//!   - tools are not only for execution - they double as a typed "output
//!     schema" for structured data extraction.
//!
//! - Cognitive science:
//!   - multimodal vision combined with tabular reasoning lets the model perform
//!     OCR + interpretation in a single step.
//!
//! - Rust:
//!   - we sum revenue with `rust_decimal::Decimal` (not `f64`) to keep
//!     financial values exact - no binary-float drift. Like Python's
//!     `Decimal(str(x))`, we parse from the JSON number's text form so no
//!     float imprecision sneaks in.
//!   - note there is no `tool_choice` here: with a single tool and a clear
//!     instruction the model reliably calls it on its own.

use claudius::{
    Anthropic, Base64ImageSource, ContentBlock, ImageBlock, KnownModel, MessageCreateParams,
    MessageParam, MessageRole, Model, TextBlock, ToolParam, ToolUnionParam,
};
use rust_decimal::Decimal;
use serde_json::json;
use std::str::FromStr;

const IMAGE_PATH: &str =
    concat!(env!("CARGO_MANIFEST_DIR"), "/../../../data/workshop/nvidia-income.png");

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    let client = Anthropic::new(None)?;

    let image = ImageBlock::new_with_base64(Base64ImageSource::from_path(IMAGE_PATH)?);

    // JSON Schema describing the structure we want extracted from the image.
    // The model fills these fields by reading the numbers off the chart.
    let tools = vec![ToolUnionParam::CustomTool(
        ToolParam::new(
            "ExtractKeyFinancialMetrics".to_string(),
            json!({
                "type": "object",
                "properties": {
                    "entries": {
                        "type": "array",
                        "items": {
                            "type": "object",
                            "properties": {
                                "year": { "type": "integer", "description": "Year of the financial data" },
                                "revenue": { "type": "number", "description": "Revenue in millions" },
                                "operatingIncome": { "type": "number", "description": "Operating income in millions" },
                                "netIncome": { "type": "number", "description": "Net income in millions" }
                            },
                            "required": ["year", "revenue", "operatingIncome", "netIncome"]
                        }
                    }
                },
                "required": ["entries"]
            }),
        )
        .with_description("Extracts key financial metrics from the report".to_string()),
    )];

    let conversation = vec![MessageParam::new_with_blocks(
        vec![
            ContentBlock::Image(image),
            ContentBlock::Text(TextBlock::new("Decode financial metrics from supplied image")),
        ],
        MessageRole::User,
    )];

    let response = client
        .send(
            MessageCreateParams::new(1024, conversation, Model::Known(KnownModel::ClaudeOpus47))
                .with_tools(tools),
        )
        .await?;

    for block in &response.content {
        if let Some(tool_use) = block.as_tool_use() {
            let entries = tool_use.input["entries"]
                .as_array()
                .expect("`entries` must be an array");

            let mut total_revenue = Decimal::ZERO;
            for entry in entries {
                println!("{entry}");
                // Parse from the JSON number's text form (like Python's
                // `Decimal(str(x))`) so binary-float imprecision never enters.
                total_revenue += Decimal::from_str(&entry["revenue"].to_string())?;
            }

            println!("Total revenue: {total_revenue}");
        }
    }

    Ok(())
}
