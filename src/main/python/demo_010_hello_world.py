"""
Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.

This code is provided for educational purposes as part of the
"Agentic AI & Creative Coding" workshop.
Unauthorized reproduction or distribution is prohibited.

Demo 010: Hello World

Observations:

- Prompt engineering:
  - a text prompt is causing the "answer" to be generated
  - inference: the process of generating the response by the model
  - early LLMs, like GPT2, were rather generating the most likely continuation

- Context engineering:
  - underlying ontology: communication theory
  - we are receiving the answer as a message (from AI "assistant")
  - the response contains metadata

- Cognitive science: the LLM output is non-deterministic
  (run it multiple times with the same prompt to get a different output)

- Python:
  - the official `anthropic` SDK exposes a synchronous client out of the box
  - messages are plain dicts with `role` and `content`
"""

import anthropic

client = anthropic.Anthropic()

response = client.messages.create(
    model="claude-opus-4-7",
    max_tokens=1024,
    messages=[
        {"role": "user", "content": "Hello World!"}
    ]
)

# `response.content` is a list of content blocks. The first one
# for a simple text exchange is a TextBlock with the `.text` attribute.
print(response.content[0].text)
# print(response)  # uncomment to also show metadata