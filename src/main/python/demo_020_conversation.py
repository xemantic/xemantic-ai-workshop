"""
Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.

This code is provided for educational purposes as part of the
"Agentic AI & Creative Coding" workshop.
Unauthorized reproduction or distribution is prohibited.

Demo 020: Conversation

Observations:

- Context engineering:
  - conducting a variable-length dialog with the LLM requires
    accumulating the conversation in the context window.

- Cognitive science:
  - the theory of mind and metacognition (introspection)

- Python:
  - the conversation is just a plain `list` of message dicts;
    we send the full history with every request - the API itself
    is stateless.
  - assistant turns are appended as `response.content` (the list
    of content blocks), so subsequent requests reproduce the
    exact prior reply verbatim.
"""

import anthropic

client = anthropic.Anthropic()

# Conversation history - this list grows with each turn and is
# resent to the model on every request so it has full context.
conversation = []
conversation.append({"role": "user", "content": "Is it true, that to know we can die is to be dead already?"})

response1 = client.messages.create(
    model="claude-opus-4-7",
    max_tokens=1024,
    messages=conversation
)

print("Response 1:")
print(response1.content[0].text)

# Append the assistant's reply back into the conversation, then ask
# a follow-up that only makes sense if the prior context is present.
conversation.append({"role": "assistant", "content": response1.content})
conversation.append({"role": "user", "content": "Why do you think I asked you this question?"})

response2 = client.messages.create(
    model="claude-opus-4-7",
    max_tokens=1024,
    messages=conversation
)

print("Response 2:")
print(response2.content[0].text)

conversation.append({"role": "assistant", "content": response2.content})

print("The whole past conversation is included in the token window:")
print(conversation)
print(response2)
