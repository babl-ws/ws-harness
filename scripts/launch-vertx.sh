#!/usr/bin/env bash

GC_LOG_FILE="vertx-gc.log"
SCRIPT_DIR="$(dirname $0)"
source "$SCRIPT_DIR/common.sh"

THREAD_COUNT=20

java $JVM_ARGS $JVM_MEM -cp 'build/libs/*:lib/*' "-Dio.netty.buffer.checkBounds=false" "-Dbabl.bench.pool.size=$THREAD_COUNT" com.aitusoftware.ws.benchmark.vertx.VertxServerMain
