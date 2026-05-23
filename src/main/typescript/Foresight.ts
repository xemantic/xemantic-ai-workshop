/*
 * Copyright (c) 2026. Kazimierz Pogoda / Xemantic. All rights reserved.
 *
 * This code is provided for educational purposes as part of the
 * "Agentic AI & Creative Coding" workshop.
 * Unauthorized reproduction or distribution is prohibited.
 */

/**
 * Foresight: helper for talking to the self-hosted Anthropic-compatible
 * endpoint instead of the public Anthropic API.
 *
 * Mirrors the Kotlin `Foresight()` builder in
 * src/main/kotlin/Foresight.kt:
 * - custom baseURL pointing at the Foresight cluster
 * - `Authorization: Bearer <key>` instead of the default `x-api-key`
 * - a default model served by that cluster
 *
 * Usage:
 *
 *     import { Foresight, FORESIGHT_MODEL } from './Foresight';
 *
 *     const anthropic = Foresight();
 *     const response = await anthropic.messages.create({
 *       model: FORESIGHT_MODEL,
 *       max_tokens: 1024,
 *       messages: [{ role: "user", content: "Hello World!" }],
 *     });
 *
 * Setup:
 *     export FORESIGHT_API_KEY="..."
 */

import Anthropic from '@anthropic-ai/sdk';

export const FORESIGHT_MODEL = "mlx-community/Kimi-K2.6-mlx-DQ3_K_M-q8";

export function Foresight(): Anthropic {
  const apiKey = process.env.FORESIGHT_API_KEY;
  if (!apiKey) {
    throw new Error("FORESIGHT_API_KEY environment variable not set");
  }
  return new Anthropic({
    // No trailing `/v1/` - the SDK appends `/v1/messages` itself.
    // The Kotlin SDK in this repo normalizes the path differently,
    // which is why its `baseUrl` does include `/v1/`.
    baseURL: "https://dev1.ycluster.net",
    // Placeholder - the constructor requires some value, but the
    // server authenticates via the Authorization header below.
    apiKey: "unused",
    defaultHeaders: { Authorization: `Bearer ${apiKey}` },
  });
}
