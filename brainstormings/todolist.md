- render les tiles ou on peut shooter dans Aim

- ia (Eloi)
    - Il faut que je rajoute dans isCorrect Trajectory le fait qu'on puisse tire depuis une case adjacente si on est caché derrière un bloc
    - Il faut continuer l'implémentation de l'ia, cad l'appel depuis game et le déplacements de tous les enneis, plus la gestion d'états qui va avec
    - Différencier la simulation et le test de la trajectoire

- créer des Game states AlliesPlaying (actions via UI) et EnemiesPlaying (actions via IA) : on alterne entres les 2
on peut sortir dans la map quand tous les ennemis sont mort
on respawn si toues les allies sont morts
- pour les états (Allies/Enemies)Playing : Map<MCAlly/Enemy, boolean> pour savoir qui a déjà joué
    - (on init avec tous les ally/enemy présents à false et quand ils sont tous à true on passe au tour de l'autre camp)

- Portals pour pouvoir changer de map !
    - props : destMap avec le chemin dans maps/

- son musique + effets sonores
