/*
 * Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.
 *
 * This code is provided for educational purposes as part of the
 * "Agentic AI & Creative Coding" workshop.
 * Unauthorized reproduction or distribution is prohibited.
 */

package com.xemantic.ai.workshop.intro

// Basic Class
class Person(val name: String, var age: Int) {
    // Property with custom getter
    val isAdult: Boolean
        get() = age >= 18

    // Method
    fun introduce() {
        println("Hi, I'm $name and I'm $age years old")
    }
}

// Data Class
data class Point(val x: Int, val y: Int)

// Object Declaration (Singleton)
object DatabaseConfig {
    val url = "jdbc:mysql://localhost:3306"
    val username = "admin"

    fun printConfig() {
        println("URL: $url, Username: $username")
    }
}

// Companion Object
class MathOperations {
    companion object {
        fun add(a: Int, b: Int) = a + b
        fun subtract(a: Int, b: Int) = a - b
    }
}

fun main() {
    // Using the Person class
    val person = Person("Alice", 25)
    person.introduce()
    println("Is adult: ${person.isAdult}")

    // Using Data Class
    val point1 = Point(1, 2)
    val point2 = Point(1, 2)
    println("Points are equal: ${point1 == point2}")  // true
    println("Point components: ${point1.component1()}, ${point1.component2()}")

    // Data Class copy
    val point3 = point1.copy(x = 3)
    println("New point: $point3")

    // Using Singleton Object
    DatabaseConfig.printConfig()

    // Using Companion Object
    println("5 + 3 = ${MathOperations.add(5, 3)}")
    println("5 - 3 = ${MathOperations.subtract(5, 3)}")
}
