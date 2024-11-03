# Lost in Wonderland

## Game Description

*Lost in Wonderland* is an adventurous jump-and-run game created as part of the 2022 Multimedia Exercise class. The game follows a young boy who dreams of being an adventurer. After accidentally falling into an underground cave, he must find his way back to his family by navigating through a dangerous Wonderland filled with enemies and obstacles. The game includes two levels with different difficulties, providing players with a challenging yet engaging experience. Through virtual touchscreen buttons, players can control the character’s movements and help him reach the goal, avoiding dangers along the way. 

This project was developed using the **Android SDK** and adheres to the requirements outlined in the course, such as implementing a 2D arcade-style gameplay with persistent high scores, an intuitive GUI, and multi-language support. The game leverages the **Android Studio IDE** and runs on an **Android Virtual Device (AVD)**, specifically optimized for Nexus 5X settings.

**Story**  
Once upon a time, there was a boy whose dream was to be an adventurer. One day, he fell into an underground cave but managed to survive. To return to his family, he must find the exit, but dangers lurk in this treacherous Wonderland.

**Gameplay**  
Lost in Wonderland is a jump-and-run game where the player’s objective is to escape from the game environment. There are two levels with varying difficulty. The player has three lives, which are lost by falling off platforms or touching monsters.

**Controls**  
The game is controlled through virtual buttons on the touchscreen, allowing the character to move forward, backward, and jump.

## How to Play

To play this game, use the virtual buttons on the screen:

- **Left Arrow**: Move left
- **Right Arrow**: Move right
- **Upper Arrow**: Jump in the last moved direction (default: right)

### Game Rules

- You have **three lives**. A life is lost each time you fall off a platform or collide with an enemy.
- Complete the game by reaching and touching the **goal**.
  
---

## Features

- **Touchscreen Controls**: Virtual buttons enable character movement.
- **Game Loop & Canvas**: For frame-independent gameplay.
- **Intro Animation**: Video/animation with an option to skip.
- **Main Menu**: Start game, view tutorial, adjust settings, view intro video, and check high scores.
- **Animated Splash Screens**: Intro and after-game screens are animated.
- **Sprite Animations**: Background character animations are implemented through sprites.
- **Pause Feature**: Pause the game anytime with a button.
- **Audio Controls**: Toggle music on/off, which pauses with the game.
- **High Scores**: Viewable in the main menu (shows level completion times).
- **Multiple Levels**: Two levels with different difficulties.
- **Multiple Lives**: Three lives are given; lives are lost by falling or colliding with enemies.
- **Help Dialog**: Tutorial viewable in the main menu.
- **Language Selection**: Switch between English and German in settings.

## Technical Implementation

The game is developed using **Android Studio** with the **Android SDK**. It follows the course requirements, including using the **Room Persistence Library** for high scores, **multi-threading** for the game loop, and persistent storage for game data. The game leverages activities and intents for navigation, while animations and sprites are implemented to create an engaging visual experience.

### Game Flow:
1. Splash Screen
2. Intro Video (skippable)
3. Main Menu (options to start game, view tutorial, check high scores, rewatch intro video, or adjust settings)
4. Select Level (easy or hard mode)
5. Game Play
6. After Game (replay, check high scores, or return to main menu)

## Code Architecture

The project is organized into different modules:

### Activities
- **MenuActivity**: Main hub for navigating to other Activities (game selection, help, high score, video, settings).
- **GameActivity**: Core game screen with game layout and connection to `GameSurfaceView`.
- **SelectGameActivity**: Level selection screen (easy or hard mode).
- **AfterGameActivity**: End screen with options to replay, view high scores, or return to main menu.
- **SettingsActivity**: Adjust language between English and German.
- **HelpActivity**: Tutorial explaining game mechanics.
- **HighScoreActivity**: Displays times required to complete levels.
- **VideoActivity**: Shows intro video, sends user to MenuActivity afterwards.

### Game Components
- **GameSurfaceView**: Displays game content and starts the GameLoop, manages assets, and initiates character and platform models.
- **GameLoop**: Implements game mechanics and rendering.
- **GameSound**: Encapsulates background music and other sounds.
- **GameGraphic**: Initializes graphics for characters, platforms, and background elements.

### Objects
- **DynamicObject**: Functions for moving objects (e.g., main character).
- **StaticObject**: Functions for static objects (e.g., virtual buttons, background).
- **SpriteObject**: Functions for sprite animations.

### Persistence
- **Score**: Entity for storing level completion times.
- **ScoreDao**: Data transfer object for score retrieval.
- **ScoreRoomDatabase**: Database storing high scores.

### Utilities
- **Concurrency**: Manages high score saving.
