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

@Serializable
data class Entry(
  val deadline: String,
  val title: String,
)

@SerialName("OpenCallsReceiver")
@Description("Receives entries from the image")
data class OpenCallsReceiver(
  val calls: List<Entry>
)

fun main3() = runBlocking {

  val myTools = listOf(
    Tool<OpenCallsReceiver> { "Data provided to client" }
  )

  val client = Anthropic()

  val response = client.messages.create {
    +Message {
      +Image("data/images/open-calls-creatives.jpg")
      +"Decode open calls from supplied image"
    }
    tools = myTools
    toolChoice = ToolChoice.Tool<OpenCallsReceiver>()
  }

//  val receiver = response.toolUse!!.decodeInput<OpenCallsReceiver>()

//  receiver.calls.forEach {
//    println(it)
//  }

}
