/*
 * Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.
 *
 * This code is provided for educational purposes as part of the
 * "Agentic AI & Creative Coding" workshop.
 * Unauthorized reproduction or distribution is prohibited.
 */

package com.xemantic.ai.workshop.intro

import kotlinx.coroutines.*

fun main() = runBlocking {
    println("Start of coroutines demo")

    // Launch a coroutine
    val job = launch {
        delay(1000)  // Non-blocking delay for 1 second
        println("World!")
    }

    print("Hello, ")
    job.join()  // Wait for the coroutine to complete

    // Concurrent execution
    val jobs = List(3) { i ->
        launch {
            delay(1000L)
            println("Coroutine $i is done")
        }
    }
    jobs.forEach { it.join() }

    // Using async for parallel execution with results
    val deferred1 = async {
        delay(1000L)
        10
    }

    val deferred2 = async {
        delay(1000L)
        20
    }

    // Wait for both results
    val sum = deferred1.await() + deferred2.await()
    println("Sum: $sum")

    // Coroutine with context
    launch(Dispatchers.Default) {
        // CPU-intensive work here
        println("Running on ${Thread.currentThread().name}")
    }

    // Exception handling
    val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        println("Caught exception: $exception")
    }

    val job2 = launch(exceptionHandler) {
        throw RuntimeException("Oops!")
    }
    job2.join()

    println("End of coroutines demo")
}
