##  Screenshot based testing framework for Android platform
##  Copyright 2013 by Tomsksoft, http://tomsksoft.com. All rights reserved.
##
## This software is licensed under
## a Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
## http://creativecommons.org/licenses/by-nc-sa/3.0/
##
##  gen_report.awk
##
##  Created by lia on 18.02.13 15:27.

BEGIN{ 
	FS="\n"
	RS=";"
	OFS=" "
	print "<?xml version='1.0' encoding='utf-8' standalone='yes' ?>" 
	print "<testsuites>"
}
#sub("./", "")	{  }
sub("/", FS)	{ print "<testsuite name=\"" "screentester" "\">" }
$2 != ""		{ print "<testcase classname=\"" $1 ".screentester" "\"", "name=\"" $2 "\"", "time=\"" $4 "\">" }
$3 == -4		{ print "<failure message=\"Excluding areas changed\"/>"}
$3 == -3		{ print "<failure message=\"Missing attested screenshot\"/>"}
$3 == -2		{ print "<failure message=\"New screenshot\"/>"}
$3 == -1		{ print "<failure message=\"Missing current screenshot\"/>"}
$3 > 0	 		{ print "<failure message=\"Screenshots differ\"/>"}
$2 != ""		{ print "</testcase>" }
$0 != ""		{ print "</testsuite>" }
END{
	print "</testsuites>"
}
