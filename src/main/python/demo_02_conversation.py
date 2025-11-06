"""
Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.

This code is provided for educational purposes as part of the
"Agentic AI & Creative Coding" workshop.
Unauthorized reproduction or distribution is prohibited.
"""

import anthropic
import json

"""
What you will learn?

- AI Dev: conducting variable length dialog with the LLM
  requires cumulating conversation in the context window.
- Cognitive Science: the theory of mind and metacognition
- Python: managing conversation state with lists
"""

client = anthropic.Anthropic()
conversation = []

# First message
conversation.append({
    "role": "user",
    "content": "Is it true, that to know we can die is to be dead already?"
})

response1 = client.messages.create(
    model="claude-3-7-sonnet-20250219",
    max_tokens=1024,
    messages=conversation
)

print("Response 1:")
print(response1.content[0].text)

conversation.append({
    "role": "assistant",
    "content": response1.content
})

# Second message
conversation.append({
    "role": "user",
    "content": "Why do you think I asked you this question?"
})

response2 = client.messages.create(
    model="claude-3-7-sonnet-20250219",
    max_tokens=1024,
    messages=conversation
)

print("\nResponse 2:")
print(response2.content[0].text)

conversation.append({
    "role": "assistant",
    "content": response2.content
})

print("\nThe whole past conversation is included in the token window:")
print(json.dumps([
    {**msg, "content": str(msg["content"])} for msg in conversation
], indent=2))

"""
  Note: We manually build the conversation list by appending
  user and assistant messages. Python's dynamic typing makes
  this straightforward.

  Discuss theory of mind and metacognition
"""