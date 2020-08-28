#!/usr/bin/env bash

rm /dev/shm/*-gc.log

RESULT_DIR="$(date +%Y%m%d_%H%M%S)"
SCRIPT_DIR="$(dirname $0)"
mkdir -p "$RESULT_DIR"
cd "$RESULT_DIR" || exit
bash "$SCRIPT_DIR/launch-$1.sh"
cp /dev/shm/*-gc.log "$RESULT_DIR/"