/*
 * Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.
 *
 * This code is provided for educational purposes as part of the
 * "Agentic AI & Creative Coding" workshop.
 * Unauthorized reproduction or distribution is prohibited.
 */

//! Demo 030: Conversation Loop
//!
//! This example accumulates the conversation in an endless loop
//! (only limited by the size of the context window accepted by the
//! model). It is the tiniest equivalent of "ChatGPT", or rather claude.ai.
//!
//! Observations:
//!
//! - Prompt engineering:
//!   - system prompt: is different from the initial message
//!
//! - Context engineering:
//!   - caching: the major factor reducing LLM costs
//!
//! - Cognitive science:
//!   - conditioning the LLM's expression comes from role-playing
//!
//! - Rust:
//!   - we read user input asynchronously with tokio's `BufReader` over
//!     `stdin`, so the loop reads naturally inside the async runtime.
//!   - the system prompt is sent as a typed `TextBlock` carrying
//!     `cache_control`, so the unchanging prefix is cache-hit on
//!     subsequent turns.

use claudius::{
    Anthropic, CacheControlEphemeral, KnownModel, MessageCreateParams, MessageParam, MessageRole,
    Model, TextBlock,
};
use std::io::Write;
use tokio::io::{AsyncBufReadExt, BufReader};

const SYSTEM_PROMPT: &str = "Act as an art critic. I am an aspiring artist.\n\
Please be very critical regarding ideas of my conceptual artwork.";

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    let client = Anthropic::new(None)?;
    let model = Model::Known(KnownModel::ClaudeOpus47);
    let mut conversation: Vec<MessageParam> = Vec::new();

    let mut reader = BufReader::new(tokio::io::stdin());
    let mut line = String::new();

    loop {
        print!("[user]> ");
        std::io::stdout().flush()?;

        line.clear();
        if reader.read_line(&mut line).await? == 0 {
            break; // EOF (Ctrl-D)
        }
        let input = line.trim();
        if input == "exit" {
            break;
        }

        conversation.push(MessageParam::user(input));
        println!("...Thinking...");

        let params = MessageCreateParams::new(1024, conversation.clone(), model.clone())
            // `cache_control` lets the API reuse this prefix across turns - the
            // system block is identical on every request, so we only pay full
            // price for it once.
            .with_system_blocks(vec![
                TextBlock::new(SYSTEM_PROMPT).with_cache_control(CacheControlEphemeral::new()),
            ]);

        let response = client.send(params).await?;

        // The response may contain non-text blocks (e.g. tool use) in more
        // advanced demos, so we explicitly filter for text here.
        for block in &response.content {
            if let Some(text) = block.as_text() {
                println!("[assistant]> {}", text.text);
            }
        }

        conversation.push(MessageParam::new_with_blocks(
            response.content,
            MessageRole::Assistant,
        ));
    }

    Ok(())
}
