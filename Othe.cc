#include <iostream>
#include "Oboard.hh"


void getPlayerMove(int* a) { //takes x,y or x, y
  std::cout << "X coor: ";
  std::cin >> a[0];
  std::cout << "Y coor: ";
  std::cin >> a[1];
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
      std::cout << "Illegal, try again. -1,-1 to skip" <<std::endl;
      getPlayerMove(pm);
      d = move(b,true,pm[0],pm[1]);
    } 
    if (pm[0] != -1 && pm[1] != -1) {
      delete b;
      b = d;
    }
    
    //std::cout << toString(b) << std::endl;
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
    std::cout << toString(b) << std::endl;
  }

  return 0;
}


