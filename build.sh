#!/bin/bash

ant -f build.xml clean
ant -f build.xml generate
cp -r output/gen_src/gnu/trove generated-src/gnu/
