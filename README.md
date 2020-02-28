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
- @mattlol85
- @kenny-designs

#### In Progress
[Github Issues Page](https://github.com/mattlol85/FitzNet-Hardcore/issues)
