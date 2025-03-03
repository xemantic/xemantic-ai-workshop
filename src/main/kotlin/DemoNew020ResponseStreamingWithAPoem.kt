package com.xemantic.ai.workshop

import com.xemantic.ai.anthropic.Anthropic
import com.xemantic.ai.anthropic.event.Delta
import com.xemantic.ai.anthropic.event.Event
import com.xemantic.ai.anthropic.message.Message
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

fun main() {
  val client = Anthropic()
  runBlocking {
    client.messages.stream {
      +Message { +"Write me a poem." }
    }
      .filterIsInstance<Event.ContentBlockDelta>()
      .map { (it.delta as Delta.TextDelta).text }
      .collect { delta -> print(delta) }
  }
}
