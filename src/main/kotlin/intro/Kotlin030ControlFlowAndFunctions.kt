/*
 * Copyright (c) 2025 by Kazimierz Pogoda / Xemantic
 * All rights reserved. For educational purposes only.
 */

package com.xemantic.ai.workshop.intro

@Suppress("KotlinConstantConditions") // just to avoid warnings on conditions which are always true
fun main() {
    // If Expression
    val number = 42
    val message = if (number > 0) {
        "Positive"
    } else if (number < 0) {
        "Negative"
    } else {
        "Zero"
    }
    println("Number is $message")

    // When Expression (Switch-case equivalent)
    val day = 3
    val dayName = when (day) {
        1 -> "Monday"
        2 -> "Tuesday"
        3 -> "Wednesday"
        4 -> "Thursday"
        5 -> "Friday"
        else -> "Weekend"
    }
    println("Day is $dayName")

    // For Loop
    for (i in 1..5) {
        print("$i ")
    }
    println()

    // While Loop
    var counter = 0
    while (counter < 3) {
        println("Counter: $counter")
        counter++
    }

    // Function Calls
    greet("Alice")
    println("Sum: ${add(5, 3)}")
    println("Doubled: ${double(7)}")
}

// Function with parameter
fun greet(name: String) {
    println("Hello, $name!")
}

// Function with return value
fun add(a: Int, b: Int): Int {
    return a + b
}

// Single-expression function
fun double(x: Int) = x * 2
