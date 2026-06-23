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
import com.xemantic.ai.anthropic.content.ThinkingBlock
import com.xemantic.ai.anthropic.message.Message
import com.xemantic.ai.anthropic.thinking.ThinkingConfigEnabled
import kotlinx.coroutines.runBlocking

/**
 * Demo 017: Extended Thinking
 *
 * You will learn:
 *
 * - prompt engineering: extended thinking allows Claude to reason through problems
 *   step-by-step before providing an answer, similar to Chain-of-Thought prompting
 *   but built into the model's behavior.
 * - context engineering: thinking blocks are separate from text responses, allowing
 *   you to see the reasoning process and final answer distinctly.
 * - cognitive science: showing the model's reasoning process helps build trust and
 *   allows debugging of the model's approach. Complex problems benefit from
 *   explicit reasoning steps.
 * - AI development: thinking budget controls how many tokens the model can use
 *   for internal reasoning (minimum 1024, recommended 2048+ for complex tasks).
 * - Kotlin: filtering content by type (ThinkingBlock vs Text) to access different
 *   parts of the response.
 */
fun main() = runBlocking {
    val anthropic = Anthropic()

    println("=== Example 1: Basic Math Reasoning ===\n")

    val response1 = anthropic.messages.create {
        model = "claude-sonnet-4-5-20250929"
        maxTokens = 4096
        thinking = ThinkingConfigEnabled {
            budgetTokens = 2048
        }
        messages = listOf(
            Message {
                +"What is 27 * 453? Show your reasoning."
            }
        )
    }

    // Extract thinking blocks and text responses
    val thinkingBlocks1 = response1.content.filterIsInstance<ThinkingBlock>()
    val textBlocks1 = response1.content.filterIsInstance<Text>()

    if (thinkingBlocks1.isNotEmpty()) {
        println("Claude's internal reasoning:")
        println(thinkingBlocks1.first().thinking)
        println("\nSignature: ${thinkingBlocks1.first().signature}")
    }

    if (textBlocks1.isNotEmpty()) {
        println("\nFinal answer:")
        println(textBlocks1.first().text)
    }

    println("\n=== Example 2: Complex Problem Solving ===\n")

    val response2 = anthropic.messages.create {
        model = "claude-sonnet-4-5-20250929"
        maxTokens = 4096
        thinking = ThinkingConfigEnabled {
            budgetTokens = 3000  // More budget for complex reasoning
        }
        messages = listOf(
            Message {
                +"""
                    Three people are on a boat: a grandmother, her daughter, and her
                    granddaughter. The grandmother is twice the age of her daughter.
                    The daughter is 24 years older than the granddaughter. The sum of
                    their ages is 80. How old is each person?
                """.trimIndent()
            }
        )
    }

    val thinkingBlocks2 = response2.content.filterIsInstance<ThinkingBlock>()
    val textBlocks2 = response2.content.filterIsInstance<Text>()

    if (thinkingBlocks2.isNotEmpty()) {
        println("Claude's problem-solving process:")
        println(thinkingBlocks2.first().thinking)
    }

    if (textBlocks2.isNotEmpty()) {
        println("\nSolution:")
        println(textBlocks2.first().text)
    }

    println("\n=== Token Usage ===")
    println("Input tokens: ${response2.usage.inputTokens}")
    println("Output tokens: ${response2.usage.outputTokens}")
    println("(Note: Thinking tokens are included in output_tokens)")
}

/*
  Note: Extended thinking is particularly useful for:
  - Mathematical problems requiring step-by-step calculation
  - Logical puzzles and reasoning challenges
  - Code debugging and algorithm design
  - Complex decision-making with multiple factors
  - Analytical tasks requiring careful consideration

  The thinking block contains:
  - thinking: The model's internal reasoning process (visible text)
  - signature: A cryptographic signature verifying the thinking came from Claude

  Budget tokens:
  - Minimum: 1024 tokens
  - Recommended: 2048-4096 for most tasks
  - Higher budgets allow more thorough reasoning for complex problems

  Model support:
  - Claude Sonnet 4.5 (claude-sonnet-4-5-20250929)
  - Claude Sonnet 4 (claude-sonnet-4-20250514)
  - Claude Sonnet 3.7 (claude-3-7-sonnet-20250219) (deprecated)
  - Claude Haiku 4.5 (claude-haiku-4-5-20251001)
  - Claude Opus 4.5 (claude-opus-4-5-20251101)
  - Claude Opus 4.1 (claude-opus-4-1-20250805)
  - Claude Opus 4 (claude-opus-4-20250514)
  See documentation: https://platform.claude.com/docs/en/build-with-claude/extended-thinking#supported-models

  The SDK provides type-safe configuration through ThinkingConfigEnabled
  instead of raw JSON, making it harder to misconfigure the feature.
*/
