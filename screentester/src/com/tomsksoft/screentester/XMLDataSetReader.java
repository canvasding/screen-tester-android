/*
 * Screenshot based testing framework for Android platform
 * Copyright 2013 by Tomsksoft, http://tomsksoft.com. All rights reserved.
 *
 * This software is licensed under
 * a Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 *
 * XMLDataSetReader.java
 *
 * Created by lia on 18.02.13 15:35
 */

package com.tomsksoft.screentester;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * This class reads xml dataset file, than according to the structure described in dbunit XMLDataSet {@linkplain plain http://www.dbunit.org/components.html }
 *
 * @author lia
 */

public class XMLDataSetReader {
    private static final String DATASET = "dataset";
    private static final String TABLE = "table";
    private static final String COLUMN = "column";
    private static final String ROW = "row";
    private static final String VALUE = "value";
    private static final String NAME_ATTR = "name";
    private static final String NULL = "null";
    private static final String NS = null;

    /**
     * Parses input with the given encoding (can be null, so then it try to determine input encoding)
     *
     * @param in       contains a raw byte input stream of possibly unknown encoding (when inputEncoding is null).
     * @param encoding if not null it MUST be used as encoding for inputStream
     * @return
     * @throws IOException if input stream cannot be closed
     */
    public List<Table> parse(InputStream in, String encoding) throws IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, encoding);
            parser.nextTag();
            return readDataSet(parser);
        } catch (XmlPullParserException ex) {
            ex.printStackTrace();
        } finally {
            in.close();
        }
        return null;
    }

    /**
     * Reads contents of {@code <dataset>} element. After finish parser points at {@code </dataset>} tag
     *
     * @param parser must be pointing on {@code <dataset>} tag
     * @return {@link List} of {@link Table}'s with data from dataset
     * @throws XmlPullParserException
     * @throws IOException
     */
    private List<Table> readDataSet(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        List<Table> tables = new ArrayList<Table>();

        parser.require(XmlPullParser.START_TAG, NS, DATASET);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals(TABLE)) {
                tables.add(readTable(parser));
            } else {
                skip(parser);
            }
        }
        return tables;
    }

    /**
     * Reads contents of {@code <table>} element. Parser must pointing on the {@code <table>} tag.
     * After finish parser points at {@code </table>} tag
     *
     * @param parser must be pointing on {@code <table>} tag
     * @return {@link Table} with data from the tag
     * @throws XmlPullParserException
     * @throws IOException
     */
    private Table readTable(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, NS, TABLE);

        String tableName = parser.getAttributeValue(NS, NAME_ATTR);
        List<String> columns = new ArrayList<String>();
        Table table = new Table(tableName, columns);

        do {
            parser.next();
            if (parser.getEventType() == XmlPullParser.START_TAG) {
                if (parser.getName() == COLUMN) {
                    columns.add(readColumn(parser));
                } else if (parser.getName() == ROW) {
                    table.addRow(readRow(parser, columns.size()));
                }
            }
        }
        while (!(parser.getEventType() == XmlPullParser.END_TAG
                && parser.getName() == TABLE));

        return table;
    }

    /**
     * Reads contents of {@code <column>} element. Parser must pointing on the {@code <column>} tag.
     * After finish parser points at {@code </column>} tag
     *
     * @param parser must be pointing on {@code <column>} tag
     * @return {@link String} name of the column
     * @throws XmlPullParserException
     * @throws IOException
     */
    private String readColumn(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, NS, COLUMN);

        String name = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            switch (parser.getEventType()) {
                case (XmlPullParser.START_TAG):
                    skip(parser);
                    break;
                case (XmlPullParser.TEXT):
                    name = parser.getText();
                    break;
            }
        }
        return name;
    }

    /**
     * Reads contents of {@code <row>} element. Parser must pointing on the {@code <row>} tag.
     * After finish parser points at {@code </row>} tag
     *
     * @param parser must be pointing on {@code <row>} tag
     * @return {@link String} array, every value corresponding to the column name
     * @throws XmlPullParserException if the number of values in rows exceeds column number
     * @throws IOException
     */
    private String[] readRow(XmlPullParser parser, int size)
            throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, NS, ROW);

        String[] row = new String[size];
        int i = 0;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            if (i >= size) {
                throw new XmlPullParserException("More values in row then expected");
            }
            if (parser.getName() == NULL) {
                row[i] = null;
                i++;
                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }
                    skip(parser);
                }
            } else if (parser.getName() == VALUE) {
                row[i] = parser.nextText();
                i++;
                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }
                    skip(parser);
                }
            }
        }

        while (i < size) {
            row[i] = null;
            i++;
        }

        return row;
    }

    /**
     * Skips unnessary tag with all sub-tags, jf parser currently on start tag, otherwise throws IllegalStateException
     *
     * @param parser
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void skip(XmlPullParser parser) throws XmlPullParserException,
            IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
