#!/usr/bin/env bash
set -e
mkdir -p splits
split -n l/4 -d logs.txt splits/part_ --additional-suffix=.txt