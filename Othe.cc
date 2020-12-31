#include <iostream>
#include "Oboard.hh"
#include <queue>

#define MAXQDEPTH 5
//only use one of the above?

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
      if (n->best < b) b = n->best; //sign fliped cause cpu plays black
      if (n->worst > w) w = n->worst;
    }
    root->best = b;
    root->worst = w;
  }
}

node* build (board* brd, int d) {
  if (d >= MAXQDEPTH) {
    delete brd;
    return nullptr;
  }
  node* n = new node;
  n->item = brd;
  n->depth = d;
  n->best = 9999;
  n->worst = -9999;
  n->children = std::vector<node*>();

  for (board* b : children(brd, (tileNum(brd)%2 == 0))) {
    node* adn = build(b,d+1);
    if (adn != nullptr) n->children.push_back(adn);
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

int main() {
//player is white
  int pm[2];
  board* b = nb();
  board* d;
  std::cout << toString(b) << std::endl;
  
  while (tileNum(b) < 60) {
    getPlayerMove(pm);
    d = move(b,true,pm[0],pm[1]);

    while (d == nullptr && (pm[0] != -1 && pm[1] != -1)) {
      std::cout << "Illegal, try again. -1,-1 if no legal moves" <<std::endl;
      getPlayerMove(pm);
      d = move(b,true,pm[0],pm[1]);
    } 
    if (pm[0] == -1 && pm[1] == -1) {
      std::cout << "Gave up, white # was " << score(b) << std::endl;
      return 0;
    }
    delete b;
    b = d;
    //finished player turn
    
    //std::cout << toString(b) << std::endl;
    //start cpu turn
    /*
    int m = 0;
    while (m < 63) {
      if ((d = move(b,false,m%8,m/8)) != nullptr) {
        delete b;
        b = d;
        std::cout << std::to_string(m%8) << " " << std::to_string(m/8) << std::endl;
        break;
      }
      m++;
    }
    */
    d = bestMove(b);
    
    if (d != nullptr) {
      delete b;
      b = d;
    } else {
      std::cout << "No legal moves" << std::endl;
      if (score(b) > tileNum(b)-score(b)) std::cout << "White wins";
      else std::cout << "Black wins";
      std::cout << std::endl;
      return 0;
    }

    std::cout << toString(b) << std::endl;
    //end cpu turn
  }

  if (score(b) > tileNum(b)-score(b)) std::cout << "White wins";
  else std::cout << "Black wins";
  std::cout << std::endl;
  return 0;
}


