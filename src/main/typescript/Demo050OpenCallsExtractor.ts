/*
 * Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.
 *
 * This code is provided for educational purposes as part of the
 * "Agentic AI & Creative Coding" workshop.
 * Unauthorized reproduction or distribution is prohibited.
 */

import Anthropic from '@anthropic-ai/sdk';
import * as fs from 'fs';

interface Call {
  deadline: string;
  title: string;
}

interface OpenCallsReceiver {
  calls: Call[];
}

const anthropic = new Anthropic();

const imageData = fs.readFileSync('data/workshop/open-calls-creatives.jpg');
const base64Image = imageData.toString('base64');

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
  model: "claude-sonnet-4-5-20250929",
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
  tool_choice: { type: "tool", name: "OpenCallsReceiver" }
});

for (const block of response.content) {
  if (block.type === 'tool_use') {
    const receiver = block.input as OpenCallsReceiver;
    const sortedCalls = receiver.calls.sort((a, b) =>
      b.deadline.localeCompare(a.deadline)
    );

    for (const call of sortedCalls) {
      console.log(`${call.deadline}: ${call.title}`);
    }
  }
}

/*
  Note: We use tool_choice to force the model to use our specific
  tool for structured data extraction. This is useful when we want
  to ensure a particular output format.
*/
