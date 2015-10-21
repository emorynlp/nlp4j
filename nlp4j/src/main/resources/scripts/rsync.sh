#!/bin/bash
jar cf emorynlp.jar$1 edu
rsync -avc emorynlp.jar jdchoi@ainos.mathcs.emory.edu:/home/jdchoi/lib
