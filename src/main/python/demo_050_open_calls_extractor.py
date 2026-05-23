"""
Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.

This code is provided for educational purposes as part of the
"Agentic AI & Creative Coding" workshop.
Unauthorized reproduction or distribution is prohibited.

Demo 050: Open Calls Extractor

What you will learn?

- Context engineering:
  - using tools just for structured input (without execution).
  - we are forcing single tool use with `tool_choice`.
- Cognitive science: powerful vision model of Claude LLM - multimodality.
- Python: encoding images as base64 for the `image` content block and
  using `tool_choice` to coerce a strict schema-shaped output.
"""

import anthropic
import base64


def encode_image(image_path):
    """Read a binary file and return its base64-encoded string form,
    suitable for the API `image.source.data` field."""
    with open(image_path, "rb") as image_file:
        return base64.standard_b64encode(image_file.read()).decode("utf-8")


client = anthropic.Anthropic()

image_data = encode_image("data/workshop/open-calls-creatives.jpg")

# We declare a tool whose only purpose is to *shape* the model's output.
# We never execute anything - we just read `block.input` and treat it
# as our structured extraction result.
tools = [
    {
        "name": "OpenCallsReceiver",
        "description": "Receives open call entries from the input",
        "input_schema": {
            "type": "object",
            "properties": {
                "calls": {
                    "type": "array",
                    "items": {
                        "type": "object",
                        "properties": {
                            "deadline": {
                                "type": "string",
                                "description": "Deadline in ISO format"
                            },
                            "title": {
                                "type": "string",
                                "description": "Title of the call"
                            }
                        },
                        "required": ["deadline", "title"]
                    }
                }
            },
            "required": ["calls"]
        }
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
                        "data": image_data
                    }
                },
                {
                    "type": "text",
                    "text": "Decode open calls from supplied image"
                }
            ]
        }
    ],
    tools=tools,
    # Forcing the model to use this specific tool guarantees that the
    # response is a tool_use block matching our schema - no free-form
    # text to parse, no schema drift.
    tool_choice={"type": "tool", "name": "OpenCallsReceiver"}
)

for block in response.content:
    if block.type == "tool_use":
        calls = block.input["calls"]
        # Sort by ISO-formatted deadline descending - most recent first.
        sorted_calls = sorted(calls, key=lambda x: x["deadline"], reverse=True)
        for call in sorted_calls:
            print(f"{call['deadline']}: {call['title']}")
