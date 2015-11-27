#!/bin/bash

db=amalga_jenkins-generate-random-33
suite=amalga_JabRef_efg-random
result=564e0ef5e4b0b36de92ae7a1
gradle -b analyze.gradle -Pdb_id=${db} -Psuite_id=${suite} -Presult_id=${result} summarizeResults
