"""
Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.

This code is provided for educational purposes as part of the
"Agentic AI & Creative Coding" workshop.
Unauthorized reproduction or distribution is prohibited.
"""

import anthropic
import base64


def encode_image(image_path):
    with open(image_path, "rb") as image_file:
        return base64.standard_b64encode(image_file.read()).decode("utf-8")


client = anthropic.Anthropic()

image_data = encode_image("data/workshop/open-calls-creatives.jpg")

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
    model="claude-sonnet-4-5-20250929",
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
    tool_choice={"type": "tool", "name": "OpenCallsReceiver"}
)

for block in response.content:
    if block.type == "tool_use":
        calls = block.input["calls"]
        sorted_calls = sorted(calls, key=lambda x: x["deadline"], reverse=True)
        for call in sorted_calls:
            print(f"{call['deadline']}: {call['title']}")
