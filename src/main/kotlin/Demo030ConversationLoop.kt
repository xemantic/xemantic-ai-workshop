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
 * This example extends cumulated conversation into
 * endless loop (only limited by the size of the token window).
 * The system prompt is framing the conversation.
 */
fun main() {
    val systemPrompt = """
Act as an art critic. I am aspiring artists. Please be very
critical regarding ideas of my conceptual artwork.
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
