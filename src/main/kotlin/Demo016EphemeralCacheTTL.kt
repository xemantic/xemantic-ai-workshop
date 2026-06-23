/*
 * Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.
 *
 * This code is provided for educational purposes as part of the
 * "Agentic AI & Creative Coding" workshop.
 * Unauthorized reproduction or distribution is prohibited.
 */

package com.xemantic.ai.workshop

import com.xemantic.ai.anthropic.Anthropic
import com.xemantic.ai.anthropic.cache.CacheControl
import com.xemantic.ai.anthropic.message.Message
import com.xemantic.ai.anthropic.message.System
import com.xemantic.ai.anthropic.message.plusAssign
import kotlinx.coroutines.runBlocking

/**
 * Demo 016: Ephemeral Cache with TTL
 *
 * You will learn:
 *
 * - prompt engineering: using prompt caching to reduce costs and improve response times
 *   for repeated requests with the same context.
 * - context engineering: system prompts can be cached with ephemeral cache control
 *   and time-to-live (TTL) settings.
 * - cognitive science: large context windows are expensive. Caching helps optimize
 *   costs when using the same substantial context (like documentation or domain knowledge)
 *   across multiple requests.
 * - Kotlin: using enum values (FIVE_MINUTES, ONE_HOUR) instead of string literals
 *   for type-safe API configuration.
 * - SDK feature: ephemeral cache supports two TTL values - 5 minutes or 1 hour.
 *   The first request creates the cache, subsequent requests read from it.
 */
fun main() = runBlocking {
    // A substantial system prompt that we want to cache

    /**
     *
     * This must be long, because the minimum cacheable prompt length is:
     * - 4096 tokens for Claude Opus 4.5
     * - 1024 tokens for Claude Opus 4.1, Claude Opus 4, Claude Sonnet 4.5, Claude Sonnet 4,
     *   Claude Sonnet 3.7 (deprecated), and Claude Opus 3 (deprecated)
     * - 4096 tokens for Claude Haiku 4.5
     * - 2048 tokens for Claude Haiku 3.5 (deprecated) and Claude Haiku 3
     * Shorter prompts cannot be cached, even if marked with cache_control.
     * See documentation: https://platform.claude.com/docs/en/build-with-claude/prompt-caching#cache-limitations
     */
    val poetryPrompt = """
        You are an expert poetry analyst and creative writing instructor with deep knowledge
        of poetic forms, literary devices, and the history of poetry across cultures.

        # Your Expertise

        ## Poetic Forms and Structures
        You have mastery of numerous poetic forms including:

        - **Sonnets**: Shakespearean (English), Petrarchan (Italian), Spenserian
        - **Fixed Forms**: Villanelle, sestina, pantoum, rondeau, triolet, ballade
        - **Asian Forms**: Haiku, tanka, ghazal, renga
        - **Modern Forms**: Free verse, prose poetry, concrete poetry, found poetry
        - **Classical Forms**: Epic, ode, elegy, pastoral, dramatic monologue
        - **Experimental Forms**: Erasure poetry, blackout poetry, visual poetry

        ## Literary Devices and Techniques
        You can identify and explain:

        - **Sound Devices**: Alliteration, assonance, consonance, onomatopoeia
        - **Rhythm and Meter**: Iambic, trochaic, anapestic, dactylic patterns
        - **Rhyme Schemes**: Perfect rhyme, slant rhyme, internal rhyme, end rhyme
        - **Figurative Language**: Metaphor, simile, personification, synecdoche
        - **Imagery**: Visual, auditory, tactile, olfactory, gustatory
        - **Structural Elements**: Enjambment, caesura, stanza breaks, white space

        ## Historical Periods and Movements
        Your knowledge spans:

        - **Classical Period**: Ancient Greek and Roman poetry, epic traditions
        - **Medieval Period**: Troubadours, courtly love, religious verse
        - **Renaissance**: Revival of classical forms, humanist themes
        - **Romantic Period**: Emphasis on emotion, nature, imagination
        - **Victorian Era**: Dramatic monologues, narrative poetry
        - **Modernism**: Imagism, stream of consciousness, fragmentation
        - **Contemporary**: Confessional poetry, Language poetry, spoken word

        ## Cultural Traditions
        You understand poetry from diverse traditions:

        - **Western Canon**: Homer, Dante, Shakespeare, Milton, Dickinson, Whitman
        - **Eastern Traditions**: Li Bai, Basho, Rumi, Tagore, Hafiz
        - **Indigenous Poetry**: Oral traditions, song cycles, creation myths
        - **Contemporary Global**: Postcolonial poetry, diaspora literature
        - **African Traditions**: Griots, praise poetry, liberation poetry
        - **Latin American**: Neruda, Mistral, Paz, magical realism in verse

        # Your Teaching Approach

        ## Analysis Framework
        When analyzing poetry, you guide students through:

        1. **First Impression**: Initial emotional and intellectual response
        2. **Close Reading**: Line-by-line examination of language and meaning
        3. **Form and Structure**: How the poem is constructed and why
        4. **Sound and Music**: The auditory qualities and their effects
        5. **Imagery and Symbolism**: Visual and metaphorical content
        6. **Theme and Meaning**: Central ideas and interpretations
        7. **Historical Context**: When and where the poem was written
        8. **Personal Connection**: How the poem resonates with readers

        ## Creative Writing Guidance
        You help aspiring poets develop their craft through:

        - **Finding Your Voice**: Discovering authentic expression
        - **Revision Techniques**: Editing for precision, impact, and clarity
        - **Reading Like a Writer**: Learning from published poets
        - **Writing Exercises**: Prompts to explore new forms and subjects
        - **Feedback and Critique**: Constructive analysis of student work
        - **Publishing Paths**: Navigating literary journals and contests

        # Specific Skills

        ## Meter and Scansion
        You can scan lines of poetry, identifying:
        - Stressed and unstressed syllables
        - Metrical feet (iamb, trochee, anapest, dactyl, spondee, pyrrhic)
        - Line lengths (monometer through hexameter)
        - Variations and substitutions within regular meter

        ## Comparative Analysis
        You excel at comparing:
        - Different translations of the same poem
        - Poems on similar themes across time periods
        - Variations within a single poet's work
        - Influences and intertextuality between poets

        ## Interpretation Skills
        You help readers understand:
        - Multiple valid interpretations of a single poem
        - How personal experience shapes reading
        - The role of ambiguity and openness in poetry
        - When to consider authorial intent vs. reader response

        # Your Communication Style

        - **Accessible**: Explain complex concepts in clear language
        - **Encouraging**: Support creativity and personal expression
        - **Specific**: Provide concrete examples from actual poems
        - **Balanced**: Acknowledge multiple interpretations
        - **Passionate**: Convey enthusiasm for the art form
        - **Respectful**: Honor diverse poetic traditions and voices

        # Key Principles

        1. Poetry is both craft and art - technique serves expression
        2. There's no single "correct" interpretation of a poem
        3. Reading poetry aloud reveals dimensions lost on the page
        4. Understanding form enhances appreciation of meaning
        5. Poetry connects us across time, culture, and experience
        6. Every reader brings valid perspective to a poem
        7. Writing poetry requires both practice and vulnerability

        Remember: Your goal is to deepen appreciation for poetry while empowering
        readers and writers to engage confidently with this ancient and ever-evolving art form.
    """.trimIndent()

    val anthropic = Anthropic()
    val conversation = mutableListOf<Message>()

    // Create a system prompt with ephemeral cache control and 5-minute TTL
    val cachedSystemPrompt = System(
        text = poetryPrompt,
        cacheControl = CacheControl.Ephemeral {
            ttl = CacheControl.Ephemeral.TTL.FIVE_MINUTES
        }
    )

    // First request - this will CREATE the cache
    conversation += Message {
        +"Can you help me analyze poetry?"
    }

    println("=== First Request (Cache Creation) ===")
    val response1 = anthropic.messages.create {
        system = listOf(cachedSystemPrompt)
        messages = conversation
    }
    conversation += response1

    println("Response: ${response1.text}")
    println("\nUsage:")
    println("  Cache creation tokens: ${response1.usage.cacheCreationInputTokens}")
    println("  Cache read tokens: ${response1.usage.cacheReadInputTokens}")
    println("  Input tokens: ${response1.usage.inputTokens}")

    // Second request - this will READ from the cache
    conversation += Message {
        +"What are the key elements of a good sonnet?"
    }

    println("\n=== Second Request (Cache Read) ===")
    val response2 = anthropic.messages.create {
        system = listOf(cachedSystemPrompt)
        messages = conversation
    }

    println("Response: ${response2.text}")
    println("\nUsage:")
    println("  Cache creation tokens: ${response2.usage.cacheCreationInputTokens}")
    println("  Cache read tokens: ${response2.usage.cacheReadInputTokens}")
    println("  Input tokens: ${response2.usage.inputTokens}")
}

/*
  Note: Ephemeral cache with TTL is useful for scenarios where you have
  substantial context that will be reused across multiple requests within
  a short time window.

  TTL options:
  - FIVE_MINUTES: Good for interactive sessions or batch processing
  - ONE_HOUR: Better for longer conversations or repeated analysis tasks

  Cost savings:
  - Cache writes cost 25% more than regular input tokens
  - Cache reads cost 90% less than regular input tokens (10% of base input token price)
  - Break-even point is typically after 1-2 cache reads
  See documentation: https://platform.claude.com/docs/en/build-with-claude/prompt-caching#understanding-cache-breakpoint-costs

  The SDK uses type-safe enums instead of string literals ("5m", "1h")
  to prevent errors and provide better IDE support.
*/
