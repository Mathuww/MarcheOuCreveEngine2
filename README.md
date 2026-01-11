# MCE2

*A data-driven tactical RPG game engine that doesn't require any lines of code*

MCE2 allows you to create your own tactial RPG without writing a single line of code. You only need to edit the maps & entities of your game in Tiled based on the template provided.

### Features 
- Build the maps you want
- Connect them the way you want
- Create your own enemies
- Create the allies that will fight them
- Design the attacks & capacities your characters can use.

MCE2 is based on libGDX 1.14 & Java 17.

## Quick start
Before starting, ensure you've got Java 17 or newer running on your computer, as well as Python 3 installed (this is required for the packing script).
Download the entire repository by clicking on **Code** at the top of this page > **Download ZIP**.

Unzip it anywhere you like and you should get the following file tree, considering only the things you should use/edit :

```plaintext
MarcheOuCreveEngine2/
- assets/
-- tiled/
--- unpacked/
--- packed/
-- random_names/
- walkordie.py
- ...
```

`walkordie.py` is the main interface for our game engine. It only has the two commands described below, and therefore only does two things : packing the game data and building a .jar executable, and running the built .jar file.

All the files you will have to edit to make your game are contained within the `assets/tiled/unpacked/` directory.
It's already filled with the data of the template game, which you can build by typing the following command :
```shell
$ python3 ./walkordie.py pack
```

The resulting Java executable will be built in `public/build.jar`, and can be launched easily via :
```shell
$ python3 ./walkordie.py run
```


## Overview

You should start in a map in Exploration mode. It's one of the two game modes :
- The mode you're currently in, the Exploration mode, allows you to freely move an ally with the keyboard around the map and to cross portals between worlds. As its name suggests, it's made to explore and you cannot lose any health nor die in this mode ;
- The Combat mode, in which allies are only controllable using a classical tactical RPG interface. In this mode, moving, attacking and other actions are obviously restricted since it's a turn-based system. It is not possible to cross portals in this game mode, for obvious reasons.

Crossing the first portal brings you to a tunnel leading to another portal. This one actually brings you to a Combat map. A Combat is a succession of Allies and Enemies turns, that only ends when one of the team defeated the whole adverse team. 
When it's your turn, you can select an ally that you may move, attack and use a special capacity with. You can only performs each one of these actions once per turn. 
- Moving an ally is restricted by the value of its maximum moves per turn.
- Several attacks may exist for each ally, allowing the player to use the one that is most adapted to the current situation.
- Same for the special capacities.

When you're done moving and attacking, you can hit **END TURN** at the upper right corner of the screen to switch to the enemies' turn. Each enemy is controlled by an AI that will make perform do the best current move. They will not necessarily attack you if it's not the best move for them to perform.

Losing to the enemies will make the current battle restart.
Winning to them will switch to the Exploration mode, turning one of the remaining allies into a freely controllable character again, for you to explore the map if that's your will, then find your way to a portal to teleport to another map.

## How maps, entities and attacks are stored
The main goal of our project wasn't to produce this demo, but to make it fairly easy to create your own game. We'll thus explain the data structure, and how you can edit/create Tiled files to edit or extend the game.

Obviously, to perform such operations, you're going to need a release of Tiled installed of your computer. If that's not the case yet, download it on [their official website](https://www.mapeditor.org/download.html) and familiarize with it.

The data tree is the following
```plaintext
assets/
-  tiled/
-- unpacked/
---- attacks/    
---- entities_anims/
---- maps/
---- spritesheets/   
```

Every time you use a spritesheet in your game, make sure it's loaded for a the `assets/tiled/unpacked/spritesheets/` folder, otherwise it's not going to be included inside the game assets and will fail to load.

**Unless it's specified in the README, DO NOT put .tmx or .tsx files in *subfolders* inside the folder it's supposed to be. For instance, don't put a map inside `maps/mywonderfulloveablefolder/`, but always inside `maps/`.**

### The `maps/` folder
We distinguish two kinds of maps :
- Exploration-only maps, which won't trigger a battle (switch to Combat game mode). They're made for visiting (which are not necessarily visible, you can set up an invisible portal at the door of the house, to make the player able to visit it in a separate map).
- Combat maps, which will trigger a battle when visited. When the battle finishes, if your team won, the game will switch to the Exploration mode while still being inside this map. One of the allies that's still alive will become able to move freely using WSAD/ZSQD, allowing you to cross a portal to exit the map.

The game begins in the map `start.tmx`.
Another file exists within this folder : it's the `misc.tmx`, containing only visuals used in the game. This file is the only file inside this folder that is not made to be a loadable map.

### The `entities_anims/` folder
This one contains entities' visuals and properties. Each entity that may spawn in the game belongs to a file that exists in here.
**Portals are entities too.**

### The `attacks/` folder
Entities' possible attacks can be customized. You can edit their damage pattern as well as their power.

We'll now dive in each one of these, explaining how to customize the template we offered you.

## Customizing maps
Maps are probably the easiest elements to customize.
The game begins in `start.tmx`, and every other map needs to be made accessible via a portal to be visitable inside the game.

The only property belonging to an entire terrain map (these properties can be shown using **Map** > **Map Properties...** in the app menu) is `boolean : isBattle`.
- If set to `true`, entering this map will trigger a battle to start. All allies and enemies will thus spawn (as well as other entities). Exploration mode will only be entered when the battle is over.
- If set to `false`, Combat mode will never be entered inside this map. No enemy will spawn, and only one ally will spawn and is going to be conttrolable using WASD/ZQSD. Entities of other kinds will still spawn.

The best way to figure out how a map is structured is however to open `battle1.tmx`, since it contains multiple layers and triggers the summoning of several entities.

### Customizing tile layers
You can edit the visual apperance of a map simply by editing the tiles inside. 
Tiles can be divided into several layers.
The only property that's important for terrain tiles is `boolean : blocked`. It determines if projectiles and entities should collide with this tile or not.
Projectiles & entities will only collide with `blocked` tiles if they belong to the uppermost layer.

### Customizing the map's entities
#### Classic entities (excluding portals)
Asking the game engine to spawn an entity at a given position is made possible by adding an object inside the `Entities` layer.
The entity will spawn at the position of the lower left corner of the object's shape.
You may choose the entity to spawn by refering to an entity `tmx` file that exists within the `entities_anims/` folder.
Entities' objects have to include only one property and it's `string : name`. You just have to refer to the file in `entities_anims/` you wan to spawn by specifying the corresponding filename, keeping only the filename part (don't put the entire path, nor the .tmx extension).

Entities can be of several types. 
- `ally` : the player's team, and characters the player will have to use to fight enemies in Combat mode. 
- `enemy` : characters the player will have to defeat in Combat mode.
- `portal` : we'll get to that in the following part.
- `projectile` : we'll get to them even later.
As each entity (each .tmx inside `entities_anims/`) can only be of a defined type, you don't have to specify the type of the entity you want to spawn in an entity object.

### Portals
You can ask MCE2 to spawn a portal in the same way you ask it spawn an entity, by creating an object inside the `Entities` layer, and by giving the property `string : name` by making it refer to the .tmx of the portal you want to spawn (portals' .tmx are stored in the same folder as the entities' .tmx).

However, portals do have some special properties, required to determine where they should take you to :
- `string : destMap` : the map (.tmx) inside `maps/` you want this portal to go. Don't add .tmx, and keep only the filename.
- `int : destPortal_ID` : specifying only the filename makes cases where multiple portals exist inside the same map unable to exist. That's way we needed each portal to have an ID. When taking a portal that has `destMap` set to `aqualand.tmx` and a `destPortal_ID` of 1, it will teleport you to the portal with a `portal_ID` (which is a separate property, see below) of 1 inside `aqualand`.
- `int : portal_ID` : **this** portal's ID. **All portals should have an ID, even if there isn't any portal teleporting to them.**

## Customizing entities
Whether it's a classic entity or a portal, each single entity that can be spawned in the game has a .tmx file here, describing its visuals and its properties.

Let's take for example `ally.tmx`, the main ally entity in the template game.

### Entities' properties
The only property common to all entity types if the `string : type` property. It can the following values :
- `ally` 
- `enemy` 
- `portal` 
- `projectile` : this is the only type that isn't aforementioned. Projectiles are launched to the target character when the attacking/"source" character has been made to attack. 

#### Visuals
Before getting to the specificities of each type, we need to explain how entities' visuals are managed inside their .tmx file.
Each line of the .tmx represents a different animation.
Look at the downmost line of `ally.tmx`, representing the `idle` animation. Each tile is a keyframe of the animation, from left to right.

The properties (including the name) of an animation are specified in the first tile of the line, so they're stored in the tileset, not in the tilemap, that's the way Tiled works.
To add properties to a tile, you actually need to edit the tile in the tileset in a separate tab.

The properties of each animation (so the properties of each line's first tile) are the following :
- `string` : `animName`. Each entity **needs** to have at least an `idle` anim. Portals and projectiles will stay forever in `idle`, and we'll describle characters' possible animations later.
- `int : fps`. You need to specify the speed at which runs each animation. A line of 4 tiles (4 frames) with `fps = 4` will take 1 second to finish.
- `string : playMode`. Possible values are `NORMAL` (it will play all frames, then stay on the last one), `LOOP` (it will play all frames, go the first frame, start again, indefinitely), `LOOP_PINGPONG` (play all frames, then play in the opposite direction right->left, then play left->right again, and so on). It's the only one of these properties than can be omitted, defaulting to `NORMAL`.

#### Characters' specific properties
On top of all these, characters also have these properties :
- `int : hp`. Their max HP.
- `int : maxMoves`. The max number of tiles they can cross in a turn. This is not counted by measuring straight-line distance, but by measuring the length of the actual path crossed, including detour to avoid obstacles/entities.
- `string : attack1`, `attack2`, ..., `attack6`. The attacks this character can use. Each of the attacks need to be a .tmx file inside `attacks/`, as this works the way as the entities .tmx system. You don't have to include 6 attacks, but at least one is obviously required.
- `string : baseAttack`. The base attack of your character. Obviously has to be one of the few stated in `attack[1,6]`. This is also required.

We'll cover the way attacks can be customized later, but for now remember that each attack existing in the game is a .tmx file in the `attacks/` folder.

#### Characters' visuals
On top of `idle` (still required), characters may/can (according to the case) have each of these animations : 
- `walk` : The default move animation.
- `walk_[up,down,left,right]` : The move animations for each directions. At least `up`, `down` and one of the X directions are required, as if you provide only `left` or `right`, MCE2 will automatically flip one to create the opposite direction's anim. If you include all required specific directions, the aforementioned `walk` is not required.
- `run_[up,down,left,right]` : if these exist, the character will use it instead of the walking anim in Combat mode.
- `hurt` : played when the character's hurt.
- `dead` : played when it dies.

Things get slightly more complex for attack animations, as you can actually specify in each attack's properties (which we'll explore later) the name of the animation using this attack should trigger.
The attacks included in the template will use the animation `attack1` for each character.

#### Let's talk about projectiles
We'll see below that each attack can use a specified type of projectile.
Several attacks can use the same projectile .tmx. The default one in the template in an arrow.

The projectile will automatically be rotated according to the shooting angle but for this to work, the original direction of the visuals in the tileset need to be **horizontal, with the damaging extremity of the projectile oriented to the right**.

Projectiles' .tmx only need to specify `type : projectile`, include an `idle` animation visual and an additional property `int : speed` that will sets the speed at which they go across the map.

## Customizing attacks
As each attack is a .tmx file, you can edit it to customize the way the attack is displayed and behaves.

Before studying how to edit its damage pattern, let's see the properties on an attack : 
- `string : attackType`. The type of the attack. For now, only `generic` is supported. Thus, please set this to `generic`.
- `string : displayName`. The name of the attack displayed in the action choice HUD. To keep up with the current aesthetics of the template, it's better to use a name in all capitals, like `KNIFE` or `BOW`. Also don't make this name longer than 10 characters or it won't display as properly.
- `string : projectileName`. The projectile entity that will be launched to the target when this attack is used. Required too.
- `string : senderAnim`. The animation of the attacking character that will be player when this attack is used. It has to exist in the .tmx file of each character that will be able to use this attack.
- `int : power`. The maximum damage this attack can inflict (in health points) to the target.

### Editing damage pattern
The damage pattern is represented quite intuitively on Tiled.
Let's take a look at `square_attack.tmx`.

To create an attack's damage pattern, you have a specify a source tile. Look at the tile at the center of the square, it has the following properties :
- `type = sender`.
Only one tile can have this propery in an attack .tmx file.

So imagine your attacking character is on its tile. All tiles marked by the property `type = attackDamage` will be reachable by the attack when the attacking character is on the tile marked by `type = sender`.

For instance, if you only put `attackDamage` tiles just besides the `sender` tile, this attack will be a hand-to-hand attack, like a knife.
You can take a look at `close_attack.tmx` to see what this looks like.

You can also add decreasing factors for some tiles in attacks' damage pattern.
For example, if you wish for your attack to be less and less effective the further you go for the sender, you can.
Adding `float : decreaseFactor` to a tile will make the actual damage at this position lower to `(1 - decreaseFactor)*power`, instead of `power` HP.