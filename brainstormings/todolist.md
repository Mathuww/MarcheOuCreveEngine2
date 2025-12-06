- render les tiles ou on peut shooter dans Aim

- ia (Eloi)
    - Il faut que je rajoute dans isCorrect Trajectory le fait qu'on puisse tire depuis une case adjacente si on est caché derrière un bloc
    - Il faut continuer l'implémentation de l'ia, cad l'appel depuis game et le déplacements de tous les enneis, plus la gestion d'états qui va avec
    - Différencier la simulation et le test de la trajectoire

- projectiles 
	- hurt : clignotement
    - dead : clignotement, fade out, anim. dead puis on reste sur la derniere frame de l'anim dead (c deja le cas en vrai)

- ui
- pour toutes les barres HP : vertes > 2/3, oranges 1-2/3, rouges < 1/3, grises (ligne fixe) quand morts
- barres HP : rectangle noir pour contour, au sein du rectangle ligne fixe (grise clair) et par dessus la ligne variable (rouge/orange/verte)
- sur terrain
    - pour les deux :
        - quand degats pris : clignotement + affichage des degats numériquement avec nuance de rouge proportionnelle aux HP totaux du character
    - ennemis: barre HP directement sur le terrain 
        - la plupart du temps au dessus d'eux, à adapter quand on est aux limites de la map bien sur (afficher en dessous du coup)
        - faire en sorte que deux barres d'HP d'ennemis ne s'overlap pas. ex : un est au bord sup. de la map donc ses HP s'affichent en dessous, un ennemi en dessous a son HP qui s'affiche dessus du coup ca s'overlap... idée : si juste a cote un ennemi est présent, on réduit la largeur de la barre d'HP à la largeur d'1 tile, et on affiche SUR l'ennemi, comme ca ca déborde pas et c'est clean
        - en survolant : indication numérique format 5/130
- batch & caméra séparées
    - allies : 3 (enfin le nombre d'alliés) AllyComponent qui affiches:
    - (format rectangle avec pixels quatres coins retirés, alignés horizontalement 1 ligne en bas de l'écran. définir NB MAX ALLIES pour pas tout péter si l'utilisateur fout 10 alliés)
      --------------------------------------------     --------------------------------------------   
    |  ----------      ELOI KIWITS    (V si joué) |  |  ----------      MATHEO OLYMPI  (V si joué) |
    |  | (anim)  |                                   |  | (anim)  |     
    |  |         |     @@@@@@@@----------------   |  |  |         |     @@@@@@@@@@@@@-----------   |
    |   ----------     HP 58/80                      |   ----------     HP 88/80
      --------------------------------------------     --------------------------------------------
        - le sprite actuel (donc ca jouera aussi l'anim)
        - le nom de l'allié
        - barre HP 
        - HP numériques format 58/130
        - contour de couleur diff pour chaque ?
        - indication si l'ally a déjà joué ou pas pour ce tour (genre contour vert + petite checkmark pixel art en haut à droite)
    - une fois un allié focus (en cliquant sur l'UI ou direct sur le terrain)
    - ActionChoiceComponent : on choisit Move, Attack ou Special et on va dans l'état qui correspond
        - pareil en tableau 2 lignes en bas qui remplacent le tableau des Allies
    - En haut à gauche (?) ptite indication discrete des fleches + "Move Camera" 

- créer des Game states AlliesPlaying (actions via UI) et EnemiesPlaying (actions via IA) : on alterne entres les 2
on peut sortir dans la map quand tous les ennemis sont mort
on respawn si toues les allies sont morts
- pour les états (Allies/Enemies)Playing : Map<MCAlly/Enemy, boolean> pour savoir qui a déjà joué
    - (on init avec tous les ally/enemy présents à false et quand ils sont tous à true on passe au tour de l'autre camp)

- panning souris + clic droit pour la caméra ? 

- Portals pour pouvoir changer de map !
    - props : destMap avec le chemin dans maps/

- son musique + effets sonores
