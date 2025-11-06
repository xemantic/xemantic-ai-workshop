/*
 * Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.
 *
 * This code is provided for educational purposes as part of the
 * "Agentic AI & Creative Coding" workshop.
 * Unauthorized reproduction or distribution is prohibited.
 */

package com.xemantic.ai.workshop

import com.xemantic.ai.anthropic.Anthropic
import com.xemantic.ai.anthropic.content.Image
import com.xemantic.ai.anthropic.message.Message
import com.xemantic.ai.anthropic.tool.Tool
import com.xemantic.ai.anthropic.tool.ToolChoice
import com.xemantic.ai.tool.schema.meta.Description
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

// This time we define more complex data structure
@OptIn(ExperimentalTime::class)
@SerialName("OpenCallsReceiver")
@Description("Receives open call entries from the input")
data class OpenCallsReceiver(
    val calls: List<Call>
) {

    // No
    @Serializable
    @SerialName("call")
    data class Call(
        val deadline: Instant, // TODO it should be LocalDate
        val title: String,
    )

}

/**
 * What you will learn?
 *
 * - context engineering:
 *     - using tools just for structured input (without use/execution).
 *     - we are forcing single tool use with `toolChoice`.
 * - cognitive science: powerful vision model of Claude LLM - multimodality
 * - Kotlin: anthropic-sdk-kotlin - convenient helpers for files
 *   and media-type detection.
 */
@OptIn(ExperimentalTime::class)
fun main() = runBlocking {
    val tool = Tool<OpenCallsReceiver>() // no tool use this time
    val anthropic = Anthropic()
    val response = anthropic.messages.create {
        +Message {
            +Image("data/workshop/open-calls-creatives.jpg")
            +"Decode open calls from supplied image"
        }
        tools += tool
        toolChoice = ToolChoice.Tool<OpenCallsReceiver>()
    }

    val receiver = response.toolUseInput<OpenCallsReceiver>()

    receiver.calls.sortedByDescending {
        it.deadline
    }.forEach {
        println("${it.deadline}: ${it.title}")
    }

}
