/*
 * Copyright (c) 2025 by Kazimierz Pogoda / Xemantic
 * All rights reserved. For educational purposes only.
 */

package com.xemantic.ai.workshop.intro

@Suppress("KotlinConstantConditions", "UnusedVariable", "unused") // avoid warnings
fun main() {
    // Lists
    val readOnlyList = listOf("apple", "banana", "orange")
    val mutableList = mutableListOf("one", "two", "three")
    mutableList.add("four")

    // Accessing Lists
    println("First fruit: ${readOnlyList[0]}")
    println("List size: ${readOnlyList.size}")

    // Lists with Map/Filter
    val numbers = listOf(1, 2, 3, 4, 5)
    val doubled = numbers.map { it * 2 }
    val evenNumbers = numbers.filter { it % 2 == 0 }

    println("Doubled numbers: $doubled")
    println("Even numbers: $evenNumbers")

    // Maps (Dictionary)
    val readOnlyMap = mapOf(
        "key1" to "value1",
        "key2" to "value2"
    )

    val mutableMap = mutableMapOf<String, Int>()
    mutableMap["one"] = 1
    mutableMap["two"] = 2

    // Null Safety
    var nullableString: String? = "Hello"
    nullableString = null  // This is OK because we used String?

    // Safe Call Operator
    println(nullableString?.length)  // Prints null if nullableString is null

    // Elvis Operator
    val length = nullableString?.length ?: 0  // Use 0 if nullableString is null

    // Not-null Assertion
    var nonNullString: String? = "Definitely not null"
    println(nonNullString!!.length)  // Will throw NPE if nonNullString is null

    // Smart Cast
    val nullable: String? = "Hello"
    if (nullable != null) {
        // Compiler knows nullable is not null here
        println(nullable.length)
    }
}