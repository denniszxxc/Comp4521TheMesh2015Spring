<?php
	class BookInfo {
		public static $DATABASE_TABLE_NAME = "book_info";
		public static $COLUMN_SERVER_BOOK_ID = "server_book_id"; 
		public static $COLUMN_NAME = "name";
		public static $COLUMN_AUTHOR = "author";
		public static $COLUMN_COVER = "cover";
		public static $COLUMN_ISBN = "iSBN";
	}
	
	class UserInfo {
		public static $DATABASE_TABLE_NAME = "user_info";
		public static $COLUMN_USER_ID = "user_id"; 
		public static $COLUMN_PHONE = "phone"; 
		public static $COLUMN_USER_SETTING= "user_setting"; 
	}
	
	class OwnerBookInfo {
		public static $DATABASE_TABLE_NAME = "owner_book_info";
		public static $COLUMN_SERVER_BOOK_ID = "server_book_id";
		public static $COLUMN_USER_ID = "user_id";
		public static $COLUMN_NUM_OF_BOOK = "num_of_book";
		public static $COLUMN_NUM_OF_BOOK_AVAILABLE = "num_of_book_available";
		public static $COLUMN_ADDED_TIME = "added_time";
		public static $COLUMN_OFFER_TYPE = "offer_type";
	}
	
	class BorrowBookInfo {
		public static $DATABASE_TABLE_NAME = "borrow_book_info";
		public static $COLUMN_LENDER_USER_ID = "user_id";
		public static $COLUMN_SERVER_BOOK_ID = "server_book_id";
		public static $COLUMN_BORROWER_USER_ID = "borrower_user_id";
	}
?>