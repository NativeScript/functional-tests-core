#!/usr/bin/env bash

mvn clean install
cp target/functional.tests.core-0.0.1.jar ../functional-tests-new/lib/functional-tests-core/
