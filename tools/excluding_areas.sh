##  Screenshot based testing framework for Android platform
##  Copyright 2013 by Tomsksoft, http://tomsksoft.com. All rights reserved.
##
## This software is licensed under
## a Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
## http://creativecommons.org/licenses/by-nc-sa/3.0/
##
##  excluding_areas.sh
##
##  Created by lia on 22.05.13 14:30.

#!/bin/bash
if [[ $# != 4 ]]; then 
	echo "Usage: excluding_areas.sh <size> <areas> <output_file> <contour>"
	echo "<size> - size of new excluding area image in format '480x800'"
	echo "<areas> - array of rectangle coordinates in format 'x1,y1,x2,y2 a1,b1,a2,b2 ...'"
	echo "<output_file> - file with result"
	exit 1; 
fi;

image_size=$1; areas=$2; shape_image=$3; contour=$4;
rm -f $shape_image
convert -size $image_size xc:transparent $shape_image
# for shape in `echo $areas | awk 'gsub(";",FS)'`
if [[ $contour == "true" ]]; then param="-fill none -stroke black -strokewidth 1"; else param="-fill black"; fi;
for shape in $areas
do
	pts=(`echo $shape | awk 'gsub(",", FS)'`)
	convert $shape_image $param -draw "rectangle ${pts[0]},${pts[1]} ${pts[2]},${pts[3]}" $shape_image
done
