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
set -e
if [[ ($# < 3) || ($# > 4) ]]; then
	echo "Usage: <image_one> <image_two> <diff_image> [out_file]"; 
	echo "<image_one> - name of the first compare image"
	echo "<image_two> - name of the second compare image"
	echo "<diff_image> - name of the diffed image"
	echo "[out_file] - name of the log file, recording results"
	exit 1; 
fi;
if [ "$#" == "3" ]; then res=/dev/null; else res=$4; fi;
if [[ "`compare -metric AE -compose src $1 $2 $3 2>&1`" == "0" ]]; then
	rm -f $3
	echo -e "0" >> $res 
	echo "equal"
else
	echo -e "1" >> $res 
	echo "differ"
fi
