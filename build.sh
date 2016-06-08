#!/bin/bash

if [ "$1" == "fast" ]; then
  echo "Fast Build"
  mvn clean install
  mvn install:install-file -Dfile=target/functional.tests.core-0.0.2.jar -DgroupId=functional.tests.core -DartifactId=functional.tests.core -Dversion=0.0.2 -Dpackaging=jar

elif [ "$1" == "deploy" ]; then
  echo "Full Build and Deploy"
  mvn install:install-file -Dfile=lib/opencv-2.4.9/opencv-249.jar -DgroupId=opencv -DartifactId=opencv -Dversion=2.4.9 -Dpackaging=jar
  mvn clean compile assembly:single

  rm -rf ../functional-tests-new/lib/functional.tests.core-0.0.2.jar
  cp target/functional.tests.core-0.0.2-jar-with-dependencies.jar ../functional-tests-new/lib/functional.tests.core-0.0.2.jar

  cd ../functional-tests-new
  mvn install:install-file -Dfile=lib/functional.tests.core-0.0.2.jar -DgroupId=functional.tests.core -DartifactId=functional.tests.core -Dversion=0.0.2 -Dpackaging=jar
  cd ../functional-tests-core

else
  echo "Full Build"
  mvn install:install-file -Dfile=lib/opencv-2.4.9/opencv-249.jar -DgroupId=opencv -DartifactId=opencv -Dversion=2.4.9 -Dpackaging=jar
  mvn clean compile assembly:single
  rm -rf target/functional.tests.core-0.0.2.jar
  mv target/functional.tests.core-0.0.2-jar-with-dependencies.jar target/functional.tests.core-0.0.2.jar
fi