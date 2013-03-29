##  Screenshot based testing framework for Android platform
##  Copyright 2013 by Tomsksoft, http://tomsksoft.com. All rights reserved.
##
## This software is licensed under
## a Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
## http://creativecommons.org/licenses/by-nc-sa/3.0/
##
##  run.sh
##
##  Created by lia on 22.03.13 14:02.

#!/bin/bash
set -e

if [[ ($# < 3) || ($# > 4) ]]; then 
	echo "Usage: compare_list.sh <attested_screenshots_dir> <current_screenshot_dir> <diff_dir> [out_file]"; 
	echo "<attested_screenshots_dir> - folder, where attested screenshots are" 
	echo "<current_screenshot_dir> - folder, where current screenshots are"
	echo "<diff_dir> - output folder, where diffed screenshots will be placed"
	echo "[out_file] - name of the log file, recording results"
	exit 1; 
fi;
if [ "$#" == "3" ]; then raw_junit=/dev/null; else raw_junit=$4; fi;

script_dir=$(dirname `realpath $0`)
attested_screenshots_dir=$1
current_screenshots_dir=$2
artifacts_dir=$3

current_screenshot_list=$current_screenshots_dir/screenshot.list
attested_screenshot_list=$attested_screenshots_dir/screenshot.list
new_screenshot_list=$artifacts_dir/screenshot.list

files=$current_screenshot_list
## checking if there are any attested screenshots
if [[ -d "$attested_screenshots_dir" && "$(ls -A $attested_screenshots_dir)" ]]; then 
	files="$files $attested_screenshot_list"; 
fi

cat $files | sort | uniq > $new_screenshot_list

## creating raw junit file
echo -n "" > $raw_junit

## going over files united screenshot.list files in form attested and current dir
echo ""
echo "Starting comparion of screenshots..."
echo ""
for filename in `cat $new_screenshot_list`
do
	echo -n "Compare ${filename}: "
	attested=`find $attested_screenshots_dir/ -type 'f' -wholename *$filename`
	current=`find $current_screenshots_dir/ -type 'f' -wholename *$filename`
	if [[ "$current" == "" ]]; then ## if somehow there was not screenshot onto device
		echo "no such attested screenshot"
		echo -e "${filename}\n-1\n0" >> $raw_junit
	else
		if [[ "$attested" == "" ]]; then ## if there is no screenshot in attested dir
			echo "new screenshot detected"
			mkdir -p `dirname $attested_screenshots_dir/$filename`
			cp $current $attested_screenshots_dir/`dirname $filename`
			echo -e "${filename}\n-2\n0" >> $raw_junit
			new_name=`echo $filename | awk 'gsub("/", ".")' | awk 'gsub("png$", "new.png")'`
			cp -f $current $artifacts_dir/$new_name
		else ## if all right
			echo -e "$filename" >> $raw_junit
			# mkdir -p `dirname $artifacts_dir/$filename`
			diff_image_name=`echo $filename | awk 'gsub("/", ".")' | awk 'gsub("png$", "diff.png")'`
			compare_result=`bash $script_dir/compare.sh $attested_screenshots_dir/${filename} $current_screenshots_dir/${filename} $artifacts_dir/${diff_image_name} ${raw_junit}`
			echo $compare_result
			if [[ "$compare_result" == "differ" ]]; then
				triple_image_name=`echo $filename | awk 'gsub("/", ".")' | awk 'gsub("png$", "triple.png")'`
				current_image_name=`echo $filename | awk 'gsub("/", ".")' | awk 'gsub("png$", "current.png")'`
				attested_image_name=`echo $filename | awk 'gsub("/", ".")' | awk 'gsub("png$", "attested.png")'`
				montage -frame 5 -geometry +0+0 $attested_screenshots_dir/${filename} $artifacts_dir/${diff_image_name} $current_screenshots_dir/${filename} $artifacts_dir/${triple_image_name} 2> /dev/null
				cp -f $attested_screenshots_dir/${filename} $artifacts_dir/${attested_image_name}
				cp -f $current_screenshots_dir/${filename} $artifacts_dir/${current_image_name}
			fi
		fi
	fi
	echo -n ";" >> $raw_junit
done

## save new list of attested screenshots
cp -f $new_screenshot_list $attested_screenshot_list