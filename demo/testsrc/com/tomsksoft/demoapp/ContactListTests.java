/*
 * Screenshot based testing framework for Android platform
 * Copyright 2013 by Tomsksoft, http://tomsksoft.com. All rights reserved.
 *
 * This software is licensed under
 * a Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 *
 * ContactListTests.java
 *
 * Created by lia on 18.02.13 15:35
 */

package com.tomsksoft.demoapp;

import android.test.ActivityInstrumentationTestCase2;
import com.tomsksoft.demoapp.activity.ContactsListActivity;
import com.tomsksoft.demoapp.storage.ContactsDbHelper;
import com.tomsksoft.demoapp.storage.DbStruct;
import com.tomsksoft.screentester.DatabaseHelper;
import com.tomsksoft.screentester.ScreenshotTaker;

import java.io.InputStream;

public class ContactListTests extends
        ActivityInstrumentationTestCase2<ContactsListActivity> {

    private ContactsListActivity activity;
    private ContactsDbHelper dbHelper;
	private ScreenshotTaker taker;

    public ContactListTests() {
        super(ContactsListActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        dbHelper = new ContactsDbHelper(getInstrumentation().getTargetContext(), DbStruct.CONTACTS_DB_NAME, DbStruct.CONTACTS_DB_VERSION);
        String datasetName = "dataset.xml";
        InputStream file = getInstrumentation().getContext().getAssets().open(datasetName);
        new DatabaseHelper(dbHelper.getWritableDatabase()).fillDatabase(file);

	    taker = new ScreenshotTaker(getInstrumentation().getTargetContext().getPackageName(),"800x480",ScreenshotTaker.SavePathFormat.SUFFIX);
        activity = getActivity();
    }

    @Override
    protected void tearDown() throws Exception {
        activity.finish();
        dbHelper.close();
    }

    public void testContactsListScreenshot() {

	    taker.doScreenShot(activity,null,null,"contactListData");

    }

}
