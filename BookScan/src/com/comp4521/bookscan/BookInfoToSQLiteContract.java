package com.comp4521.bookscan;

import android.provider.BaseColumns;

public final class BookInfoToSQLiteContract {
	public static abstract class BookInfo implements BaseColumns {
		public static final String DATABASE_TABLE_NAME = "book_info";
		public static final String COLUMN_BOOK_ID = "book_id"; 
		public static final String COLUMN_NAME = "name";
		public static final String COLUMN_AUTHOR = "author";
		public static final String COLUMN_COVER = "cover";
		public static final String COLUMN_ISBN = "iSBN";
	}
	
	public static abstract class OwnerBookInfo implements BaseColumns {
		public static final String DATABASE_TABLE_NAME = "owner_book_info";
		public static final String COLUMN_BOOK_ID = "book_id";
		public static final String COLUMN_NUM_OF_BOOK = "num_of_book";
		public static final String COLUMN_NUM_OF_BOOK_AVAILABLE = "num_of_book_available";
		public static final String COLUMN_ADDED_TIME = "added_time";
		public static final String COLUMN_OFFER_TYPE = "offer_type";
	}
	
	public static abstract class BorrowBookInfo implements BaseColumns {
		public static final String DATABASE_TABLE_NAME = "borrow_book_info";
		public static final String COLUMN_BOOK_ID = "book_id";
		public static final String COLUMN_BORROW_USER_ID = "borrow_user_id";
	}
}