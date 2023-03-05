#!/bin/sh

_classpath=$(mvn dependency:build-classpath | grep '\.jar')
java -cp ${_classpath}:target/cachemeshpoc-0.0.1.jar cachemeshpoc.Main {{.CLI_ARGS}}

