#!/bin/sh

_classpath=$(mvn dependency:build-classpath | grep '\.jar')
java -jar ${_classpath}:target/cachemeshpoc-0.0.1-with-dependencies {{.CLI_ARGS}}

