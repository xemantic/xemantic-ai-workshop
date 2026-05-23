/*
 * Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.
 *
 * This code is provided for educational purposes as part of the
 * "Agentic AI & Creative Coding" workshop.
 * Unauthorized reproduction or distribution is prohibited.
 */

/**
 * Demo 050 (zod variant): Open Calls Extractor
 *
 * The structured-extraction counterpart to Demo040ToolsZod.ts: the tool
 * is never executed - we use it purely to force the model into a
 * schema-shaped reply, then parse that reply back into a typed object.
 *
 * What you will learn?
 *
 * - Context engineering:
 *   - using tools just for structured input (without execution).
 *   - we are forcing single tool use with `tool_choice`.
 * - Cognitive science: powerful vision model of Claude LLM -
 *   multimodality.
 * - TypeScript:
 *   - Zod 4 gives us schema, JSON Schema (via the built-in
 *     `z.toJSONSchema`) and TS type from a single definition.
 *   - `z.coerce.date()` parses the model's ISO string into a real
 *     `Date` object during validation.
 *
 * Setup:
 *     npm install zod
 */

import Anthropic from '@anthropic-ai/sdk';
import * as fs from 'fs';
import { z } from 'zod';

// Nested Zod schemas compose into a single JSON Schema and validate
// end-to-end. `z.coerce.date()` accepts an ISO string and returns a
// real `Date` after parsing - same trick as Pydantic's `datetime`.
const Call = z.object({
  deadline: z.coerce.date().describe("Deadline in ISO 8601 format"),
  title: z.string().describe("Title of the open call"),
});

const OpenCallsReceiver = z.object({
  calls: z.array(Call),
}).describe("Receives open call entries from the input");

type OpenCallsReceiver = z.infer<typeof OpenCallsReceiver>;

const anthropic = new Anthropic();

const imageData = fs.readFileSync('data/workshop/open-calls-creatives.jpg');
const base64Image = imageData.toString('base64');

const tools = [
  {
    name: "OpenCallsReceiver",
    description: "Receives open call entries from the input",
    // `z.toJSONSchema` (Zod 4, built-in) emits JSON Schema from the
    // Zod tree - the model never sees Zod, only the schema.
    input_schema: z.toJSONSchema(OpenCallsReceiver, { target: "draft-7" }) as any,
  },
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
          data: base64Image,
        },
      },
      {
        type: "text",
        text: "Decode open calls from supplied image",
      },
    ],
  }],
  tools: tools,
  // Forcing the model to use this specific tool guarantees that the
  // response is a tool_use block matching our schema - no free-form
  // text to parse, no schema drift.
  tool_choice: { type: "tool", name: "OpenCallsReceiver" },
});

for (const block of response.content) {
  if (block.type === 'tool_use') {
    // `parse` validates the raw dict and gives us a typed object
    // with real `Date`s in place of the ISO strings.
    const receiver: OpenCallsReceiver = OpenCallsReceiver.parse(block.input);
    const sorted = [...receiver.calls].sort(
      (a, b) => b.deadline.getTime() - a.deadline.getTime()
    );
    for (const call of sorted) {
      console.log(`${call.deadline.toISOString()}: ${call.title}`);
    }
  }
}
