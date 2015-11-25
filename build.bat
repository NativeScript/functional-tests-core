IF "%1"=="fast" GOTO FastBuild
call mvn install:install-file -Dfile=lib/opencv-2.4.9/opencv-249.jar -DgroupId=opencv -DartifactId=opencv -Dversion=2.4.9 -Dpackaging=jar
call mvn clean compile assembly:single
call del ..\functional-tests-new\lib\functional.tests.core-0.0.1.jar
call copy target\functional.tests.core-0.0.1-jar-with-dependencies.jar ..\functional-tests-new\lib\functional.tests.core-0.0.1.jar
call mvn install:install-file -Dfile=../functional-tests-new/lib/functional.tests.core-0.0.1.jar -DgroupId=functional.tests.core -DartifactId=functional.tests.core -Dversion=0.0.1 -Dpackaging=jar
:FastBuild
call mvn clean install
call mvn install:install-file -Dfile=target/functional.tests.core-0.0.1.jar -DgroupId=functional.tests.core -DartifactId=functional.tests.core -Dversion=0.0.1 -Dpackaging=jar