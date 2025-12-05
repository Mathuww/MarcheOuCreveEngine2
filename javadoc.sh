#!/bin/bash
./mcgradlew javadoc
rm -r public/javadoc
cp -r core/build/docs/javadoc public/