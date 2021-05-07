package com.example.clothesapp.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class myDbHelper(_context: Context) : SQLiteOpenHelper(
    _context, DATABASE_NAME,null, DATABASE_VERSION
) {

    lateinit var context:Context

    init{
        context=_context
    }
    companion object
    {
        const val DATABASE_NAME="myDb.db"
        const val DATABASE_VERSION=1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(DataBaseInfo.CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DataBaseInfo.DELETE_TABLE)
        onCreate(db)
    }
}