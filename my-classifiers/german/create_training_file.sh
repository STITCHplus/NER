#!/bin/sh

file=$1

if test -z $file
then
	echo "No filename was supplied"
	exit -1
fi

java -cp ../../lib/stanford-ner-1.5.jar edu.stanford.nlp.process.PTBTokenizer $file > $file.tok
cat $file.tok | sed "s/\(.*\)/\1\tO/" > $file.tsv

