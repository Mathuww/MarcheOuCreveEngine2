# MCE2

*A data-driven tactical RPG game engine that doesn't require any lines of code*

MCE2 allows you to create your own tactial RPG without writing a single line of code. You only need to edit the maps & entities of your game in Tiled based on the template provided.

### Features 
Design your own adventure :
- Build the maps you want
- Connect them the way you want
- Create your own enemies
- Create the allies that will fight them
- Design the attacks & capacities your characters can use.

Based on libGDX 1.14 & Java 17.

## Quick start
Before starting, ensure you've got Java 17 or newer running on your computer.
Download the entire repository by clicking on **Code** at the top of this page > **Download ZIP**.

Unzip it anywhere you like and you should get the following file tree, considering only the things you should use/edit :

```plaintext
MarcheOuCreveEngine2/
- assets/
-- tiled/
-- random_names/
- mcpack.sh
- mcpack.bat
```

All the files you will have to edit to make your game are contained within the `assets/tiled/` directory.
It's already filled with the data of the template game, which you can build by typing the following command :
```shell
$ python3 ./mcpack.py
```

The resulting Java executable will be built in `dist/build.jar`

