package com.comp4521.bookscan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.comp4521.bookscan.BookInfoToSQLiteContract.BookInfo;
import com.comp4521.bookscan.BookInfoToSQLiteContract.OwnerBookInfo;

public class BookInfoToSQLite {
	LocalDatabaseHelper mDbHelper;
	private Context activityContext;
	
	BookInfoToSQLite(Context context) {
		activityContext = context;
		mDbHelper = new LocalDatabaseHelper(activityContext);
	}
	
	public long searchBookByISBN(String iSBN) {
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		String where = BookInfo.COLUMN_ISBN + "='" + iSBN + "'";
		Cursor cursor = db.query(BookInfo.DATABASE_TABLE_NAME, null, where, null, null, null, null);
		if(cursor.moveToNext()) {
			long bookId = cursor.getLong(0);
			cursor.close();
			return bookId;
		} else {
			cursor.close();
			return -1;
		}
	}
	
	public boolean insertDataToBookInfo(long bookId, String bookName, String author, String coverLink, String iSBN) {
		// Gets the data repository in write mode
		SQLiteDatabase db = mDbHelper.getWritableDatabase();

		// Create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		values.put(BookInfo.COLUMN_BOOK_ID, bookId);
		values.put(BookInfo.COLUMN_NAME, bookName);
		values.put(BookInfo.COLUMN_AUTHOR, author);
		values.put(BookInfo.COLUMN_COVER, coverLink);
		values.put(BookInfo.COLUMN_ISBN, iSBN);
		
		// Insert the new row, returning the primary key value of the new row
		long id = db.insert(BookInfo.DATABASE_TABLE_NAME, null, values);
		return id > 0; // id > 0 mean insert is success
	}
	
	public boolean insertDataToOwnerBookInfo(long bookId, String time, String type) {
		// Gets the data repository in write mode
		SQLiteDatabase db = mDbHelper.getWritableDatabase();

		// Create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		values.put(BookInfo.COLUMN_BOOK_ID, bookId);
		values.put(OwnerBookInfo.COLUMN_NUM_OF_BOOK, 1);
		values.put(OwnerBookInfo.COLUMN_NUM_OF_BOOK_AVAILABLE, 1);
		values.put(OwnerBookInfo.COLUMN_ADDED_TIME, time);
		values.put(OwnerBookInfo.COLUMN_OFFER_TYPE, type);
		
		// Insert the new row, returning the primary key value of the new row
		long id = db.insert(OwnerBookInfo.DATABASE_TABLE_NAME, null, values);
		return id > 0;
	}
	
	public int[] searchOwnerBookDataByIdAndType(long bookId, String type) {
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		String where = OwnerBookInfo.COLUMN_BOOK_ID + "=" + bookId + " AND " + OwnerBookInfo.COLUMN_OFFER_TYPE + "='" + type + "'";
		Cursor cursor = db.query(OwnerBookInfo.DATABASE_TABLE_NAME, null, where, null, null, null, null);
		if(cursor.moveToNext()) {
			int[] numOfBookAndAvailable = new int[2];
			numOfBookAndAvailable[0] = cursor.getInt(1);
			numOfBookAndAvailable[1] = cursor.getInt(2);
			cursor.close();
			return numOfBookAndAvailable;
		} else {
			cursor.close();
			return null;
		}
	}
	
	public boolean updateDataInOwnerBookInfoById(long bookId, int[] numOfBookAndAvailable, String time) { // update number of book and add date
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(OwnerBookInfo.COLUMN_NUM_OF_BOOK, ++numOfBookAndAvailable[0]);
		values.put(OwnerBookInfo.COLUMN_NUM_OF_BOOK_AVAILABLE, ++numOfBookAndAvailable[1]);
		values.put(OwnerBookInfo.COLUMN_ADDED_TIME, time);
		
		String where = OwnerBookInfo.COLUMN_BOOK_ID + "=" + bookId;
		return db.update(OwnerBookInfo.DATABASE_TABLE_NAME, values, where, null) > 0; 
	}
	
	public void close() {
		mDbHelper.close();
	}
}
