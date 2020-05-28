#!/usr/bin/env bash

set -e

cd "${BASH_SOURCE%/*}/"../src || exit

javac ./*.java

java Main
