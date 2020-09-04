#!/bin/bash

function rename_structure {
  pattern="s#/$1\$##"
  for f in $(ls -R ./ | grep ":$" | sed "s/:$//" | grep "$1$" | sed $pattern ) ; do
     git mv "$f/$2" "$f/$3"
  done
}

function change_files {
  find . -not -path '*/\.*' -type f -exec sed -i '' "s/$1/$2/g" {} \;
}

cd ../

#TODO replace new_one with the new
rename_structure io io new_one
rename_structure rheem/rheem rheem new_one

change_files "io\.rheem\.rheem" "io\.rheem"

#pattern="s#/rheem\$##"
#for f in $(ls -R ./ | grep ":$" | sed "s/:$//" | grep "rheem/rheem$" | sed $pattern ) ; do
#  for f2 in $(ls "$f/rheem") ; do
#    if [ -d "$f/rheem/$f2" ]
#    then
#      first="$f/$f2"
#      second="$f/rheem/$f2"
#      third="$f/rheem"
#      echo ""
#      echo "1 - $first"
#      echo "2 - $second"
#      echo "3 - $third"
#      git mv "$second" "$f/"
#      rm -r "$third"
#    fi
#  done
#done
