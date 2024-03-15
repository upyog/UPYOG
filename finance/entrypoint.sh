#!/bin/sh
metadata=$(wget http://nexus-repo.digit.org/nexus/content/groups/public/org/egov/egov-ear/3.0.0-SNAPSHOT/maven-metadata.xml)
snapshotVersion=$(sed -n 's/.*<value>\([^<]*\)<\/value>.*/\1/p' ./maven-metadata.xml 2>&1 | head -n 1)
ear=$(wget http://nexus-repo.digit.org/nexus/content/groups/public/org/egov/egov-ear/3.0.0-SNAPSHOT//egov-ear-$snapshotVersion.ear)
$(mkdir /app/egov/egov-ear/target)
$(mv egov-ear-$snapshotVersion.ear /app/egov/egov-ear/target/)
#echo "$snapshotVersion"
