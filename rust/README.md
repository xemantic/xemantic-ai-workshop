# Rust examples

Rust variants of the workshop demos, mirroring the Kotlin, Python, and
TypeScript examples.

> **Note — there is no official Anthropic Rust SDK.**
> Anthropic ships official SDKs for Python, TypeScript, Java, Go, Ruby, C#, and
> PHP, but not Rust. These examples therefore use
> [`claudius`](https://crates.io/crates/claudius), a community-maintained crate.
> It is the most complete Rust option (typed content blocks, streaming, tools),
> but it is **not** endorsed by Anthropic — pin the version and review updates
> accordingly.

## Setup

Requires a recent Rust toolchain (edition 2024, i.e. Rust 1.85+). Install via
[rustup](https://rustup.rs/) if needed.

Set your API key (read automatically by `Anthropic::new(None)`):

```bash
export ANTHROPIC_API_KEY="your-api-key-here"
```

## Running a demo

Each demo is a standalone file under `examples/`, run by name (this is the
Rust analog of `./gradlew run -Dlaunch=Demo010HelloWorld`):

```bash
cargo run --example demo_010_hello_world
cargo run --example demo_015_response_streaming
cargo run --example demo_020_conversation
cargo run --example demo_030_conversation_loop   # interactive; type "exit" or Ctrl-D to quit
cargo run --example demo_040_tools_in_the_hands_of_ai
cargo run --example demo_040_tools_schemars       # typed-schema variant
cargo run --example demo_050_open_calls_extractor
cargo run --example demo_050_open_calls_schemars  # typed-schema variant
cargo run --example demo_061_ocr_key_financial_metrics
```

The image-based demos (050, 061) read the workshop images from `data/workshop/`
at the repo root; the paths are resolved relative to the crate, so they work
from any working directory.

## Demos

| Demo | Shows |
|------|-------|
| `demo_010_hello_world` | Basic request/response with the typed `Model` enum |
| `demo_015_response_streaming` | Consuming the SSE event stream with `futures::StreamExt` |
| `demo_020_conversation` | Accumulating a `Vec<MessageParam>` across turns |
| `demo_030_conversation_loop` | Interactive REPL with a cache-controlled system prompt |
| `demo_040_tools_in_the_hands_of_ai` | Tool use with a hand-written JSON Schema |
| `demo_040_tools_schemars` | Tool use with a schema **derived** from a typed struct (the Rust analog of Kotlin `@Description`, Python Pydantic, TS Zod) via [`schemars`](https://crates.io/crates/schemars) |
| `demo_050_open_calls_extractor` | Multimodal: image input + a tool used purely to **shape** output (`tool_choice` forces it), no execution |
| `demo_050_open_calls_schemars` | Same, with a derived schema parsed back into typed structs (`inline_subschemas` flattens the nested type for the tool API) |
| `demo_061_ocr_key_financial_metrics` | OCR of an income statement into structured metrics; exact revenue sum with [`rust_decimal`](https://crates.io/crates/rust_decimal) (no float drift) |

## Notes

- All API calls are `async`; every demo runs inside a `#[tokio::main]` runtime.
- The model is a typed `Model::Known(KnownModel::ClaudeOpus47)` enum value, so a
  model-name typo is a compile error rather than a runtime 404.
- `claudius`'s default `binaries` feature (its own CLI) is disabled in
  `Cargo.toml`; only the client and `native-tls` are pulled in.
