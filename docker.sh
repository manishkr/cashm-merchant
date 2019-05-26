git pull

sbt docker:publishLocal

docker tag cashm-merchant:0.1 recash-merchant:latest

docker-compose up -d
