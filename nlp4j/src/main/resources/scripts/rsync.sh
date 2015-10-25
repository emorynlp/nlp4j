#!/bin/bash
jar cf emorynlp.jar$1 edu
rsync -avc emorynlp.jar jdchoi@ainos.mathcs.emory.edu:/home/jdchoi/lib
#rsync -avc emorynlp.jar choi@lab0z.mathcs.emory.edu:/home/choi/lib
#scp choi@lab0z.mathcs.emory.edu:/home/choi/lib/emorynlp.jar jdchoi@ainos.mathcs.emory.edu:/home/jdchoi/lib/emorynlp.jar
