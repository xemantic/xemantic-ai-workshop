/*
 * Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.
 *
 * This code is provided for educational purposes as part of the
 * "Agentic AI & Creative Coding" workshop.
 * Unauthorized reproduction or distribution is prohibited.
 */

import Anthropic from '@anthropic-ai/sdk';
import type { MessageParam } from '@anthropic-ai/sdk/resources/messages';
import * as readline from 'readline/promises';

const systemPrompt = `
Act as an art critic. I am an aspiring artists.
Please be very critical regarding ideas of my conceptual artwork.
`.trim();

const anthropic = new Anthropic();
const conversation: MessageParam[] = [];

const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout,
});

while (true) {
  const line = await rl.question('[user]> ');

  if (line === 'exit') break;

  conversation.push({
    role: 'user',
    content: line
  });

  console.log('...Thinking...');

  const response = await anthropic.messages.create({
    model: "claude-sonnet-4-5-20250929",
    max_tokens: 1024,
    system: systemPrompt,
    messages: conversation,
  });

  conversation.push({
    role: 'assistant',
    content: response.content
  });

  // Filter and display only text content
  response.content
    .filter(block => block.type === 'text')
    .forEach(block => {
      if (block.type === 'text') {
        console.log(`[assistant]> ${block.text}`);
      }
    });
}

rl.close();
