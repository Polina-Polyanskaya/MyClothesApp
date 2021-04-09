package com.example.motivator.db

import android.provider.BaseColumns

object DataBaseInfo:BaseColumns {
    const val TABLE_NAME="registrationTable"
    const val COLUMN_ITEM_NAME="name"
    const val COLUMN_ITEM_NUMBER="number"
    const val COLUMN_ITEM_EMAIL="email"
    const val COLUMN_ITEM_PASSWORD="password"
    const val COLUMN_ITEM_PASSWORD_REPEAT="password_repeat"
    
    const val CREATE_TABLE =
        "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "$COLUMN_ITEM_NAME TEXT,"+
                "$COLUMN_ITEM_NUMBER TEXT,"+
                "$COLUMN_ITEM_EMAIL TEXT,"+
                "$COLUMN_ITEM_PASSWORD TEXT,"+
                "$COLUMN_ITEM_PASSWORD_REPEAT TEXT)"

    const val DELETE_TABLE="DROP TABLE IF EXISTS $TABLE_NAME"


}