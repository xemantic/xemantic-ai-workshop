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
 * - AI: sending a message to Anthropic API and receiving a response
 * - Kotlin: handling suspended calls in Kotlin `main` function with `runBlocking`
 * - SDK: using
 */
fun main() {
    val anthropic = Anthropic()
    val response = runBlocking {
        anthropic.messages.create {
            +Message {
                +"Hello World!"
            }
        }
    }
    println(response.text)
}

/*
  Note: anthropic.messages.create is a suspended function.
  We can only run it in a coroutine scope and runBlocking is one of the ways to do it.

  Note: we are using overloaded `unaryPlus` operator to
*/
