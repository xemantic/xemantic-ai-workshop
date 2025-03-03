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
)

@Serializable
data class Entry(
  val year: Int,
  val revenue: BigDecimal,
  val operatingIncome: BigDecimal,
  val netIncome: BigDecimal
)

fun main() {

  val tool = Tool<KeyFinancialMetrics>()
  val client = Anthropic()

  val response = runBlocking {
    client.messages.create {
      +Message {
        +"Decode financial metrics from supplied image"
        +Image("data/nvidia-income.png")
      }
      tools += tool
    }
  }

  //val tool = response.content[0] as ToolUse
//  val report = tool.input<KeyFinancialMetrics>()
//  report.entries.forEach {
//    println(it)
//  }

}
