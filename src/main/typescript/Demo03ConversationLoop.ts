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

/**
 * Demo 03: Conversation Loop
 *
 * This example cumulates the conversation in an
 * endless loop (only limited by the size of the context window
 * accepted by the model). It is the tiniest equivalent of "ChatGPT"
 * or rather Claude AI.
 *
 * What you will learn?
 *
 * - AI Dev: the use of system prompts for conditioning the conversation
 * - Cognitive Science: conditioning AI's behaviour comes from role-playing
 * - TypeScript: using readline for interactive CLI input
 */

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
    model: "claude-3-7-sonnet-20250219",
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

/*
  Note: This time we are filtering the response content to only
  display text blocks. Text is not the only possible content
  to be provided to and received from the LLM. We can also have
  images and documents on the input and tool_use requests on
  the output.
*/