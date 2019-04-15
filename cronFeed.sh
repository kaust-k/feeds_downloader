#!/bin/sh

FEEDS_URL="https://url.com/feed"
FILENAME="feeds.html"
DUMMYFILENAME="feeds123.html"
PWD="/home/k2/folder/"

cd $PWD

sqlite3 "$PWD/feeds.db" 'select count(title) from feeds;'

wget -t 10 -T 60 -w 60 -O $DUMMYFILENAME $FEEDS_URL && java -jar feeds.jar

sqlite3 "$PWD/feeds.db" 'select count(title) from feeds;'

