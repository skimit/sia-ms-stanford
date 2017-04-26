#!/bin/bash

set -e

echo "Updating EBS environment..."

commit=$1
environment="sia-ms-stanford-$2"

echo "commit: $commit"
echo "environment: $environment"

echo "...updating $environment environment to use new version $commit..."
aws elasticbeanstalk update-environment \
    --environment-name $environment \
    --version-label $commit

echo "...done"
