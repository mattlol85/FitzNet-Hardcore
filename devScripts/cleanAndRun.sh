#!/bin/bash
#Matthew Fitzgerald 2020
#Alexander Aguilar 2020

# returns true if the file exists, false otherwise
fileExists() {
    ls $1 1> /dev/null 2>&1
}

SERVER_PATH=$fitz_server_path # Directory to Development Server
PLUGIN_PATH=$fitz_plugin_path # Plugin Source Directory

# check if environment variable set for the server
if [ -z $SERVER_PATH ]
then
    echo 'No environment variable for fitz_server_path was found.'
    echo 'Remedy this by entering the command: export fitz_server_path="path/to/my/devServer"'
    exit 1
fi

# check if environment variable set for the plugin
if [ -z $PLUGIN_PATH ]
then
    echo 'No environment variable for fitz_plugin_path was found.'
    echo 'Remedy this by entering the command: export fitz_plugin_path="path/to/my/FitzNet-Hardcore"'
    exit 1
fi

# Remove old files (Clean)
if fileExists $SERVER_PATH/banned*; then rm $SERVER_PATH/banned*; fi
if fileExists $SERVER_PATH/plugins/Fitz*; then rm -r $SERVER_PATH/plugins/Fitz*; fi

# Maven Compile
cd $PLUGIN_PATH
mvn

# Add new jar into file
cp $PLUGIN_PATH/target/Fitz-NetHardcore-*-SNAPSHOT.jar $SERVER_PATH/plugins/ 

# Run Server
cd $SERVER_PATH
#java -jar spigot*.jar nogui
java -jar paper*.jar nogui
