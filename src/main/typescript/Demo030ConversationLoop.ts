/*
 * Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.
 *
 * This code is provided for educational purposes as part of the
 * "Agentic AI & Creative Coding" workshop.
 * Unauthorized reproduction or distribution is prohibited.
 */

/**
 * Demo 030: Conversation Loop
 *
 * This example accumulates the conversation in an endless loop
 * (only limited by the size of the context window accepted by the
 * model). It is the tiniest equivalent of "ChatGPT", or rather claude.ai.
 *
 * Observations:
 *
 * - Prompt engineering:
 *   - system prompt: is different from the initial message
 *
 * - Context engineering:
 *   - caching: the major factor reducing LLM costs
 *
 * - Cognitive science:
 *   - conditioning the LLM's expression comes from role-playing
 *
 * - TypeScript:
 *   - `readline/promises` gives us a Promise-based `question()`,
 *     so the loop reads naturally without callback nesting.
 *   - the system prompt is sent as a typed block with
 *     `cache_control` so the unchanging prefix is cache-hit on
 *     subsequent turns.
 */

import Anthropic from '@anthropic-ai/sdk';
import type { MessageParam } from '@anthropic-ai/sdk/resources/messages';
import * as readline from 'readline/promises';

const systemPrompt = `
Act as an art critic. I am an aspiring artist.
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
    model: "claude-opus-4-7",
    max_tokens: 1024,
    // `cache_control` lets the API reuse this prefix across turns -
    // the system block is identical on every request, so we only pay
    // full price for it once.
    system: [{
      type: "text",
      text: systemPrompt,
      cache_control: { type: "ephemeral" }
    }],
    messages: conversation,
  });

  conversation.push({
    role: 'assistant',
    content: response.content
  });

  // The response may contain non-text blocks (e.g. tool use) in more
  // advanced demos, so we explicitly filter for text here.
  response.content
    .filter(block => block.type === 'text')
    .forEach(block => {
      if (block.type === 'text') {
        console.log(`[assistant]> ${block.text}`);
      }
    });
}

rl.close();
