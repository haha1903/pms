#! /usr/bin/env bash

usage() {
    echo "Usage: $0 -g GROUP_ID -a ARTIFACT_ID -v VERSION"
    exit
}

quit() {
    echo "Quitting..."
    exit -1
}

trap 'quit' SIGINT

DIR=$(dirname $0)

#
# Parse and check arguments
#
while getopts 'g:a:v:' OPT; do
    case $OPT in
        g)
            GROUP_ID="$OPTARG";;
        a)
            ARTIFACT_ID="$OPTARG";;
        v)
            VERSION="$OPTARG";;
    esac
done

shift $(($OPTIND - 1))

if [ -z $GROUP_ID ] || [ -z $ARTIFACT_ID ] || [ -z $VERSION ]; then
    usage
fi

echo "Re-packaging for jenkins/artifactory..."
echo "groupId: " $GROUP_ID
echo "artifactId: " $ARTIFACT_ID
echo "version: " $VERSION

PMS_WAR="pms.war"
PMS_JENKINS="pms-$VERSION"

#
# Unzip the war file and directories for pom
#
cd $DIR/../target

if [ ! -f $PMS_WAR ]; then
    echo "ERROR: target/$PMS_WAR not found"
    exit -1
fi

rm -rf $PMS_JENKINS artifactory
mkdir -p $PMS_JENKINS
cd $PMS_JENKINS

echo "Unzipping $PMS_WAR..."
jar -xf ../$PMS_WAR
mkdir -p META-INF/maven/$GROUP_ID/$ARTIFACT_ID

#
# Generate pom.xml
#
echo "Generating pom.xml..."
cat > META-INF/maven/$GROUP_ID/$ARTIFACT_ID/pom.xml <<EOF
#Generated by $0
#$(date)
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>$GROUP_ID</groupId>
    <artifactId>$ARTIFACT_ID</artifactId>
    <version>$VERSION</version>
    <packaging>war</packaging>
EOF

#
# Re-package the war
#
cd ..
echo "Re-packaging $PMS_JENKINS.war..."
mkdir artifactory
jar -cf artifactory/$PMS_JENKINS.war $PMS_JENKINS

echo "Done re-packaing for jenkins/artifactory."
