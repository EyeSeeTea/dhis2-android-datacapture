/*
 * Copyright (c) 2015, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.dhis2.mobile.sdk.persistence.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public final class DbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "dhis2.db";
    private static final String ENABLE_FOREIGN_KEYS = "PRAGMA foreign_keys = ON;";
    private static final int DATABASE_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DbSchema.CREATE_ORGANIZATION_UNIT_TABLE);
        db.execSQL(DbSchema.CREATE_DATA_SET_TABLE);
        db.execSQL(DbSchema.CREATE_UNIT_DATA_SETS_TABLE);
        db.execSQL(DbSchema.CREATE_CATEGORY_COMBOS_TABLE);
        db.execSQL(DbSchema.CREATE_DATA_SET_CATEGORY_COMBO_TABLE);
        db.execSQL(DbSchema.CREATE_CATEGORIES_TABLE);
        db.execSQL(DbSchema.CREATE_COMBO_CATEGORIES_TABLE);
        db.execSQL(DbSchema.CREATE_CATEGORY_OPTIONS_TABLE);
        db.execSQL(DbSchema.CREATE_CATEGORIES_TO_OPTIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DbSchema.DROP_ORGANIZATION_UNIT_TABLE);
        db.execSQL(DbSchema.DROP_DATA_SET_TABLE);
        db.execSQL(DbSchema.DROP_UNIT_DATA_SETS_TABLE);
        db.execSQL(DbSchema.DROP_CATEGORY_COMBOS_TABLE);
        db.execSQL(DbSchema.DROP_DATA_SET_CATEGORY_COMBO_TABLE);
        db.execSQL(DbSchema.DROP_CATEGORIES_TABLE);
        db.execSQL(DbSchema.DROP_COMBO_CATEGORIES_TABLE);
        db.execSQL(DbSchema.DROP_CATEGORY_OPTIONS_TABLE);
        db.execSQL(DbSchema.DROP_CATEGORIES_TO_OPTIONS_TABLE);
        onCreate(db);
    }

    /**
     * Enabling support of ForeignKeys in SQLite database
     * each time it is being used. Works on android from 2.2
     */
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL(ENABLE_FOREIGN_KEYS);
        }
    }
}
