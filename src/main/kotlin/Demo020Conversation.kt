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
 * - AI: conducting variable length conversation with LLM
 * - Kotlin: operator overloading for
 */
fun main() = runBlocking {
    val anthropic = Anthropic()
    // we will be collecting
    val conversation = mutableListOf<Message>()
    conversation += Message {
        +"Is it true, that to know we can die is to be dead already?"
    }
    val response1 = anthropic.messages.create {
        messages = conversation
    }
    println(response1.text)
    conversation += response1
    conversation += Message {
        +"Why do you think I asked you this question?"
    }
    val response2 = anthropic.messages.create {
        messages = conversation
    }
    println(response2.text)
    conversation += response2

    println("The whole past conversation is included in the token window")
    println(conversation)
}
