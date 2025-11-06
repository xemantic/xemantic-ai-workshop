"""
Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.

This code is provided for educational purposes as part of the
"Agentic AI & Creative Coding" workshop.
Unauthorized reproduction or distribution is prohibited.
"""

import anthropic


def fibonacci(n, a=0, b=1):
    if n == 0:
        return a
    elif n == 1:
        return b
    else:
        return fibonacci(n - 1, b, a + b)


client = anthropic.Anthropic()

tools = [
    {
        "name": "Fibonacci",
        "description": "Calculates Fibonacci number n",
        "input_schema": {
            "type": "object",
            "properties": {
                "n": {
                    "type": "integer",
                    "description": "The Fibonacci number to calculate"
                }
            },
            "required": ["n"]
        }
    }
]

conversation = []
conversation.append({"role": "user", "content": "What's Fibonacci number 42"})

tool_use_response = client.messages.create(
    model="claude-sonnet-4-5-20250929",
    max_tokens=1024,
    messages=conversation,
    tools=tools
)

print(f"Stop reason: {tool_use_response.stop_reason}")
print(tool_use_response.content[0].text if tool_use_response.content[0].type == "text" else "")

conversation.append({"role": "assistant", "content": tool_use_response.content})

for block in tool_use_response.content:
    if block.type == "tool_use":
        tool_result = fibonacci(block.input["n"])
        conversation.append({
            "role": "user",
            "content": [{
                "type": "tool_result",
                "tool_use_id": block.id,
                "content": str(tool_result)
            }]
        })

final_response = client.messages.create(
    model="claude-sonnet-4-5-20250929",
    max_tokens=1024,
    messages=conversation,
    tools=tools
)

print(final_response.content[0].text)
