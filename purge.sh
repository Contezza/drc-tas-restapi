#!/bin/bash

mvn clean

docker stop $(docker ps -a -q) && docker rm $(docker ps -a -q) || true &&
    docker volume rm docker_vol_drc-docker-platform_alf_data -f &&
    docker volume rm docker_vol_drc-docker-platform_solr_data -f &&
    docker volume rm docker_vol_drc-docker-platform_shared_file_store -f &&
    docker volume rm docker_vol_drc_db -f &&
    docker volume rm docker_vol_drc_media -f &&
    docker volume rm docker_vol_drc_private_media -f
