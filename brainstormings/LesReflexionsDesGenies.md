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

"Pourquoi avoir une attackfactory, il semblerait plus logique d'avoir une complète factory non ?"

"modifier le shoot, pour que si ça miss ça fasse quand même des dégats (rajouter un getHurt dans entity ?) à ce qui a bloquer"

"Move display e pourrrait-il pas servir aussi pour le shoot ? La class me semble un peu trop précise. Pk pas une areadisplay ou jsp quoi"

"Modifier power dans attack, c'est plutôt un multiplieur non ? Signé AST"

"Pour ajouter un bouclier, ajouter une fonction dans entity ou character qui est multiplie des dégats par jsp la valeur de défense et l'utiliser dans attack getDamageTo"

"Fonctions suspectes :
    - playGloabalAnimation(String anim) : utilisé quand ? A enlever
    - setStateManager(MCStateMachine<MCCharacterState, MCEntity> stateManager) : pas osé y toucher, mais ça devrait être MCCharacter non ?
    - getMoveDisplay() : a quoi ça sert ?
    - getAttack() : exception non throws
    - getTargetAnim() : Très chelou non ? Parce que l'anim ne dépend pas que de l'attaque...

"
"Revoir structure des states (ajouté listen input pressed dans la base etc). Copiez la javadoc au passage. Pareil pour change state. et y'en a d'autres"

## Structure des données 
- assets/
-- tiled/
---- entities_anims/
-------- entity1.tmx - map 1 layer 
-------- entity2.tmx
---- attacks/
-------- attack1.tmx - map : 1 layer pattern [+ 1 layer visuel opt.]
---- maps/
-------- freemove/
----------- start.tmx
----------- indoor/  (exemple)
------------- house1.tmx
-------- combat/
------------ combat1.tmx
-- music/
----- bgmusic.wav
-- soundfx/
----- swordshit.wav

### entities_anims/X.tmx

Visuel de l'entité X, sous forme de map.

Une entité n'existe pas dans le jeu si elle n'existe pas dans ce dossier .

Pour chaque map, donc chaque entité, on définit :
- Son nom ***displayName*** qui sera son nom in-game.
- type/class : définit le type d'entité (Character, Enemy, Obstacle...)
 - ... jsp 

Définit surtout toutes ces animations.
Chaque ligne de la map est une animation.
On définit sur chaque 1ère tile de la ligne l'animation à laquelle elle correspond(ex : ***animName = "idle"*** sur la 1ere ligne).

Propriété globale :00
- ***fps***

### attacks/X.tmx

Définit les propriétés (globales sur la map) et le pattern de l'attaque.
Propriétés de la map :
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

### AI

Refaire la logique de l'ia pour que les décisions osit liées et non pas indépendantes

# pseudo code collisions

On fonctionne avec 2 rectangle, la hitbox et la projection de la hitbox (on déplace la hitbox uniquement si sa projection overlappe rien)

targetX = currentX + relativeMove.x
targetY = currentY + relativeMove.y

for (i = floor(startX), i < ceil(endX), i++)
    for (j= floor(startY), j < ceil(endY), j++>)
        if (!map.isWalkable(... (i,j)))
            return false

for (e: entities) {
    if !e.equals(this) 
        if e.overlappswith(next_hitbox)
            return false.

setPosition(next_hitbox.x, next_hitbox.y)
}

créer nouvelle fonction collides with avec en paramètre le nouveau rectangle