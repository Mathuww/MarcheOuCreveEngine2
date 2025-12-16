- render les tiles ou on peut shooter dans Aim

- ia (Eloi)
    - Il faut que je rajoute dans isCorrect Trajectory le fait qu'on puisse tire depuis une case adjacente si on est caché derrière un bloc
    - Il faut continuer l'implémentation de l'ia, cad l'appel depuis game et le déplacements de tous les enneis, plus la gestion d'états qui va avec
    - Différencier la simulation et le test de la trajectoire

- résoudre ce truc (ca crash tout le temps) :
Exception in thread "main" java.lang.NullPointerException: Cannot invoke "java.lang.Runnable.run()" because the return value of "java.util.List.get(int)" is null
        at com.walk.or.die.engine.ui.MCUICarousel.validate(MCUICarousel.java:156)
        at com.walk.or.die.engine.ui.MCCharacterHUD.inputPressed(MCCharacterHUD.java:235)
        at com.walk.or.die.engine.shared.MCEventBus.emit(MCEventBus.java:188)
        at com.walk.or.die.engine.input.MCInputManager.keyDown(MCInputManager.java:282)
        at com.badlogic.gdx.InputEventQueue.drain(InputEventQueue.java:58)
        at com.badlogic.gdx.backends.lwjgl3.DefaultLwjgl3Input.update(DefaultLwjgl3Input.java:190)
        at com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window.update(Lwjgl3Window.java:414)
        at com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application.loop(Lwjgl3Application.java:181)
        at com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application.<init>(Lwjgl3Application.java:153)
        at com.walk.or.die.engine.lwjgl3.Lwjgl3Launcher.createApplication(Lwjgl3Launcher.java:1

- créer des Game states AlliesPlaying (actions via UI) et EnemiesPlaying (actions via IA) : on alterne entres les 2
on peut sortir dans la map quand tous les ennemis sont mort
on respawn si toues les allies sont morts
- pour les états (Allies/Enemies)Playing : Map<MCAlly/Enemy, boolean> pour savoir qui a déjà joué
    - (on init avec tous les ally/enemy présents à false et quand ils sont tous à true on passe au tour de l'autre camp)

- Portals pour pouvoir changer de map !
    - props : destMap avec le chemin dans maps/

- son musique + effets sonores
