/*
 * Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.
 *
 * This code is provided for educational purposes as part of the
 * "Agentic AI & Creative Coding" workshop.
 * Unauthorized reproduction or distribution is prohibited.
 */

package com.xemantic.ai.workshop

import com.xemantic.ai.anthropic.Anthropic
import kotlinx.coroutines.runBlocking

/**
 * Demo 010: Hello World
 *
 * Observations:
 *
 * - **Prompt engineering**:
 *   - a text prompt is causing the "answer" to be generated
 *   - inference: the process of generating the response by the model
 *   - early LLMs, like GPT2, were rather generating the most likely continuation
 *
 * - **Context engineering**:
 *   - underlying ontolgy: communication theory
 *   - we are receiving the answer as a message (from AI "assistant")
 *   - the response contains metadata
 *
 * - **Cognitive science**: the LLM output is non-deterministic
 *   (run it multiple times with the same prompt to get a different output)
 *
 * - **Kotlin**:
 *   - `runBlocking` needed to call suspended functions from `main`
 *   - the `+` operator builds a message.
 */
fun main() = runBlocking {
    val anthropic = Anthropic()
    val response = anthropic.messages.create {
        +"Hello World!"
    }
    println(response.text)
    //println(response) // uncomment to also show metadata
}

/*
  Note: anthropic.messages.create is a suspended function.
  We can only run it in a coroutine scope and runBlocking
  is one of the ways to do it. The runBlocking can be
  also put around the whole main function, if several
  suspended functions are being invoked, as you will
  see in subsequent examples.

  Note: we are using overloaded `unaryPlus` operator to add
  a message to the request and the content to the message
  alternatively we would have to write:

    messages = listOf(Message {
        content = listOf(Text("Hello World!"))
    })

  Note: the whole received response contains much more details
  and nested contend. For the convenience we are accessing
  extension property named `text`, so only the actual textual
  response from the LLM is displayed.
*/
