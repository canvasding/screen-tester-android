/*
 * Screenshot based testing framework for Android platform
 * Copyright 2013 by Tomsksoft, http://tomsksoft.com. All rights reserved.
 *
 * This software is licensed under
 * a Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 *
 * ContactsListActivity.java
 *
 * Created by lia on 18.02.13 15:35
 */

package com.tomsksoft.demoapp.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import com.tomsksoft.demoapp.R;
import com.tomsksoft.demoapp.storage.ContactsContentProvider;
import com.tomsksoft.demoapp.storage.DbStruct;
import com.tomsksoft.demoapp.storage.MyContact;

import java.util.Arrays;
import java.util.List;

public class ContactsListActivity extends Activity implements
		OnItemClickListener
{
	
	public static final String CONTACT = "id";
	public static final String NAME = "name";
	public static final String PHONE = "lastName";
	public static final String EMAIL = "email";
	
	private SimpleCursorAdapter adapter;
	private ListView contactsListView;
	
	public List<MyContact> contacts = Arrays.asList(new MyContact("ContactOne", "011011010", "One@post.org"),
            new MyContact("ContactTwo", "02211010", "TwoOne@post.org"),
            new MyContact("ContactThree", "013311010", "Three@post.org"),
            new MyContact("ContactFour", "011441010", "Four@post.org"),
            new MyContact("ContactFive", "01155010", "Five@post.org"),
            new MyContact("ContactSix", "011661010", "Six@post.org"),
            new MyContact("ContactSeven", "011777010", "Seven@post.org"),
            new MyContact("ContactEight", "011888010", "Eight@post.org"),
            new MyContact("ContactNine", "0199991010", "Nine@post.org"),
            new MyContact("ContactTen", "01010101010", "Ten@post.org"));
	
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts_list_main);
		contactsListView = (ListView)findViewById(R.id.ContactsListView);
		
		Cursor cursor = managedQuery(
				ContactsContentProvider.CONTACT_CONTENT_URI, null, null, null,
				null);
					
		//if there is no records in database
		if (cursor.getCount() == 0){
			for (MyContact contact : contacts) {
				ContentValues cv = new ContentValues();
				cv.put(DbStruct.ContactColumns.CONTACT_FIRST_NAME, contact.name);
				cv.put(DbStruct.ContactColumns.CONTACT_EMAIL, contact.email);
				cv.put(DbStruct.ContactColumns.CONTACT_LAST_NAME, contact.lastName);
				cv.put(DbStruct.ContactColumns.CONTACT_FAVORITE, 0);
				getContentResolver().insert(
						ContactsContentProvider.CONTACT_CONTENT_URI, cv);
			}
		}
		
//		cursor.requery();
		
		String[] from = { DbStruct.ContactColumns.CONTACT_LAST_NAME,
				DbStruct.ContactColumns.CONTACT_EMAIL };
		int[] to = { android.R.id.text1, android.R.id.text2 };
		
		adapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_2, cursor, from, to);
		
		contactsListView.setAdapter(adapter);
		contactsListView.setOnItemClickListener(this);
	}
	
	@Override
	public void onItemClick(final AdapterView<?> arg0, final View arg1,
			final int arg2, final long arg3)
	{
		arg1.getId();
		Intent contactView = new Intent(this, ContactViewActivity.class);
		contactView.putExtra(DbStruct.ContactColumns.CONTACT_ID, arg3);
		startActivity(contactView);
	}

}