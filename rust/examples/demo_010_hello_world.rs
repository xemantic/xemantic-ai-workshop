/*
 * Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.
 *
 * This code is provided for educational purposes as part of the
 * "Agentic AI & Creative Coding" workshop.
 * Unauthorized reproduction or distribution is prohibited.
 */

//! Demo 010: Hello World
//!
//! Observations:
//!
//! - Prompt engineering:
//!   - a text prompt is causing the "answer" to be generated
//!   - inference: the process of generating the response by the model
//!   - early LLMs, like GPT2, were rather generating the most likely
//!     continuation
//!
//! - Context engineering:
//!   - underlying ontology: communication theory
//!   - we are receiving the answer as a message (from AI "assistant")
//!   - the response contains metadata
//!
//! - Cognitive science: the LLM output is non-deterministic
//!   (run it multiple times with the same prompt to get a different output)
//!
//! - Rust:
//!   - there is no official Anthropic Rust SDK; this uses the community
//!     `claudius` crate
//!   - API calls are `async`, so we run inside a `#[tokio::main]` runtime
//!   - `Anthropic::new(None)` reads the `ANTHROPIC_API_KEY` env variable;
//!     pass `Some(key)` to provide it explicitly
//!   - unlike the stringly-typed `model` in Python/TS, the model is a typed
//!     `Model` enum, so a typo is a compile error rather than a 404

use claudius::{Anthropic, KnownModel, MessageCreateParams, MessageParam, Model};

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    let client = Anthropic::new(None)?;

    let response = client
        .send(MessageCreateParams::new(
            1024,
            vec![MessageParam::user("Hello World!")],
            Model::Known(KnownModel::ClaudeOpus47),
        ))
        .await?;

    // `response.content` is a list of content blocks. For a simple text
    // exchange the first one is a text block; `as_text()` narrows the
    // `ContentBlock` enum to it without a manual `match`.
    if let Some(text) = response.content.first().and_then(|block| block.as_text()) {
        println!("{}", text.text);
    }
    // println!("{response:#?}"); // uncomment to also show metadata

    Ok(())
}
