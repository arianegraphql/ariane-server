#!/bin/bash
cd ../server || exit
../gradlew :server:publishToMavenLocal --info

echo "Successfully published Server library"

cd ../codegen || exit
../gradlew :codegen:publishToMavenLocal --info

echo "Successfully published codegen plugin"