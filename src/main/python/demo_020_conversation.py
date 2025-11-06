"""
Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.

This code is provided for educational purposes as part of the
"Agentic AI & Creative Coding" workshop.
Unauthorized reproduction or distribution is prohibited.
"""

import anthropic

client = anthropic.Anthropic()

conversation = []
conversation.append({"role": "user", "content": "Is it true, that to know we can die is to be dead already?"})

response1 = client.messages.create(
    model="claude-sonnet-4-5-20250929",
    max_tokens=1024,
    messages=conversation
)

print("Response 1:")
print(response1.content[0].text)

conversation.append({"role": "assistant", "content": response1.content})
conversation.append({"role": "user", "content": "Why do you think I asked you this question?"})

response2 = client.messages.create(
    model="claude-sonnet-4-5-20250929",
    max_tokens=1024,
    messages=conversation
)

print("Response 2:")
print(response2.content[0].text)

conversation.append({"role": "assistant", "content": response2.content})

print("The whole past conversation is included in the token window:")
print(conversation)
print(response2)