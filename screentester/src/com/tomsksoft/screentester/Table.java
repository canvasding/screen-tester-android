/*
 * Screenshot based testing framework for Android platform
 * Copyright 2013 by Tomsksoft, http://tomsksoft.com. All rights reserved.
 *
 * This software is licensed under
 * a Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 *
 * Table.java
 *
 * Created by lia on 18.02.13 15:35
 */

package com.tomsksoft.screentester;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing table structure
 *
 * @author lia
 */
public class Table {
    private String tableName;

    public String getTableName() {
        return tableName;
    }

    private List<String[]> rows;
    private List<String> columns;

    /**
     * Creating instance with given name and columns
     *
     * @param name    of the table
     * @param columns which will be in the table
     */
    public Table(String name, List<String> columns) {
        tableName = name;
        this.columns = columns;
        rows = new ArrayList<String[]>();
    }

    /**
     * Adds a row with data, length must be equal to the number of columns, otherwise row won't be added
     *
     * @param row
     */
    public void addRow(String[] row) {
        if (row.length == columns.size()) {
            rows.add(row);
        }
    }

    public List<String[]> getRows() {
        return rows;
    }

    public List<String> getColumns() {
        return columns;
    }


}
