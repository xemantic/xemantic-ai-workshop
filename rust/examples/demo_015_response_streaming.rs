/*
 * Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.
 *
 * This code is provided for educational purposes as part of the
 * "Agentic AI & Creative Coding" workshop.
 * Unauthorized reproduction or distribution is prohibited.
 */

//! Demo 015: Response Streaming
//!
//! Observations:
//!
//! - Prompt engineering:
//!   - prompting is like if philosophy was a programming language
//!
//! - Context engineering:
//!   - inference takes time, streaming works better for interactive
//!     use cases with "human in the loop", so a human can start
//!     reading the LLM output ASAP.
//!
//! - Cognitive science:
//!   - LLMs excel at generating a stream of text constrained by
//!     specified criteria. Initially it was just a "poetry" of
//!     probabilistic continuation. Nowadays, the output can be
//!     sharper than the sharpest human mind, unmistakably adhering
//!     to rules in play.
//!
//! - Rust:
//!   - `stream()` returns an `impl Stream` of SSE events; we pin it on
//!     the stack with `std::pin::pin!` and pull events with
//!     `StreamExt::next` from the `futures` crate
//!   - events are an enum; we `match`/`if let` to pull text deltas and
//!     ignore everything else (message_start, ping, content_block_stop, ...)
//!   - note `new_streaming` (not `new`): the SDK validates that `stream`
//!     is enabled before calling `stream()`

use claudius::{
    Anthropic, ContentBlockDelta, KnownModel, MessageCreateParams, MessageParam,
    MessageStreamEvent, Model,
};
use futures::StreamExt;
use std::io::Write;
use std::pin::pin;

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    let client = Anthropic::new(None)?;

    let params = MessageCreateParams::new_streaming(
        1024,
        vec![MessageParam::user("Write me a poem.")],
        Model::Known(KnownModel::ClaudeOpus47),
    );

    let mut stream = pin!(client.stream(&params).await?);

    // Iterate over raw SSE events. We only care about the incremental text
    // deltas - everything else is filtered out by the enum match below.
    while let Some(event) = stream.next().await {
        if let MessageStreamEvent::ContentBlockDelta(delta_event) = event?
            && let ContentBlockDelta::TextDelta(delta) = delta_event.delta
        {
            print!("{}", delta.text);
            std::io::stdout().flush()?; // show each token as it arrives
        }
    }

    println!();
    Ok(())
}
