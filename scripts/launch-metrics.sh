#!/usr/bin/env bash

GC_LOG_FILE="babl-gc.log"
SCRIPT_DIR="$(dirname $0)"
source "$SCRIPT_DIR/common.sh"
source "$SCRIPT_DIR/hosts_conf.sh"

if [[ "${MONITORING_HOST}" == "" ]]; then
  echo "Specify MONITORING_HOST env var"
  exit 1
fi

sed -i -e "s/MONITORING_HOST/${MONITORING_HOST}/" babl-metrics.properties

java $JVM_ARGS $JVM_MEM -cp 'build/libs/*:lib/*' com.aitusoftware.babl.ext.monitoring.MicrometerExporter babl-performance.properties babl-metrics.properties