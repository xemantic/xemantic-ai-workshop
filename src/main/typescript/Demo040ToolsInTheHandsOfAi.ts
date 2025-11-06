/*
 * Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.
 *
 * This code is provided for educational purposes as part of the
 * "Agentic AI & Creative Coding" workshop.
 * Unauthorized reproduction or distribution is prohibited.
 */

import Anthropic from '@anthropic-ai/sdk';
import type { MessageParam } from '@anthropic-ai/sdk/resources/messages';

function fibonacci(n: number, a: number = 0, b: number = 1): number {
  if (n === 0) return a;
  if (n === 1) return b;
  return fibonacci(n - 1, b, a + b);
}

const anthropic = new Anthropic();
const conversation: MessageParam[] = [];

const tools = [
  {
    name: "Fibonacci",
    description: "Calculates Fibonacci number n",
    input_schema: {
      type: "object" as const,
      properties: {
        n: {
          type: "number",
          description: "The Fibonacci number to calculate"
        }
      },
      required: ["n"]
    }
  }
];

conversation.push({
  role: "user",
  content: "What's Fibonacci number 42"
});

const toolUseResponse = await anthropic.messages.create({
  model: "claude-sonnet-4-5-20250929",
  max_tokens: 1024,
  messages: conversation,
  tools: tools,
});

console.log(`Stop reason: ${toolUseResponse.stop_reason}`);

const textContent = toolUseResponse.content.find(block => block.type === 'text');
if (textContent && textContent.type === 'text') {
  console.log(textContent.text);
}

conversation.push({
  role: "assistant",
  content: toolUseResponse.content
});

for (const block of toolUseResponse.content) {
  if (block.type === 'tool_use') {
    const result = fibonacci(block.input.n);
    conversation.push({
      role: "user",
      content: [{
        type: "tool_result",
        tool_use_id: block.id,
        content: String(result)
      }]
    });
  }
}

const finalResponse = await anthropic.messages.create({
  model: "claude-sonnet-4-5-20250929",
  max_tokens: 1024,
  messages: conversation,
  tools: tools,
});

const finalText = finalResponse.content.find(block => block.type === 'text');
if (finalText && finalText.type === 'text') {
  console.log(finalText.text);
}

/*
  Note: TypeScript requires explicit type guards when working with
  union types like content blocks. We check block.type before
  accessing type-specific properties.
*/
