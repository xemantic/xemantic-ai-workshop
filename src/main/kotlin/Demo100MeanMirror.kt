package com.xemantic.ai.workshop

import kotlinx.coroutines.delay
import org.openrndr.application
import org.openrndr.draw.loadFont
import org.openrndr.draw.loadImage
import org.openrndr.ffmpeg.loadVideoDevice
import org.openrndr.launch

fun main() = application {
    configure {
        width = 768
        height = 576
    }

    program {
        val image = loadImage("data/images/pm5544.png")
        val font = loadFont("data/fonts/default.otf", 64.0)

        val device = loadVideoDevice()
        device.play()
        launch {
            delay(1000)
            val buffer = device.colorBuffer
            //buffer.saveToFile()
            println(buffer)
        }
        extend {
            device.draw(drawer)
        }
    }
}
