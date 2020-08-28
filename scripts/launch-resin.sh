#!/usr/bin/env bash

GC_LOG_FILE="resin-gc.log"
SCRIPT_DIR="$(dirname $0)"
source "$SCRIPT_DIR/common.sh"

java $JVM_ARGS $JVM_MEM -cp 'build/libs/*:lib/*' com.aitusoftware.ws.benchmark.resin.ResinServerMain