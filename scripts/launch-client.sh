#!/usr/bin/env bash

RESULT_DIR="$(date +%Y%m%d_%H%M%S)"
GC_LOG_FILE="client-gc-$(date +%Y%m%d_%H%M%S).log"
SCRIPT_DIR="$(dirname $0)"
export JVM_ARGS="-XX:MaxDirectMemorySize=32g -XX:PerfDataSaveFile=/dev/shm/pf.bin -XX:+UnlockDiagnosticVMOptions -XX:GuaranteedSafepointInterval=300000 -XX:+UnlockExperimentalVMOptions -XX:+TrustFinalNonStaticFields -XX:BiasedLockingStartupDelay=0 -XX:+AlwaysPreTouch -Djava.lang.Integer.IntegerCache.high=65536 -Djava.net.preferIPv4Stack=true -Dagrona.disable.bounds.checks=true -Daeron.pre.touch.mapped.memory=true -Daeron.term.buffer.sparse.file=false -Dbabl.socket.tcpNoDelay.enabled=true"
export JVM_MEM="-XX:+UseParallelOldGC -Xmx1g -Xms1g -XX:NewSize=512m -Xlog:gc*,safepoint=info:file=/dev/shm/$GC_LOG_FILE:time"
source "$SCRIPT_DIR/hosts_conf.sh"

java $JVM_ARGS $JVM_MEM -cp 'build/libs/*:lib/*' -Dbabl.perf.results.dir=$RESULT_DIR com.aitusoftware.ws.benchmark.LatencyTestMain "${SERVER_HOST}:8080" latency-client.properties

mkdir -p "$RESULT_DIR"
cp /dev/shm/*-gc.log "$RESULT_DIR/"