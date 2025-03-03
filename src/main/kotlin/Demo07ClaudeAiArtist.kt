/*
 * Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.
 *
 * This code is provided for educational purposes as part of the
 * "Agentic AI & Creative Coding" workshop.
 * Unauthorized reproduction or distribution is prohibited.
 */

package com.xemantic.ai.workshop

import com.xemantic.ai.anthropic.Anthropic
import com.xemantic.ai.anthropic.message.Message
import com.xemantic.ai.anthropic.tool.Tool
import com.xemantic.ai.anthropic.tool.ToolChoice
import com.xemantic.ai.tool.schema.meta.Description
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.launch
import org.openrndr.math.Vector2

var linesToDraw = emptyList<Line>()

fun main() = application {

    configure {
        width = 800
        height = 600
    }

    program {

        val tool = Tool<DrawLines> {
            linesToDraw = lines
            "line drawn"
        }
        val systemPrompt =
            "You can draw on a canvas visible to the human. Current resolution: ${width}x${height}, the background is black"
        val anthropic = Anthropic()

        launch(Dispatchers.IO) {
            val response = anthropic.messages.create {
                system(systemPrompt)
                +Message { +"Draw a car" }
                toolChoice = ToolChoice.Tool<DrawLines>()
                tools += tool
            }
            response.useTools()
        }

        extend {
            linesToDraw.forEach { line ->
                drawer.stroke = line.color
                drawer.strokeWeight = line.thickness
                drawer.lineSegment(line.start, line.end)
            }
        }
    }

}

@SerialName("DrawLines")
@Description("Draws lines specified in the lines list")
data class DrawLines(
    val lines: List<Line>
)

@Serializable
@SerialName("line")
data class Line(
    val color: ColorRGBa,
    val thickness: Double,
    val start: Vector2,
    val end: Vector2
)
