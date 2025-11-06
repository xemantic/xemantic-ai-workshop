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
import com.xemantic.ai.anthropic.tool.Toolbox
import com.xemantic.ai.tool.schema.meta.Description
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.draw.loadImage
import org.openrndr.launch
import org.openrndr.math.Vector2

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

fun Circle.draw(drawer: Drawer) {
    drawer.apply {
        stroke = color
        strokeWeight = thickness
        fill = null
        circle(position, radius)
    }
}

// Downloaded from Wikipedia
private const val monaLisaPath = "data/workshop/mona-lisa.jpeg"

fun main() = application {

    // the size is matching image size
    configure {
        width = 512
        height = 760
    }

    program {
        val monaLisaImage = loadImage(monaLisaPath)
        var circlesToDraw = emptyList<Circle>()
        val toolbox = Toolbox {
            tool<DrawCircles> {
                circlesToDraw += circles
                "circle drawn"
            }
        }
        val systemPrompt = """
            You can draw on a canvas visible to the human.
            Your expression will be drawn on top of the image given to you.
            
            Current resolution: ${width}x${height}"
        """.trimIndent()
        val anthropic = Anthropic()

        extend {
            drawer.image(
                monaLisaImage,
                x = 0.0,
                y = 0.0,
                width = width.toDouble(),
                height = height.toDouble()
            )
            circlesToDraw.forEach { circle ->
                circle.draw(drawer)
            }
        }

        launch(Dispatchers.IO) {
            println("Prompting Claude (Anthropic API) in the background")
            val response = anthropic.messages.create {
                system(systemPrompt)
                tools = toolbox.tools
                +Message {
                    +Image(monaLisaPath)
                    +"Draw black circles around the eyes of the person on this picture."
                }
            }
            println(response.text)
            response.useTools(toolbox)
        }
    }

}

