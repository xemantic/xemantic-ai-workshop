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
 * Demo 020: Conversation
 *
 * Observations:
 *
 * - **Context engineering**:
 *   - conducting a variable length dialog with the LLM
 *     requires cumulating conversation in the context window.
 *
 * - **Cognitive science**:
 *   - the theory of mind and metacognition (introspection)
 *
 * - **Kotlin**:
 *   - operator overloading for adding messages and content
 */
fun main() = runBlocking {

    val anthropic = Anthropic()
    val context = mutableListOf<Message>()

    context += "Is it true, that to know we can die is to be dead already?"
    val response1 = anthropic.messages.create {
        messages = context
    }
    context += response1
    println("Response 1: ${response1.text}")

    context += "Why do you think I asked you this question?"
    val response2 = anthropic.messages.create {
        messages = context
    }
    context += response2
    println("Response 2: ${response2.text}")

    println(context) // the whole past convesation is included in the token window
}

/*
  Note: we are overloading `plusAssign` operator to add message
  and the response content to the conversation.

  Discuss theory of mind and metacognition
 */
