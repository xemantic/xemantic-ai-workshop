/*
 * Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.
 *
 * This code is provided for educational purposes as part of the
 * "Agentic AI & Creative Coding" workshop.
 * Unauthorized reproduction or distribution is prohibited.
 */

package com.xemantic.ai.workshop

import com.xemantic.ai.anthropic.Anthropic
import com.xemantic.ai.anthropic.content.ToolUse
import com.xemantic.ai.anthropic.message.Message
import com.xemantic.ai.anthropic.tool.Tool
import com.xemantic.ai.tool.schema.meta.Description
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerialName

fun main2() = runBlocking {
  val client = Anthropic {
    Tool<FibonacciTool> {
      fibonacci(n)
    }
  }
  val response = client.messages.create {
    +Message { +"What's Fibonacci number 42" }
  }
  val toolUse = response.content.filterIsInstance<ToolUse>().first()
  val toolResult = toolUse.use()
  //println(toolResult)
}

tailrec fun fibonacci(
  n: Int, a: Int = 0, b: Int = 1
): Int = when (n) {
  0 -> a; 1 -> b; else -> fibonacci(n - 1, b, a + b)
}

@SerialName("Fibonacci")
@Description("Calculates Fibonacci number n")
data class FibonacciTool(val n: Int)
