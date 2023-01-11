#!/bin/bash
cd /mulval
make
cd ../
cd /fullstack/backend/
cp docker.conf .conf
./gradlew run
