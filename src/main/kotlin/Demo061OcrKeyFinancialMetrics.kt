/*
 * Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.
 *
 * This code is provided for educational purposes as part of the
 * "Agentic AI & Creative Coding" workshop.
 * Unauthorized reproduction or distribution is prohibited.
 */

@file:UseSerializers(BigDecimalSerializer::class)

package com.xemantic.ai.workshop

import com.xemantic.ai.anthropic.Anthropic
import com.xemantic.ai.anthropic.content.Image
import com.xemantic.ai.anthropic.message.*
import com.xemantic.ai.anthropic.tool.Tool
import com.xemantic.ai.tool.schema.meta.Description
import com.xemantic.ai.tool.schema.serialization.BigDecimalSerializer
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.math.BigDecimal

@SerialName("ExtractKeyFinancialMetrics")
@Description("Extracts key financial metrics from the report")
data class KeyFinancialMetrics(
    val entries: List<Entry>
) {

    @Serializable
    @SerialName("entry")
    data class Entry(
        val year: Int,
        val revenue: BigDecimal,
        val operatingIncome: BigDecimal,
        val netIncome: BigDecimal
    )

}

/**
 * Demo 061: OCR Key Financial Metrics
 *
 * A practical application of structured extraction: we send a screenshot
 * of an income statement and ask the model to populate a typed data
 * structure with the figures.
 *
 * Observations:
 *
 * - **Context engineering**:
 *   - tools are not only for execution - they double as a typed
 *     "output schema" for structured data extraction.
 *
 * - **Cognitive science**:
 *   - multimodal vision combined with tabular reasoning lets the
 *     model perform OCR + interpretation in a single step.
 *
 * - **Kotlin**:
 *   - `BigDecimal` via `@file:UseSerializers` keeps financial values
 *     exact - no floating-point drift when we sum revenues.
 */
fun main() = runBlocking {

    val tool = Tool<KeyFinancialMetrics>()
    val anthropic = Anthropic()

    val response = anthropic.messages.create {
        +Message {
            +Image("data/workshop/nvidia-income.png")
            +"Decode financial metrics from supplied image"
        }
        tools += tool
    }

    val metrics = response.toolUseInput<KeyFinancialMetrics>()
    metrics.entries.forEach {
        println(it)
    }
    val totalRevenue = metrics.entries.map {
        it.revenue
    }.sumOf { it }
    println("Total revenue: $totalRevenue")
}
