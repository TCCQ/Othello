#include <string>
#include <vector>

class Board {
  private:
    char filled[8];
    char coloredWhite[8];
    char turnAndTile; //first bit is isWhite'sTurn, last 7 are number of tiles on board
    signed char scoreInt;

    bool isFilled(int, int); 
    bool isWhite(int, int); 
    void flipToWhite(int, int); 
    void flipToBlack(int, int); 
  
  public:
    Board();
    Board(Board*);
    int score();
    Board* move(bool,int,int);
    int tileNum();
    void copy(Board*);
    std::string toStringSmall();
    std::string toStringBig();
    std::string toString();
    std::string toString(bool);
    std::vector<Board*> children(bool);
    bool whitesTurn();
    bool isLegal(bool, int, int);
    bool anyLegalMoves(bool);
};
