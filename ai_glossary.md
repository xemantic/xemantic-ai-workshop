# A glossary of AI-related terms

## Anthropic's documentation

https://docs.anthropic.com/

Glossary: https://docs.anthropic.com/en/docs/resources/glossary

## Large Language Model (LLM)

A type of AI system trained on vast amounts of text data to recognize, summarize, translate, predict, and generate text. LLMs like Claude use neural networks with billions of parameters to process and generate human-like text based on the patterns they've learned during training.

**Learn more**: https://en.wikipedia.org/wiki/Large_language_model

## Training

The process of teaching an AI model by exposing it to vast amounts of data, allowing it to learn patterns and relationships. LLM training involves processing enormous text datasets and adjusting billions of parameters to minimize prediction errors. Training large models requires substantial computational resources and specialized techniques.

**Learn more**: https://en.wikipedia.org/wiki/Training,_validation,_and_test_data_sets

## Anthropic API

Anthropic's application programming interface that allows developers to integrate Claude AI capabilities into their applications. The API provides access to Claude's text generation capabilities, including chat functionalities, content generation, and tool use.

**Learn more**: https://docs.anthropic.com/claude/reference/getting-started-with-the-api

## Prompt

Input text provided to an AI system that guides the model's response. Effective prompts clearly communicate the desired task, provide necessary context, and set appropriate constraints for the AI to follow.

**Learn more**: https://docs.anthropic.com/en/docs/build-with-claude/prompt-engineering/overview

## System Prompt

A special type of prompt that establishes the AI's persona, capabilities, constraints, and behavior patterns. System prompts are not visible to end users but guide how the AI responds to all subsequent user inputs.

**Learn more**: https://docs.anthropic.com/claude/docs/system-prompts

## Token / Context Window

**Token**: The basic unit of text that LLMs process. Tokens are typically word fragments - common words may be a single token, while uncommon words might be split into multiple tokens.

**Context Window**: The maximum amount of text (measured in tokens) that an AI model can process at once, including both the input prompt and generated output. Claude's context window varies by model version, with Claude 3.7 Sonnet supporting up to 200K tokens.

**Learn more**:
- https://docs.anthropic.com/en/docs/resources/glossary#context-window
- https://docs.anthropic.com/en/docs/resources/glossary#tokens

## Inference

The process where an AI model applies its learned knowledge to generate outputs based on new inputs. For LLMs like Claude, inference is when the model processes your prompt and generates a response. Inference speed and resource requirements are key considerations for real-time AI applications.

**Learn more**: https://en.wikipedia.org/wiki/Inference

## AI Agent

An autonomous or semi-autonomous software entity powered by AI that can perceive its environment, make decisions, and take actions to achieve specific goals. AI agents typically combine an LLM with tools and capabilities that allow them to interact with external systems.

**Learn more**: https://www.anthropic.com/research/building-effective-agents

## Tool Use / Function Calling

The capability of an AI system to use defined tools or functions to interact with external systems, retrieve information, or perform actions that go beyond text generation. This enables AI agents to have real-world impact through API calls, data processing, and other programmatic capabilities.

**Learn more**: https://docs.anthropic.com/en/docs/build-with-claude/tool-use/overview

## Agentic AI

AI systems that can autonomously plan and execute sequences of actions to accomplish complex goals. Agentic AI combines reasoning capabilities with the ability to use tools and adapt to new information, allowing it to solve problems with minimal human intervention.

**Learn more**: https://blogs.nvidia.com/blog/what-is-agentic-ai/

## Prompt Engineering

The practice of crafting input text (prompts) to elicit desired behaviors from an AI system. Effective prompt engineering involves understanding how the AI interprets instructions and designing prompts that clearly communicate tasks and constraints.

**Learn more**: https://docs.anthropic.com/claude/docs/prompt-engineering

## Reasoning

The ability of an AI system to process information logically, make inferences, and arrive at conclusions. LLMs demonstrate reasoning by breaking down complex problems into steps, considering alternatives, and explaining their thought processes.

**Learn more**: https://arxiv.org/abs/2212.10403

## Anthropic's Claude

A family of large language models developed by Anthropic, designed to be helpful, harmless, and honest. Claude models are trained to be conversational assistants that can understand context, follow instructions, and provide detailed, thoughtful responses.

**Learn more**: https://www.anthropic.com/claude

## API Key

A unique identifier used to authenticate requests to the Anthropic API. API keys should be kept secure and not exposed in client-side code or public repositories.

**Learn more**: https://docs.anthropic.com/claude/reference/authentication

## Compute

The computational resources (processing power, memory, etc.) required to train and run AI models. Large language models typically require significant compute resources, especially during training. The term also refers to the infrastructure and hardware that enable AI operations.

**Learn more**: https://en.wikipedia.org/wiki/Computing

## Multimodal AI

AI systems capable of processing and generating multiple types of media, such as text, images, and potentially other formats. Claude 3 models are multimodal, able to analyze images and respond to them in context.

**Learn more**: https://www.anthropic.com/news/claude-3-family

## Streaming

A method of receiving AI-generated content incrementally as it's being produced, rather than waiting for the complete response. Streaming allows for more responsive user experiences when working with AI systems.

**Learn more**: https://docs.anthropic.com/claude/reference/messages-streaming

## Message API

Anthropic's primary API for interacting with Claude models. The Messages API supports multi-turn conversations, tool use, and multimodal inputs.

**Learn more**: https://docs.anthropic.com/claude/reference/messages_post

## Tool Schema

A formal description of the functions or tools available to an AI system, typically defined using JSON Schema. Tool schemas define the name, description, parameters, and expected behavior of each tool the AI can use.

**Learn more**: https://docs.anthropic.com/claude/docs/use-claude-with-tools

## Kotlin Multiplatform

A feature of the Kotlin programming language that allows code sharing across multiple platforms, including JVM, JavaScript, iOS, and native targets. Xemantic's anthropic-sdk-kotlin uses Kotlin Multiplatform to enable AI agent development across diverse environments.

**Learn more**: https://kotlinlang.org/docs/multiplatform.html

## Autonomous Reasoning

The ability of an AI system to independently analyze problems, form hypotheses, gather information, and reach conclusions without step-by-step human guidance. In agentic AI, autonomous reasoning allows the system to determine what actions to take to achieve goals.

**Learn more**: https://arxiv.org/abs/2303.11366

## JSON Schema

A vocabulary that allows you to annotate and validate JSON documents. In the context of AI tools, JSON Schema is used to define the structure and constraints of the tools that an LLM can call.

**Learn more**: https://json-schema.org/

## Creative Coding

An artistic and technical practice that uses computer programming as a creative medium for artistic expression, design, and interactive experiences. Creative coding often emphasizes experimentation, aesthetics, and novel interactions.

**Learn more**: https://en.wikipedia.org/wiki/Creative_coding