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
import com.xemantic.ai.anthropic.tool.Toolbox
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

/**
 * Demo 070: Play Music from Notes
 *
 * The LLM is given a tool that plays MIDI notes. We ask it to "compose"
 * a melody and it emits a list of timed `noteOn`/`noteOff` events that
 * are handed straight to the local synthesizer.
 *
 * Observations:
 *
 * - **Context engineering**:
 *   - tools can extend the LLM with real-world effectors, not just
 *     calculators - here, a MIDI synthesizer.
 *
 * - **Cognitive science**:
 *   - LLMs carry musical knowledge implicit in their training data,
 *     and can express it through any output channel we expose to them.
 *
 * - **Kotlin**:
 *   - structured concurrency: each note is scheduled on its own
 *     coroutine via `launch { delay(...) }`, which gives us
 *     polyphony almost for free.
 */
fun main4() = runBlocking {
    val synthesizer = getSynthesizer()
    val toolbox = Toolbox {
        tool<PlayMusic> {
            notes.forEach { note ->
                launch {
                    delay(note.startTime)
                    synthesizer.noteOn(note.midiKey, 127)
                    delay(note.duration)
                    synthesizer.noteOff(note.midiKey, 0)
                }
            }
        }
    }
    val anthropic = Anthropic()
    val response = anthropic.messages.create {
        +Message {
            +"Compose music in arabic scale"
        }
        tools = toolbox.tools
    }
    println(response.text)
    response.useTools(toolbox)
}

private fun getSynthesizer(): MidiChannel = MidiSystem.getSynthesizer().run {
    open()
    loadInstrument(defaultSoundbank.instruments[0])
    channels[0]
}
