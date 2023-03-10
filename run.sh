#!/bin/sh

#_classpath=$(mvn dependency:build-classpath | grep '\.jar')
#java -cp ${_classpath}:target/cachemeshpoc-0.0.1.jar cachemeshpoc.Main $1 $2
java -cp target/cachemeshpoc-0.0.1-jar-with-dependencies.jar cachemeshpoc.Main $1 $2
