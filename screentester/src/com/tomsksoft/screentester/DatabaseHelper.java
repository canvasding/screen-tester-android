/*
 * Screenshot based testing framework for Android platform
 * Copyright 2013 by Tomsksoft, http://tomsksoft.com. All rights reserved.
 *
 * This software is licensed under
 * a Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 *
 * DatabaseHelper.java
 *
 * Created by lia on 18.02.13 15:35
 */

package com.tomsksoft.screentester;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lia
 * Date: 13.02.13
 * Time: 14:55
 */
public class DatabaseHelper
{
	private final SQLiteDatabase database;

	/**
	 * Creates a helper to manage database content
	 * @param db database to be managed
	 */
	public DatabaseHelper(SQLiteDatabase db)
	{
		database = db;
	}

	/**
	 * This method fills up given database with data, provided in XML document as described in
	 * {@link -linkoffline http://www.dbunit.org/components.html } XMLDataSet
	 *
	 * @param dataset XML document with data
	 */
	public void fillDatabase(InputStream dataset)
	{
		try {
			List<Table> tables = new XMLDataSetReader().parse(dataset, null);
			fillDatabase(tables);
		}
		catch ( IOException ex ) {
			ex.printStackTrace();
		}
		finally {
			database.close();
		}
	}

	/**
	 * This method fills up given database}with data, provided in {@link List}<{@link Table}>, where
	 * each {@link Table} element is representation of table in database.
	 * @param tables list of {@link Table}'s with data
	 */
	public void fillDatabase( List<Table> tables )
	{
		try {
			for (Table table : tables) {
				String tableName = table.getTableName();
				database.delete(tableName, null, null);

				List<String> columns = table.getColumns();
				for (String[] row : table.getRows()) {
					ContentValues cv = new ContentValues();
					for (int col = 0; col < columns.size(); col++) {
						if (row[col] == null) {
							cv.putNull(columns.get(col));
						} else {
							cv.put(columns.get(col), row[col]);
						}
					}
					database.insert(tableName, null, cv);
				}
			}
		}
		finally {
			database.close();
		}
	}
}
