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
    data class Entry(
        val year: Int,
        val revenue: BigDecimal,
        val operatingIncome: BigDecimal,
        val netIncome: BigDecimal
    )

}

fun main() {

    val tool = Tool<KeyFinancialMetrics>()
    val anthropic = Anthropic()

    val response = runBlocking {
        anthropic.messages.create {
            +Message {
                +Image("data/workshop/nvidia-income.png")
                +"Decode financial metrics from supplied image"
            }
            tools += tool
        }
    }

    val metrics = response.toolUse!!.decodeInput() as KeyFinancialMetrics
    metrics.entries.forEach {
        println(it)
    }
    val totalRevenue = metrics.entries.map {
        it.revenue
    }.sumOf { it }
    println("Total revenue: $totalRevenue")
}
