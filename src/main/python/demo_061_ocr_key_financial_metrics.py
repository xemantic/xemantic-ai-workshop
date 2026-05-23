"""
Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.

This code is provided for educational purposes as part of the
"Agentic AI & Creative Coding" workshop.
Unauthorized reproduction or distribution is prohibited.

Demo 061: OCR Key Financial Metrics

A practical application of structured extraction: we send a screenshot
of an income statement and ask the model to populate a typed structure
with the figures.

Observations:

- Context engineering:
  - tools are not only for execution - they double as a typed
    "output schema" for structured data extraction.

- Cognitive science:
  - multimodal vision combined with tabular reasoning lets the
    model perform OCR + interpretation in a single step.

- Python:
  - we use `Decimal` (not `float`) to keep financial values exact;
    no floating-point drift when we sum revenues.
"""

import anthropic
import base64
from decimal import Decimal


def encode_image(image_path):
    """Read a binary file and return its base64-encoded string form."""
    with open(image_path, "rb") as image_file:
        return base64.standard_b64encode(image_file.read()).decode("utf-8")


client = anthropic.Anthropic()

image_data = encode_image("data/workshop/nvidia-income.png")

# JSON Schema describing the structure we want extracted from the image.
# The model will fill these fields by reading the numbers off the chart.
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

        # Convert through `str` first - Decimal(float) would carry the
        # binary-float imprecision into the result; Decimal(str) doesn't.
        total_revenue = sum(Decimal(str(entry["revenue"])) for entry in entries)
        print(f"Total revenue: {total_revenue}")
