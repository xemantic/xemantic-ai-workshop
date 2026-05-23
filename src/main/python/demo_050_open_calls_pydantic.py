"""
Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.

This code is provided for educational purposes as part of the
"Agentic AI & Creative Coding" workshop.
Unauthorized reproduction or distribution is prohibited.

Demo 050 (pydantic variant): Open Calls Extractor

The structured-extraction counterpart to demo_040_tools_pydantic.py:
the tool is never executed - we use it purely to force the model into
a schema-shaped reply, then parse that reply back into Pydantic models.

What you will learn?

- Context engineering:
  - using tools just for structured input (without execution).
  - we are forcing single tool use with `tool_choice`.
- Cognitive science: powerful vision model of Claude LLM - multimodality.
- Python:
  - Pydantic auto-generates the JSON Schema *and* validates the
    response into a typed object - one model definition serves both
    sides of the boundary.
  - `datetime` fields are parsed from ISO strings automatically.

Setup:
    pip install anthropic pydantic
"""

import anthropic
import base64
from datetime import datetime

from pydantic import BaseModel, Field


def encode_image(image_path: str) -> str:
    """Read a binary file and return its base64-encoded string form."""
    with open(image_path, "rb") as image_file:
        return base64.standard_b64encode(image_file.read()).decode("utf-8")


# Nested Pydantic models compose into a single JSON Schema and validate
# end-to-end. Note the typed `deadline: datetime` - the model will emit
# an ISO 8601 string and Pydantic will parse it for us.
class Call(BaseModel):
    deadline: datetime = Field(description="Deadline in ISO 8601 format")
    title: str = Field(description="Title of the open call")


class OpenCallsReceiver(BaseModel):
    """Receives open call entries from the input."""
    calls: list[Call]


client = anthropic.Anthropic()

image_data = encode_image("data/workshop/open-calls-creatives.jpg")

# Schema is derived from the model; description comes from the docstring.
tools = [
    {
        "name": "OpenCallsReceiver",
        "description": OpenCallsReceiver.__doc__,
        "input_schema": OpenCallsReceiver.model_json_schema(),
    }
]

response = client.messages.create(
    model="claude-opus-4-7",
    max_tokens=1024,
    messages=[
        {
            "role": "user",
            "content": [
                {
                    "type": "image",
                    "source": {
                        "type": "base64",
                        "media_type": "image/jpeg",
                        "data": image_data,
                    },
                },
                {
                    "type": "text",
                    "text": "Decode open calls from supplied image",
                },
            ],
        }
    ],
    tools=tools,
    # Forcing the model to use this specific tool guarantees that the
    # response is a tool_use block matching our schema - no free-form
    # text to parse, no schema drift.
    tool_choice={"type": "tool", "name": "OpenCallsReceiver"},
)

for block in response.content:
    if block.type == "tool_use":
        # `model_validate` turns the raw dict into a typed object and
        # parses each `deadline` string into a real `datetime`.
        receiver = OpenCallsReceiver.model_validate(block.input)
        for call in sorted(receiver.calls, key=lambda c: c.deadline, reverse=True):
            print(f"{call.deadline.isoformat()}: {call.title}")
