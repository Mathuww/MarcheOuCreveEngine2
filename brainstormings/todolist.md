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

- son musique + effets sonores

# BUGS
- !!! Bug du HUD au changement de map
- !!!! La prévisualisation des attaques reste affichée alors que l'ennemi n'est pas/plus sélectionné à ce moment-là*
- !!!!! Les enemies se déplacent BCPPPPPP trop, car ils comptent la distance à vol d'oiseau
- Carré violet qui apparait 1 frame quand on lance un projectile (ca doit avoir un rapport avec l'ordre des opérations au lancement du projectile)
- !!! Ca crash quand on quitte mdr (It's a feature, not a bug)
- !!!!!! Quand un game state exit() son inputPressed unregister pas du event bus
- !!!! Les trous dans les attaques

# FEATURES
- ??? Faire HUD quand les ennemies gagnes ("[en petit :: you are] dead") + la gestion pour recommencer la map
- ??? Faire HUD quand les alliés gagne ("Walk [en petit :: or die]")
- !! Différencier corps-à-corps et attaque à dist
- ! Prévisualiser moves ennemies
- !!!! Capacité spé : intégration graphique
- ????? Mode de jeu adapté à la situation
- ???? Allié > expl. player à la fin d'un combat
- ??? Alliés au début d'un combat
- !? Son