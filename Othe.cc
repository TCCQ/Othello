#include <iostream>
#include "Oboard.hh"
#include <queue>
#include <cmath>

int MAXQDEPTH;

void getPlayerMove(int* a) { //takes x,y or x, y
  std::cout << "X coor: ";
  std::cin >> a[0];
  std::cout << "Y coor: ";
  std::cin >> a[1];
}

struct node {
  board* item;
  std::vector<node*> children;
  int best;
  int worst;
  int depth;
};

void bestWorst (node* root) {
  if (root->children.empty()) {
    root->best = score(root->item);
    root->worst = score(root->item);
  } else {
    int b,w;
    b = 9999;
    w = -9999;
    for (node* n : root->children) {
      if (n->best == 9999 || n->worst == -9999) {
        bestWorst(n);
      }
      if (n->best < b) b = n->best; //sign fliped cause cpu plays black. best case: cpu smart, player dumb
      if (whitesTurn(n->item)) { //worst case: cpu smart, player smart. IF PLAYER JUST PLAYED
        if (n->worst > w) w = n->worst;
      } else { //CPU JUST PLAYED. should _always_ take the better move, even in worst case calculations
        if (n->best < b) w = n->worst;
      }
    }
    root->best = b;
    root->worst = w;
  }
}

node* build (board* brd, int d) {
  if (d > MAXQDEPTH) {
    delete brd;
    return nullptr;
  }
  node* n = new node;
  n->item = brd;
  n->depth = d;
  n->best = 9999;
  n->worst = -9999;
  n->children = std::vector<node*>();

  std::vector<board*> cdrn = children(brd, whitesTurn(brd));
  if (!cdrn.empty()) {
    for (board* b : cdrn) {
      node* adn = build(b,d+1);
      if (adn != nullptr) n->children.push_back(adn);
    }
  } else { //catch if no legal moves
    for (board* b : children(brd, !whitesTurn(brd))) {
      node* adn = build(b,d+1);
      if (adn != nullptr) n->children.push_back(adn);
    }
  }
  return n;
}

void delall (node* root) {
  for (node* n : root->children) {
    delall(n);
  }
  delete root->item;
  delete root;
}

board* bestMove (board* current) {
  node* root = build(current,0);
  bestWorst(root);
  //do the dumb thing, pick with the best worst move
  node* c = root->children[0];
  for (node* n : root->children) {
    if (n->worst < c->worst) c = n;
  }
  board* out = new board;
  copy(c->item, out);
  for (node* n : root->children) {
    delall(n);
  }
  return out;
}

int main(int argc, char* argv[]) {
//player is white

  //arguments and defaults
  int index = 1;
  bool displayTypeBig = true;
  MAXQDEPTH = 4; 
  while (index < argc) {
    if (std::string(argv[index]) == "-c") displayTypeBig = false; //compact mode
    if (std::string(argv[index]) == "-q") {
      if (argc <= index + 1) {
        std::cout << "Illegal arguments" << std::endl;
        return 1;
      } else {
        MAXQDEPTH = std::stoi(argv[index+1]);
        index++;
      }
    }
    index++;   
  }
  int pm[2];
  board* b = nb();
  board* d;
  int turnCase = 0;
  std::cout << toString(b,displayTypeBig) << std::endl;
  
  while (tileNum(b) < 64) {
    if (anyLegalMoves(b,true)) { //white can play
      getPlayerMove(pm);
      d = move(b,true,pm[0],pm[1]);

      while (d == nullptr) {
        std::cout << "Illegal, try again." <<std::endl;
        getPlayerMove(pm);
        d = move(b,true,pm[0],pm[1]);
      } 
      delete b;
      b = d;
    } else if (tileNum(b) != 64){
      turnCase = 1;
    }
    //finished player turn
    
    if (score(b) == std::abs(tileNum(b))) {
      std::cout << toString(b,displayTypeBig) << std::endl;
      std::cout << "White wins" << std::endl;
      return 0;
    }

    //start cpu turn
    if (anyLegalMoves(b,false)) { //black can play
      d = bestMove(b);
      if (d != nullptr) {
        delete b;
        b = d;
      }
    } else if (tileNum(b) != 64){
      turnCase = 2;
    }
    //end cpu turn

    if (score(b) == std::abs(tileNum(b))) {
      std::cout << toString(b,displayTypeBig) << std::endl;
      std::cout << "Black wins" << std::endl;
      return 0;
    }

    std::cout << toString(b,displayTypeBig) << std::endl;
    if (turnCase != 0) {
      if (turnCase == 1) {
        std::cout << "White had no legal moves, Black goes again" << std::endl;
      } else {
        std::cout << "Black had no legal moves, White goes again" << std::endl;
      }
      turnCase = 0;
    }
  }

  if (score(b) > 0) std::cout << "White wins";
  else std::cout << "Black wins";
  std::cout << std::endl;
  return 0;
}


