package com.comp4521.bookscan;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.comp4521.bookscan.BookInfoToSQLiteContract.BookInfo;
import com.comp4521.bookscan.BookInfoToSQLiteContract.BorrowBookInfo;
import com.comp4521.bookscan.BookInfoToSQLiteContract.OwnerBookInfo;

// this file may be edit by anyone of group member, since this file it is the schema of SQLiteDB
public class LocalDatabaseHelper extends SQLiteOpenHelper {
	private static final String LOCAL_DATABASE_NAME = "MyLibrary.db";
	private static final int LOCAL_DATABASE_VERSION = 1;
	private static final String SQL_CREATE_TABLE_BookInfo = "create table " +
			BookInfo.DATABASE_TABLE_NAME + "(" +
			BookInfo.COLUMN_BOOK_ID + " integer primary key, " +
			BookInfo.COLUMN_NAME + " text not null, " +
			BookInfo.COLUMN_AUTHOR + " text not null, " +
			BookInfo.COLUMN_COVER + " text not null, " +
			BookInfo.COLUMN_ISBN + " text not null);";
	private static final String SQL_CREATE_TABLE_OwnerBookInfo = "create table " +
			OwnerBookInfo.DATABASE_TABLE_NAME + "(" +
			OwnerBookInfo.COLUMN_BOOK_ID + " integer primary key, " +
			OwnerBookInfo.COLUMN_NUM_OF_BOOK + " int, " +
			OwnerBookInfo.COLUMN_NUM_OF_BOOK_AVAILABLE + " int, " +
			OwnerBookInfo.COLUMN_ADDED_TIME + " text not null, " +
			OwnerBookInfo.COLUMN_OFFER_TYPE + " text not null, " +
			"FOREIGN KEY(" + OwnerBookInfo.COLUMN_BOOK_ID + ") REFERENCES " + BookInfo.DATABASE_TABLE_NAME + "(" + BookInfo.COLUMN_BOOK_ID + ") );";
	private static final String SQL_CREATE_TABLE_BorrowBookInfo = "create table " +
			BorrowBookInfo.DATABASE_TABLE_NAME + "(" +
			BorrowBookInfo.COLUMN_BOOK_ID + " int not null, " +
			BorrowBookInfo.COLUMN_BORROW_USER_ID + " text not null, " +
			"PRIMARY KEY (" + BorrowBookInfo.COLUMN_BOOK_ID + ", " + BorrowBookInfo.COLUMN_BORROW_USER_ID + "), " +
			"FOREIGN KEY(" + BorrowBookInfo.COLUMN_BOOK_ID + ") REFERENCES " + BookInfo.DATABASE_TABLE_NAME + "(" + BookInfo.COLUMN_BOOK_ID + ") );";
	private static final String SQL_DELETE_TABLE_BookInfo = 
			"DROP TABLE IF EXIStS " + BookInfo.DATABASE_TABLE_NAME;
	private static final String SQL_DELETE_TABLE_OwnerBookInfo = 
			"DROP TABLE IF EXIStS " + OwnerBookInfo.DATABASE_TABLE_NAME;
	private static final String SQL_DELETE_TABLE_BorrowBookInfo = 
			"DROP TABLE IF EXIStS " + BorrowBookInfo.DATABASE_TABLE_NAME;
	
	LocalDatabaseHelper(Context context) {
		super(context, LOCAL_DATABASE_NAME, null, LOCAL_DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(SQL_CREATE_TABLE_BookInfo);
		db.execSQL(SQL_CREATE_TABLE_OwnerBookInfo);
		db.execSQL(SQL_CREATE_TABLE_BorrowBookInfo);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL(SQL_DELETE_TABLE_BookInfo);
		db.execSQL(SQL_DELETE_TABLE_OwnerBookInfo);
		db.execSQL(SQL_DELETE_TABLE_BorrowBookInfo);
		onCreate(db);
	}
}
