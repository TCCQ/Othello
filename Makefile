ARGS = -std=c++11 

all: Oboard.o Othe.o othe
clean:
	rm *.o othe

Oboard.o : Oboard.cc Oboard.hh
	g++ ${ARGS} -c Oboard.cc
Othe.o : Oboard.hh Othe.cc
	g++ ${ARGS} -c Othe.cc
othe : Oboard.o Othe.o
	g++ ${ARGS} Oboard.o Othe.o -o othe
