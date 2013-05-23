Screen-tester-android
=====================

Screenshot based testing framework for Android platform

Screentester
------------

This folder contains source files for framework. It's a library android project, so you may make you project dependent from it, or you may build a .jar file and link it to you project.

Source files
------------

* ScreenshotTaker - class, that takes pictures of views
* DatabaseHelper - tool class for cleaning up and setting up database if you need certain data in the database on test
* XMLDataSetReader - SAX-based XML reader for parsing data from dataset of dbunit [XmlDataSet format](http://www.dbunit.org/components.html) 
* Table - simple representation of database table
* View object - representation of areas, that will be excluded from comparison

To use this, device or emulator needs to have sdcard. By default, screenshots placed at /mnt/sdcard/screentester/ subfolders. Also, in your project AndroidManifest.xml set permission WRITE_EXTERNAL_STORAGE.

More information you can find in java-doc comments.

