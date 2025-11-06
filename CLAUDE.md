# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

This is a workshop repository for "Agentic AI & Creative Coding" that demonstrates various use cases of the Anthropic API. It's based on the OPENRNDR creative coding framework and uses Kotlin as the primary language, with additional examples in Python and TypeScript.

## Project Structure

The repository is a multi-language educational project:

- **Kotlin demos** (primary): `src/main/kotlin/Demo*.kt` - Numbered demonstrations (010-120) showing progressively complex AI techniques
- **Kotlin intro**: `src/main/kotlin/intro/Kotlin*.kt` - Kotlin crash course for workshop attendees
- **OPENRNDR examples**: `src/main/kotlin/openrndr/` - Creative coding framework integration
- **Python examples**: `src/main/python/` - Basic Anthropic API examples
- **TypeScript examples**: `src/main/typescript/` - Basic Anthropic API examples

### Demo Progression

Demos are numbered and should be read sequentially:
- `Demo010HelloWorld.kt` - Basic API communication
- `Demo015ResponseStreaming.kt` - Streaming responses
- `Demo020Conversation.kt` - Multi-turn conversations
- `Demo030ConversationLoop.kt` - Interactive conversation loops
- `Demo040ToolsInTheHandsOfAi.kt` - Tool use basics
- `Demo050OpenCallsExtractor.kt` - Advanced tool patterns
- `Demo061OcrKeyFinancialMetrics.kt` - OCR and multimodal
- `Demo070PlayMusicFromNotes.kt` - Audio generation
- `Demo090ClaudeAiArtist.kt` - Visual generation with OPENRNDR
- And more advanced examples...

## Build and Run Commands

### Kotlin (Primary)

```bash
# Run a specific demo
./gradlew run -Dlaunch=Demo010HelloWorld

# Build executable JAR
./gradlew shadowJar

# Run tests
./gradlew test

# Check for dependency updates
./gradlew dependencyUpdates
```

### Python

```bash
# Setup virtual environment
python3 -m venv venv
source venv/bin/activate  # On macOS/Linux
# venv\Scripts\activate   # On Windows

# Install dependencies
pip install anthropic

# Run example
python3 src/main/python/hello_world.py
```

### TypeScript

```bash
# Install dependencies
npm install @anthropic-ai/sdk

# Run example
npx ts-node src/main/typescript/hello-world.ts
```

## Architecture

### SDK Integration

The project uses `anthropic-sdk-kotlin` (version 0.19.0) which provides:

- **DSL-based API**: Kotlin-idiomatic request building with operator overloading (e.g., `+"Hello World!"`)
- **Tool system**: Type-safe tool definitions using `@SerialName` and `@Description` annotations
- **Coroutines**: All API calls are suspending functions requiring coroutine context
- **Serialization**: Kotlinx.serialization for tool input/output with compile-time metadata

### Key Patterns

1. **Tool Definition Pattern**:
   - Define a data class with `@SerialName` and `@Description` annotations
   - Create a `Tool<YourClass>` instance with lambda handling the tool logic
   - Pass tools to `messages.create { tools = myTools }`

2. **Conversation Management**:
   - Use `mutableListOf<Message>()` to track conversation history
   - Use `conversation += message` and `conversation += response` to build context
   - Call `response.useTools()` to execute tools and get results
   - Continue conversation by passing updated message list

3. **OPENRNDR Integration**:
   - Use `application { }` block to create visual programs
   - Launch coroutines with `launch(Dispatchers.IO)` for API calls to avoid blocking UI
   - Store tool results in mutable state (e.g., `var linesToDraw`) and render in `extend { }` block

### API Configuration

The SDK expects `ANTHROPIC_API_KEY` environment variable. Set it before running:

```bash
export ANTHROPIC_API_KEY="your-api-key-here"
```

## Dependencies

- Kotlin 2.2.21 with JVM target 17
- OPENRNDR 0.4.4 (creative coding framework)
- ORX 0.4.4 (OPENRNDR extensions)
- Anthropic SDK Kotlin 0.19.0
- Ktor 3.1.1 (HTTP client)
- Log4j2 for logging (configured in `application.log`)

## Educational Context

Each demo includes "What you will learn?" comments categorized as:
- **AI Development**: LLM programming techniques
- **Cognitive Science**: Theoretical foundations
- **Kotlin**: Language-specific idioms

Refer to `ai_glossary.md` for AI terminology definitions.

## Notes

- All Kotlin examples use coroutines with `runBlocking` or `launch`
- The `+` operator is overloaded for message/content building
- Response text is accessed via `response.text` extension property
- Tool inputs are validated at compile time through Kotlin serialization
- OPENRNDR programs require `-XstartOnFirstThread` JVM arg on macOS (handled by Gradle)