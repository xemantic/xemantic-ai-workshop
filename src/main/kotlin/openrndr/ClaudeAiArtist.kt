package com.xemantic.ai.workshop.openrndr

import com.xemantic.ai.anthropic.Anthropic
import com.xemantic.ai.anthropic.message.Message
import com.xemantic.ai.anthropic.message.plusAssign
import com.xemantic.ai.anthropic.tool.Tool
import com.xemantic.ai.tool.schema.meta.Description
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.SerialName
import org.openrndr.application
import org.openrndr.draw.Drawer
import org.openrndr.launch

private const val systemPrompt = """
Act as a generative artist, creating sketches with OPENRNDR.

You can send me new sketches via DrawSketch tool. Upon receiving it I will render it for you, and send it back so you can asses the effect.
"""

@SerialName("DrawSketch")
@Description("Draws a sketch according to provided drawFunction")
class DrawSketch(
    @Description("""
Sends a function using OPENRNDR Drawer instance for drawing. E.g.:

```kotlin
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer

fun draw(drawer: Drawer) {
    drawer.fill = ColorRGBa.PINK
    drawer.circle(100.0, 100.0, 100.0)
}        
```

    """)
    val drawFunction: String
)

fun main() = application {

    val executor = ScriptExecutor()
    val anthropic = Anthropic()
    var drawUnit: ((Drawer) -> Unit)? = null

    val tool = Tool<DrawSketch> {
        println("drawFunction:")
        println(drawFunction)
        val foo = executor.execute("""
                $drawFunction
                ::draw
            """.trimIndent())
        drawUnit = foo as ((Drawer) -> Unit)
    }
    configure {
        width = 800
        height = 800
    }
    program {
        extend {
            drawUnit?.invoke(drawer)
        }
        launch(Dispatchers.IO) {
            val conversation = mutableListOf<Message>()
            conversation += Message { +"Please draw me something" }
            var repeat = true
            do {
                val response = anthropic.messages.create {
                    system(systemPrompt)
                    messages = conversation
                    tools += tool
                }
                conversation += response
                println(response.text)
                conversation += response.useTools()
            } while (repeat)
        }
    }
}

