#include <string>
#include <functional>
#include <vector>

struct board {
  char filled[8];
  char coloredWhite[8];
};

board* nb();
int score(board*);
board* move(board*,bool,int,int);
int tileNum(board*);
void copy(board*, board*);
std::string toString(board*);
std::vector<board*> children(board*, bool);
