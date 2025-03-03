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
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// This time we define more complex data structure
@SerialName("OpenCallsReceiver")
@Description("Receives open call entries from the input")
data class OpenCallsReceiver(
    val calls: List<Call>
) {

    // No
    @Serializable
    data class Call(
        val deadline: Instant, // TODO it should be LocalDate
        val title: String,
    )

}

/**
 * What you will learn?
 *
 * - AI: using tools just for structured input (without use)
 * - AI: we are forcing single tool use with `toolChoice`
 * - AI: powerful vision model of Claude LLM
 * - anthropic-sdk-kotlin: convenient helpers for files
 *   and media-type detection
 */
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

    // TODO it should be response.toolUseInput<OpenCallReceiver>()
    val receiver = response.toolUse!!.decodeInput() as OpenCallsReceiver

    receiver.calls.sortedByDescending {
        it.deadline
    }.forEach {
        println("${it.deadline.toLocalDateTime(TimeZone.UTC).date}: ${it.title}")
    }

}
