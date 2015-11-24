call mvn clean compile assembly:single
call copy target\functional.tests.core-0.0.1-jar-with-dependencies.jar ..\functional-tests-new\lib\functional-tests-core\functional.tests.core-0.0.1.jar