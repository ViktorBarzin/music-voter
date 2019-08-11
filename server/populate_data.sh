#!/bin/bash

curl -X POST -d 'username=gosho' musicvoter.viktorbarzin.me/api/users  &&\
curl -X POST -d 'username=pesho' musicvoter.viktorbarzin.me/api/users  &&\
curl -X POST -d 'name=test&owner_username=gosho&password=securepass' musicvoter.viktorbarzin.me/api/rooms &&\
curl -X POST -d 'add_vote=true&url=https://www.youtube.com/watch?v=k5ek9AjyrNc&username=gosho' musicvoter.viktorbarzin.me/api/vote/test
