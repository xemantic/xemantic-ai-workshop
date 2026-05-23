"""
Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.

This code is provided for educational purposes as part of the
"Agentic AI & Creative Coding" workshop.
Unauthorized reproduction or distribution is prohibited.

Demo 015: Response Streaming

Observations:

- Prompt engineering:
  - prompting is like if philosophy was a programming language

- Context engineering:
  - inference takes time, streaming works better for interactive
    use cases with "human in the loop", so a human can start reading
    the LLM output ASAP.

- Cognitive science:
  - LLMs excel at generating a stream of text constrained by specified
    criteria. Initially it was just a "poetry" of probabilistic
    continuation. Nowadays, the output can be sharper than the sharpest
    human mind, unmistakably adhering to rules in play.

- Python:
  - the SDK exposes streaming via a context manager (`with ... as stream`),
    which guarantees the underlying HTTP connection is closed cleanly.
  - `stream.text_stream` yields decoded text deltas - no need to filter
    raw events ourselves.
"""

import anthropic

client = anthropic.Anthropic()

with client.messages.stream(
    model="claude-opus-4-7",
    max_tokens=1024,
    messages=[
        {"role": "user", "content": "Write me a poem."}
    ]
) as stream:
    # Print each text delta as it arrives, with no newline / no buffering,
    # so the poem appears word by word in the terminal.
    for text in stream.text_stream:
        print(text, end="", flush=True)

print()
