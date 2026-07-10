#!/usr/bin/bash
mkdir -p splits
split -l 24 -d entrada.txt splits/part_ --additional-suffix=.txt