/*
 * Screenshot based testing framework for Android platform
 * Copyright 2013 by Tomsksoft, http://tomsksoft.com. All rights reserved.
 *
 * This software is licensed under
 * a Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 *
 * DbStruct.java
 *
 * Created by lia on 18.02.13 15:35
 */

package com.tomsksoft.demoapp.storage;

public interface DbStruct
{
    public String CONTACTS_DB_NAME = "demoapp_db";
    public int CONTACTS_DB_VERSION = 1;

    public interface Tables
    {
        public String CONTACTS = "contacts";
        public String CONTACT_ITEMS = "contact_items";
    }

    public interface ContactColumns
    {
        public String CONTACT_ID = "_id";
        public String CONTACT_FIRST_NAME = "first_name";
        public String CONTACT_LAST_NAME = "last_name";
        public String CONTACT_DISPLAY_NAME = "display_name";

    }
}