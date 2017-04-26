#!/bin/bash

set -e

echo "Creating EBS Application Version..."

application="sia-ms-stanford"
commit=$1
bucket="sia-elb-docker-definitions"

echo "commit: $commit"
echo "bucket: $bucket"
echo "application: $application"

dockerRunFile="dockerrun-$commit.aws.json"
echo "...creating application definition file $dockerRunFile..."
sed "s/<TAG>/$commit/" < ./scripts/dockerrun.aws.json.template > $dockerRunFile

echo "...uploading application definition file to s3://$bucket/$application/$dockerRunFile..."
aws s3 cp "$dockerRunFile" "s3://$bucket/$application/$dockerRunFile"

echo "...creating application version for ELB application $application..."
aws elasticbeanstalk create-application-version \
    --application-name $application \
    --version-label $commit \
    --source-bundle S3Bucket="$bucket",S3Key="$application/$dockerRunFile"

echo "...done"
