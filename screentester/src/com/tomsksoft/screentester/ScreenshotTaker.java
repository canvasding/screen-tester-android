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
import android.graphics.Rect;
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
	}

	/**
	 * Creates an instance of ScreenshotTaker with format params.
	 * This instance will save screenshots to SDCard in "/mnt/sdcard/screentester/"
	 * @param format declares how result file name looks like
	 */
	public ScreenshotTaker(SavePathFormat format)
	{
		saveFormat = format;

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
	 * @param excludedViews array of ViewObject with ids and params that we want to exclude from screening. Can be null.
	 *
	 *
	 *
	 *
	 * @param screenshotName name, that will identify screenshot
	 */
	@SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
	public void doScreenShot(
			final Activity activity, Integer screeningViewId, List<ViewObject> excludedViews, String screenshotName)
	{
		if (activity == null) Assert.fail("Activity is null. Maybe not started or already finished");

		Display disp = activity.getWindowManager().getDefaultDisplay();
		resolution = new Point(disp.getWidth(),disp.getHeight());
		this.savePath = activity.getPackageName();
		ArrayList<Rect> areas = new ArrayList<Rect>();

		if ( screeningViewId == null ) {
			screeningViewId = android.R.id.content;
		}
		if ( excludedViews != null && excludedViews.size() > 0 ) {
			for ( final ViewObject view : excludedViews ) {
				if (view.needFullExclude())
					activity.runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						activity.findViewById(view.getViewId()).setVisibility(View.INVISIBLE);
					}
				});
				areas.add(view.computeExcludedArea(activity, screeningViewId));
			}
		}

		final Integer finalScreeningViewId = screeningViewId;
		Runnable getDrawningCacheRunnable = new Runnable()
		{
			@Override
			public void run()
			{
				View root = activity.findViewById(android.R.id.content);
				root.setFocusable(true);
				root.setFocusableInTouchMode(true);
				root.requestFocus();
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
			saveScreenshot(activity.getLocalClassName(),screenshotName, areas);
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

	private String getFormedScreenshotName(String className,String screenshotName, int number)
	{
		switch ( saveFormat ) {
			case PREFIX:
				return String.format("%s/%dx%d/%s_%s%d", savePath, resolution.x, resolution.y,className,screenshotName,number);
			case SUFFIX:
				return String.format("%s/%s_%s%d_%dx%d", savePath, className, screenshotName, number, resolution.x,resolution.y);
			default:
				return null;
		}
	}

	private void saveScreenshot(String className,String screenshotName, ArrayList<Rect> areas)
	{
		String resultFileName = getFormedScreenshotName(className, screenshotName, screenshotAlbum.size()) + FILE_EXT;
		File resultFile = new File(DEFAULT_SCREENSHOTS_PATH + resultFileName);
		resultFile.getParentFile().mkdirs();
		String record = formRecord(resultFileName,areas);
		try {
			Bitmap lastScreenshot = screenshotAlbum.get(screenshotAlbum.size() - 1);
			if ( lastScreenshot == null ) {
				resultFile.delete();
				Assert.fail("Screenshot was not created");
			}

			FileOutputStream pictureOutStream = new FileOutputStream(resultFile);
			lastScreenshot.compress(Bitmap.CompressFormat.PNG, 100, pictureOutStream);
			pictureOutStream.close();

			// need to avoid OutOfMemoryException, o'rly?
			lastScreenshot.recycle();

			FileWriter fileListWriter = new FileWriter(fileList, true);
			fileListWriter.write(record + '\n');
			fileListWriter.close();
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

	private String formRecord(String filename, ArrayList<Rect> objects){
		if ( objects == null ) return filename;
		String res = filename + ":";
		for ( Rect rect: objects){
			if (rect == null) continue;
			res += String.format("%s,%s,%s,%s;", rect.left, rect.top, rect.right, rect.bottom);
		}
		return res.trim();
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
		 * screenshot path will be look like "package/dimension/activityname_screenshotname"
		 */
		PREFIX,
		/**
		 * screenshot path will be look like "package/activityname_screenshotname_dimension"
		 */
		SUFFIX
	}

}
