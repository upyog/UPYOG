#!/bin/sh
#metadata=$(wget http://repo.egovernments.org/nexus/content/groups/public/org/egov/egov-ear/3.0.2-COE-SNAPSHOT/maven-metadata.xml)
#snapshotVersion=$(sed -n 's/.*<value>\([^<]*\)<\/value>.*/\1/p' ./maven-metadata.xml 2>&1 | head -n 1)
ear=$(wget https://upyog-sandbox-assets.s3.ap-south-1.amazonaws.com/librarie/egov-ear-3.0.2-COE-SNAPSHOT.ear)
$(mkdir /app/egov/egov-ear/target)
$(mv egov-ear-3.0.2-COE-SNAPSHOT.ear /app/egov/egov-ear/target/)
#echo "$snapshotVersion"
