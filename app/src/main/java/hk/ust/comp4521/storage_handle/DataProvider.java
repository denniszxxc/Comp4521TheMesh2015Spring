package hk.ust.comp4521.storage_handle;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class DataProvider extends ContentProvider {
	
	private static final String TAG = "DataProvider";
	
	private static final String AUTHORITY = "hk.ust.comp4521.provider";

	public static final String COL_ID = "_id";
	
	public static final String TABLE_USERS = "user";
	public static final String USERS_COL_UID = "uid";
	public static final String USERS_COL_COUNT = "count";
	
	public static final String TABLE_MESSAGES = "messages";
	public static final String MESSAGES_COL_MSG = "msg";
	public static final String MESSAGES_COL_FROM = "from_username";
	public static final String MESSAGES_COL_TO = "to_username";
	public static final String MESSAGES_COL_AT = "at";
	public static final String MESSAGES_COL_SENDED = "sended";
	//public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	public static final Uri CONTENT_URI_USERS = Uri.parse("content://" + AUTHORITY +"/" + TABLE_USERS);
	public static final Uri CONTENT_URI_MESSAGES = Uri.parse("content://" + AUTHORITY +"/" + TABLE_MESSAGES);
	
	private CustomSQLiteOpenHelper customSQLiteOpenHelper;
	
	private static final int MESSAGES_ALLROWS = 1;
	private static final int MESSAGES_SINGLE_ROW = 2;
	private static final int USER_ALLROWS = 3;
	private static final int USER_SINGLE_ROW = 4;
	
	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, TABLE_MESSAGES, MESSAGES_ALLROWS);
		uriMatcher.addURI(AUTHORITY, TABLE_MESSAGES+"/#", MESSAGES_SINGLE_ROW);
		uriMatcher.addURI(AUTHORITY, TABLE_USERS, USER_ALLROWS);
		uriMatcher.addURI(AUTHORITY, TABLE_USERS+"/#", USER_SINGLE_ROW);
	}

	@Override
	public boolean onCreate() {
		customSQLiteOpenHelper = new CustomSQLiteOpenHelper(getContext());
		return true;
	}

	private static class CustomSQLiteOpenHelper extends SQLiteOpenHelper {
		
		private static final String DATABASE_NAME = "chat.db";
		private static final int DATABASE_VERSION = 2;

		public CustomSQLiteOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i(TAG,"start onCreate-first");
			db.execSQL(String.format("create table %1$s (%2$s integer primary key autoincrement, %3$s text unique on conflict replace, %4$s integer default 0)",new Object[]{TABLE_USERS, COL_ID, USERS_COL_UID, USERS_COL_COUNT}));
			Log.i(TAG,"start onCreate-second");
			db.execSQL(String.format("create table  %1$s (%2$s integer primary key autoincrement, %3$s text, %4$s text default null, %5$s text default null, %6$s datetime default current_timestamp, %7$s boolean default 0 check(%7$s in (0,1)))",new Object[]{TABLE_MESSAGES, COL_ID, MESSAGES_COL_MSG, MESSAGES_COL_FROM, MESSAGES_COL_TO, MESSAGES_COL_AT, MESSAGES_COL_SENDED}));
			Log.i(TAG,"end onCreate");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("drop table if exists "+TABLE_USERS);
			db.execSQL("drop table if exists "+TABLE_MESSAGES);
			onCreate(db);
		}
	}
	
	// -----------------------------------------------------------
	
	@Override
	synchronized public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = customSQLiteOpenHelper.getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		
		switch(uriMatcher.match(uri)) {
		case MESSAGES_ALLROWS:
			qb.setTables(TABLE_MESSAGES);
			break;			
			
		case MESSAGES_SINGLE_ROW:
			qb.setTables(TABLE_MESSAGES);
			qb.appendWhere(COL_ID + "=" + uri.getLastPathSegment());
			break;

		case USER_ALLROWS:
			qb.setTables(TABLE_USERS);
			break;			
			
		case USER_SINGLE_ROW:
			qb.setTables(TABLE_USERS);
			qb.appendWhere(COL_ID + "=" + uri.getLastPathSegment());
			ContentValues values = new ContentValues(1);
			values.put(DataProvider.USERS_COL_COUNT, 0);
			getContext().getContentResolver().update(uri, values, null, null);
			break;
			
		default:
			Log.e(TAG, "Unsupported URI: " + uri);	
			return null;
		}
		
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}
	
	@Override
	synchronized public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = customSQLiteOpenHelper.getWritableDatabase();
		
		long id;
		switch(uriMatcher.match(uri)) {
		case MESSAGES_ALLROWS:
			id = db.insertOrThrow(TABLE_MESSAGES, null, values);
			if (values.containsKey(MESSAGES_COL_FROM)) {
				db.execSQL(String.format("update %1$s set %2$s = %2$s + 1 where %3$s = ?",TABLE_USERS,USERS_COL_COUNT,USERS_COL_UID), new Object[]{values.get(MESSAGES_COL_FROM)});
				getContext().getContentResolver().notifyChange(CONTENT_URI_USERS, null);
			}
			break;
			
		case USER_ALLROWS:
			id = db.insertOrThrow(TABLE_USERS, null, values);
			break;
			
		default:
			Log.e(TAG, "Unsupported URI: " + uri);
			return null;
		}
		if(id == -1)
			return null;
		Uri insertUri = ContentUris.withAppendedId(uri, id);
		getContext().getContentResolver().notifyChange(insertUri, null);
		return insertUri;
	}

	@Override
	synchronized public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = customSQLiteOpenHelper.getWritableDatabase();
		
		int count;
		switch(uriMatcher.match(uri)) {
		case MESSAGES_ALLROWS:
			count = db.update(TABLE_MESSAGES, values, selection, selectionArgs);
			break;			
			
		case MESSAGES_SINGLE_ROW:
			count = db.update(TABLE_MESSAGES, values, COL_ID+"=?", new String[]{uri.getLastPathSegment()});
			break;

		case USER_ALLROWS:
			count = db.update(TABLE_USERS, values, selection, selectionArgs);
			break;			
			
		case USER_SINGLE_ROW:
			count = db.update(TABLE_USERS, values, COL_ID+"=?", new String[]{uri.getLastPathSegment()});
			break;
			
		default:
			Log.e(TAG, "Unsupported URI: " + uri);	
			return 0;
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	@Override
	synchronized public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = customSQLiteOpenHelper.getWritableDatabase();
		
		int count;
		switch(uriMatcher.match(uri)) {
		case MESSAGES_ALLROWS:
			count = db.delete(TABLE_MESSAGES, selection, selectionArgs);
			break;			
			
		case MESSAGES_SINGLE_ROW:
			count = db.delete(TABLE_MESSAGES, COL_ID+"=?", new String[]{uri.getLastPathSegment()});
			break;

		case USER_ALLROWS:
			count = db.delete(TABLE_USERS, selection, selectionArgs);
			break;			
			
		case USER_SINGLE_ROW:
			count = db.delete(TABLE_USERS, COL_ID+"=?", new String[]{uri.getLastPathSegment()});
			break;
			
		default:
			Log.e(TAG, "Unsupported URI: " + uri);	
			return 0;
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}	
	
}
