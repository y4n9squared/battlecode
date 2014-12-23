#!/bin/bash

set -x
set -e

mkdir -p ~/.ant/lib

if [ ! -e ~/.ant/lib/ivy.jar ]; then
  echo 'Installing ivy...'
  wget http://repo2.maven.org/maven2/org/apache/ivy/ivy/2.3.0/ivy-2.3.0.jar
  mv ivy-2.3.0.jar ~/.ant/lib/ivy.jar
fi

if [ ! -e ~/.ant/lib/checkstyle.jar ]; then
  echo 'Installing checkstyle...'
  wget http://downloads.sourceforge.net/projects/checkstyle/checkstyle/6.1.1/checkstyle-6.1.1-all.jar
  mv checkstyle-6.1.1-all.jar ~/.ant/lib/checkstyle.jar
fi
