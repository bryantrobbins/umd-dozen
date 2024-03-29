#!/bin/bash
#  
#  Copyright (c) 2009-@year@. The GUITAR group at the University of Maryland. Names of owners of this group may
#  be obtained by sending an e-mail to atif@cs.umd.edu
# 
#  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
#  documentation files (the "Software"), to deal in the Software without restriction, including without 
#  limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
#  the Software, and to permit persons to whom the Software is furnished to do so, subject to the following 
#  conditions:
# 
#  The above copyright notice and this permission notice shall be included in all copies or substantial 
#  portions of the Software.
#
#  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
#  LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO 
#  EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
#  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
#  THE USE OR OTHER DEALINGS IN THE SOFTWARE. 

####################################################
# JFC ripper launching script
#
#	By	baonn@cs.umd.edu
#	Date: 	06/08/2011
# Required ENV vars:
# ------------------
#   JAVA_HOME - location of a JDK home dir
#
# Optional ENV vars
#   GUITAR_OPTS - parameters passed to the Java VM when running
#   GUITAR
#     e.g.,
#       To debug GUITAR itself
#          export GUITAR_OPTS="$GUITAR_OPTS -Dlog4j.configuration=log/guitar-debug-only.glc"
# 
#       To print component info during ripping  
#          export GUITAR_OPTS="$GUITAR_OPTS -Dguitar.ripper.PrintCompInfo"
####################################################


function usage {
	echo "Usage: `basename $0` -cp <aut classpath> [guitar arguments]"
}
base_dir=`dirname $0`
guitar_lib=$base_dir/jars


if [ $# -lt 1 ];
then
	echo "Invalid parameter(s)"
	usage
	exit
fi


if [ $1 == "-cp" -a $# -ge 2 ]
then
	addtional_classpath=$2
	guitar_args=${@:3}
elif [ $1 == "-?" ]
then
	guitar_args=("-?")
else
	echo "Invalid parameter(s)"
	usage
	exit
fi

classpath=$addtional_classpath:$classpath

# Main classes 
ripper_launcher=edu.umd.cs.guitar.ripper.JFCRipperMain
guitar_classpath="$guitar_lib/gui-ripper-jfc.jar"

if [ -z "$JAVA_CMD_PREFIX" ];
then
    # Run with clean log file 
    JAVA_CMD_PREFIX="java"
fi

classpath=$base_dir:$guitar_classpath

if [ ! -z $addtional_classpath ] 
then
	classpath=$classpath:$addtional_classpath
else
	classpath=$classpath
fi

RIPPER_CMD="$JAVA_CMD_PREFIX $JAVA_OPTS $GUITAR_OPTS -cp $classpath $ripper_launcher $guitar_args"
echo $RIPPER_CMD
exec $RIPPER_CMD
