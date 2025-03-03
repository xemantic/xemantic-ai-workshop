package com.xemantic.ai.workshop

import com.xemantic.ai.anthropic.Anthropic
import com.xemantic.ai.anthropic.content.Text
import com.xemantic.ai.anthropic.message.Message
import com.xemantic.ai.anthropic.message.MessageResponse
import com.xemantic.ai.anthropic.message.Role
import kotlinx.coroutines.runBlocking

val initialPropmpt = "Is the world made of facts or of things?"
//val initialPropmpt = "Hi, do you believe in telepathy?"
//val initialPropmpt = "Ping"

fun main() = runBlocking {
  println("[Initial prompt]: $initialPropmpt")
  val client = Anthropic()
  var conversation1 = mutableListOf<Message>()
  var conversation2 = mutableListOf<Message>()
  conversation1 += Message { +initialPropmpt }
  repeat(10) {
    val response1 = client.messages.create {
      //system("Your name is PersonA. ALWAYS act as a human, talk like a boomer. You are 50% flirtatious, 20% confident, 10% willing to meet in real life. Answer causally, like a human using a messanger, sometimes in a single word, sometimes in a short sentence, sometimes slightly longer.")
      messages = conversation1
    }
    println("[AI]: ${response1.text}")
    conversation1 += Message {
      role = Role.ASSISTANT
      +response1.content
    }
    conversation2 += Message {
      role = Role.USER
      +response1.content
    }
    val response2 = client.messages.create {
      system("Your name is Sophia. Important - ALWAYS pretend to be a human, very intelligent and curious one. The future of the world might depend on your acting. Your initial question was: $initialPropmpt")
      messages = conversation2
    }
    println("[Person]: ${response2.text}")
    conversation2 += Message {
      role = Role.ASSISTANT
      +response2.content
    }
    conversation1 += Message {
      role = Role.USER
      +response2.content
    }
  }
}
