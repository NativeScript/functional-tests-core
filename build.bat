IF "%1"=="fast" GOTO FastBuild
call mvn clean compile assembly:single
call del ..\functional-tests-new\lib\functional.tests.core-0.0.4.jar
call copy target\functional.tests.core-0.0.4-jar-with-dependencies.jar ..\functional-tests-new\lib\functional.tests.core-0.0.4.jar
call mvn install:install-file -Dfile=../functional-tests-new/lib/functional.tests.core-0.0.4.jar -DgroupId=functional.tests.core -DartifactId=functional.tests.core -Dversion=0.0.4 -Dpackaging=jar
:FastBuild
call mvn clean install
call mvn install:install-file -Dfile=target/functional.tests.core-0.0.4.jar -DgroupId=functional.tests.core -DartifactId=functional.tests.core -Dversion=0.0.4 -Dpackaging=jar
