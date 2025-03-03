/*
 * Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.
 *
 * This code is provided for educational purposes as part of the
 * "Agentic AI & Creative Coding" workshop.
 * Unauthorized reproduction or distribution is prohibited.
 */

package com.xemantic.ai.workshop

import com.xemantic.ai.anthropic.Anthropic
import com.xemantic.ai.anthropic.content.Image
import com.xemantic.ai.anthropic.message.Message
import com.xemantic.ai.anthropic.tool.Tool
import com.xemantic.ai.anthropic.tool.ToolChoice
import com.xemantic.ai.tool.schema.meta.Description
import kotlinx.coroutines.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import javax.sound.midi.MidiChannel
import javax.sound.midi.MidiSystem

@SerialName("PlayMusic")
@Description("Plays the music on the local MIDI device")
data class PlayMusic(val notes: List<Note>)

@Serializable
@SerialName("note")
data class Note(
    val midiKey: Int,
    @Description("Note start time in milliseconds")
    val startTime: Long,
    @Description("Note duration in milliseconds")
    val duration: Long
)

fun main() {
    val synthesizer = getSynthesizer()
    runBlocking {
        val playMusicTool = Tool<PlayMusic> {
            notes.forEach { note ->
                launch {
                    delay(note.startTime)
                    synthesizer.noteOn(note.midiKey, 127)
                    delay(note.duration)
                    synthesizer.noteOff(note.midiKey, 0)
                }
            }
        }
        val anthropic = Anthropic()
        val response = anthropic.messages.create {
            +Message {
                +"Can you play the music from the attached picture?"
                +Image("data/workshop/happy-birthday-chords-two-hands.webp")
            }
            tools += playMusicTool
            toolChoice = ToolChoice.Tool<PlayMusic>()
        }
        response.toolUse!!.use()
    }
}

fun getSynthesizer(): MidiChannel = MidiSystem.getSynthesizer().run {
    open()
    loadInstrument(defaultSoundbank.instruments[0])
    channels[0]
}
