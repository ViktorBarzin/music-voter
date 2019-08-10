#!/bin/bash

curl -X POST -d 'username=gosho' musicvoter.viktorbarzin.me/api/users  &&\
curl -X POST -d 'username=pesho' musicvoter.viktorbarzin.me/api/users  &&\
curl -X POST -d 'name=test&owner_username=gosho&password=securepass' musicvoter.viktorbarzin.me/api/rooms &&\
curl -X POST -d 'title=title&url=url_kek&username=gosho' musicvoter.viktorbarzin.me/api/vote/test
