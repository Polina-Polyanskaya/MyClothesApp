package com.example.motivator.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns

class myDbManager(val context:Context) {

    val myDbHelper = com.example.motivator.db.myDbHelper(context)
    var db: SQLiteDatabase? = null

    fun openDb() {
        db = myDbHelper.writableDatabase
    }

    fun insertToDb(
        name: String,
        number: String,
        email: String,
        password: String,
        password_repeat: String
    ) {
        val values = ContentValues().apply {
            put(DataBaseInfo.COLUMN_ITEM_NAME, name)
            put(DataBaseInfo.COLUMN_ITEM_NUMBER, number)
            put(DataBaseInfo.COLUMN_ITEM_EMAIL, email)
            put(DataBaseInfo.COLUMN_ITEM_PASSWORD, password)
            put(DataBaseInfo.COLUMN_ITEM_PASSWORD_REPEAT, password_repeat)
        }
        db?.insert(DataBaseInfo.TABLE_NAME, null, values)
    }

    fun readDbData(): ArrayList<String> {
        val dataList = ArrayList<String>()
        val cursor = db?.query(DataBaseInfo.TABLE_NAME, null, null, null,
            null, null, null)
        with(cursor) {
            while (this?.moveToNext()!!) {
                val dataText = cursor?.getString(cursor.getColumnIndex(DataBaseInfo.COLUMN_ITEM_PASSWORD_REPEAT))
                dataList.add(dataText.toString())
            }
        }
        cursor?.close()
        return dataList
    }

    fun closeDb()
    {
        myDbHelper.close()
    }
}