##  Screenshot based testing framework for Android platform
##  Copyright 2013 by Tomsksoft, http://tomsksoft.com. All rights reserved.
##
## This software is licensed under
## a Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
## http://creativecommons.org/licenses/by-nc-sa/3.0/
##
##  clean_data.sh
##
##  Created by lia on 18.02.13 15:27.

#!/bin/bash
set -e

if [ "$#" != "1" ]; then 
	echo "Usage: clean_data <device_file_dir> "; 
	echo "<device_file_dir> - folder on device to delete"
	exit 1; 
fi;

echo ""
echo "Cleaning up device last screenshots"
$ANDROID_HOME/platform-tools/adb shell rm -r ${1}
exit 0
