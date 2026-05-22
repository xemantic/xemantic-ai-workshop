/*
 * Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.
 *
 * This code is provided for educational purposes as part of the
 * "Agentic AI & Creative Coding" workshop.
 * Unauthorized reproduction or distribution is prohibited.
 */

package com.xemantic.ai.workshop

import com.xemantic.ai.anthropic.Anthropic
import com.xemantic.ai.anthropic.Model
import com.xemantic.ai.anthropic.content.Text
import com.xemantic.ai.anthropic.message.Message
import com.xemantic.ai.anthropic.message.Role
import kotlinx.coroutines.runBlocking

/**
 * Demo 012: Inference Parameters
 *
 * Observations:
 *
 * - **Context engineering**:
 *   - we are encapsulating prompts / data in messages
 *   - multimodal - other content types can be specified
 *   - the inference can be controlled with parameters
 */
fun main() = runBlocking {
    val anthropic = Anthropic()
    val response = anthropic.messages.create {
        messages = listOf(
            Message {
                role = Role.USER
                content = listOf(
                    Text("Hello World!")
                    //Document { }
                    //Image { }
                )
                +"Hello World!"
            }
        )
        model(Model.CLAUDE_OPUS_4_7)
        maxTokens = 1024
        topK = 32
        temperature = 1.0
//        thinking = ThinkingConfig.Adaptive {
//            display = ThinkingConfig.Display.SUMMARIZED
//        }

    }
    println(response.text)
    //println(response) // uncomment to also show metadata
}
