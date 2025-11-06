"""
Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.

This code is provided for educational purposes as part of the
"Agentic AI & Creative Coding" workshop.
Unauthorized reproduction or distribution is prohibited.
"""

import anthropic
import base64
from decimal import Decimal


def encode_image(image_path):
    with open(image_path, "rb") as image_file:
        return base64.standard_b64encode(image_file.read()).decode("utf-8")


client = anthropic.Anthropic()

image_data = encode_image("data/workshop/nvidia-income.png")

tools = [
    {
        "name": "ExtractKeyFinancialMetrics",
        "description": "Extracts key financial metrics from the report",
        "input_schema": {
            "type": "object",
            "properties": {
                "entries": {
                    "type": "array",
                    "items": {
                        "type": "object",
                        "properties": {
                            "year": {
                                "type": "integer",
                                "description": "Year of the financial data"
                            },
                            "revenue": {
                                "type": "number",
                                "description": "Revenue in millions"
                            },
                            "operatingIncome": {
                                "type": "number",
                                "description": "Operating income in millions"
                            },
                            "netIncome": {
                                "type": "number",
                                "description": "Net income in millions"
                            }
                        },
                        "required": ["year", "revenue", "operatingIncome", "netIncome"]
                    }
                }
            },
            "required": ["entries"]
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
                        "media_type": "image/png",
                        "data": image_data
                    }
                },
                {
                    "type": "text",
                    "text": "Decode financial metrics from supplied image"
                }
            ]
        }
    ],
    tools=tools
)

for block in response.content:
    if block.type == "tool_use":
        entries = block.input["entries"]
        for entry in entries:
            print(entry)

        total_revenue = sum(Decimal(str(entry["revenue"])) for entry in entries)
        print(f"Total revenue: {total_revenue}")
