#!/bin/bash

BASEDIR="$( cd "$( dirname "$0" )" && pwd )"

msg() {
  echo -e "\n\n\033[32;32m$1\033[0m"
}

msg "Cleaning root"
rm -rf node_modules

msg "Cleaning css"
cd "$BASEDIR/packages/css" && rm -rf node_modules

msg "Cleaning libraries"
cd "$BASEDIR/packages/libraries" && rm -rf node_modules

msg "Cleaning Core module"
cd "$BASEDIR/packages/modules/core" && rm -rf node_modules
