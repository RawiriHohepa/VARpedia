#!/bin/bash
JDK_PATH=/home/rawiri/Programs/jdk-13/bin
JAVA_FX_PATH=/home/rawiri/Programs/javafx-sdk-13/lib
PATH=$JDK_PATH:$PATH
java --module-path $JAVA_FX_PATH --add-modules javafx.base,javafx.controls,javafx.media,javafx.graphics,javafx.fxml -jar VARpedia.jar
