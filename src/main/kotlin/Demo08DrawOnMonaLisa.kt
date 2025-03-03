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
import com.xemantic.ai.anthropic.content.Text
import com.xemantic.ai.anthropic.content.ToolUse
import com.xemantic.ai.anthropic.message.Message
import com.xemantic.ai.anthropic.message.StopReason
import com.xemantic.ai.anthropic.tool.Tool
import com.xemantic.ai.tool.schema.meta.Description
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.loadImage
import org.openrndr.launch
import org.openrndr.math.Vector2

var circlesToDraw = emptyList<Circle>()

// Downloaded from Wikipedia
private const val monaLisaPath = "data/workshop/mona-lisa.jpeg"

fun main() = application {

    configure {
        width = 512
        height = 760
    }

    program {

        val monaLisaImage = loadImage(monaLisaPath)

        val myTools = listOf(
            Tool<DrawCircles> {
                circlesToDraw += circles
                "circle drawn"
            }
        )
        val systemPrompt = "You can draw on a canvas visible to the human. Current resolution: ${width}x${height}"
        val anthropic = Anthropic()

        launch(Dispatchers.IO) {
            println("Prompting Claude (Anthropic API)")
            val response = anthropic.messages.create {
                system(systemPrompt)
                tools = myTools
                +Message {
                    +Image(monaLisaPath)
                    +"Draw black circles around the eyes of the person on this picture."
                }
            }
            if (response.stopReason == StopReason.TOOL_USE) {
                response.content.forEach {
                    when (it) {
                        is Text -> println(it.text)
                        is ToolUse -> it.use()
                        else -> {}
                    }
                }
            }
        }
        extend {
            drawer.image(monaLisaImage, 0.0, 0.0, width.toDouble(), height.toDouble())
            circlesToDraw.forEach { circle ->
                drawer.apply {
                    stroke = circle.color
                    strokeWeight = circle.thickness
                    fill = null
                    circle(
                        position = circle.position,
                        radius = circle.radius
                    )
                }
            }
        }

    }

}

@SerialName("DrawCircles")
@Description("Draws circles specified in the circle list")
data class DrawCircles(
    val circles: List<Circle>
)

@Serializable
@SerialName("circle")
data class Circle(
    val position: Vector2,
    val radius: Double,
    val color: ColorRGBa,
    val thickness: Double
)
