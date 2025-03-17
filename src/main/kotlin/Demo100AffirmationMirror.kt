package com.xemantic.ai.workshop

import com.xemantic.ai.anthropic.Anthropic
import com.xemantic.ai.anthropic.content.Image
import com.xemantic.ai.anthropic.message.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import org.openrndr.KEY_SPACEBAR
import org.openrndr.application
import org.openrndr.ffmpeg.VideoPlayerFFMPEG
import org.openrndr.launch
import java.io.File

fun main() = application {
    configure {
        width = 640
        height = 480
    }

    program {

        val anthropic = Anthropic()

        val videoPlayer = VideoPlayerFFMPEG.fromDevice(
            imageWidth = 640,
            imageHeight = 480
        )
        videoPlayer.play()
        extend {
            videoPlayer.draw(drawer)
        }
        keyboard.keyDown.listen {
            if (it.key == KEY_SPACEBAR) {
                val mirrorFile = File("mirror.jpg")
                videoPlayer.colorBuffer!!.saveToFile(mirrorFile)
                launch(Dispatchers.IO) {
                    delay(100)
                    val response = anthropic.messages.create {
                        +Message {
                            +Image("mirror.jpg")
                            +"What's on this image"
                        }
                    }
                    println(response.text)
                }
            }
        }
    }
}
