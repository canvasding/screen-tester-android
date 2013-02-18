/*
 * Screenshot based testing framework for Android platform
 * Copyright 2013 by Tomsksoft, http://tomsksoft.com. All rights reserved.
 *
 * This software is licensed under
 * a Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 *
 * MyContact.java
 *
 * Created by lia on 18.02.13 15:35
 */

package com.tomsksoft.demoapp.storage;

import java.io.Serializable;

public class MyContact implements Serializable
{
	
	private static final long	serialVersionUID	= 4377832219041566024L;
	
	public String				name;
	public String				phone;
	public String				email;
	
	public MyContact(final String name, final String phone, final String email)
	{
		this.name = name;
		this.phone = phone;
		this.email = email;
	}
}
