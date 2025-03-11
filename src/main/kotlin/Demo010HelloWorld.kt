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
import kotlinx.coroutines.runBlocking

/**
 * What you will learn?
 *
 * - AI Dev: communicating with Anthropic API basic input/output.
 * - Cognitive Science: the LLM output is non-deterministic.
 *   Run it multiple times with the same prompt to get a different output.
 *   Each time the internal mechanics of the neural net will differ.
 * - Kotlin: handling suspended calls in Kotlin `main` function
 *   with `runBlocking`.
 * - Kotlin: request building DSL with operator overloading
 *   (+something).
 */
fun main() {
    val anthropic = Anthropic()
    val response = runBlocking {
        anthropic.messages.create {
            +"Hello World!"
        }
    }
    println(response.text)
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
