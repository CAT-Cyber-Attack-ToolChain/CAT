default: all

all: dir adapter attack_graph metrics

dir:
	mkdir bin

adapter:
	mkdir bin/adapter
	(cd src/adapter; make; make install)

attack_graph:
	(cd src/attack_graph; make install)

metrics:
	mkdir bin/metrics
	(cd src/metrics; make; make install)

clean:
	(cd src/attack_graph; make clean)
	(cd src/adapter; make clean)
	(cd src/metrics; make clean)
	rm -rf bin
