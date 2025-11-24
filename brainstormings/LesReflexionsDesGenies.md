# Les Reflexions Des Génies 

## Préambule 
Ce journal est composé d'archives, retrouvés il y a quelques années. Elles semblent avoir été écrites par des 
esprits brillants, des génies oubliés. Elles contiennent les bribes d'un savoir inestimable, malheureusement 
perdu aujourd'hui. Je l'ai ai rassemblées pour former ce recueil qui, je l'espère, pourra aider les jeunes
developpeurs dont le but est de créer le meilleur moteur de jeu du monde


## Réflexions

"Les possibilités du joueur en mode combat serait de :
    - déplacer un personnage
    - déclancher l'attaque d'un personnage
    - déclancher une capacité d'un personnage
    - déplacer la caméra
    - terminer son tour"

"Il faudrait donc que la classe character ait ses propres méthodes pour se déplacer, activer ses capacités etc
 Si on veut respecter MVC, c'est pte bien d'avoir une sorte de class AnimateSprite jsp quoi, dans laquelle on met les anims et qui
 est appelée par character.draw() (ptet ça existe déjà et on a pas besoin de gérer les anims à la main ?)"


### Remarques 

"J'ai ajouté une fonction clean dans pathfinder mais il aurait fallu le faire dans reconstruct path (quand j'aurais pas la flemme)"

## Structure des données Tiled
tiled_data/
---- main.tsx
---- entities_anims/
-------- entity1.tsx
-------- entity2.tsx
---- attacks/
-------- attack1.tmx
---- maps/
-------- freemove/
----------- start.tmx
----------- indoor/  (exemple)
------------- house1.tmx
-------- combat/
------------ combat1.tmx

### main.tsx
Tileset de toutes les tiles qui ne sont pas les entités.
**Comprend** le visuel des projectiles pour les attaques qui en utilisent.
Comprend les tiles de terrain, d'intérieur, de maps de combat... tout sauf les entités.

+ une tile de propriété globale ***type = "attackDamage"*** qui servira dans les tmx d'attacks/ pour repérer les positions des degats.

### entities_anims/X.tsx

Visuel de l'entité X.

Une entité n'existe pas dans le jeu si elle n'existe pas dans ce dossier .

Pour chaque tileset, donc chaque entité, on définit :
- Son nom ***displayName*** qui sera son nom in-game.
- type/class : définit le type d'entité (Character, Enemy, Obstacle...)
 - ... jsp 

Définit surtout toutes ces animations.
Chaque ligne d'une tileset est une animation.
On définit sur chaque 1ère tile de la ligne l'animation à laquelle elle correspond(ex : ***animName = "idle"*** sur la 1ere ligne).

Propriété globale :
- ***fps***

### attacks/X.tmx

Définit les propriétés (globales sur le tileset) et le pattern de l'attaque.
Propriétés du tileset :
- power : puissance de l'attaque
- senderAnimName : nom de l'anim du chara qui lance l'attaque
- receiverAnimName : nom de l'anim du chara qui recoit l'attaque
- flame : enflammee ?
- etc. pour les trucs spéciaux

Le pattern de l'attaque est défini sur la map. 
On place une tile de propriété ***type = sender*** qui correspond à la source de l'attaque. on peut ensuite designer le pattern d'attaque intuitivement + visuellement autour de la source.
On place une tile ***type = attackDamage*** pour définir un endroit ou l'attaque cause des degats.
On peut définir un facteur *int decreaseFactor* pour chaque tile, par défaut à 1 (le receiver recoit les memes degats à 1 case de la source qu'à 32 quoi). Les degats infligés seront donc power * (1 - decreaseFactor) à cette position relative.


### maps/

Valable pour toutes les maps :
Layers d'objects :
- Entities : positions de spawn d'une entité. On définit laquelle avec ***entityName*** sur cette tile.

#### freemove/

Carte d'exploration. On peut définir un portail vers une autre map d'exploration ou de combat.
Layer supplémentaire :
- Portals : envoie sur une autre map, spécifiée par ***destMapPath***. On spécifie aussi le mode de la map cible avec ***destMapType*** (*combat* ou *freemove*) (on envoie vers la map maps/\[type\]/\[mapPath\]). Envoie à la tile ***destX*** , ***destY*** sur la map destination.

#### combat/

Carte de combat.

=> MCEntityFactory récupère toutes les entités possibles

## Factories nécessaires

Factory = retourner un objet utile (MCEntity, MCEntity.Attack) à partir de données Tiled

### MCEntityFactory
Si une entité existe dans *entities_anims*, on peut la build à travers cette factory.

### MCAttackFactory
Si une attaque existe dans *attacks*, on peut la build à travers cette factory.