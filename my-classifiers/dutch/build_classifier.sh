#!/bin/sh


#rm training.tsv
#cat *tsv > training.tsv


java -Xmx500M -cp ../../lib/stanford-ner-1.5.jar edu.stanford.nlp.ie.crf.CRFClassifier -prop classifier.properties


