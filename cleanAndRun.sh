#!/bin/sh
#Matthew Fitzgerald 2020

## CHANGE THESE
## Directory to Development Server
SERVER_PATH='/home/matt/Desktop/DevServer'
## Plugin Source File
PLUGIN_PATH='/home/matt/Desktop/FitzNet-Hardcore'
# Remove old files (Clean)
cd $SERVER_PATH

rm banned*

cd plugins

rm -r Fitz*

# Maven Compile
cd $PLUGIN_PATH
mvn

# Add new jar into file
cp $PLUGIN_PATH/target/Fitz-NetHardcore-1.15.2-SNAPSHOT.jar /home/matt/Desktop/DevServer/plugins/ 

# Run Server
cd $SERVER_PATH
java -jar spigot.jar nogui
