/*
 * Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.
 *
 * This code is provided for educational purposes as part of the
 * "Agentic AI & Creative Coding" workshop.
 * Unauthorized reproduction or distribution is prohibited.
 */

package com.xemantic.ai.workshop.openrndr

import kotlin.script.experimental.api.ResultValue
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.dependenciesFromClassContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost

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
