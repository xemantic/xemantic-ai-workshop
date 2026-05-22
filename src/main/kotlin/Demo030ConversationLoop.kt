/*
 * Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.
 *
 * This code is provided for educational purposes as part of the
 * "Agentic AI & Creative Coding" workshop.
 * Unauthorized reproduction or distribution is prohibited.
 */

package com.xemantic.ai.workshop

import com.xemantic.ai.anthropic.Anthropic
import com.xemantic.ai.anthropic.cache.CacheControl
import com.xemantic.ai.anthropic.content.Text
import com.xemantic.ai.anthropic.message.Message
import com.xemantic.ai.anthropic.message.System
import com.xemantic.ai.anthropic.message.addCacheBreakpoint
import com.xemantic.ai.anthropic.message.plusAssign
import kotlinx.coroutines.runBlocking

/**
 * Demo 030: Conversation Loop
 *
 * This example cumulates the conversation in the
 * endless loop (only limited by the size of the context window
 * accepted by the model). It is the tiniest equivalent of "ChatGPT"
 * or rather claude.ai.
 *
 * Observaitions:
 *
 * - **Prompt engineering**:
 *   - system prompt: is different that the initial message
 *
 * - **Context engineering**:
 *   - caching: the major factor reducing LLM costs
 *
 * - **Cognitive science**:
 *   - conditioning LLM's expresion comes from role-playing
 */
fun main() = runBlocking {

    val systemPrompt = """
        Act as an art critic. I am an aspiring artists.
        Please be very critical regarding ideas of my conceptual artwork.
    """.trimIndent()

    val anthropic = Anthropic()
    val conversation = mutableListOf<Message>()

    while (true) {
        print("[user]> ")
        val line = readln()
        if (line == "exit") break
        conversation += line
        println("...Thinking...")
        val response = anthropic.messages.create {
            system = listOf(System(
                text = systemPrompt,
                cacheControl = CacheControl.Ephemeral()
            ))
            messages = conversation.addCacheBreakpoint()
        }
        conversation += response
        response.content.filterIsInstance<Text>().forEach {
            println("[assistant]> ${it.text}")
        }
    }

}

/*
  Note: this time we are not accessing text content through
  the response.text property, but by inspecting the full response
  content elements instead. Text is not the only possible content
  to be provided to and received from the LLM. We can also have
  Images and Documents on the input and ToolUse requests on
  the output.
 */