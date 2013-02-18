/*
 * Screenshot based testing framework for Android platform
 * Copyright 2013 by Tomsksoft, http://tomsksoft.com. All rights reserved.
 *
 * This software is licensed under
 * a Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 *
 * ContactViewTests.java
 *
 * Created by lia on 18.02.13 15:35
 */

package com.tomsksoft.demoapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.test.ActivityInstrumentationTestCase2;
import com.tomsksoft.demoapp.activity.ContactViewActivity;
import com.tomsksoft.demoapp.storage.ContactsContentProvider;
import com.tomsksoft.demoapp.storage.ContactsDbHelper;
import com.tomsksoft.demoapp.storage.DbStruct;
import com.tomsksoft.screentester.DatabaseHelper;
import com.tomsksoft.screentester.ScreenshotTaker;

import java.io.InputStream;

public class ContactViewTests extends
        ActivityInstrumentationTestCase2<ContactViewActivity> {

    private ContactsDbHelper dbHelper;
	private ScreenshotTaker taker;


	public ContactViewTests() {
        super(ContactViewActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        dbHelper = new ContactsDbHelper(getInstrumentation().getTargetContext(), DbStruct.CONTACTS_DB_NAME, DbStruct.CONTACTS_DB_VERSION);
        String datasetName = "dataset.xml";
        InputStream file = getInstrumentation().getContext().getAssets().open(datasetName);
	    new DatabaseHelper(dbHelper.getWritableDatabase()).fillDatabase(file);

	    taker = new ScreenshotTaker(getInstrumentation().getTargetContext().getPackageName(),"800x480",ScreenshotTaker.SavePathFormat.SUFFIX);
    }

    @Override
    protected void tearDown() throws Exception {
        dbHelper.close();
    }

    public void testContactViewScreenshot() {

		/*
		 * do not use root.managedQuery()! 
		 * somehow after launch child activity cursor data looses, or it's requerying
		 */
        Context targetContext = getInstrumentation().getTargetContext();
        Cursor cursor = targetContext.getContentResolver().query(
                ContactsContentProvider.CONTACT_CONTENT_URI, null, null, null,
                null);

        cursor.moveToFirst(); // maybe set if number of records in db equals number got from query
        while (!cursor.isAfterLast()) {
            long id = cursor.getLong(cursor
                    .getColumnIndex(DbStruct.ContactColumns.CONTACT_ID));

            Intent contactViewIntent = new Intent(targetContext,
                    ContactViewActivity.class);
            contactViewIntent.putExtra(DbStruct.ContactColumns.CONTACT_ID, id);

            final Activity activity = launchActivityWithIntent(
                    getInstrumentation().getTargetContext().getPackageName(),
                    ContactViewActivity.class, contactViewIntent);

	        taker.doScreenShot(activity,null,new int[] {com.tomsksoft.demoapp.R.id.contact_name},"contantData");

            activity.finish();
            cursor.moveToNext();
        }
        cursor.close();
    }

}
