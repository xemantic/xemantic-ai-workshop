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
import com.xemantic.ai.anthropic.tool.Tool
import com.xemantic.ai.tool.schema.meta.Description
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerialName

// first we define a standard Kotlin function
// look how beautiful tail recursion is in Kotlin!
tailrec fun fibonacci(
    n: Int, a: Int = 0, b: Int = 1
): Int = when (n) {
    0 -> a
    1 -> b
    else -> fibonacci(n - 1, b, a + b)
}

// then we define the class carrying the tool
// input, in this case just one attribute named n
// this is the data LLM will send us as structured JSON
@SerialName("Fibonacci")
@Description("Calculates Fibonacci number n")
data class FibonacciTool(val n: Int)

/**
 * What you will learn?
 *
 * - AI Dev: tools as a basis for agentic use cases.
 * - AI Dev: how to define a tool input and seamlessly connect it
 *   with Kotlin logic.
 * - Cognitive Science: LLMs suck at math,
 *   do math with calculator, not with harnessed stochastic entropy.
 * - Kotlin: serialization offers compile time class metadata.
 */
fun main() = runBlocking {
    val tool = Tool<FibonacciTool> {
        fibonacci(n)
    }
    val myTools = listOf(tool)
    val conversation = mutableListOf<Message>()
    conversation += "What's Fibonacci number 42"
    val anthropic = Anthropic()
    val toolUseResponse = anthropic.messages.create {
        messages = conversation
        tools = myTools
    }
    println("Stop reason: ${toolUseResponse.stopReason}")
    println(toolUseResponse.text)
    conversation += toolUseResponse
    conversation += toolUseResponse.useTools()
    val finalResponse = anthropic.messages.create {
        messages = conversation
        tools = myTools
    }
    println(finalResponse.text)
}


/*
  Note: @Description annotation implies @Serializable
 */