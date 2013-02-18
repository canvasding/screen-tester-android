Screen-tester-android
=====================

Screenshot based testing framework for Android platform

Demo
----

Here is sample project how to use farmework. It's a self-tested project, so look on test source files ("testsrc" folder). 

To make it work, make dependent from framework project or compile framework project into .jar and link to this project.

Launsh steps
------------

	Step 0: start an emulator (or a physical devise) with 800*480 resolution and sdcard with enough space for 4 screenshots ~ 1MB. From current directory execute: "bash ant clean debug install" to install build and install demoapp
	
		Now you dont have any screenshots to compare with, so first launch will create them
	
	Step 1: Creating attested screenshots. From current directory execute: "bash ant run-tests"
	
		Now you can see new folder named "attested_screenshots" in current directory. In than folder will be screenshots to compare with subsequent screenshots. If there are new screenshots, they will be put into that directory and in junit report test marked as failed. 
		In "bin" folder you can see "junit-report.xml", "current_screenshots" (there are screenshots from current test launch), "diffed_screenshots" (there are files that show where exactly screenshots differs).
		By looking into junit report you may sure that all tests fails with message "New screenshot"
		Lets run again.
	
	Step 2: Checking if attested screenshots equals new. From current directory execute: "bash ant run-tests"
	
		Notice: screenshots may differs with locked and unlocked screen.
	
		As new screenshots must be the same as attested the junit report now shows us that all tests succeeds.
		
	Step 3: Modify some of the attested screenshots to look how diff works. Then again execute: "bash ant run-tests"
	
		Notice: screenshots are ".png" files, so they have alfa-channel. Rememder if modifying them.
	
		Now look in "diffed_screenshots" folder. There must be image with difference between screenshots. Also look into junit report to ensure that test fails with message "screenshots differs"
	
		Notice: If you want to exclude some of attested screenshots from testing, modify "screenshots.list" in "attested_screenshots" folder by removing screenshot name from it. Also disable appropriate test that screens that screenshot in test project. IMPORTANT! If you dont disable test, attested screenshot will be overriden.

Customization
-------------

	If you want to customize output look at "ant.properties file".
	
	* screentester.attested.dir - path, where attested screenshots will be searching
	* screentester.result.dir - output folder for junit report, screenshots from current launch and diff of them	

	If you are testing you projects somehow else (not using Ant), follow next steps:
	* before launching tests execute: "bash tools/clear_data.sh /mnt/sdcard/screentester" - to clean up sdcard from previous lauch screenshots
	* run your tests
	* execute: "bash tools/run.sh <dir_for_attested_screenshots> /mnt/sdcard/screentester/  <dir_to_put_diffed_screenshots_and_junit_result>"
	
Important
---------

Screenshots differs with locked and unlocked screen.
	