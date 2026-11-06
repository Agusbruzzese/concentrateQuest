#!/bin/sh
# Compile everything and start the game. Usage: ./run.sh   or   ./run.sh --demo
cd "$(dirname "$0")"
javac -d out $(find src -name '*.java') && java -cp out concentratequest.Main "$@"
