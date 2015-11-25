#!/bin/bash

mvn install:install-file -Dfile=lib/opencv-2.4.9/opencv-249.jar -DgroupId=opencv -DartifactId=opencv -Dversion=2.4.9 -Dpackaging=jar
mvn clean compile assembly:single
rm -rf ../functional-tests-new/lib/functional.tests.core-0.0.1.jar
cp target/functional.tests.core-0.0.1-jar-with-dependencies.jar ../functional-tests-new/lib/functional.tests.core-0.0.1.jar
mvn install:install-file -Dfile=../functional-tests-new/lib/functional.tests.core-0.0.1.jar -DgroupId=functional.tests.core -DartifactId=functional.tests.core -Dversion=0.0.1 -Dpackaging=jar