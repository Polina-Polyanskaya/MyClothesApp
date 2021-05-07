package com.example.clothesapp.db

import android.provider.BaseColumns

object DataBaseInfo:BaseColumns {
    const val TABLE_NAME="LIKED_CLOTHES_TABLE"
    const val COLUMN_ITEM_PATH="path"
    const val COLUMN_ITEM_COMMENT="comment"
    
    const val CREATE_TABLE =
        "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "$COLUMN_ITEM_PATH TEXT,"+
                "$COLUMN_ITEM_COMMENT TEXT)"

    const val DELETE_TABLE="DROP TABLE IF EXISTS $TABLE_NAME"
}