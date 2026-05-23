/*
 * Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.
 *
 * This code is provided for educational purposes as part of the
 * "Agentic AI & Creative Coding" workshop.
 * Unauthorized reproduction or distribution is prohibited.
 */

/**
 * Demo 015: Response Streaming
 *
 * Observations:
 *
 * - Prompt engineering:
 *   - prompting is like if philosophy was a programming language
 *
 * - Context engineering:
 *   - inference takes time, streaming works better for interactive
 *     use cases with "human in the loop", so a human can start
 *     reading the LLM output ASAP.
 *
 * - Cognitive science:
 *   - LLMs excel at generating a stream of text constrained by
 *     specified criteria. Initially it was just a "poetry" of
 *     probabilistic continuation. Nowadays, the output can be
 *     sharper than the sharpest human mind, unmistakably adhering
 *     to rules in play.
 *
 * - TypeScript:
 *   - we use async iteration (`for await ... of`) to consume the
 *     event stream as it arrives
 *   - events are a discriminated union; we narrow on `type` to pull
 *     text deltas safely
 */

import Anthropic from '@anthropic-ai/sdk';

const anthropic = new Anthropic();

const stream = await anthropic.messages.stream({
  model: "claude-opus-4-7",
  max_tokens: 1024,
  messages: [{ role: "user", content: "Write me a poem." }],
});

// Iterate over raw SSE events. We only care about the incremental text
// deltas - everything else (message_start, ping, content_block_stop, ...)
// is filtered out by the type guards below.
for await (const event of stream) {
  if (event.type === 'content_block_delta' && event.delta.type === 'text_delta') {
    process.stdout.write(event.delta.text);
  }
}

console.log();
