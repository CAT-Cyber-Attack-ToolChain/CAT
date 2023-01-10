#!/bin/bash
cd /frontend/
cp docker.env .env
npm i
npm install -g serve
npm run build
serve -s build
