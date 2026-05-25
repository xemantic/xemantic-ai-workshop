/*
 * Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.
 *
 * This code is provided for educational purposes as part of the
 * "Agentic AI & Creative Coding" workshop.
 * Unauthorized reproduction or distribution is prohibited.
 */

//! Demo 020: Conversation
//!
//! Observations:
//!
//! - Context engineering:
//!   - conducting a variable-length dialog with the LLM requires
//!     accumulating the conversation in the context window.
//!
//! - Cognitive science:
//!   - the theory of mind and metacognition (introspection)
//!
//! - Rust:
//!   - we accumulate the dialog in a `Vec<MessageParam>`; the API itself
//!     is stateless, so we resend the entire history on every request.
//!   - `MessageCreateParams::new` takes the messages by value, so we
//!     `clone()` the running history into each request.
//!   - the assistant's reply blocks are pushed back verbatim with
//!     `new_with_blocks(.., MessageRole::Assistant)`.

use claudius::{
    Anthropic, KnownModel, Message, MessageCreateParams, MessageParam, MessageRole, Model,
};

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    let client = Anthropic::new(None)?;
    let model = Model::Known(KnownModel::ClaudeOpus47);
    let mut context: Vec<MessageParam> = Vec::new();

    context.push(MessageParam::user(
        "Is it true, that to know we can die is to be dead already?",
    ));
    let response1 = client
        .send(MessageCreateParams::new(
            1024,
            context.clone(),
            model.clone(),
        ))
        .await?;
    println!("Response 1: {}", text_of(&response1));
    context.push(MessageParam::new_with_blocks(
        response1.content,
        MessageRole::Assistant,
    ));

    // A follow-up that only makes sense if the prior context is present.
    context.push(MessageParam::user(
        "Why do you think I asked you this question?",
    ));
    let response2 = client
        .send(MessageCreateParams::new(
            1024,
            context.clone(),
            model.clone(),
        ))
        .await?;
    println!("Response 2: {}", text_of(&response2));
    context.push(MessageParam::new_with_blocks(
        response2.content,
        MessageRole::Assistant,
    ));

    // the whole past conversation is included in the token window
    println!("{}", serde_json::to_string_pretty(&context)?);

    Ok(())
}

/// Concatenates the text blocks of a response, ignoring any non-text content.
fn text_of(message: &Message) -> String {
    message
        .content
        .iter()
        .filter_map(|block| block.as_text().map(|text| text.text.as_str()))
        .collect::<Vec<_>>()
        .join("")
}
