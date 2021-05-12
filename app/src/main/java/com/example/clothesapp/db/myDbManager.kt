package com.example.clothesapp.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import com.example.clothesapp.classesForActivities.Page

class myDbManager(val context:Context) {

    val helper = myDbHelper(context)
    var db: SQLiteDatabase? = null

    fun openDb() {
        db = helper.writableDatabase
    }

    fun insertToDb(
        path: String,
        comment: String
    ) {
        val values = ContentValues().apply {
            put(DataBaseInfo.COLUMN_ITEM_PATH, path)
            put(DataBaseInfo.COLUMN_ITEM_COMMENT, comment)
        }
        db?.insert(DataBaseInfo.TABLE_NAME, null, values)
    }

    fun readDbData(): ArrayList<Page> {
        val dataList = ArrayList<Page>()
        val cursor = db?.query(
            DataBaseInfo.TABLE_NAME, null, null, null,
            null, null, null
        )
        with(cursor) {
            while (this?.moveToNext()!!) {
                val path = cursor?.getString(cursor.getColumnIndex(DataBaseInfo.COLUMN_ITEM_PATH))
                val comment =
                    cursor?.getString(cursor.getColumnIndex(DataBaseInfo.COLUMN_ITEM_COMMENT))
                dataList.add(
                    Page(
                        path,
                        "",
                        comment
                    )
                )
            }
        }
        cursor?.close()
        return dataList
    }

    fun deleteString(strForSearch: String) {
        db?.delete(
            DataBaseInfo.TABLE_NAME,
            "${DataBaseInfo.COLUMN_ITEM_PATH} = ?",
            arrayOf(strForSearch)
        )
    }

    fun clearDbData()
    {
        db?.delete(DataBaseInfo.TABLE_NAME,null,null)
    }

    fun closeDb()
    {
        helper.close()
    }
}