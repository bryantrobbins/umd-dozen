#!/bin/bash

db=amalga_jenkins-generate-random-19
suite=

gradle -b analyze.gradle -Pdb_id=${db} -Psuite_id=${suite} inspect
