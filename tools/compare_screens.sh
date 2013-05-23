##  Screenshot based testing framework for Android platform
##  Copyright 2013 by Tomsksoft, http://tomsksoft.com. All rights reserved.
##
## This software is licensed under
## a Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
## http://creativecommons.org/licenses/by-nc-sa/3.0/
##
##  compare_screens.sh
##
##  Created by lia on 22.03.13 14:02.

#!/bin/bash
# set -e

if [[ ($# < 3) || ($# > 4) ]]; then 
	echo "Usage: compare_screens.sh <attested_screenshots_dir> <current_screenshot_dir> <diff_dir> [out_file]"; 
	echo "<attested_screenshots_dir> - folder, where attested screenshots are" 
	echo "<current_screenshot_dir> - folder, where current screenshots are"
	echo "<diff_dir> - output folder, where diffed screenshots will be placed"
	echo "[out_file] - name of the log file, recording results"
	exit 1; 
fi;
if [ "$#" == "3" ]; then raw_junit=/dev/null; else raw_junit=$4; fi;

function write_junit(){
	echo -n -e "${1}\n${2}\n${3}\n;" >> $raw_junit
}

function compare_screens_with_equal_areas(){
	attested_screen=$1; current_screen=$2; excluding_areas=$3; output_dir=$4; #declaration

	first_image=$output_dir/first.png	
	second_image=$output_dir/second.png
	
	if [[ !(-z "$excluding_areas") ]]; then
		size=`identify -format "%[fx:w]x%[fx:h]" $attested_screen`
		excluding_image=`bash $script_dir/excluding_areas.sh $size "$excluding_areas" $output_dir`
		
		composite $excluding_image $attested_screen $first_image
		composite $excluding_image $current_screen $second_image
	else
		cp -f $attested_screen $first_image
		cp -f $current_screen $second_image
	fi
	
	result_image_name=$output_dir/`gen_name $attested_screen diff png`
	compare_result=`bash $script_dir/compare.sh $first_image $second_image $output_dir/${result_image_name}`
	# rm -f $first_image $excluding_image $second_image
	echo $compare_result $result_image_name
}

function compare_screens_with_different_areas(){
	attested_screen=$1; attested_areas=$2; current_screen=$3; current_areas=$4; output_dir=$5; # declaration
	
	first_image=$output_dir/first.png	
	second_image=$output_dir/second.png
	size=`identify -format "%[fx:w]x%[fx:h]" $attested_screen`
	
	if [[ !(-z "$attested_areas") ]]; then
		excluding_image=`bash $script_dir/excluding_areas.sh $size "$attested_areas" $output_dir`
		composite $excluding_image $attested_screen $first_image
	else
		cp -f $attested_screen $first_image
	fi
	if [[ !(-z "$current_areas") ]]; then
		excluding_image=`bash $script_dir/excluding_areas.sh $size "$current_areas" $output_dir`
		composite $excluding_image $current_screen $second_image
	else
		cp -f $current_screen $second_image
	fi
	
	result_image=$output_dir/`gen_name $attested_screen diff png`
	compare_result=`bash $script_dir/compare.sh $first_image $second_image $result_image`
	# rm -f $first_image $excluding_image $second_image
	echo $compare_result $result_image
}

function make_triple_image(){
	attested_screen=$1; current_screen=$2; diff_screen=$3; output_dir=$4; # declaration

	names=($(get_triple_names $attested_screen))
	montage -frame 5 -geometry +0+0 $attested_screen $diff_screen $current_screen $output_dir/${names[0]} 2> /dev/null
	echo ${names[1]} ${names[2]}
	# cp -f $attested_screen $output_dir/${names[2]}
	# cp -f $current_screen $output_dir/${names[1]}
}

AREAS_CHANGED=-4; MISSING_ATTESTED=-3; NEW_SCREENSHOT=-2; MISSING_CURRENT=-1;

script_dir=$(dirname `realpath $0`)
attested_screenshots_dir=$1
current_screenshots_dir=$2
artifacts_dir=$3
tmp_dir=$artifacts_dir/tmp_dir
mkdir -p $tmp_dir

. $script_dir/utils.sh

current_screenshot_list=$current_screenshots_dir/screenshot.list
attested_screenshot_list=$attested_screenshots_dir/screenshot.list
new_screenshot_list=$artifacts_dir/screenshot.list

echo ""
echo "Starting comparsion of screenshots..."
echo ""

echo -n "" > $new_screenshot_list
echo -n "" > $raw_junit

if [[ !(-e $current_screenshot_list) ]]; then 
	echo "No current screenshot list was found"
	exit 1
fi

if [[ !(-e $attested_screenshot_list) ]]; then ## if there are no attested screenshot.list
	echo "There is no list of attested screenshots. Is it the first run?"
	cp -f $current_screenshot_list $new_screenshot_list; 
	for record in `cat $current_screenshot_list`
	do
		filename=`get_filename $record`
		echo -n "Processing ${filename}: "
		current=`find $current_screenshots_dir/ -type 'f' -wholename "*$filename"`
		if [[ "$current" == "" ]]; then 
			echo "there is name in screenshot.list but no file found in current screenshot folder"
			write_junit $filename $MISSING_CURRENT 0
		else
			echo "new screenshot"
			write_junit $filename $NEW_SCREENSHOT 0
			new_name=`gen_name $filename new png`
			cp -f $current $artifacts_dir/$new_name
		fi
		echo -e "$record" >> $new_screenshot_list
	done
	exit 0
fi

for attested_record in `cat $attested_screenshot_list`
do
	found_match=""
	attested_filename=`get_filename $attested_record`
	attested=`find $attested_screenshots_dir/ -type 'f' -wholename "*$attested_filename"`
	echo -n "Processing ${attested_filename}: "
	if [[ -z $attested ]]; then	## if no attested screenshot file
		echo "no attested screenshot. Check attested screenshot directory or remove filename from screenshot.list"
		write_junit $attested_filename $MISSING_ATTESTED 0
		echo -e "$attested_record" >> $new_screenshot_list
		continue;
	fi
	for current_record in `cat $current_screenshot_list`
	do
		current_filename=`get_filename $current_record`
		if [[ $current_filename = $attested_filename ]]; then 
			found_match=$current_record; 
			break; 
		fi;
	done
	if [[ -z $found_match ]]; then ## if no filename in current screenshot.list
		echo "there is no name of attested screenshot in current screenshot.list. Purhaps it wasn't screened or removed from tests"
		write_junit $attested_filename $MISSING_CURRENT 0
		continue
	fi
	
	## checking existance of current screenshot file
	current=`find $current_screenshots_dir/ -type 'f' -wholename "*$attested_filename"`
	if [[ -z $current ]]; then	## if no current screenshot file
		echo "no current screenshot in directory. Maybe screenshot was not been screened. Check screenshots on the device"
		write_junit $attested_filename $MISSING_CURRENT 0
		continue;
	fi
	
	## checking equality of areas
	areas_equal="true"
	attested_areas=`get_areas $attested_record`
	current_areas=`get_areas $found_match`
	if [[ !(-z $attested_areas) ]]; then
		for attested_area in $attested_areas; do
			match="false"
			for current_area in $current_areas; do
				if [[ $attested_area == $current_area ]]; then match="true"; break; fi;
			done;
			if [[ $match == "false" ]]; then areas_equal="false"; break; fi;
		done;
	fi
	if [[ !(-z $current_areas) ]]; then
		for current_area in $current_areas; do
			match="false"
			for attested_area in $attested_areas; do
				if [[ $attested_area == $current_area ]]; then match="true"; break; fi;
			done;
			if [[ $match == "false" ]]; then areas_equal="false"; break; fi;
		done;
	fi
	
	## start comparying screenshots
	attested_image=$attested_screenshots_dir/${attested_filename}
	current_image=$current_screenshots_dir/${attested_filename}
	
	if [[ $areas_equal == "true" ]]; then 
	
		area_mask=$tmp_dir/mask.png
		size=`identify -format "%[fx:w]x%[fx:h]" $attested_image`
		bash $script_dir/excluding_areas.sh $size "$attested_areas" $area_mask "false"
		
		first_image=$tmp_dir/first.png
		composite $area_mask $attested_image $first_image
		
		second_image=$tmp_dir/second.png
		composite $area_mask $current_image $second_image
		
		diff_image=$artifacts_dir/`gen_name $attested_image diff png`
		compare_result=(`bash $script_dir/compare.sh $first_image $second_image $diff_image`)
		
		if [[ "${compare_result[0]}" != "0" ]]; then
			
			bash $script_dir/excluding_areas.sh $size "$attested_areas" $area_mask "true"
			composite $area_mask $attested_image $first_image
			composite $area_mask $current_image $second_image
			
			names=($(get_triple_names $attested_image))
			montage -frame 5 -geometry +0+0 $first_image $diff_image $second_image $artifacts_dir/${names[0]} 2> /dev/null
			cp -f $attested_image $artifacts_dir/${names[2]}
			cp -f $current_image $artifacts_dir/${names[1]}
			
			echo "screenshots differ; "
		else
			rm -f $diff_image $first_image $second_image
			echo "screenshots equal; "
		fi
		write_junit $attested_filename ${compare_result[0]} ${compare_result[1]}
	else
	
		area_mask=$tmp_dir/mask.png
		size=`identify -format "%[fx:w]x%[fx:h]" $attested_image`
		
		bash $script_dir/excluding_areas.sh $size "$attested_areas" $area_mask "false"
		first_image=$tmp_dir/first.png
		composite $area_mask $attested_image $first_image
		
		bash $script_dir/excluding_areas.sh $size "$current_areas" $area_mask "false"
		second_image=$tmp_dir/second.png
		composite $area_mask $current_image $second_image
		
		diff_image=$artifacts_dir/`gen_name $attested_image diff png`
		compare_result=(`bash $script_dir/compare.sh $first_image $second_image $diff_image`)
		
		bash $script_dir/excluding_areas.sh $size "$attested_areas" $area_mask "true"
		first_image=$tmp_dir/first.png
		composite $area_mask $attested_image $first_image
		
		bash $script_dir/excluding_areas.sh $size "$current_areas" $area_mask "true"
		second_image=$tmp_dir/second.png
		composite $area_mask $current_image $second_image
		
		names=($(get_triple_names $attested_image))
		montage -frame 5 -geometry +0+0 $first_image $diff_image $second_image $artifacts_dir/${names[0]} 2> /dev/null
		
		cp -f $attested_image $artifacts_dir/${names[2]}
		cp -f $current_image $artifacts_dir/${names[1]}
		echo "excluding areas differ;"
		write_junit $attested_filename $AREAS_CHANGED ${compare_result[1]}
	fi
	echo -e "$found_match" >> $new_screenshot_list
done

for current_record in `cat $current_screenshot_list`
do
	found_match=""
	current_filename=`get_filename $current_record`
	for attested_record in `cat $attested_screenshot_list`
	do
		attested_filename=`get_filename $attested_record`
		if [[ $current_filename = $attested_filename ]]; then found_match=$attested_record; break; fi;
	done
	if [[ -z $found_match ]]; then
		new_name=`gen_name $current_filename new png`
		cp -f $current_screenshots_dir/${current_filename} $artifacts_dir/$new_name
		write_junit $current_filename $NEW_SCREENSHOT 0
		echo -n "Processing ${current_filename}: "
		echo "new screenshot"
		echo -e "$current_record" >> $new_screenshot_list
		continue
	fi
done;

rm -rf $tmp_dir