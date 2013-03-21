Screen-tester-android
=====================

Screenshot based testing framework for Android platform

Tools
-----

This folder contains support scripts for framework.

* run.sh - main script file that pulls screenshots from device, creates a diff between them and attested screenshots and forms junit-report based on diff results.
* pull_data.sh - simple wrapper for android "adb pull" tool
* clean_data.sh - simple wrapper for android "adb shell rm -r" tool
* compare.sh - image comparison script
* gen_report.awk - AWK program file for generating junit-report

Prerequisites
-------------

To make this scripts work:

* You'll need to install Android SDK (and create enviroment variable $ANDROID_SDK).
* For Windows users, install cygwin and ImageMagic, 'awk', maybe 'realpath' and 'dirname' cygwin packages.
* For others only ImageMagic and 'awk', 'realpath', 'dirname', if they dont installed yet. 
* Also python2.7 need to be installed and can be executed by '/usr/bin/python'
