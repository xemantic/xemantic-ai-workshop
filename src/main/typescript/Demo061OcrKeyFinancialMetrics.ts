/*
 * Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.
 *
 * This code is provided for educational purposes as part of the
 * "Agentic AI & Creative Coding" workshop.
 * Unauthorized reproduction or distribution is prohibited.
 */

import Anthropic from '@anthropic-ai/sdk';
import * as fs from 'fs';

interface Entry {
  year: number;
  revenue: number;
  operatingIncome: number;
  netIncome: number;
}

interface KeyFinancialMetrics {
  entries: Entry[];
}

const anthropic = new Anthropic();

const imageData = fs.readFileSync('data/workshop/nvidia-income.png');
const base64Image = imageData.toString('base64');

const tools = [
  {
    name: "ExtractKeyFinancialMetrics",
    description: "Extracts key financial metrics from the report",
    input_schema: {
      type: "object" as const,
      properties: {
        entries: {
          type: "array",
          items: {
            type: "object",
            properties: {
              year: {
                type: "number",
                description: "Year of the financial data"
              },
              revenue: {
                type: "number",
                description: "Revenue in millions"
              },
              operatingIncome: {
                type: "number",
                description: "Operating income in millions"
              },
              netIncome: {
                type: "number",
                description: "Net income in millions"
              }
            },
            required: ["year", "revenue", "operatingIncome", "netIncome"]
          }
        }
      },
      required: ["entries"]
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
          media_type: "image/png",
          data: base64Image
        }
      },
      {
        type: "text",
        text: "Decode financial metrics from supplied image"
      }
    ]
  }],
  tools: tools
});

for (const block of response.content) {
  if (block.type === 'tool_use') {
    const metrics = block.input as KeyFinancialMetrics;

    for (const entry of metrics.entries) {
      console.log(entry);
    }

    const totalRevenue = metrics.entries.reduce(
      (sum, entry) => sum + entry.revenue,
      0
    );
    console.log(`Total revenue: ${totalRevenue}`);
  }
}
