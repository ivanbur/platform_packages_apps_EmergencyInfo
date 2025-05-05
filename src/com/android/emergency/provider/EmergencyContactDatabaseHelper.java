/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.emergency.provider;

import static android.platform.uiautomatorhelpers.DeviceHelpers.getContext;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;

import com.android.emergency.PreferenceKeys;
import com.android.emergency.preferences.*;
import android.preference.*;

import androidx.preference.PreferenceManager;

import java.util.Calendar;

/**
 * Helper class for opening the database from multiple providers.  Also provides
 * some common functionality.
 */
class EmergencyContactDatabaseHelper extends SQLiteOpenHelper {
    /**
     * Original Emergency Contact Database.
     **/
    private static final int VERSION_1 = 1;

    // Database and table names
    static final String DATABASE_NAME = "emergencycontacts.db";
    static final String EMERGENCY_CONTACTS_TABLE_NAME = "emergency_contacts_tables";
    static final String CONTACT_URI = "contact_uri";

    private static void createEmergencyContactsTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + EMERGENCY_CONTACTS_TABLE_NAME + " (" +
                ContactsContract.Contacts._ID + " INTEGER PRIMARY KEY, " +
                ContactsContract.Contacts.DISPLAY_NAME + " TEXT, " +
                ContactsContract.CommonDataKinds.Phone.NUMBER + " TEXT NOT NULL, " +
                ContactsContract.CommonDataKinds.Phone.TYPE + " INTEGER NOT NULL, " +
                ContactsContract.CommonDataKinds.Phone.LABEL + " TEXT, " +
                ContactsContract.CommonDataKinds.Photo.PHOTO_ID + " INTEGER, " +
                ContactsContract.Contacts.LOOKUP_KEY + " TEXT, " +
                CONTACT_URI + " TEXT NOT NULL" +
                ");");
    }

    public EmergencyContactDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION_1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createEmergencyContactsTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int currentVersion) {}
}
