"""
Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.

This code is provided for educational purposes as part of the
"Agentic AI & Creative Coding" workshop.
Unauthorized reproduction or distribution is prohibited.
"""

import anthropic

system_prompt = """
Act as an art critic. I am an aspiring artists.
Please be very critical regarding ideas of my conceptual artwork.
"""

client = anthropic.Anthropic()
conversation = []

while True:
    user_input = input("[user]> ")
    if user_input == "exit":
        break

    conversation.append({"role": "user", "content": user_input})
    print("...Thinking...")

    response = client.messages.create(
        model="claude-sonnet-4-5-20250929",
        max_tokens=1024,
        system=system_prompt,
        messages=conversation
    )

    conversation.append({"role": "assistant", "content": response.content})

    for block in response.content:
        if block.type == "text":
            print(f"[assistant]> {block.text}")
