##  Screenshot based testing framework for Android platform
##  Copyright 2013 by Tomsksoft, http://tomsksoft.com. All rights reserved.
##
## This software is licensed under
## a Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
## http://creativecommons.org/licenses/by-nc-sa/3.0/
##
##  utils.sh
##
##  Created by lia on 22.05.13 14:07.

#!/bin/bash

function gen_name(){
	echo `echo $1 | awk 'gsub(".*/", "")' | awk 'gsub(".'"$3"'$", ".'"$2.$3"'")'`
}

function get_triple_names(){
	echo $(gen_name $1 triple png) 
	echo $(gen_name $1 current png) 
	echo $(gen_name $1 attested png) 
}

function get_filename(){
	echo `echo $1 | awk 'gsub(":",FS) {print $1}'`
}

function get_areas(){
	echo `echo $1 | awk 'gsub(":",FS) {print $2}' | awk 'gsub(";",FS)'`
}
