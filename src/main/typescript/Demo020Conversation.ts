/*
 * Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.
 *
 * This code is provided for educational purposes as part of the
 * "Agentic AI & Creative Coding" workshop.
 * Unauthorized reproduction or distribution is prohibited.
 */

/**
 * Demo 020: Conversation
 *
 * Observations:
 *
 * - Context engineering:
 *   - conducting a variable-length dialog with the LLM requires
 *     accumulating the conversation in the context window.
 *
 * - Cognitive science:
 *   - the theory of mind and metacognition (introspection)
 *
 * - TypeScript:
 *   - we type the conversation as `MessageParam[]` so the compiler
 *     enforces the role/content shape on every push.
 *   - the API itself is stateless - we resend the entire history
 *     on every request.
 */

import Anthropic from '@anthropic-ai/sdk';
import type { MessageParam } from '@anthropic-ai/sdk/resources/messages';

const anthropic = new Anthropic();
const conversation: MessageParam[] = [];

// First turn.
conversation.push({
  role: "user",
  content: "Is it true, that to know we can die is to be dead already?"
});

const response1 = await anthropic.messages.create({
  model: "claude-opus-4-7",
  max_tokens: 1024,
  messages: conversation,
});

console.log("Response 1:");
console.log(response1.content[0].text);

// Append the assistant's reply, then ask a follow-up that only makes
// sense if the prior context is present.
conversation.push({
  role: "assistant",
  content: response1.content
});

// Second turn - relies on the model remembering what was asked first.
conversation.push({
  role: "user",
  content: "Why do you think I asked you this question?"
});

const response2 = await anthropic.messages.create({
  model: "claude-opus-4-7",
  max_tokens: 1024,
  messages: conversation,
});

console.log("\nResponse 2:");
console.log(response2.content[0].text);

conversation.push({
  role: "assistant",
  content: response2.content
});

console.log("\nThe whole past conversation is included in the token window:");
console.log(JSON.stringify(conversation, null, 2));
