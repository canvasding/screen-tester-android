/*
 * Screenshot based testing framework for Android platform
 * Copyright 2013 by Tomsksoft, http://tomsksoft.com. All rights reserved.
 *
 * This software is licensed under
 * a Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 *
 * ContactViewActivity.java
 *
 * Created by lia on 18.02.13 15:35
 */

package com.tomsksoft.demoapp.activity;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import com.tomsksoft.demoapp.R;
import com.tomsksoft.demoapp.storage.ContactsContentProvider;
import com.tomsksoft.demoapp.storage.DbStruct;

public class ContactViewActivity extends Activity
{
	
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_view_main);
		
		String id = Long.toString(getIntent().getExtras().getLong(
				DbStruct.ContactColumns.CONTACT_ID));
		
		Cursor cursor = managedQuery(
				ContactsContentProvider.CONTACT_CONTENT_URI, null,
				DbStruct.ContactColumns.CONTACT_ID + " = ?",
				new String[] { id }, null);
		if (cursor.moveToFirst()) {
			
			String displayName = cursor
					.getString(cursor
							.getColumnIndex(DbStruct.ContactColumns.CONTACT_DISPLAY_NAME));
			String firstName = cursor
					.getString(cursor
							.getColumnIndex(DbStruct.ContactColumns.CONTACT_FIRST_NAME));
			String lastName = cursor.getString(cursor
					.getColumnIndex(DbStruct.ContactColumns.CONTACT_LAST_NAME));
			
			TextView tv = (TextView)findViewById(R.id.contact_name);
			tv.setText(displayName);
			tv = (TextView)findViewById(R.id.contact_email);
			tv.setText(firstName);
			tv = (TextView)findViewById(R.id.contact_phone);
			tv.setText(lastName);
		}
	}
	
}
