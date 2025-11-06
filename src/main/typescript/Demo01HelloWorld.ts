/*
 * Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.
 *
 * This code is provided for educational purposes as part of the
 * "Agentic AI & Creative Coding" workshop.
 * Unauthorized reproduction or distribution is prohibited.
 */

import Anthropic from '@anthropic-ai/sdk';

/**
 * What you will learn?
 *
 * - AI Dev: communicating with Anthropic API basic input/output.
 * - Cognitive Science: the LLM output is non-deterministic.
 *   Run it multiple times with the same prompt to get a different output.
 *   Each time the internal mechanics of the neural net will differ.
 * - TypeScript: async/await for handling promises
 */

const anthropic = new Anthropic();

const response = await anthropic.messages.create({
  model: "claude-3-7-sonnet-20250219",
  max_tokens: 1024,
  messages: [{ role: "user", content: "Hello World!" }],
});

console.log(response.content[0].text);

/*
  Note: In TypeScript, API calls return Promises which we handle
  with async/await. The top-level await is supported in modern
  TypeScript when using ES modules.

  Note: Unlike Kotlin's DSL with operator overloading, TypeScript
  uses plain object literals for configuration.

  Note: The response.content is an array. For text responses,
  we access the first element's text property.
*/