#!/usr/bin/env bash

sudo yum update -y
sudo yum install -y yum-utils java-11-amazon-corretto-headless curl

export JAVA_HOME=/usr/lib/jvm/java-11-amazon-corretto.x86_64/
export PATH=$JAVA_HOME/bin:$PATH

sudo sysctl -w net.core.rmem_max=2097152
sudo sysctl -w net.core.wmem_max=2097152
sudo sysctl -w vm.min_free_kbytes=1048576
echo tsc | sudo tee /sys/devices/system/clocksource/clocksource0/current_clocksource
echo 1 | sudo tee /sys/bus/workqueue/devices/writeback/cpumask

/usr/bin/env bash
