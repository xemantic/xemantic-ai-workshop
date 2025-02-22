/*
 * Copyright (c) 2025 by Kazimierz Pogoda / Xemantic
 * All rights reserved. For educational purposes only.
 */

package com.xemantic.ai.workshop.intro

@Suppress("UnusedVariable", "unused", "RedundantExplicitType") // to avoid warnings
fun main() {
    // Basic Types and Variables
    val immutableString = "I cannot be changed"  // val - immutable
    var mutableString = "I can be changed"       // var - mutable
    mutableString = "Changed!"

    // Basic Types
    val myInt: Int = 42
    val myDouble: Double = 3.14
    val myFloat: Float = 3.14f
    val myLong: Long = 123456789L
    val myBoolean: Boolean = true
    val myChar: Char = 'A'

    // Type Inference
    val inferredInt = 42       // Kotlin knows this is Int
    val inferredString = "Hi"  // Kotlin knows this is String

    // String Templates
    val name = "Student"
    println("Hello, $name!")
    println("The sum of 2 and 2 is ${2 + 2}")

    // Multi-line Strings
    val multiLine = """
        This is a multi-line
        string in Kotlin.
        No escape characters needed!
    """.trimIndent()

    println(multiLine)
}