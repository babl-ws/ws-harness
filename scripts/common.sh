#!/usr/bin/env bash

export JVM_ARGS="-XX:MaxDirectMemorySize=32g -XX:PerfDataSaveFile=/dev/shm/pf.bin -XX:+UnlockDiagnosticVMOptions -XX:GuaranteedSafepointInterval=300000 -XX:+UnlockExperimentalVMOptions -XX:+TrustFinalNonStaticFields -XX:BiasedLockingStartupDelay=0 -XX:+AlwaysPreTouch -Djava.lang.Integer.IntegerCache.high=65536 -Djava.net.preferIPv4Stack=true -Dagrona.disable.bounds.checks=true -Daeron.pre.touch.mapped.memory=true -Daeron.term.buffer.sparse.file=false -Dbabl.socket.tcpNoDelay.enabled=true"
export JVM_MEM="-XX:+UseParallelOldGC -Xmx1g -Xms1g -XX:NewSize=512m -Xlog:gc*,safepoint=info:file=/dev/shm/$GC_LOG_FILE:time"

