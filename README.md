# xemantic-ai-workshop

_This is a private GitHub repository accessible only to attendees of the [Agentic AI & Creative Coding workshops](https://xemantic.com/ai/workshops)._

Workshop Repository URL:
https://github.com/xemantic/xemantic-ai-workshop

Workshop Slides:
https://xemantic.com/ai/workshops/slides/

> [!NOTE]
> This project is based on the [openrndr-template](https://github.com/openrndr/openrndr-template) project, to bring all goodies of the [OPENRNDR](https://openrndr.org/) creative coding framework to Agentic AI projects. Please refer to [OPENRNDR](https://openrndr.org/) documentation regarding this part.

## What do I need to start?

### Integrated Development Environment (IDE) for Kotlin

You need to have either [IntelliJ IDEA](https://www.jetbrains.com/idea/download/) or [Android Studio](https://developer.android.com/studio) installed on your computer. In case of the IntelliJ IDEA the open source community edition (CE) is good enough.

> [!NOTE]
> IntelliJ IDEA (and Android Studio derived from it), are Integrated Development Environments (IDE) allowing to conveniently edit and execute Kotlin code. Kotlin is the language used in all the code demonstrations in this repository.

### A GitHub account

https://github.com

### Cloning workshop repository from GitHub

> [!TIP]
> If you've never configured GitHub access before on your computer, then [GitHub Desktop](https://github.com/apps/desktop) will be probably the easiest way to start

## Learning material

### Demonstrations based on the source code

This project demonstrates several use cases of calling [Anthropic API](https://www.anthropic.com/api), while gradually introducing more complex techniques and concepts.

In the [src/main/kotlin](src/main/kotlin) folder you will find these demo programs sorted by growing numbers, starting from [Demo010HelloWorld.kt](src/main/kotlin/Demo010HelloWorld.kt).

### Categories of acquired knowledge

Each `Demo` is annotated with "What you will learn" comments, and the knowledge to be acquired in each step is split into 3 categories:

- **AI Development**: particular techniques of programming against LLM systems 
- **Cognitive Science**: the psychological and philosophical foundation of a technique
- **Kotlin**: particular Kotlin idioms used in the source code

Each category will be expanded and discussed during the workshop.

> [!NOTE]
> While the _Kotlin_ category is practical, the _AI Development_ and _Cognitive Science_ categories are theoretical, and easily applicable in other environments and frameworks. They provide the meta-theory of building powerful agentic AI systems. 

### A glossary of AI-related terms

Navigating through Agentic AI development requires particular vocabulary, and this project also comes with [a glossary of AI-related terms](ai_glossary.md).

## Kotlin crash course (optional)

If you are not familiar with Kotlin, the first hour of the workshop is devoted to kotlin crash course. Follow materials in the [src/main/kotlin/intro](src/main/kotlin/intro) folder, starting with [Kotlin010HelloWorld.kt](src/main/kotlin/intro/Kotlin010HelloWorld.kt)

## OPENRNDR examples

The [src/main/kotlin/openrndr](src/main/kotlin/openrndr) folder contains the original 


## Python

```shell
# Create a virtual environment
python3 -m venv venv

# Activate the virtual environment
# On Windows:
venv\Scripts\activate
# On macOS/Linux:
source venv/bin/activate

# Install the Anthropic SDK
pip install anthropic
python3 src/main/python/hello_world.py
```

## TypeScript

```shell
npm install @anthropic-ai/sdk
npx ts-node src/main/typescript/hello-world.ts
```
