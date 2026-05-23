"""
Copyright (c) 2026. Kazimierz Pogoda / Xemantic. All rights reserved.

This code is provided for educational purposes as part of the
"Agentic AI & Creative Coding" workshop.
Unauthorized reproduction or distribution is prohibited.

Demo 010 (foresight variant): Hello World on a self-hosted endpoint

Same conversation as demo_010_hello_world.py, but talks to the
Foresight cluster - a self-hosted server that speaks the Anthropic
wire protocol while serving a different model.

What you will learn?

- Context engineering:
  - the Anthropic SDK is not locked to api.anthropic.com - any server
    that implements the same protocol is a drop-in target
  - swapping the endpoint is purely a client-construction concern;
    the `messages.create(...)` call stays identical
- Python:
  - keep cluster-specific wiring (base URL, auth header, model name)
    in one helper module so demos stay focused on the AI behavior

Setup:
    export FORESIGHT_API_KEY="..."
"""

from foresight import Foresight, FORESIGHT_MODEL

# Only the client construction changes vs demo_010_hello_world.py.
client = Foresight()

response = client.messages.create(
    model=FORESIGHT_MODEL,
    max_tokens=1024,
    messages=[
        {"role": "user", "content": "Hello World!"}
    ]
)

# The Foresight-served model emits a `thinking` block before the
# `text` block, so `content[0]` is no longer guaranteed to be text.
# Find the text block explicitly instead of indexing blindly.
text_block = next(block for block in response.content if block.type == "text")
print(text_block.text)
