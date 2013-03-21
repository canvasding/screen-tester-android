##  Screenshot based testing framework for Android platform
##  Copyright 2013 by Tomsksoft, http://tomsksoft.com. All rights reserved.
##
## This software is licensed under
## a Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
## http://creativecommons.org/licenses/by-nc-sa/3.0/
##
##  run.sh
##
##  Created by lia on 18.02.13 15:27.

#!/bin/bash
set -e

if [ "$#" != "3" ]; then 
	echo "Usage: run.sh <attested_screenshots_dir> <device_screenshot_dir> <result_dir>"; 
	echo "<attested_screenshots_dir> - folder, where attested screenshots are" 
	echo "<device_screenshot_dir> - folder on device or emulator, where new screenshots were created"
	echo "<result_dir> - output folder, where new and diffed screenshots and junit-report will be placed"
	exit 1; 
fi;

## checking prerequisites
echo ""
echo "Checking prerequisites..."
if [ "$ANDROID_SDK" == "" ]; then echo "You don't have $ANDROID_SDK eviromental variable set. Exiting..."; return 1; fi
if [ "`realpath /bin`" != "/bin" ]; then echo "You don't have 'realpath' installed. Exiting..."; return 1; fi
if [[ `compare` && $? != 0 ]]; then echo "You don't have ImageMagick installed. Exiting..."; return 1; fi
if [[ `/usr/bin/python -c'print "work"'` != work ]]; then echo "You don't have python installed. Exiting..."; return 1; fi

## setup initial paths
echo ""
echo "Setting initial paths..."
script_dir=$(dirname `realpath $0`)
attested_screenshots_dir=`realpath $1`
remote_device_dir=$2
result_dir=`realpath $3`

current_screenshots_dir=$result_dir/current_screenshots
diffed_screenshots_dir=$result_dir/diffed_screenshots

current_screenshot_list=$current_screenshots_dir/screenshot.list
attested_screenshot_list=$attested_screenshots_dir/screenshot.list
tmp_screenshot_list=$result_dir/screenshot.list
raw_junit=$result_dir/raw_junit.txt
junit_report=$result_dir/screentester-junit-report.xml

## cleanup output direcrories
echo ""
echo "Cleanup output direcrories..."
rm -rf $current_screenshots_dir/
rm -rf $diffed_screenshots_dir/
rm -f $junit_report

mkdir -p $result_dir/
mkdir -p $attested_screenshots_dir/
mkdir -p $current_screenshots_dir/
mkdir -p $diffed_screenshots_dir/

## getting screenshots from device
bash $script_dir/pull_data.sh $remote_device_dir $current_screenshots_dir

files=$current_screenshot_list
## checking if there are any attested screenshots
if [[ -d "$attested_screenshots_dir" && "$(ls -A $attested_screenshots_dir)" ]]; then 
	files="$files $attested_screenshot_list"; 
fi

cat $files | sort | uniq > $tmp_screenshot_list

## creating raw junit file
echo -n "" > $raw_junit

## going over files united screenshot.list files in form attested and current dir
echo ""
echo "Starting comparion of screenshots..."
echo ""
for var in `cat $tmp_screenshot_list`
do
	echo -n "Compare ${var}: "
	attested=`find $attested_screenshots_dir/ -type 'f' -wholename *$var`
	current=`find $current_screenshots_dir/ -type 'f' -wholename *$var`
	if [[ "$current" == "" ]]; then ## if somehow there was not screenshot onto devise
		echo "no such attested screenshot"
		echo -e "${var}\n-1\n0" >> $raw_junit
	else
		if [[ "$attested" == "" ]]; then ## if there is no screenshot in attested dir
			echo "new screenshot detected"
			mkdir -p `dirname $attested_screenshots_dir/$var`
			cp $current $attested_screenshots_dir/`dirname $var`
			echo -e "${var}\n-2\n0" >> $raw_junit
		else ## if all right
			echo -e "$var" >> $raw_junit
			mkdir -p `dirname $diffed_screenshots_dir/$var`
			# /bin/time --format="%e" 
			bash $script_dir/compare.sh $attested_screenshots_dir/${var} $current_screenshots_dir/${var} $diffed_screenshots_dir/${var} ${raw_junit} 
			# 2>> $raw_junit
		fi
	fi
	echo -n ";" >> $raw_junit
done

## save new list of attested screenshots
cp -f $tmp_screenshot_list $attested_screenshot_list
echo ""
echo "generating junit report in $junit_report"
cat $raw_junit | awk -f $script_dir/gen_report.awk > $junit_report #&& rm $raw_junit

