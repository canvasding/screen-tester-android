/*
 * Screenshot based testing framework for Android platform
 * Copyright 2013 by Tomsksoft, http://tomsksoft.com. All rights reserved.
 *
 * This software is licensed under
 * a Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 *
 * ContactsDbHelper.java
 *
 * Created by lia on 18.02.13 15:35
 */

package com.tomsksoft.demoapp.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ContactsDbHelper extends SQLiteOpenHelper
{
	
	public ContactsDbHelper(final Context context, final String name, final int version)
	{
		super(context, name, null, version);
	}
	
	@Override
	public void onCreate(final SQLiteDatabase db)
	{
		// table for local contacts storing
		db.execSQL("CREATE TABLE " + DbStruct.Tables.CONTACTS + " ("
				+ DbStruct.ContactColumns.CONTACT_ID + " INTEGER PRIMARY KEY,"
				+ DbStruct.ContactColumns.CONTACT_FIRST_NAME + " TEXT,"
				+ DbStruct.ContactColumns.CONTACT_LAST_NAME + " TEXT,"
				+ DbStruct.ContactColumns.CONTACT_DISPLAY_NAME + " TEXT" + ");");
	}
	
	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion)
	{
		// TODO Auto-generated method stub
	}
}
