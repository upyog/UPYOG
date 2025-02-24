#!/bin/bash

curl -v -X POST https://builds.digit.org/job/builds/job/cnd-ui/buildWithParameters \
  --user saurabh-egov:114cbf3df675835931688b2d3f0014a1f7 \
  --data-urlencode json='{"parameter": [{"name":"BRANCH", "value":"origin/'$1'"}]}'

# curl https://builds.digit.org/job/builds/job/cnd-ui/lastBuild/api/json | grep --color result\":null

