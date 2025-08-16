#!/bin/bash
# Whenever this file/curl hit, it will automatically start the build, 
# In line 4, Shivankshuklaa is my jenkins username and 
# after colon that is my API token which is generated from Jenkins

curl -v -X POST https://niua-cicd.niua.in/job/builds/job/upyog/job/frontend/job/sv-ui/buildWithParameters \
  --user Shivankshuklaa:11cf899f75b5d9c9c3d58e76eb6d2ef9b0 \
  --data-urlencode json='{"parameter": [{"name":"BRANCH", "value":"origin/'$1'"}]}'



