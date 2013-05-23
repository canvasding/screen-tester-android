##  Screenshot based testing framework for Android platform
##  Copyright 2013 by Tomsksoft, http://tomsksoft.com. All rights reserved.
##
## This software is licensed under
## a Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
## http://creativecommons.org/licenses/by-nc-sa/3.0/
##
##  compare.sh
##
##  Created by lia on 18.02.13 15:27.

#!/bin/bash
if [[ $# != 3 ]]; then
	echo "Usage: compare.sh <image_one> <image_two> <diff_image>"; 
	echo "<image_one> - name of the first compare image"
	echo "<image_two> - name of the second compare image"
	echo "<diff_image> - name of the diffed image"
	exit 1; 
fi;

function get_time(){
	echo "`/usr/bin/python -c'import time; print repr(time.time()*1000)' | awk -F. '{print $1}'`"
}

t0=$(get_time)
compare_res="`compare -metric AE -compose src $1 $2 $3 2>&1`"
t1=$(get_time)
t=$(( ($t1-$t0) ))
t="$(( $t/1000 )).$(( $t%1000 ))"
echo "$compare_res $t"
