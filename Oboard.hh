#include <string>
#include <functional>
#include <vector>

struct board {
  char filled[8];
  char coloredWhite[8];
  bool whitesTurn;//wasteful?
};

board* nb();
int score(board*);
board* move(board*,bool,int,int);
int tileNum(board*);
void copy(board*, board*);
std::string toStringSmall(board*);
std::string toStringBig(board*);
std::string toString(board*);
std::string toString(board*, bool);
std::vector<board*> children(board*, bool);
bool whitesTurn(board*);
bool isLegal(board*, bool, int, int);
bool anyLegalMoves(board*, bool);
