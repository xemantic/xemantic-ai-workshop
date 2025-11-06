"""
Copyright (c) 2025. Kazimierz Pogoda / Xemantic. All rights reserved.

This code is provided for educational purposes as part of the
"Agentic AI & Creative Coding" workshop.
Unauthorized reproduction or distribution is prohibited.
"""

import anthropic

"""
Demo 030: Conversation Loop

This example cumulates the conversation in an
endless loop (only limited by the size of the context window
accepted by the model). It is the tiniest equivalent of "ChatGPT"
or rather Claude AI.

What you will learn?

- AI Dev: the use of system prompts for conditioning the conversation
- Cognitive Science: conditioning AI's behaviour comes from role-playing
- Python: using input() for interactive CLI input
"""

system_prompt = """
Act as an art critic. I am an aspiring artists.
Please be very critical regarding ideas of my conceptual artwork.
""".strip()

client = anthropic.Anthropic()
conversation = []

while True:
    line = input('[user]> ')

    if line == 'exit':
        break

    conversation.append({
        'role': 'user',
        'content': line
    })

    print('...Thinking...')

    response = client.messages.create(
        model="claude-3-7-sonnet-20250219",
        max_tokens=1024,
        system=system_prompt,
        messages=conversation
    )

    conversation.append({
        'role': 'assistant',
        'content': response.content
    })

    # Filter and display only text content
    for block in response.content:
        if block.type == 'text':
            print(f'[assistant]> {block.text}')

"""
  Note: This time we are filtering the response content to only
  display text blocks. Text is not the only possible content
  to be provided to and received from the LLM. We can also have
  images and documents on the input and tool_use requests on
  the output.
"""