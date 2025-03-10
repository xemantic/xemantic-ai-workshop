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
import com.xemantic.ai.anthropic.message.plusAssign
import com.xemantic.ai.anthropic.tool.Tool
import com.xemantic.ai.tool.schema.meta.Description
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.serialization.SerialName
import org.openrndr.application
import org.openrndr.color.ColorHSLa
import org.openrndr.draw.Drawer
import org.openrndr.extensions.Screenshots
import org.openrndr.launch
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

When using HSLa, do something like ColorHSLa(h, s, l, a).toRGBa()

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
        val screenshot = extend(Screenshots()) {
            name = "shot.png"
        }
        extend {
            drawUnit?.invoke(drawer)
            drawer.fill = ColorHSLa(1.0, 1.0, 1.0, 1.0).toRGBa()
        }
        screenshot.trigger()
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
                delay(3000)
                screenshot.trigger()
            } while (repeat)
        }
    }
}

class ScriptExecutor {

    private val scriptingHost = BasicJvmScriptingHost()

    fun execute(script: String): Any? {

        val compilationConfiguration = ScriptCompilationConfiguration {
            //defaultImports(DependsOn::class, Repository::class)
            jvm {
                dependenciesFromClassContext(contextClass = ScriptExecutor::class, wholeClasspath = true)
            }
            //providedProperties(*(dependencies.map { it.name to KotlinType(it.type) }.toTypedArray()))
        }

        val evaluationConfiguration = ScriptEvaluationConfiguration {
            //providedProperties(*(dependencies.map { it.name to it.value }.toTypedArray()))
            //implicitReceivers(*serviceMap.values.toTypedArray())
            //implicitReceivers(Unit)
        }

        val result = scriptingHost.eval(script.toScriptSource(), compilationConfiguration, evaluationConfiguration)
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

//        return runBlocking {
//            val result = scriptingHost.eval(script.toScriptSource(), compilationConfiguration, evaluationConfiguration)
//
//        }
    }

}

class ScriptExecutionException(msg: String) : Exception(msg)
