#!/bin/sh

_classpath=$(mvn dependency:build-classpath | grep '\.jar')
java -cp ${_classpath}:target/cachemesh-0.0.1.jar cachemesh.Main $@
#java -cp target/cachemesh-0.0.1-jar-with-dependencies.jar cachemesh.Main $@
