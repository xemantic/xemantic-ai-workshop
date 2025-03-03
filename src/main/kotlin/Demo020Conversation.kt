/*
 * Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.
 *
 * This code is provided for educational purposes as part of the
 * "Agentic AI & Creative Coding" workshop.
 * Unauthorized reproduction or distribution is prohibited.
 */

package com.xemantic.ai.workshop

import com.xemantic.ai.anthropic.Anthropic
import com.xemantic.ai.anthropic.message.Message
import com.xemantic.ai.anthropic.message.plusAssign
import kotlinx.coroutines.runBlocking

/**
 * What you will learn?
 *
 * - AI: conducting variable length conversations with the LLM
 *   requires cumulating dialog in the context window
 * - Cognitive Science: the theory of mind and metacognition
 * - Kotlin: operator overloading for adding messages and content
 */
fun main() = runBlocking {
    val anthropic = Anthropic()
    val conversation = mutableListOf<Message>()
    conversation += Message {
        +"Is it true, that to know we can die is to be dead already?"
    }
    val response1 = anthropic.messages.create {
        messages = conversation
    }
    println("Response 1:")
    println(response1.text)
    conversation += response1
    conversation += Message {
        +"Why do you think I asked you this question?"
    }
    val response2 = anthropic.messages.create {
        messages = conversation
    }
    println("Response 2:")
    println(response2.text)
    conversation += response2

    println("The whole past conversation is included in the token window:")
    println(conversation)
}

/*
  Note: we are overloading `plusAssign` operator to add message
  and the response content to the conversation.

  Discuss theory of mind and metacognition
 */