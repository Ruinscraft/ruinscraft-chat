# ruinscraft-chat
A chat plugin for Ruinscraft

## Building
Building requires Maven

To build, run `mvn package`. A binary .jar file will be in each module's `target` directory.

## Testing
Automated testing requires Docker and Docker Compose

To test this software on a Bukkit server, run `./build_and_run` from a bash terminal.

It will:
1. Build the ruinscraft-chat Bukkit plugin
2. Spin up a Paper server container (with the ruinscraft-chat plugin installed), a MySQL container, and a Redis container
3. Allow networking between all 3 so all functionality works immediately

To join, just open Minecraft and connect to `localhost`
