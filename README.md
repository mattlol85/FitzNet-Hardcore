# FitzNet-Hardcore: A Hardcore Mode Alternative to Servers

Fitz-Net Hardcore is a plugin that is designed for low to medium population servers that implments a new type of hardcore mode. ***This plugin is not to be run on hardcore mode!*** This plugin **should** however be installed on a hard difficulty server.

## Features
1. Life Regeneration.
    - Each player starts with StartingLives
        - This value is *StartingLives* in the config.yml
    - After *LifeRegenTime* minuites, the player will have a new life.
        - May implement further ways to deal with lives
            - (Ex. Killing player steals life, granting a friend a life but costing 2 for them.)
2. Scoreboard
    - Tracks Your lives.
    - Shows lives under username visible to other players.
3. Server Permissions
    - WIP
## Using config.yml
The config.yml file is relatively straightforward. There are a few variables there but all of them are extremely important.

- *MaxLives* - How many lives can a player gain maximum?
- *StartingLives* - How many lives should a new player start with?
- *LifeRegenTime* - How many minuites should it take to regenerate a new life?

## Reading And Understanding <PLAYERUUID>.yml


### Contributors
- [@mattlol85](https://github.com/mattlol85)
- [@kenny-designs](https://github.com/kenny-designs)

#### In Progress
[Github Issues Page](https://github.com/mattlol85/FitzNet-Hardcore/issues)

## Setting Up a Development Environment
Want to contribute? We'd love to have you! Here are some instructions on how to get started:
1) You'll need a place to program in. We recommend the text editor [Visual Studio Code](https://code.visualstudio.com/).
    1) To make the process even simpler, you can also install [IntelliJ](https://www.jetbrains.com/idea/) along with the [Minecraft Development Plugin](https://plugins.jetbrains.com/plugin/8327-minecraft-development).
2) Clone this repo with git using this URL: https://github.com/mattlol85/FitzNet-Hardcore.git.
3) Make a directory next to **but NOT in** the FitzNet-Hardcore repo you just cloned.
    1) Name this new directory something like *Fitznet-DevServer*. This is where your local server is going to end up.
4) You'll also need Spigot to get your local server going. To do this, download the BuildTools.jar from [here](https://www.spigotmc.org/wiki/buildtools/) and place it in your Fitznet-DevServer directory you just created.
    1) **Only** download the BuildTools.jar! Do not run it yet.
5) In addition, you need Maven so you can run the mvn command. Download it from [here](https://maven.apache.org/).
6) Now we're ready for the cool stuff! cd inside the FitzNet-Hardcore repo you cloned and run the command 'mvn'.
    1) This will create a Fitz-NetHardcore-&ast;-SNAPSHOT.jar in the target directory.
    2) Copy the Fitz-NetHardcore-&ast;-SNAPSHOT.jar from the FitzNet-Hardcore/target directory and place it inside your Fitznet-DevServer/plugins directory. *If the plugins folder doesn't exist, create it!*
7) Inside of Fitznet-DevServer, run the following command: java -jar BuildTools.jar.
8) Once BuildTools.jar finishes, there will be a new jar labeled spigot-&ast;.jar. Run the following command to get your server up and running: java -jar spigot-&ast;.jar nogui
9) Once the world is finished building, we can finally connect!
    1) Launch Minecraft Java Edition and go to multiplayer
    2) Add a server called something like *Fitznet-Dev* and make its address *localhost*.
10) And that's it! Connect to your server and enjoy messing around with the plugin!
    1) Trying typing /lives. If you get a response then the plugin is working!
11) When you're done playing around, enter 'stop' into the console that you started the server from to shut it down.

Everytime you want to see a change you made in action, generate a new Fitz-NetHardcore-&ast;-SNAPSHOT.jar as we did above and then place it in your servers plugins directory. Then just restart your server and check out what you did!
