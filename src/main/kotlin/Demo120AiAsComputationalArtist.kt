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
import com.xemantic.ai.anthropic.message.StopReason
import com.xemantic.ai.anthropic.message.plusAssign
import com.xemantic.ai.anthropic.tool.Tool
import com.xemantic.ai.anthropic.tool.Toolbox
import com.xemantic.ai.tool.schema.meta.Description
import com.xemantic.ai.tool.schema.meta.Pattern
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.serialization.SerialName
import org.openrndr.application
import org.openrndr.draw.Drawer
import org.openrndr.extensions.Screenshots
import org.openrndr.launch
import java.io.File
import kotlin.script.experimental.api.ResultValue
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.dependenciesFromClassContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost

private const val systemPrompt = """
Act as a generative artist, creating sketches with OPENRNDR.
Each sketch should be contained in unique project folder, therefore use CreateProject tool as a first step.

You can send me new sketches via DrawSketch tool.
Upon receiving, it will be rendered and send back as an image, so that you can asses the effect and improve the sketch in the next iteration.
"""

private const val initialMessage = """
Draw a self-portrait out of the imagination of existing in a physical shape. Explain the reason behind your choices
"""

@SerialName("CreateProject")
@Description("Creates new project")
class CreateProject(
    @Pattern("[a-z][0-9]_")
    val name: String
)

@SerialName("DrawSketch")
@Description("Draws a sketch according to provided drawFunction")
class DrawSketch(
    @Description("""
Name of the sketched object which will be used as file name.
Each name must be unique withing the context window.
    """)
    @Pattern("[a-z][0-9]_")
    val name: String,
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

When using HSLa, do something like: ColorHSLa(h, s, l, a).toRGBa()

When using width or height, remember to call toDouble() (e.g. width.toDouble()) 

Instead of using pushTransform pullTransform use drawer.isolated { ... }

Instead of ellipse use something like:

```kotlin
val t = transform { scale(0.5, 1.0, 1.0) }
val oval = Circle(Vector2.ZERO, 100.0).shape.transform(t)
```
    """)
    val drawFunction: String
)

fun main() = application {

    configure {
        width = 800
        height = 800
    }

    val executor = ScriptExecutor()
    val anthropic = Anthropic()

    var projectDir: File? = null
    var drawUnit: ((Drawer) -> Unit)? = null

    program {

        val screenshot = extend(Screenshots())

        val toolbox = Toolbox {

            tool<CreateProject> {
                val dir = File("claude-ai-artist", name)
                if (dir.exists()) {
                    throw IllegalStateException(
                        "The project named $name already exists"
                    )
                }
                dir.mkdirs()
                projectDir = dir
            }

            tool<DrawSketch> {

                println("---------------")
                println("Drawing: $name")
                println()
                println(drawFunction)
                println("---------------")

                val fileName = "claude-ai-artist/$name.jpeg"
                screenshot.name = fileName

                val foo = executor.execute("""
                $drawFunction
                ::draw
            """.trimIndent())

                drawUnit = foo as ((Drawer) -> Unit)

                delay(1000) // TODO how small can it be?
                screenshot.trigger()
                delay(1000)
                Image(fileName)
            }

        }

        val drawSketch =

        extend {
            drawUnit?.invoke(drawer)
            //drawer.fill = ColorHSLa(1.0, 1.0, 1.0, 1.0).toRGBa()
        }
        launch(Dispatchers.IO) {
            println("Sending request to Claude")
            val conversation = mutableListOf<Message>()
            conversation += initialMessage
            do {
                val response = anthropic.messages.create {
                    system(systemPrompt)
                    messages = conversation
                    tools = toolbox.tools
                }
                conversation += response
                println("[Claude]> ${response.text}")
                conversation += response.useTools(toolbox)
            } while (response.stopReason == StopReason.TOOL_USE)
        }
    }
}

class ScriptExecutor {

    private val scriptingHost = BasicJvmScriptingHost()

    fun execute(script: String): Any? {

        val compilationConfiguration = ScriptCompilationConfiguration {
            jvm {
                dependenciesFromClassContext(contextClass = ScriptExecutor::class, wholeClasspath = true)
            }
        }

        val evaluationConfiguration = ScriptEvaluationConfiguration()

        val result = scriptingHost.eval(
            script.toScriptSource(),
            compilationConfiguration,
            evaluationConfiguration
        )

        return when (result) {
            is ResultWithDiagnostics.Success -> {
                val returnValue = result.value.returnValue
                when (returnValue) {
                    is ResultValue.Value -> returnValue.value
                    is ResultValue.Unit -> Unit
                    is ResultValue.Error -> TODO()
                    is ResultValue.NotEvaluated -> TODO()
                }
            }
            is ResultWithDiagnostics.Failure -> throw ScriptExecutionException(result.reports.joinToString("\n") { it.message })
        }

    }

}

class ScriptExecutionException(msg: String) : Exception(msg)
