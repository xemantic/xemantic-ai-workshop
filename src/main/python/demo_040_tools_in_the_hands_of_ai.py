"""
Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.

This code is provided for educational purposes as part of the
"Agentic AI & Creative Coding" workshop.
Unauthorized reproduction or distribution is prohibited.

Demo 040: Tools in the Hands of AI

What you will learn?

- Context engineering: tools as a basis for agentic use cases -
  how to define a tool input schema and connect it with Python logic.
- Cognitive science: LLMs are bad at math - do math with a calculator,
  not with harnessed stochastic entropy.
- Python: tool schemas are described as JSON Schema dicts; the model
  responds with a `tool_use` block carrying parsed arguments which
  we forward to the matching Python function.
"""

import anthropic


# A plain Python function - the "tool" implementation. The LLM never
# runs this itself; it only requests that we run it on its behalf.
def fibonacci(n, a=0, b=1):
    if n == 0:
        return a
    elif n == 1:
        return b
    else:
        return fibonacci(n - 1, b, a + b)


client = anthropic.Anthropic()

# Tool declaration: name, description and a JSON Schema for the inputs.
# The model uses the description to decide *when* to call the tool and
# the schema to know *what* to pass.
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

# First turn: the model decides to call the Fibonacci tool instead of
# attempting (and likely failing) to compute the value itself.
tool_use_response = client.messages.create(
    model="claude-opus-4-7",
    max_tokens=1024,
    messages=conversation,
    tools=tools
)

print(f"Stop reason: {tool_use_response.stop_reason}")
print(tool_use_response.content[0].text if tool_use_response.content[0].type == "text" else "")

conversation.append({"role": "assistant", "content": tool_use_response.content})

# Execute every tool_use block the model emitted and feed the results
# back as a `tool_result` content block keyed by the original tool_use_id.
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

# Second turn: now the model has the real number and can phrase the
# final natural-language answer.
final_response = client.messages.create(
    model="claude-opus-4-7",
    max_tokens=1024,
    messages=conversation,
    tools=tools
)

print(final_response.content[0].text)
