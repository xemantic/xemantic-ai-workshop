"""
Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.

This code is provided for educational purposes as part of the
"Agentic AI & Creative Coding" workshop.
Unauthorized reproduction or distribution is prohibited.

Demo 030: Conversation Loop

This example accumulates the conversation in an endless loop
(only limited by the size of the context window accepted by the model).
It is the tiniest equivalent of "ChatGPT", or rather claude.ai.

Observations:

- Prompt engineering:
  - system prompt: is different from the initial message

- Context engineering:
  - caching: the major factor reducing LLM costs

- Cognitive science:
  - conditioning the LLM's expression comes from role-playing

- Python:
  - the `system` parameter takes a string (single text block) or
    a list of typed blocks - we use the list form to attach a
    `cache_control` breakpoint so the unchanging system prompt
    is read from cache on subsequent turns.
"""

import anthropic

system_prompt = """
Act as an art critic. I am an aspiring artist.
Please be very critical regarding ideas of my conceptual artwork.
""".strip()

client = anthropic.Anthropic()
conversation = []

while True:
    user_input = input("[user]> ")
    if user_input == "exit":
        break

    conversation.append({"role": "user", "content": user_input})
    print("...Thinking...")

    response = client.messages.create(
        model="claude-opus-4-7",
        max_tokens=1024,
        # `cache_control` on the system prompt lets the API reuse a
        # prefix of the request - the system block is identical on
        # every turn, so we only pay full price for it once.
        system=[{
            "type": "text",
            "text": system_prompt,
            "cache_control": {"type": "ephemeral"}
        }],
        messages=conversation
    )

    conversation.append({"role": "assistant", "content": response.content})

    # The response may contain non-text blocks (e.g. tool use) in
    # more advanced demos, so we explicitly filter for text here.
    for block in response.content:
        if block.type == "text":
            print(f"[assistant]> {block.text}")
