/*
 * Screenshot based testing framework for Android platform
 * Copyright 2013 by Tomsksoft, http://tomsksoft.com. All rights reserved.
 *
 * This software is licensed under
 * a Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 *
 * viewObject.java
 *
 * Created by lia on 16.05.13 11:28
 */

package com.tomsksoft.screentester;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;

/**
 * Created with IntelliJ IDEA.
 * User: lia
 * Date: 16.05.13
 * Time: 11:28
 */
public class ViewObject
{
	private final int viewId;
	private final Rect bounds;

	/**
	 * Constructs ViewObject that need to be fully exclude from screenshot
	 */
	public ViewObject(int viewId)
	{
		this.viewId = viewId;
		bounds = null;
	}

	/**
	 * Constructs ViewObject with area of this view that need to be excluded
	 * ex. new ViewObject(R.id.progressbar, new Rect(10,10,50,40)) will exclude part of progress bar
	 * from 10 to 50 percent horizontally and from 10 to 50 percent vertically
	 */
	public ViewObject(int viewId, Rect bounds)
	{
		this.viewId = viewId;
		this.bounds = bounds;
	}

	public int getViewId()
	{
		return viewId;
	}

	public boolean needFullExclude()
	{
		return bounds == null;
	}

	public Rect computeExcludedArea(Activity activity, int rootId)
	{
		View view = activity.findViewById(viewId);
		View root = activity.findViewById(rootId);
		if (view == null || root == null) return null;
		int[] viewAnchor = new int[2];
		int[] rootAnchor = new int[2];
		Rect excludedArea = new Rect();
		root.getLocationInWindow(rootAnchor);
		view.getLocationInWindow(viewAnchor);
		float widthPercent = (float)view.getWidth()/100;
		float heightPercent = (float)view.getHeight()/100;
		if (bounds == null) return null;
		excludedArea.set(
				viewAnchor[0] - rootAnchor[0] + (int) (bounds.left*widthPercent),
				viewAnchor[1] - rootAnchor[1] + (int) (bounds.top*heightPercent),
				viewAnchor[0] - rootAnchor[0] + (int) (bounds.right*widthPercent),
				viewAnchor[1] - rootAnchor[1] + (int) (bounds.bottom*heightPercent));
		return excludedArea;
	}

//	public String getString(Activity activity){
//		Rect rect = this.computeExcludedArea(activity);
//		return String.format("%s,%s,%s,%s", rect.left, rect.top, rect.right, rect.bottom);
//	}
}
