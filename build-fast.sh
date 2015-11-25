mvn clean install
rm -rf ../functional-tests-new/lib/functional-tests-core/functional.tests.core-0.0.1.jar
cp target/functional.tests.core-0.0.1.jar ../functional-tests-new/lib/functional-tests-core/functional.tests.core-0.0.1.jar
mvn install:install-file -Dfile=../functional-tests-new/lib/functional-tests-core/functional.tests.core-0.0.1.jar -DgroupId=functional.tests.core -DartifactId=functional.tests.core -Dversion=0.0.1 -Dpackaging=jar