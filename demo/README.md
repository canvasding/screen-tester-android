Screen-tester-android
=====================

Screenshot based testing framework for Android platform

Demo
----

Here is sample project how to use farmework. It's a self-tested project, so look on test source files ("testsrc" folder). 

To make it work, make dependent from framework project or compile framework project into .jar and link to this project.

Launsh steps
------------

Step 0: start an emulator (or a physical devise) with 800*480 resolution (for example) and sdcard (nessesary) with enough space for 4 screenshots ~ 1MB. From current directory execute: "bash ant clean debug install" to install build and install demoapp

Now you dont have any screenshots to compare with, so first launch will mark all of them as "new"

Step 1: Creating attested screenshots. From current directory execute: "bash ant run-tests"

Now you can see new folder named "attested_screenshots" in current directory. In than folder will be screenshots to compare with subsequent screenshots. You should put in there screenshots that you think acceptable.
In "bin" folder you can see "junit-report.xml", "current_screenshots" (there are screenshots from current test launch), "screenshot_artifatcs" (there are files that show where exactly screenshots differs).
If you look into "screenshot_artifacts" dir, you shall see that there are 4 screenshots and a file "screenshot.list", which is very important.
As it was first run, you can simply copy all content from "current_screenshots" to "attested_screenshots", however on futher launches copy filen ONLY from "screenshot_artifacts".
By looking into junit report you may sure that all tests fails with message "New screenshot"
Let's run again.

Step 2: Checking if attested screenshots equals new. From current directory execute: "bash ant run-tests"

NOTICE: screenshots may differs with locked and unlocked screen.

As all new screenshots must be the same as attested the junit report now shows us that all tests succeeds.

Step 3: Modify some of the attested screenshots to look how diff works. Then again execute: "bash ant run-tests"

NOTICE: screenshots are ".png" files, so they have alfa-channel. Rememder it when modifying them.

Now look in "screenshot_artifacts" folder. There must be lots of files. Let's investigate "who is who".
All image files in this directory has a suffix, so:
".new." - means that there was new screenshot created during the test;
".current." - means that excluding areas has changed for this screenshot. It doesn't means, that screenshot is differ from attested;
".diff." - file with differences between attested and current screenshot;
".attested." - just appropriate attested screenshot;
".triple." - combination of attested, differed and current screenshots in one picture;

NOTICE: If you want to exclude some of attested screenshots from testing, modify "screenshots.list" in "attested_screenshots" folder by removing screenshot name from it. Also disable appropriate test that screens that screenshot in test project.

Step 4: Modify attested screenshot.list to look how it works. Then again execute: "bash ant run-tests"

screenshot.list have structure like:
	<screenshot_file_name>:<excluded_area_coordinates_1>;<excluded_area_coordinates_2>;<excluded_area_coordinates_3>;....
where <excluded_area_coordinates_1> is a rectangle coordinates <x1,y1,x2,y2>, which will be excluded from comparison by filling it with black color

So, if you change this coords, the excluding areas will move.

Customization
-------------

If you want to customize output look at "ant.properties file".

* screentester.attested.dir - path, where attested screenshots will be searching
* screentester.result.dir - output folder for junit report, screenshots from current launch and diff of them	

If you are testing you projects somehow else (not using Ant), follow next steps:
* before launching tests execute: "bash tools/clear_data.sh /mnt/sdcard/screentester" - to clean up sdcard from previous lauch screenshots
* run your tests
* execute: "bash tools/run.sh <dir_for_attested_screenshots> /mnt/sdcard/screentester/  <dir_to_put_diffed_screenshots_and_junit_result>"
	