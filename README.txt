This is a rewrite of my original java othello playing script.

This branch is written in C++ and is more memory efficient. It uses similar processes, however, so it may not be more efficient computationally. It is written to maximize the use of bitlevel opertations and minimize the size of each object to enable God's Algorithm style gameplay. My intent is explore as many possible gameplay branches (possible moves of the cpu and of the player) according to a breadth first search, and use a hueristic to determine which is the most desirable, and then guide the game along that branch when it is the cpu's turn. 

Feel free to steal this and credit me.

This branch is much more likely to be updated than main.

--Thomas Ulmer
