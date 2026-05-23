/*
 * Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.
 *
 * This code is provided for educational purposes as part of the
 * "Agentic AI & Creative Coding" workshop.
 * Unauthorized reproduction or distribution is prohibited.
 */

/**
 * Demo 040 (zod variant): Tools in the Hands of AI
 *
 * This mirrors the Kotlin `anthropic-sdk-kotlin` pattern where a tool
 * is defined as a typed data class with field descriptions, and the
 * SDK generates the JSON Schema automatically. In TypeScript we get
 * the same ergonomics with Zod 4, which ships JSON Schema generation
 * as a built-in (`z.toJSONSchema`).
 *
 * What you will learn?
 *
 * - Context engineering: defining the tool input as a typed schema
 *   rather than a hand-written JSON Schema object.
 * - TypeScript:
 *   - `z.infer<typeof Schema>` gives us the TS type for free.
 *   - `Schema.parse(block.input)` validates *and* narrows the type
 *     in a single call - no more `as` casts.
 *
 * Setup:
 *     npm install zod
 */

import Anthropic from '@anthropic-ai/sdk';
import type { MessageParam } from '@anthropic-ai/sdk/resources/messages';
import { z } from 'zod';

// Plain TypeScript function - the "tool" implementation. The LLM never
// runs this itself; it only requests that we run it on its behalf.
function fibonacci(n: number, a: number = 0, b: number = 1): number {
  if (n === 0) return a;
  if (n === 1) return b;
  return fibonacci(n - 1, b, a + b);
}

// Single source of truth: the schema drives both the JSON Schema we
// hand to the model AND the static TS type we use in our own code.
const FibonacciInput = z.object({
  n: z.number().int().describe("The Fibonacci number to calculate"),
});
type FibonacciInput = z.infer<typeof FibonacciInput>;

const anthropic = new Anthropic();
const conversation: MessageParam[] = [];

const tools = [
  {
    name: "Fibonacci",
    description: "Calculates Fibonacci number n",
    // Zod 4 generates JSON Schema natively; describe() calls become
    // JSON Schema `description` fields. `target: "draft-7"` keeps the
    // output compatible with the Anthropic tool input schema format.
    input_schema: z.toJSONSchema(FibonacciInput, { target: "draft-7" }) as any,
  },
];

conversation.push({ role: "user", content: "What's Fibonacci number 42" });

// First turn: the model decides to call the Fibonacci tool instead of
// attempting (and likely failing) to compute the value itself.
const toolUseResponse = await anthropic.messages.create({
  model: "claude-opus-4-7",
  max_tokens: 1024,
  messages: conversation,
  tools: tools,
});

console.log(`Stop reason: ${toolUseResponse.stop_reason}`);
conversation.push({ role: "assistant", content: toolUseResponse.content });

// Execute every tool_use block. `parse` validates the model's output
// against the schema; if the model ever returns the wrong shape, we
// fail fast here instead of silently producing wrong math.
for (const block of toolUseResponse.content) {
  if (block.type === 'tool_use') {
    const args: FibonacciInput = FibonacciInput.parse(block.input);
    const result = fibonacci(args.n);
    conversation.push({
      role: "user",
      content: [{
        type: "tool_result",
        tool_use_id: block.id,
        content: String(result),
      }],
    });
  }
}

// Second turn: now the model has the real number and can phrase the
// final natural-language answer.
const finalResponse = await anthropic.messages.create({
  model: "claude-opus-4-7",
  max_tokens: 1024,
  messages: conversation,
  tools: tools,
});

const finalText = finalResponse.content.find(block => block.type === 'text');
if (finalText && finalText.type === 'text') {
  console.log(finalText.text);
}
