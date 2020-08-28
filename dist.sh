#!/usr/bin/env bash

mkdir -p build/dist

tar czf build/dist/latency-test.tar.gz build/libs/* lib/* scripts/* src/main/resources/*
