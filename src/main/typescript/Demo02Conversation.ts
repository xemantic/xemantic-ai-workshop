/*
 * Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.
 *
 * This code is provided for educational purposes as part of the
 * "Agentic AI & Creative Coding" workshop.
 * Unauthorized reproduction or distribution is prohibited.
 */

import Anthropic from '@anthropic-ai/sdk';
import type { MessageParam } from '@anthropic-ai/sdk/resources/messages';

/**
 * What you will learn?
 *
 * - AI Dev: conducting variable length dialog with the LLM
 *   requires cumulating conversation in the context window.
 * - Cognitive Science: the theory of mind and metacognition
 * - TypeScript: managing conversation state with typed arrays
 */

const anthropic = new Anthropic();
const conversation: MessageParam[] = [];

// First message
conversation.push({
  role: "user",
  content: "Is it true, that to know we can die is to be dead already?"
});

const response1 = await anthropic.messages.create({
  model: "claude-3-7-sonnet-20250219",
  max_tokens: 1024,
  messages: conversation,
});

console.log("Response 1:");
console.log(response1.content[0].text);

conversation.push({
  role: "assistant",
  content: response1.content
});

// Second message
conversation.push({
  role: "user",
  content: "Why do you think I asked you this question?"
});

const response2 = await anthropic.messages.create({
  model: "claude-3-7-sonnet-20250219",
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

/*
  Note: We manually build the conversation array by pushing
  user and assistant messages. TypeScript's type system helps
  ensure we use the correct message format.

  Discuss theory of mind and metacognition
*/