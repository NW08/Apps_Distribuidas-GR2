#!/bin/bash
mkdir -p splits
split -n l/4 entrada.csv splits/part_ --additional-suffix=.csv