This Grava Hal implementation makes use of VAADIN.

Unfortunately, it turns out that this is not conductive to 'carefree cloning' of the project :(

Follow the following steps:

0. Use eclipse
1. Make sure the Vaadin plugin is installed in eclipse
2. Clone this project into your workspace
3. In eclipse, choose new java project, give the project the same name as the new dir step 2 yielded.
4. Instead of finish, choose next. Under libraries add IvyDE manually
5. finish; now edit project properties, under project facets (convert to facet form) and check Dynamic Web Module and Vaadin plugin
6. apply; under Deployment Assembly add (Java Build Path Entries) Ivy.

Whew.

How to play a game:
1. navigate to localhost:8080/gravahal
2. log in using any name you want, the password is always "SECRET"
3. repeat for a second window, now you can play against yourself
4. in the lobby each player can open one game, and join any number of open games. You are the start player iff you opened the game
5. You can either name your game, or leave the box open and then the game will be named "USER's game"
6. Navigation should be self explanatory

Rules of the game, as posed in the assignment:

"
Board Setup
Each of the two players has his six pits in front of him.
To the right of the six pits, each player has a larger pit, his Grava Hal.
In each of the six round pits are put six stones when the game starts.

Grava Hal Rules
Game Play
The player who begins with the first move picks up all the stones in anyone
of his own six pits, and sows the stones on to the right, one in each of the
following pits, including his own Grava Hal. No stones are put in the opponents'
Grava Hal. If the player's last stone lands in his own Grava Hal, he gets another
turn. This can be repeated several times before it's the other player's turn.

Capturing Stones
During the game the pits are emptied on both sides. Always when the last stone
lands in an own empty pit, the player captures his own stone and all stones in
the opposite pit (the other players' pit) and puts them in his own Grava Hal.


The Game Ends
The game is over as soon as one of the sides run out of stones. The player who
still has stones in his pits keeps them and puts them in his/hers Grava Hal. Winner
of the game is the player who has the most stones in his Grava Hal.
"

Note that the assignment doesn't explicitly say if sowing should be cyclic (it says
simply 'to the right') but cyclic is implied (since the alternative makes no sense).
It also doesn't explicitly say if opponents' pits are included when sowing. This
version supports both - change the value of the FULLSOWING constant to false/true
depending on your preferences.