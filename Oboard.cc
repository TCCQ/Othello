#include "Oboard.hh"

const int order[8][2] = {{0,-1},{1,-1},{1,0},{1,1},{0,1},{-1,1},{-1,0},{-1,-1}};

bool isFilled(board* b, int x, int y) {
  return (b->filled[y] & (0b10000000 >> x));
};

bool isWhite(board* b, int x, int y) {
  return (b->coloredWhite[y] & (0b10000000 >> x));
};

void flipToWhite(board* b, int x, int y) {
  b->filled[y] = b->filled[y] | (0b10000000 >> x);
  b->coloredWhite[y] = b->coloredWhite[y] | (0b10000000 >> x);
};

void flipToBlack(board* b, int x, int y) {
  b->filled[y] = b->filled[y] | (0b10000000 >> x);
  b->coloredWhite[y] = b->coloredWhite[y] & (~(0b10000000 >> x));
};

board* nb() {
  board* out = new board;
  for (int i = 0; i < 8; i++) {
    if (i < 5 && i > 2) {
      out->filled[i] = 0b00011000;
      out->coloredWhite[i] = (i==3)? 0b00001000:0b00010000;
    } else {
      out->filled[i] = 0;
      out->coloredWhite[i] = 0;
    }
  }
  out->turnAndTile = 0b10000100; //white's turn and 4 pieces on board
  out->score = 0;
  return out;
}

int score (board* b) { // # of white tiles - # black tiles
  return b->score;
}

int tileNum (board* b) {
  return (int)(b->turnAndTile & 0b01111111); //last seven bits
}

board* move(board* b, bool playWhite, int x, int y) {
  if (x > 7 || x < 0 || y > 7 || y < 0) return nullptr;
  if (isFilled(b,x,y)) return nullptr;
  int tmpx, tmpy, distance, end;
  int toFlip[8];
  for (int ray = 0; ray < 8; ray++) { //iter over cardinal + diagonals
    distance = 1;
    end = 0;
    while (end == 0) {
      tmpx = x + (distance*order[ray][0]);
      tmpy = y + (distance*order[ray][1]);

      if (tmpx < 0 || tmpx > 7 || tmpy < 0 || tmpy > 7) end = 1; //ran off edge
      else if (!isFilled(b,tmpx,tmpy)) end = 2; //ran into an empty space
      else if (isWhite(b,tmpx,tmpy) == playWhite) end = (distance > 1)? 3:4; //ran into own color late vs early
      else distance++;
    }
    if (end == 3) toFlip[ray] = distance;
    else toFlip[ray] = 0;
  } //checked all rays and populated toFlip

  bool legal = false;
  for (int i : toFlip) { //any legal flips from playing here?
    if (i != 0) {
      legal = true;
      break;
    }
  }

  if (legal) {
    int numFlipped = 0;
    board* c = new board;
    copy(b,c);
    for (int r = 0; r < 8; r++) { //rays, must be at least 1 that is > 0
      if (toFlip[r] != 0) numFlipped += toFlip[r]-1;
      for (int d = 1; d < toFlip[r]; d++) { //above calced length
        if (playWhite) flipToWhite(c, x + (d*order[r][0]), y + (d*order[r][1])); //update board c according to color
        else flipToBlack(c, x + (d*order[r][0]), y + (d*order[r][1]));
      }
    }
    if (playWhite) flipToWhite(c,x,y); //place new tile
    else flipToBlack(c,x,y);

    numFlipped = (2*numFlipped)+1; //account for placed tile and that score -1 for lost white and -1 from new black of each flip. thus 2*flip + 1
    char tn = (b->turnAndTile & 0b01111111) + 1;
    if (!playWhite) {
      tn = tn | 0b10000000; //change turn back. if just played white, then its B's turn and it can stay as a 0;
      c->score = b->score - numFlipped; //play black, score decreases
    } else {
      c->score = b->score + numFlipped; //play white, score increases
    }
    c->turnAndTile = tn;
    return c;
  } else { //not legal
    return nullptr;
  }
}

void copy(board* from, board* to) {
  for (int i = 0; i < 8; i++) {
    to->filled[i] = from->filled[i];
    to->coloredWhite[i] = from->coloredWhite[i];
  }
  to->turnAndTile = from->turnAndTile;
  to->score = from->score;
}

std::string toString(board* b, bool big) {
  if (big) return toStringBig(b);
  else return toStringSmall(b);
}

std::string toString(board* b) {
  return toString(b,false);
}

std::string toStringSmall(board* b) {
  std::string out = " 01234567\n";
  const char esc = 27;
  std::string fg, bg, tile;
  for (int y = 0; y < 8; y++) {
    out += std::to_string(y);
    for (int x = 0; x < 8; x++) {
      bg = (y%2 == x%2)? "42":"43";
      fg = (isWhite(b,x,y))? "37":"30";
      tile = (isWhite(b,x,y))? "W":"B";
      if (!isFilled(b,x,y)) tile = " ";
      //out += esc + "[" + fg + ";" + bg + "m" + tile;
      out.push_back(esc);
      out.push_back('[');
      out += fg;
      out.push_back(';');
      out += bg;
      out.push_back('m');
      out += tile;
    }
    out.push_back(esc);
    out += "[0m";
    out += std::to_string(y);
    out += '\n';
  }
  out += " 01234567\n";
  return out;
}

std::string toStringBig(board* b) {
  std::string out = "";
  const char esc = 27;
  std::string fg,bg,tile;
  std::string reset = "";
  reset.push_back(esc);
  reset.push_back('[');
  reset += "0m";

  out.push_back(esc);
  out += "[1;1H";
  out.push_back(esc);
  out += "[0J";
  out += "  0  1  2  3  4  5  6  7 \n";

  for (int y = 0; y < 8; y++) {
    out += " ";
    for (int c = 0; c < 8; c++) {
      bg = (c%2 == y%2)? "42":"43";
      out.push_back(esc);
      out.push_back('[');
      out += ";" + bg; //no fg
      out.push_back('m');
      out += "   ";
    }  
    out += reset + "\n";
    out += std::to_string(y);
    for (int x = 0; x < 8; x++) {
      bg = (y%2 == x%2)? "42":"43";
      fg = (isWhite(b,x,y))? "37":"30";
      if (isFilled(b,x,y)){
        tile = (isWhite(b,x,y))? "W":"B"; 
      } else tile = " ";
      out.push_back(esc);
      out.push_back('[');
      out += fg;
      out.push_back(';');
      out += bg;
      out.push_back('m');
      out += " " + tile + " ";
    }
    out += reset + std::to_string(y) + "\n";
    out += " ";
    for (int c = 0; c < 8; c++) {
      bg = (c%2 == y%2)? "42":"43";
      out.push_back(esc);
      out.push_back('[');
      out += ";" + bg; //no fg
      out.push_back('m');
      out += "   ";
    }  
    out += reset + "\n";
  }
  out += "  0  1  2  3  4  5  6  7 \n";
  return out;
}

std::vector<board*> children (board* b, bool playingWhite) {
  std::vector<board*> out = std::vector<board*>();
  board* d;
  for (int x = 0; x < 8; x++) {
    for (int y = 0; y < 8; y++) {
      if ((d = move(b,playingWhite,x,y)) != nullptr) {
        out.push_back(d);
      }
    }
  }
  return out;
}

bool whitesTurn(board* b) {
  return ((b->turnAndTile & 0b10000000) != 0);
}

bool isLegal(board* b, bool playWhite, int x, int y) { //slightly modified version of move()
  if (x > 7 || x < 0 || y > 7 || y < 0) return false;
  if (isFilled(b,x,y)) return false;
  int tmpx, tmpy, distance, end;
  for (int ray = 0; ray < 8; ray++) { //iter over cardinal + diagonals
    distance = 1;
    end = 0;
    while (end == 0) {
      tmpx = x + (distance*order[ray][0]);
      tmpy = y + (distance*order[ray][1]);

      if (tmpx < 0 || tmpx > 7 || tmpy < 0 || tmpy > 7) end = 1; //ran off edge
      else if (!isFilled(b,tmpx,tmpy)) end = 2; //ran into an empty space
      else if (isWhite(b,tmpx,tmpy) == playWhite) end = (distance > 1)? 3:4; //ran into own color late vs early
      else distance++;
    }
    if (end == 3) return true;
  } 

  return false;
}

bool anyLegalMoves(board* b, bool playWhite) {
  for (int x = 0; x < 8; x++) {
    for (int y = 0; y < 8; y++) {
      if (isLegal(b,playWhite,x,y)) return true;
    }
  }
  return false;
}
