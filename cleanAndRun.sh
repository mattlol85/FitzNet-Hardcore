#!/bin/sh
#Matthew Fitzgerald 2020

## CHANGE THESE
## Directory to Development Server
SERVER_PATH='/home/aziraphale/projects/FitzServer'
## Plugin Source File
PLUGIN_PATH='/home/aziraphale/projects/FitzNet-Hardcore'
# Remove old files (Clean)
cd $SERVER_PATH

rm banned*

cd plugins

rm -r Fitz*

# Maven Compile
cd $PLUGIN_PATH
mvn package

# Add new jar into file
cp $PLUGIN_PATH/target/Fitz-NetHardcore-1.15.2-SNAPSHOT.jar $SERVER_PATH/plugins

# Run Server
cd $SERVER_PATH
java -jar spigot.jar nogui
