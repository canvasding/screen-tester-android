/*
 * Screenshot based testing framework for Android platform
 * Copyright 2013 by Tomsksoft, http://tomsksoft.com. All rights reserved.
 *
 * This software is licensed under
 * a Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 *
 * ContactsContentProvider.java
 *
 * Created by lia on 18.02.13 15:35
 */

package com.tomsksoft.demoapp.storage;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

public class ContactsContentProvider extends ContentProvider
{
	/*
	 * Constants
	 */
	private static final String AUTHORITY = "com.tomsksoft.demoapp.dbprovider";
	private static final String CONTACTS_PATH = "contacts";

	/*
	 * URI to tables this uri's used from outside to access tables in database
	 * when calling query(), insert(), update(), delete()
	 */
	public static final Uri CONTACT_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + CONTACTS_PATH);
	/*
	 * Mime-types
	 */
	private static final String CONTACT_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
			+ AUTHORITY + "." + CONTACTS_PATH;
	private static final String CONTACT_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
			+ AUTHORITY + "." + CONTACTS_PATH;

	private static final int URI_CONTACTS = 1;
	private static final int URI_CONTACTS_ID = 2;

	/*
	 * URI Matcher this matcher used to convert given uri to the constant, that
	 * will be used inside the class
	 */
	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, CONTACTS_PATH, URI_CONTACTS);
		uriMatcher.addURI(AUTHORITY, CONTACTS_PATH + "/#", URI_CONTACTS_ID);
	}
	
	private ContactsDbHelper dbHelper;
	private SQLiteDatabase db;
	
	@Override
	public boolean onCreate()
	{
		dbHelper = new ContactsDbHelper(getContext(),
				DbStruct.CONTACTS_DB_NAME, DbStruct.CONTACTS_DB_VERSION);
		return true;
	}
	
	@Override
	public Cursor query(final Uri uri, final String[] projection,
			String selection, final String[] selectionArgs, String sortOrder)
	{
		String table;
		String id;
		switch (uriMatcher.match(uri)) {
			case URI_CONTACTS:
				table = DbStruct.Tables.CONTACTS;
				break;
			case URI_CONTACTS_ID:
				table = DbStruct.Tables.CONTACTS;
				id = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection)) {
					selection = DbStruct.ContactColumns.CONTACT_ID + " = " + id;
				}
				else {
					selection = selection + " AND "
							+ DbStruct.ContactColumns.CONTACT_ID + " = " + id;
				}
				break;
			default:
				throw new IllegalArgumentException("Wrong URI: " + uri);
		}
		db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query(table, projection, selection, selectionArgs,
				null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(),
				CONTACT_CONTENT_URI);
//		db.close();
		return cursor;
	}
	
	@Override
	public int delete(final Uri uri, String selection,
			final String[] selectionArgs)
	{
		String table;
		String id;
		switch (uriMatcher.match(uri)) {
			case URI_CONTACTS:
				table = DbStruct.Tables.CONTACTS;
				break;
			case URI_CONTACTS_ID:
				table = DbStruct.Tables.CONTACTS;
				id = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection)) {
					selection = DbStruct.ContactColumns.CONTACT_ID + " = " + id;
				}
				else {
					selection = selection + " AND "
							+ DbStruct.ContactColumns.CONTACT_ID + " = " + id;
				}
				break;
			default:
				throw new IllegalArgumentException("Wrong URI: " + uri);
		}
		db = dbHelper.getWritableDatabase();
		int cnt = db.delete(table, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
//		db.close();
		return cnt;
	}
	
	@Override
	public String getType(final Uri uri)
	{
		switch (uriMatcher.match(uri)) {
			case URI_CONTACTS:
				return CONTACT_CONTENT_TYPE;
			case URI_CONTACTS_ID:
				return CONTACT_CONTENT_ITEM_TYPE;
		}
		return null;
	}
	
	@Override
	public Uri insert(final Uri uri, final ContentValues values)
	{
		String table;
		Uri tableUri;
		switch (uriMatcher.match(uri)) {
			case URI_CONTACTS:
				table = DbStruct.Tables.CONTACTS;
				tableUri = CONTACT_CONTENT_URI;
				break;
			default:
				throw new IllegalArgumentException("Wrong URI: " + uri);
		}
		db = dbHelper.getWritableDatabase();
		long rowID = db.insert(table, null, values);
		Uri resultUri = ContentUris.withAppendedId(tableUri, rowID);
		getContext().getContentResolver().notifyChange(resultUri, null);
//		db.close();
		return resultUri;
	}
	
	@Override
	public int update(final Uri uri, final ContentValues values,
			String selection, final String[] selectionArgs)
	{
		String table;
		String id;
		switch (uriMatcher.match(uri)) {
			case URI_CONTACTS:
				table = DbStruct.Tables.CONTACTS;
				break;
			case URI_CONTACTS_ID:
				table = DbStruct.Tables.CONTACTS;
				id = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection)) {
					selection = DbStruct.ContactColumns.CONTACT_ID + " = " + id;
				}
				else {
					selection = selection + " AND "
							+ DbStruct.ContactColumns.CONTACT_ID + " = " + id;
				}
				break;
			default:
				throw new IllegalArgumentException("Wrong URI: " + uri);
		}
		db = dbHelper.getWritableDatabase();
		int cnt = db.update(table, values, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
//		db.close();
		return cnt;
	}
	
}
