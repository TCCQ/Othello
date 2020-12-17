ARGS = -g -std=c++11

all: Oboard.o Othe.o try
clean:
	rm *.o try

Oboard.o : Oboard.cc Oboard.hh
	g++ ${ARGS} -c Oboard.cc
Othe.o : Oboard.hh Othe.cc
	g++ ${ARGS} -c Othe.cc
try : Oboard.o Othe.o
	g++ ${ARGS} Oboard.o Othe.o -o try
