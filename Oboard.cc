#include "Oboard.hh"

const int order[8][2] = {{0,-1},{1,-1},{1,0},{1,1},{0,1},{-1,1},{-1,0},{-1,-1}};

std::function<bool(board*,int,int)> isFilled = [](board* b, int x, int y) {
  return (b->filled[y] & (0b10000000 >> x)) != 0;
};

std::function<bool(board*,int,int)> isWhite = [](board* b, int x, int y) {
  return (b->coloredWhite[y] & (0b10000000 >> x)) != 0;
};

std::function<void(board*,int,int)> flipToWhite = [](board* b, int x, int y) {
  b->filled[y] = b->filled[y] | (0b10000000 >> x);
  b->coloredWhite[y] = b->coloredWhite[y] | (0b10000000 >> x);
};

std::function<void(board*,int,int)> flipToBlack = [](board* b, int x, int y) {
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
  return out;
}

int score (board* b) { //rework later
  int s = 0;
  for (int x = 0; x < 8; x++) {
    for (int y = 0; y < 8; y++) {
      if (isFilled(b,x,y) && isWhite(b,x,y)) s++;
    }
  }
  return s;
}

int tileNum (board* b) {
  int s = 0;
  for (int x = 0; x < 8; x++) {
    for (int y = 0; y < 8; y++) {
      if (isFilled(b,x,y)) s++;
    }
  }
  return s-4;
}

board* move(board* b, bool playWhite, int x, int y) {
  if (x > 7 || x < 0 || y > 7 || y < 0) return nullptr;
  if (isFilled(b,x,y)) return nullptr;
  board* c = new board;
  copy(b,c);
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
  } //checked all rays and popilated toFlip

  bool legal = false;
  for (int i : toFlip) { //any legal flips from playing here?
    if (i != 0) {
      legal = true;
      break;
    }
  }

  if (legal) {
    for (int r = 0; r < 8; r++) { //rays, must be at least 1 that is > 0
      for (int d = 0; d < toFlip[r]; d++) { //above calced length, start 0 to flip x,y (happens more than once)
        if (playWhite) flipToWhite(c, x + (d*order[r][0]), y + (d*order[r][1])); //update board c according to color
        else flipToBlack(c, x + (d*order[r][0]), y + (d*order[r][1]));
      }
    }
    return c;
  } else { //not legal
    delete c;
    return nullptr;
  }
}

void copy(board* from, board* to) {
  for (int i = 0; i < 8; i++) {
    to->filled[i] = from->filled[i];
    to->coloredWhite[i] = from->coloredWhite[i];
  }
}

std::string toString(board* b) {
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
    out += '\n';
  }
  //out += esc + "[0m";
  out.push_back(esc);
  out += "[0m";
  return out;
}
