-----

## 🇫🇷 Documentation en Français 🇫🇷

### ⚓ **Documentation Complète : Battleship Premium V2** ⚓

#### 🚀 **1. Introduction & Présentation**

Bienvenue dans la documentation de **Battleship Premium V2**, une version moderne et riche en fonctionnalités du jeu de bataille navale classique. Ce projet a été développé avec soin en utilisant Java et Swing pour l'interface graphique, offrant une expérience de jeu fluide et visuellement agréable. Le jeu intègre une intelligence artificielle pour l'adversaire, un système de niveaux progressifs, et la persistance des meilleurs scores pour encourager la compétition. 🎮

Ce document a pour but de vous fournir toutes les informations nécessaires pour comprendre, utiliser et potentiellement étendre ce projet.

#### 📧 **2. Auteur & Contact**

Ce projet a été imaginé et développé par :

  * **Nom** : TechNerdSam (Samyn-Antoy ABASSE)
  * **Email** : samynqntoy@gmail.com

Pour toute question, suggestion ou proposition de collaboration, n'hésitez pas à prendre contact via l'adresse e-mail ci-dessus.

#### ✨ **3. Fonctionnalités Clés**

  * **Interface Graphique Moderne** 🎨 : Une interface soignée avec des dégradés, des polices personnalisées et des animations pour une expérience immersive.
  * **Placement de Navires Interactif** 🚢 : Les joueurs peuvent placer leurs navires sur la grille de manière intuitive, avec une aide visuelle pour la validité du positionnement (vert si possible, rouge sinon) et une option de rotation avec la touche 'R'.
  * **Système de Niveaux** 📈 : La difficulté augmente progressivement. Le nombre de navires à affronter augmente avec les niveaux, jusqu'à un maximum de 5 niveaux.
  * **Intelligence Artificielle (IA) Avancée** 🤖 : L'ordinateur dispose d'un mode "recherche" (tirs aléatoires) et d'un mode "chasse" plus agressif qui cible les cases adjacentes après un tir réussi.
  * **Meilleurs Scores** 🏆 : Le jeu enregistre les 10 meilleurs scores dans un fichier `highscores_premium.json`.
  * **Sauvegarde Automatique** 💾 : Les meilleurs scores sont automatiquement sauvegardés à la fin d'une partie si le score est suffisant.

#### 📂 **4. Structure du Projet**

Le projet est organisé autour des fichiers suivants :

  * `BattleshipPremiumV2.java` : Le cœur de l'application. Ce fichier unique contient toutes les classes nécessaires au fonctionnement du jeu.
  * `highscores_premium.json` : Fichier de données au format JSON qui stocke les meilleurs scores des joueurs.

#### 💻 **5. Guide d'Installation et de Lancement**

Pour compiler et lancer le jeu, vous avez besoin de :

  * Un **JDK (Java Development Kit)**.
  * Un terminal ou un environnement de développement intégré (IDE).

**Étapes pour lancer le jeu :**

1.  **Compilation** : Ouvrez un terminal, naviguez jusqu'au dossier contenant le projet et exécutez la commande :
    ```bash
    javac bataille-navale/BattleshipPremiumV2.java
    ```
2.  **Exécution** : Une fois la compilation terminée sans erreur, lancez le jeu avec la commande :
    ```bash
    java bataille-navale.BattleshipPremiumV2
    ```
3.  **Jouer** : La fenêtre du jeu devrait maintenant s'ouvrir, affichant le menu principal. 🎉

#### 룰 **6. Règles du Jeu et Gameplay**

1.  **Menu Principal** : Au lancement, vous pouvez choisir de commencer une "Nouvelle Partie", de consulter les "Meilleurs Scores" ou de "Quitter".
2.  **Placement des Navires** : Vous devez placer votre flotte sur votre grille. La taille et le nombre de navires dépendent du niveau actuel.
      * Déplacez la souris sur la grille pour choisir l'emplacement.
      * Appuyez sur la touche **'R'** pour faire pivoter le navire.
      * Cliquez pour placer un navire. Une aide visuelle (fantôme de navire) vous indique si le placement est valide.
3.  **Phase de Combat** : À tour de rôle, vous et l'ordinateur tirez sur la grille de l'adversaire.
      * Cliquez sur une case de la "Grille Ennemie" (celle du haut) pour tirer.
      * Un tir réussi est marqué en rouge (Touché \! 💥), un tir manqué par un point blanc (Manqué \! 🌊).
4.  **Fin de la Partie** :
      * **Victoire** : Si vous coulez tous les navires de l'ordinateur, vous gagnez le niveau et votre score augmente. Si vous terminez le niveau 5, vous gagnez la partie.
      * **Défaite** : Si l'ordinateur coule tous vos navires, la partie est terminée (GAME OVER 💀).
      * **Sauvegarde du Score** : Si votre score final est assez élevé pour entrer dans le top 10, vous serez invité à entrer votre nom.

#### 🏛️ **7. Architecture du Code**

Le code est structuré en plusieurs classes internes imbriquées, chacune avec un rôle précis.

  * `BattleshipPremiumV2` (Classe principale) : Hérite de `JFrame` et gère les différents écrans (panneaux) via un `CardLayout`.
  * `GameState` (Enum) : Définit les différents états du jeu (`MAIN_MENU`, `SHIP_PLACEMENT`, `PLAYING`, `GAME_OVER`, etc.) pour une gestion claire de la logique d'affichage.
  * `GameEngine` : C'est le cerveau du jeu. Il gère la logique des tours, le score, les niveaux, l'IA et l'état de la partie.
  * `Board` : Représente une grille de jeu (10x10) et contient une liste des navires ainsi qu'un tableau de caractères pour suivre l'état de chaque case (`~` pour eau, `S` pour navire, `H` pour touché, `M` pour manqué).
  * `Ship` : Modélise un navire avec sa taille, son type, sa position, et son état (nombre de fois touché).
  * `HighScoreManager` & `HighScoreEntry` : Gèrent la lecture, l'écriture et le tri des meilleurs scores.
  * `Theme` : Classe interne qui contient toutes les constantes esthétiques (couleurs, polices, dimensions), facilitant la personnalisation.

#### 🏆 **8. Système de Meilleurs Scores**

Le système de highscore est conçu pour être persistant et compétitif.

  * **Stockage** : Les scores sont stockés dans le fichier `highscores_premium.json`. Chaque entrée contient un nom de joueur (`playerName`) et un score (`score`).
  * **Logique** : La classe `HighScoreManager` charge ces scores au démarrage. À la fin d'une partie, si le score du joueur est suffisant pour entrer dans le top 10, il est ajouté à la liste, qui est ensuite triée et sauvegardée.
  * **Conditions** : Un score est considéré comme un "highscore" s'il est supérieur au score le plus bas du top 10, ou si le top 10 n'est pas encore rempli. Seuls les 10 meilleurs scores sont conservés.

-----

## 🇬🇧 English Documentation 🇬🇧

### ⚓ **Complete Documentation: Battleship Premium V2** ⚓

#### 🚀 **1. Introduction & Overview**

Welcome to the documentation for **Battleship Premium V2**, a modern, feature-rich version of the classic Battleship game. This project was carefully developed using Java and Swing for the graphical user interface, offering a smooth and visually pleasing gaming experience. The game includes an artificial intelligence for the opponent, a progressive level system, and high score persistence to encourage competition. 🎮

This document aims to provide you with all the necessary information to understand, use, and potentially extend this project.

#### 📧 **2. Author & Contact**

This project was conceived and developed by:

  * **Name**: TechNerdSam (Samyn-Antoy ABASSE)
  * **Email**: samynqntoy@gmail.com

For any questions, suggestions, or collaboration proposals, feel free to get in touch via the email address above.

#### ✨ **3. Key Features**

  * **Modern GUI** 🎨: A polished interface with gradients, custom fonts, and animations for an immersive experience.
  * **Interactive Ship Placement** 🚢: Players can intuitively place their ships on the grid, with visual feedback on placement validity (green for valid, red for invalid) and a rotation option using the 'R' key.
  * **Level System** 📈: The difficulty increases progressively. The number of ships to face increases with each level, up to a maximum of 5 levels.
  * **Advanced Artificial Intelligence (AI)** 🤖: The computer opponent has two modes: a "search" mode (random shots) and a more aggressive "hunt" mode that targets adjacent cells after a successful hit.
  * **High Scores** 🏆: The game saves the top 10 high scores in a `highscores_premium.json` file.
  * **Automatic Save** 💾: High scores are automatically saved at the end of a game if the score is high enough.

#### 📂 **4. Project Structure**

The project is organized around the following files:

  * `BattleshipPremiumV2.java`: The core of the application. This single file contains all the necessary classes for the game to function.
  * `highscores_premium.json`: A JSON data file that stores the players' high scores.

#### 💻 **5. Setup and Launch Guide**

To compile and run the game, you need:

  * A **JDK (Java Development Kit)**.
  * A terminal or an Integrated Development Environment (IDE).

**Steps to launch the game:**

1.  **Compilation**: Open a terminal, navigate to the project directory, and run the command:
    ```bash
    javac bataille-navale/BattleshipPremiumV2.java
    ```
2.  **Execution**: Once the compilation is successfully completed, launch the game with the command:
    ```bash
    java bataille-navale.BattleshipPremiumV2
    ```
3.  **Play**: The game window should now open, displaying the main menu. 🎉

#### 룰 **6. Game Rules & Gameplay**

1.  **Main Menu**: On startup, you can choose to start a "New Game" (Nouvelle Partie), view the "High Scores" (Meilleurs Scores), or "Quit" (Quitter).
2.  **Ship Placement**: You must place your fleet on your grid. The size and number of ships depend on the current level.
      * Move the mouse over the grid to choose a location.
      * Press the **'R'** key to rotate the ship.
      * Click to place a ship. A visual aid (a ghost ship) indicates if the placement is valid.
3.  **Combat Phase**: You and the computer take turns firing at the opponent's grid.
      * Click on a cell in the "Enemy Grid" (the top one) to fire.
      * A successful hit is marked in red (Hit\! 💥), while a miss is marked with a white dot (Miss\! 🌊).
4.  **End of Game**:
      * **Victory**: If you sink all the computer's ships, you win the level, and your score increases. If you complete level 5, you win the game.
      * **Defeat**: If the computer sinks all your ships, the game is over (GAME OVER 💀).
      * **Score Saving**: If your final score is high enough to make the top 10, you will be prompted to enter your name.

#### 🏛️ **7. Code Architecture**

The code is structured into several nested inner classes, each with a specific role.

  * `BattleshipPremiumV2` (Main Class): Inherits from `JFrame` and manages the different screens (panels) using a `CardLayout`.
  * `GameState` (Enum): Defines the different possible states of the game (`MAIN_MENU`, `SHIP_PLACEMENT`, `PLAYING`, `GAME_OVER`, etc.) for clear management of the display logic.
  * `GameEngine`: This is the brain of the game. It handles turn logic, scoring, levels, AI, and the game state.
  * `Board`: Represents a game grid (10x10) and contains a list of ships and a 2D char array to track the state of each cell (`~` for water, `S` for ship, `H` for hit, `M` for miss).
  * `Ship`: Models a ship with its size, type, position, and status (hit count).
  * `HighScoreManager` & `HighScoreEntry`: Manage the reading, writing, and sorting of high scores.
  * `Theme`: An inner class that holds all aesthetic constants (colors, fonts, dimensions), making customization easy.

#### 🏆 **8. High Score System**

The high score system is designed to be persistent and competitive.

  * **Storage**: Scores are stored in the `highscores_premium.json` file. Each entry contains a player name (`playerName`) and a score (`score`).
  * **Logic**: The `HighScoreManager` class loads these scores on startup. At the end of a game, if the player's score is high enough to enter the top 10, it is added to the list, which is then sorted and saved.
  * **Conditions**: A score is considered a "high score" if it is higher than the lowest score in the top 10, or if the top 10 is not yet full. Only the top 10 scores are kept.
