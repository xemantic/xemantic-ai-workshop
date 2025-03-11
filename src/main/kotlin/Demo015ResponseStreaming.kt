/*
 * Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.
 *
 * This code is provided for educational purposes as part of the
 * "Agentic AI & Creative Coding" workshop.
 * Unauthorized reproduction or distribution is prohibited.
 */
package com.xemantic.ai.workshop

import com.xemantic.ai.anthropic.Anthropic
import com.xemantic.ai.anthropic.event.Delta
import com.xemantic.ai.anthropic.event.Event
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

/**
 * What you will learn?
 *
 * - AI Dev: inference takes time, streaming works better for interactive
 *   use cases with "human in the loop", so a human can start reading the
 *   LLM output ASAP.
 * - Cognitive Science: LLMs excel at generating stream of text constrained
 *   by specified criteria. Initially it was just a "poetry" of probabilistic
 *   continuation. Nowadays, the output can be sharper than the sharpest
 *   human mind.
 * - Kotlin: handling suspended calls in Kotlin `main` function
 *   with `runBlocking`.
 * - Kotlin: streaming API for processing continuous input.
 */
fun main() = runBlocking {
    Anthropic()
        .messages
        .stream { +"Write me a poem." }
        .filterIsInstance<Event.ContentBlockDelta>()
        .map { (it.delta as Delta.TextDelta).text }
        .collect { delta -> print(delta) }
}
