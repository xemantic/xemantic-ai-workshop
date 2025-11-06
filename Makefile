.PHONY: help setup-ts setup-py run-ts run-py clean

help:
	@echo "Xemantic AI Workshop - Build Commands"
	@echo ""
	@echo "Setup:"
	@echo "  make setup-ts    - Install TypeScript dependencies"
	@echo "  make setup-py    - Create Python venv and install dependencies"
	@echo ""
	@echo "Run TypeScript:"
	@echo "  make run-ts FILE=Demo01HelloWorld       - Run TypeScript example (without .ts)"
	@echo "  npm run demo01                          - Run Demo01HelloWorld.ts directly"
	@echo "  npm run demo02                          - Run Demo02Conversation.ts directly"
	@echo "  npm run demo03                          - Run Demo03ConversationLoop.ts (interactive)"
	@echo ""
	@echo "Run Python:"
	@echo "  make run-py FILE=demo_01_hello_world      - Run Python example (without .py)"
	@echo "  make run-py FILE=demo_02_conversation     - Run Demo02 conversation example"
	@echo "  make run-py FILE=demo_03_conversation_loop - Run Demo03 conversation loop (interactive)"
	@echo ""
	@echo "Run Kotlin:"
	@echo "  ./gradlew run -Dlaunch=Demo010HelloWorld"
	@echo "  ./gradlew run -Dlaunch=Demo020Conversation"
	@echo ""
	@echo "Cleanup:"
	@echo "  make clean       - Remove build artifacts and dependencies"

# TypeScript setup
setup-ts:
	npm install
	@echo "✓ TypeScript dependencies installed"

# Python setup
setup-py:
	python3 -m venv venv
	. venv/bin/activate && pip install -e ".[dev]"
	@echo "✓ Python environment created and dependencies installed"
	@echo "  Activate with: source venv/bin/activate"

# Run TypeScript (usage: make run-ts FILE=Demo01HelloWorld)
run-ts:
	@if [ -z "$(FILE)" ]; then \
		echo "Error: Please specify FILE=<name>"; \
		echo "Example: make run-ts FILE=Demo01HelloWorld"; \
		exit 1; \
	fi
	npm run run src/main/typescript/$(FILE).ts

# Run Python (usage: make run-py FILE=demo_01_hello_world)
run-py:
	@if [ -z "$(FILE)" ]; then \
		echo "Error: Please specify FILE=<name>"; \
		echo "Example: make run-py FILE=demo_01_hello_world"; \
		exit 1; \
	fi
	@if [ ! -d "venv" ]; then \
		echo "Error: Python virtual environment not found. Run 'make setup-py' first."; \
		exit 1; \
	fi
	. venv/bin/activate && python3 src/main/python/$(FILE).py

# Cleanup
clean:
	rm -rf venv
	rm -rf node_modules
	rm -rf build
	rm -rf dist
	rm -rf *.egg-info
	./gradlew clean
	@echo "✓ Cleanup complete"