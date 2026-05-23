/*
 * Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.
 *
 * This code is provided for educational purposes as part of the
 * "Agentic AI & Creative Coding" workshop.
 * Unauthorized reproduction or distribution is prohibited.
 */

/**
 * Demo 050: Open Calls Extractor
 *
 * What you will learn?
 *
 * - Context engineering:
 *   - using tools just for structured input (without execution).
 *   - we are forcing single tool use with `tool_choice`.
 * - Cognitive science: powerful vision model of Claude LLM -
 *   multimodality.
 * - TypeScript:
 *   - we describe the expected output as both a JSON Schema (for
 *     the model) and a TS interface (for our code) - the cast on
 *     `block.input` bridges the two.
 */

import Anthropic from '@anthropic-ai/sdk';
import * as fs from 'fs';

// TS shape that mirrors the JSON Schema below - used after we cast
// `block.input`, so consumers get static types instead of `any`.
interface Call {
  deadline: string;
  title: string;
}

interface OpenCallsReceiver {
  calls: Call[];
}

const anthropic = new Anthropic();

// The API expects images as base64-encoded strings, regardless of
// whether they live on disk or in memory.
const imageData = fs.readFileSync('data/workshop/open-calls-creatives.jpg');
const base64Image = imageData.toString('base64');

// Tool with no implementation: we never run it. Its only purpose is to
// *shape* the model's output - we then read `block.input` directly.
const tools = [
  {
    name: "OpenCallsReceiver",
    description: "Receives open call entries from the input",
    input_schema: {
      type: "object" as const,
      properties: {
        calls: {
          type: "array",
          items: {
            type: "object",
            properties: {
              deadline: {
                type: "string",
                description: "Deadline in ISO format"
              },
              title: {
                type: "string",
                description: "Title of the call"
              }
            },
            required: ["deadline", "title"]
          }
        }
      },
      required: ["calls"]
    }
  }
];

const response = await anthropic.messages.create({
  model: "claude-opus-4-7",
  max_tokens: 1024,
  messages: [{
    role: "user",
    content: [
      {
        type: "image",
        source: {
          type: "base64",
          media_type: "image/jpeg",
          data: base64Image
        }
      },
      {
        type: "text",
        text: "Decode open calls from supplied image"
      }
    ]
  }],
  tools: tools,
  // Forcing the model to use this specific tool guarantees that the
  // response is a tool_use block matching our schema - no free-form
  // text to parse, no schema drift.
  tool_choice: { type: "tool", name: "OpenCallsReceiver" }
});

for (const block of response.content) {
  if (block.type === 'tool_use') {
    const receiver = block.input as OpenCallsReceiver;
    // Sort by ISO-formatted deadline descending - lexicographic order
    // matches chronological order for ISO 8601 strings.
    const sortedCalls = receiver.calls.sort((a, b) =>
      b.deadline.localeCompare(a.deadline)
    );

    for (const call of sortedCalls) {
      console.log(`${call.deadline}: ${call.title}`);
    }
  }
}
