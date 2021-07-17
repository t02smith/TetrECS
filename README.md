
# TetrECS

TetrECS is a tetris-esqe game built in JavaFX as part of my coursework for COMP1206. \

## Game Modes

TetrECS has three main gamemodes available to the user:

```txt
NOTE: The multiplayer and online scoreboard section relies on the TetrECS server,
which users outside of the University of Southamtpon will not be able to access. 
The code for this server is to be added to this repo eventually.
```

### Singleplayer

The singleplayer section is where the main game loop is presented to the user.
All the other game modes will in some way present this to the users. The rules
for TetrECS are as follows:

```txt
- The user is presented with a 5x5 grid, in which they are tasked with placing
  pieces on to fill up rows and columns to gain points.
- When a row/column is full, the tiles disappear allowing the user to place 
  more pieces on the board.
- The user has access to two pieces at a time, which they can switch between
  and rotate to fit onto the board.
- If the user cannot place the piece in the given time, then they will lose
  a life and the game will end upon losing all 3 lives.
- The time the user has to place the piece decreases as the game goes on.
```

Upon completing a game, the user will be able to save their score to a scoreboard.
The user will be able to submit their score to the online scoreboard to be viewed
by TetrECS players on other devices.

### Multiplayer

The multiplayer section can be broken down into several different components.

#### Joining a Lobby

The user will initially be presented with the option to join any lobbies that are
currently active by clicking on them on the lobby screen. The user will also be able
to create their own lobby for other users to join.

#### In a Lobby

When the user is inside a lobby they will be able to chat with the other users
in the lobby with them. The messaging has several different markdown stylings
available to the user:

```txt
** Bold
++ Italic
-- Strikethrough
__ Undeline
```

These stylings can be used by wrapping the desired text in between the given identifiers.

The user will also be able to change their nickname in the lobby so users can more
easily identify themselves. Any nickname changes will be visible to all users in
the lobby.

When the host is ready, they can start the game.

#### In Game

When the user is in-game, they will be presented with the same gameloop as they would
be in the singleplayer gamemode. Except in the multiplayer there is a new section on
the left hand side of the screen. This section can be toggled through and includes:

- Scoreboard \
  The list of users and their current scores from highest to lowest.
- Chat \
  A chat window for users to communicate in mid-game that functions just as it did
  in the lobby.
- Other User's Boards \
  Will display the boards of all other users in real time as they place pieces.

A multiplayer game ends when all users have lost all of their lives.

### Power-Up

The third game mode is a special version of the singleplayer game mode that introduces
special powerups that the user can use during their game. Each powerup will cost a certain
amount of points that will double upon use. The points are simply equal to the in-game score
but once spent cannot be gotten back. However using powerups does not affect your overall score.

Some examples of powerups include:

- Push Down
  Push all pieces on the board as far down as possible
- Nuke
  Clear the board of all pieces without gaining any points
- New Piece
  Gain a new piece to replace your current piece
- ...
