"""
Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.

This code is provided for educational purposes as part of the
"Agentic AI & Creative Coding" workshop.
Unauthorized reproduction or distribution is prohibited.

Demo 040 (pydantic variant): Tools in the Hands of AI

This mirrors the Kotlin `anthropic-sdk-kotlin` pattern where a tool is
defined as a typed data class with field descriptions, and the SDK
generates the JSON Schema from it automatically. In Python we get the
same ergonomics with Pydantic v2.

What you will learn?

- Context engineering: defining the tool input as a typed model
  rather than a hand-written JSON Schema dict.
- Python:
  - `BaseModel.model_json_schema()` produces the schema the API needs.
  - `Model.model_validate(block.input)` parses the model's tool call
    back into a typed object - no `block.input["n"]` dict access.

Setup:
    pip install anthropic pydantic
"""

import anthropic
from pydantic import BaseModel, Field


# Plain Python function - the "tool" implementation. The LLM never runs
# this itself; it only requests that we run it on its behalf.
def fibonacci(n: int, a: int = 0, b: int = 1) -> int:
    if n == 0:
        return a
    if n == 1:
        return b
    return fibonacci(n - 1, b, a + b)


# Typed model = both the tool's input schema *and* the parsed-input
# Python type. Field(description=...) flows into the generated JSON Schema
# so the model sees the same documentation we read in the code.
class FibonacciInput(BaseModel):
    n: int = Field(description="The Fibonacci number to calculate")


client = anthropic.Anthropic()

# Build the tool declaration from the model - no hand-rolled JSON Schema.
tools = [
    {
        "name": "Fibonacci",
        "description": "Calculates Fibonacci number n",
        "input_schema": FibonacciInput.model_json_schema(),
    }
]

conversation = [{"role": "user", "content": "What's Fibonacci number 42"}]

# First turn: the model decides to call the Fibonacci tool instead of
# attempting (and likely failing) to compute the value itself.
tool_use_response = client.messages.create(
    model="claude-opus-4-7",
    max_tokens=1024,
    messages=conversation,
    tools=tools,
)

print(f"Stop reason: {tool_use_response.stop_reason}")
conversation.append({"role": "assistant", "content": tool_use_response.content})

# Execute every tool_use block the model emitted. `model_validate` turns
# the raw dict into a typed `FibonacciInput` - misshapen input would
# raise a ValidationError here instead of silently producing wrong math.
for block in tool_use_response.content:
    if block.type == "tool_use":
        args = FibonacciInput.model_validate(block.input)
        result = fibonacci(args.n)
        conversation.append({
            "role": "user",
            "content": [{
                "type": "tool_result",
                "tool_use_id": block.id,
                "content": str(result),
            }],
        })

# Second turn: now the model has the real number and can phrase the
# final natural-language answer.
final_response = client.messages.create(
    model="claude-opus-4-7",
    max_tokens=1024,
    messages=conversation,
    tools=tools,
)

print(final_response.content[0].text)
