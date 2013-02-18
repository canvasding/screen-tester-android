##  Screenshot based testing framework for Android platform
##  Copyright 2013 by Tomsksoft, http://tomsksoft.com. All rights reserved.
##
## This software is licensed under
## a Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
## http://creativecommons.org/licenses/by-nc-sa/3.0/
##
##  pull_data.sh
##
##  Created by lia on 18.02.13 15:27.

#!/bin/bash
set -e

if [ "$#" != "2" ]; then 
	echo "Usage: pull_data <device_file_dir> <local_file_dir>"; 
	echo "<device_file_dir> - folder on device which is need to be pulled"
	echo "<local_file_dir> - folder to which data will be pulled"
	exit 1; 
fi;

if [ "$OSTYPE" == "cygwin" ]
then
	local_path=`cygpath -w $2`;
else
	local_path=$2;
fi
	
echo ""
echo "Getting screenshots from device..."
$ANDROID_HOME/platform-tools/adb pull ${1} ${local_path}
if [ ! -d ${local_path} ] 
then 
	echo Screenshots doesnt copy! Check path to the screenshots on the device.
	echo ""
	exit 1
fi
echo Screensots copied to ${2}
echo ""
exit 0
