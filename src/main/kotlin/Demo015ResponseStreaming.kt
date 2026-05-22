/*
 * Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.
 *
 * This code is provided for educational purposes as part of the
 * "Agentic AI & Creative Coding" workshop.
 * Unauthorized reproduction or distribution is prohibited.
 */
package com.xemantic.ai.workshop

import com.xemantic.ai.anthropic.Anthropic
import com.xemantic.ai.anthropic.event.Event
import com.xemantic.ai.anthropic.event.Event.ContentBlockDelta.Delta.TextDelta
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

/**
 * Demo 015: Response Streaming
 *
 * Observations:
 *
 * - **Prompt engineering**:
 *   - prompting is like if philosophy was a programming language
 *
 * - **Context engineering**:
 *   - inference takes time, streaming works better for interactive
 *     use cases with "human in the loop", so a human can start reading the
 *     LLM output ASAP.
 *
 * - **Cognitive science**:
 *   - LLMs excel at generating stream of text constrained by specified criteria.
 *     Initially it was just a "poetry" of probabilistic continuation.
 *     Nowadays, the output can be sharper than the sharpest human mind,
 *     unmistakably adhering to rules in play.

 * - **Kotlin**:
 *   - streaming API for processing continuous input.
 */
fun main() = runBlocking {
    Anthropic()
        .messages
        .stream { +"Write me a poem." }
        .filterIsInstance<Event.ContentBlockDelta>()
        .map { it.delta }
        .filterIsInstance<TextDelta>()
        .map { it.text }
        .collect { delta -> print(delta) }
}
