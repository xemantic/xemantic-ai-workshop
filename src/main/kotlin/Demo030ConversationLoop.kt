/*
 * Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.
 *
 * This code is provided for educational purposes as part of the
 * "Agentic AI & Creative Coding" workshop.
 * Unauthorized reproduction or distribution is prohibited.
 */

package com.xemantic.ai.workshop

import com.xemantic.ai.anthropic.Anthropic
import com.xemantic.ai.anthropic.content.Text
import com.xemantic.ai.anthropic.message.Message
import com.xemantic.ai.anthropic.message.plusAssign
import kotlinx.coroutines.runBlocking

/**
 * This example cumulates the conversation in the
 * endless loop (only limited by the size of the context window
 * accepted by the model).
 *
 * What you will learn?
 *
 * - AI: the use of system prompts for conditioning the conversation
 * - Cognitive Science: conditioning AI's behaviour comes from role-playing
 * - Kotlin: multiline strings come handy for prompts
 */
fun main() {
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
        conversation += Message { +line }
        println("...Thinking...")
        val response = runBlocking {
            anthropic.messages.create {
                messages = conversation
                system(systemPrompt)
            }
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