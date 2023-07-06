# Better Server Packs (BSP)
Better Server Packs is a project that aims to replace the oftentimes annoying 
vanilla implementation of Server Resourcepacks. It does this by calculating the
resourcepack's sha1-hash itself, saving it in a file for caching.


## What is wrong with Server Resourcepacks?
The issue Server Resourcepacks face currently is that all config about them
(including the hash) is stored in the server.properties file. Although hashes are
technically optional for servers, not using one will cause all sorts of
weirdness when updating the resourcepack resulting in packs not updating on the
client. 

Using a hash on the other hand, requires a server restart for every update of 
the resourcepack and hashes are also a pain to compute. 
(Often tools will generate incorrect hashes for a file.)


## How does BSP solve this problem?
BSP removes the need for server restarts by implementing its own logic
essentially replacing the vanilla system. BSP also calculates the hash on its
own solving the issue of finding a functional hash generator and increasing
ease of use.

BSP provides a simple and responsive command interface for operators through 
a new `/pack` command through which all on-demand functionalities of the plugin
can be modified and used.

BSP also allows caching of the resource pack hash to increase boot times.


## Commands
BSP adds one new command `/pack` to your server with the following command tree:
+ `/pack`
  + [`set [<url>]`](#set)
  + [`reload [push]`](#reload)

These subcommands serve the following functions.

### `set`
`/pack set [<url>]`

This command sets the url of the server resourcepack. Simply pass the URL of the
pack as an argument. Passing no argument will set no resourcepack, effectively
disabling the plugin.

*Note that this follows the same restrictions as a regular server resourcepack.
The link must be "direct", meaning it cannot lead to a webpage, but must lead to
the file itself.*

### `reload`
`/pack reload [push]`

Recalculates the resourcepack hash. This should be called after updating the
resourcepack to ensure new players can receive the pack correctly. This is not
necessary after restarting the server. Passing "push" as an argument will update the resourcepack for all active 
players.

Reloading the resourcepack is done asynchronously, doing will, disregarding edge
cases, not impact server performance. 


## Permissions
BSP implements four total permissions to regulate usage of the commands. These
can be modified through permission plugins like LuckPerms without issue.
The following permissions are implemented:
+ `betterserverpack`:
  + Controls usage of all commands. Disabling this permission will disable 
    access to all commands moderated by this plugin.
  + Default: Operator Only
+ `betterserverpack.set`:
  + Controls access to the [`set`](#set) subcommand.
  + Default: Operator Only
+ `betterserverpack.reload`
  + Controls access to the [`reload`](#reload) subcommand.
  + Default: Operator Only