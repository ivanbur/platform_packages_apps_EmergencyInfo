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

import android.provider.ContactsContract;
import static com.android.emergency.provider.EmergencyContactDatabaseHelper.EMERGENCY_CONTACTS_TABLE_NAME;
import static com.android.emergency.provider.EmergencyContactDatabaseHelper.CONTACT_URI;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.util.Objects;

public class EmergencyContactProvider extends ContentProvider {

    private EmergencyContactDatabaseHelper mOpenHelper;

    public static final String CONTACT_URI_COLUMN = CONTACT_URI;
    public static final Uri AUTHORITY_URI = Uri.parse("content://com.android.emergency.contacts");

    public EmergencyContactProvider() {
    }

    @Override
    @TargetApi(Build.VERSION_CODES.N)
    public boolean onCreate() {
        final Context context = getContext();
        final Context storageContext;
//        if (Utils.isNOrLater()) {
//            // All N devices have split storage areas, but we may need to
//            // migrate existing database into the new device encrypted
//            // storage area, which is where our data lives from now on.
        storageContext = context.createDeviceProtectedStorageContext();
//            if (!storageContext.moveDatabaseFrom(context, ClockDatabaseHelper.DATABASE_NAME)) {
//                LogUtils.wtf("Failed to migrate database: %s", ClockDatabaseHelper.DATABASE_NAME);
//            }
//        } else {
//            storageContext = context;
//        }

        mOpenHelper = new EmergencyContactDatabaseHelper(storageContext);
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projectionIn, String selection,
            String[] selectionArgs, String sort) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        qb.setTables(EMERGENCY_CONTACTS_TABLE_NAME);
        qb.appendWhere(ContactsContract.Contacts._ID + "=");
        qb.appendWhere(Objects.requireNonNull(uri.getLastPathSegment()));

        Cursor ret = qb.query(db, projectionIn, selection, selectionArgs, null, null, sort);

//        if (ret == null) {
//            LogUtils.e("Alarms.query: failed");
//        } else {
        ret.setNotificationUri(getContext().getContentResolver(), uri);
//        }

        return ret;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String where, String[] whereArgs) {
        int count;
        String primaryKey = uri.getLastPathSegment();
        if (TextUtils.isEmpty(where)) {
            where = ContactsContract.Contacts._ID + "=" + primaryKey;
        } else {
            where = ContactsContract.Contacts._ID + "=" + primaryKey + " AND (" + where + ")";
        }
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        count = db.update(EMERGENCY_CONTACTS_TABLE_NAME, values, where, whereArgs);
        notifyChange(getContext().getContentResolver(), uri);
        return count;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues initialValues) {
        long rowId;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        rowId = db.insert(EMERGENCY_CONTACTS_TABLE_NAME, null, initialValues);
        Uri uriResult = ContentUris.withAppendedId(uri, rowId);
        notifyChange(getContext().getContentResolver(), uriResult);
        return uriResult;
    }

    @Override
    public int delete(@NonNull Uri uri, String where, String[] whereArgs) {
        int count;
        String primaryKey = uri.getLastPathSegment();
        if (TextUtils.isEmpty(where)) {
            where = ContactsContract.Contacts._ID + "=" + primaryKey;
        } else {
            where = ContactsContract.Contacts._ID + "=" + primaryKey + " AND (" + where + ")";
        }
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        count = db.delete(EMERGENCY_CONTACTS_TABLE_NAME, where, whereArgs);
        notifyChange(getContext().getContentResolver(), uri);
        return count;
    }

    /**
     * Notify affected URIs of changes.
     */
    private void notifyChange(ContentResolver resolver, Uri uri) {
        resolver.notifyChange(uri, null);
    }
}
