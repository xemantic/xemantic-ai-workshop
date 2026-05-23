/*
 * Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.
 *
 * This code is provided for educational purposes as part of the
 * "Agentic AI & Creative Coding" workshop.
 * Unauthorized reproduction or distribution is prohibited.
 */

/**
 * Demo 010: Hello World
 *
 * Observations:
 *
 * - Prompt engineering:
 *   - a text prompt is causing the "answer" to be generated
 *   - inference: the process of generating the response by the model
 *   - early LLMs, like GPT2, were rather generating the most likely
 *     continuation
 *
 * - Context engineering:
 *   - underlying ontology: communication theory
 *   - we are receiving the answer as a message (from AI "assistant")
 *   - the response contains metadata
 *
 * - Cognitive science: the LLM output is non-deterministic
 *   (run it multiple times with the same prompt to get a different output)
 *
 * - TypeScript:
 *   - top-level `await` is supported in ESM modules - no `main()`
 *     wrapper needed
 *   - `response.content` is a discriminated union; for a simple text
 *     reply the first block is a text block
 */

import Anthropic from '@anthropic-ai/sdk';

const anthropic = new Anthropic();

const response = await anthropic.messages.create({
  model: "claude-opus-4-7",
  max_tokens: 1024,
  messages: [{ role: "user", content: "Hello World!" }],
});

// For a plain text exchange, the first content block is a text block.
console.log(response.content[0].text);
// console.log(response); // uncomment to also show metadata
