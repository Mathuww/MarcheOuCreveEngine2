#!/bin/bash
if [ -z $1 ]
then
    echo "Missing argument."
else
    echo $1 > $CREVE_DIR/public/session_url.crevedata
fi
