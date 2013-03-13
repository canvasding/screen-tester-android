/*
 * Screenshot based testing framework for Android platform
 * Copyright 2013 by Tomsksoft, http://tomsksoft.com. All rights reserved.
 *
 * This software is licensed under
 * a Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 *
 * ScreenshotTaker.java
 *
 * Created by lia on 18.02.13 15:35
 */

package com.tomsksoft.screentester;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import junit.framework.Assert;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class takes view screenshots and saves them onto SDCard
 */
public class ScreenshotTaker {

	/**
	 * Root path element for saving screens
	 */
	public static final String DEFAULT_SCREENSHOTS_PATH = "/mnt/sdcard/screentester/";
	private static final String SCREENSHOT_LIST_FILENAME = "screenshot.list";

	private final String FILE_EXT = ".png";
	private final List<Bitmap> screenshotAlbum = new ArrayList<Bitmap>();
    private final File fileList;
	private final SavePathFormat saveFormat;
	private final boolean autoSave;

	private String savePath;
	private Point resolution;

	/**
	 * Create an instance without saving screenshots to the storage.
	 * You can retrieve screenshots after finish calling getScreenshotAlbum()
	 */
	public ScreenshotTaker()
	{
		savePath = null;
		resolution = null;
		saveFormat = null;
        fileList = null;
		autoSave = false;
    }

	/**
	 * Creates an instance of ScreenshotTaker with format params.
	 * This instance will save screenshots to SDCard in "/mnt/sdcard/screentester/"
	 * @param format declares how result file name looks like
	 */
	public ScreenshotTaker(SavePathFormat format)
	{
        saveFormat = format;
		autoSave = true;

        fileList = new File(DEFAULT_SCREENSHOTS_PATH, SCREENSHOT_LIST_FILENAME);
		File parent = fileList.getParentFile();
		if ( parent.exists() ) {
//			cleanupOutput(parent);
		}
		else if ( !parent.mkdirs() ) {
			Assert.fail("Cannot create " + DEFAULT_SCREENSHOTS_PATH + " folder");
		}
    }

	/**
	 * Makes a screenshot.
	 * @param activity which views need to be screened
	 * @param screeningViewId id of the view we want to screen. If null, screen root of the activity
	 * @param excludedViewIds array of ids that we want to exclude from screening. Can bu null.
	 * @param screenshotName name, that will identify screenshot
	 */
	public void doScreenShot(final Activity activity, Integer screeningViewId, int[] excludedViewIds,
	                         String screenshotName)
	{

        if (activity == null) Assert.fail("Activity is null. Maybe not started or already finished");
        // BUG: next line will not screenshot activity if there is dialog
		// Should be be another mechanism to check whether activity was drawed
//        if (!activity.hasWindowFocus()) Assert.fail("Activity is not displayed or doesn't have focus (May be taken be pop-up or dialog)");

		Display disp = activity.getWindowManager().getDefaultDisplay();
		resolution = new Point(disp.getWidth(),disp.getHeight());
		this.savePath = activity.getPackageName();

		if ( screeningViewId == null ) {
			screeningViewId = android.R.id.content;
		}
		if ( excludedViewIds != null && excludedViewIds.length > 0 ) {
			for ( final int excludedViewId : excludedViewIds ) {
				activity.runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						activity.findViewById(excludedViewId).setVisibility(View.INVISIBLE);
					}
				}
				);
			}
		}

		final Integer finalScreeningViewId = screeningViewId;
		Runnable getDrawningCacheRunnable = new Runnable()
		{
			@Override
			public void run()
			{
				View view = activity.findViewById(finalScreeningViewId);
				view.setDrawingCacheEnabled(true);
                Bitmap screenshot = view.getDrawingCache();
				screenshotAlbum.add(screenshot);
				synchronized (this) {
					this.notify();
				}
			}
		};
		synchronized (getDrawningCacheRunnable) {

			activity.runOnUiThread(getDrawningCacheRunnable);
			try {
				getDrawningCacheRunnable.wait();
			}
			catch ( InterruptedException e ) {
				e.printStackTrace();
                Assert.fail("Thread interrupted during screening");
			}
		}
		if ( savePath != null ) {
			saveScreenshot(activity.getLocalClassName(),screenshotName);
		}
	}

	/**
	 * Returns list of all taken screenshots
	 * @return list of bitmaps
	 */
	public List<Bitmap> getScreenshotAlbum()
	{
		return screenshotAlbum;
	}

	private String getFormedScreenshotName(String className,String screensotName, int number)
	{
		switch ( saveFormat ) {
			case PREFIX:
				return String.format("%s/%dx%d/%s_%s%d", savePath, resolution.x, resolution.y,className,screensotName,number);
			case SUFFIX:
				return String.format("%s/%s_%s%d_%dx%d", savePath, className, screensotName, number, resolution.x,resolution.y);
			default:
				return null;
		}
	}

	private void saveScreenshot(String className,String screensotName)
	{
		String resultFileName = getFormedScreenshotName(className, screensotName, screenshotAlbum.size()) + FILE_EXT;
		File resultFile = new File(DEFAULT_SCREENSHOTS_PATH + resultFileName);
		resultFile.getParentFile().mkdirs();
		try {
			FileWriter fileListWriter = new FileWriter(fileList, true);
			fileListWriter.write(resultFileName + '\n');
			fileListWriter.close();

			Bitmap lastScreenshot = screenshotAlbum.get(screenshotAlbum.size() - 1);
			if ( lastScreenshot == null ) {
				resultFile.delete();
				Assert.fail("Screenshot was not created");
			}

			FileOutputStream pictureOutStream = new FileOutputStream(resultFile);
			lastScreenshot.compress(Bitmap.CompressFormat.PNG, 100, pictureOutStream);
			pictureOutStream.close();

			// need to avoid OutOfMemoryException
			lastScreenshot.recycle();

		}
		catch ( FileNotFoundException e ) {
			e.printStackTrace();
			Assert.fail("Cannot create file");
		}
		catch ( IOException e ) {
			e.printStackTrace();
			Assert.fail("Error closing file");
		}
	}

    /**
     * Recursive deletion of directory or file
     * @param dir folder or file to delete
     */
    private void cleanupOutput(File dir) {
        if (dir.isDirectory())
            for (File child : dir.listFiles())
                cleanupOutput(child);
        if (!dir.delete()) Assert.fail("Cannot cleanup output directory");
    }

	/**
	 * Specifies filename format on save
	 */
	public enum SavePathFormat{
		/**
		 * screenshot path will be looks like "package/dimension/activityname_screenshotname"
		 */
		PREFIX,
		/**
		 * screenshot path will be looks like "package/activityname_screenshotname_dimension"
		 */
		SUFFIX
	}

}
