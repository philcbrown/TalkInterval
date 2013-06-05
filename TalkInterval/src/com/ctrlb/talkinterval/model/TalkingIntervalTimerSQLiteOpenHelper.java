package com.ctrlb.talkinterval.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class TalkingIntervalTimerSQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = "TalkingIntervalTimer";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "talking_interval.db";

    // INTERVAL Table
    // Table Name
    public static final String TBL_INTERVAL = "INTERVAL";

    // Columns
    public static final String INTERVAL_SET_ID = "set_id";
    public static final String INTERVAL_NAME = "name";
    public static final String INTERVAL_MINUTES = "minutes";
    public static final String INTERVAL_SECONDS = "seconds";
    public static final String INTERVAL_ALERT_HALF = "alert_half";
    public static final String INTERVAL_ALERT_COUNTDOWN = "alert_countdown";
    public static final String INTERVAL_ALERT_MINUTES = "alert_minutes";
    public static final String INTERVAL_ALERT_NAME = "alert_name";
    public static final String INTERVAL_COLOR = "color";
    public static final String INTERVAL_ORDER = "interval_order";

    // INTERVAL_SET Table
    // Table Name
    public static final String TBL_INTERVAL_SET = "INTERVAL_SET";

    // Columns
    public static final String INTERVAL_SET_NAME = "name";

    private static final String INTERVAL_TBL_CREATE = "CREATE TABLE " + TBL_INTERVAL + 
	    " (" + BaseColumns._ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
	    INTERVAL_NAME + " TEXT NOT NULL, " + 
	    INTERVAL_MINUTES + " INTEGER NOT NULL, " + 
	    INTERVAL_SECONDS + " INTEGER NOT NULL, " + 
	    INTERVAL_ALERT_HALF+ " INTEGER NOT NULL CHECK(" + INTERVAL_ALERT_HALF + " IN (0, 1)), " + 
	    INTERVAL_ALERT_COUNTDOWN + " INTEGER NOT NULL CHECK(" + INTERVAL_ALERT_COUNTDOWN + " IN (0, 1)), " + 
	    INTERVAL_ALERT_MINUTES + " INTEGER NOT NULL CHECK(" + INTERVAL_ALERT_MINUTES + " IN (0, 1)), " + 
	    INTERVAL_ALERT_NAME + " INTEGER NOT NULL CHECK(" + INTERVAL_ALERT_NAME + " IN (0, 1)), " +
	    INTERVAL_COLOR + " INTEGER NOT NULL, " + 
	    INTERVAL_ORDER + " INTEGER NOT NULL, " + 
	    INTERVAL_SET_ID + " INTEGER NOT NULL, " + "FOREIGN KEY(" + INTERVAL_SET_ID + ") " +
	    		"REFERENCES " + TBL_INTERVAL_SET + "(" + BaseColumns._ID + ") "
	    + ");";

    private static final String INTERVAL_SET_TBL_CREATE = "CREATE TABLE " + TBL_INTERVAL_SET + " (" + BaseColumns._ID
	    + " INTEGER PRIMARY KEY AUTOINCREMENT, " + INTERVAL_SET_NAME + " TEXT NOT NULL);";

    public TalkingIntervalTimerSQLiteOpenHelper(Context context) {
	super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
	Log.v(TAG, "TalkingIntervalTimerSQLiteOpenHelper onCreate");
	db.execSQL(INTERVAL_SET_TBL_CREATE);
	db.execSQL(INTERVAL_TBL_CREATE);
	createTestData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	// TODO Auto-generated method stub

    }

    public void createTestData(SQLiteDatabase db) {

	for (int i = 0; i < 3; i++) {

	    ContentValues values = new ContentValues();
	    values.put(TalkingIntervalTimerSQLiteOpenHelper.INTERVAL_SET_NAME, "Interval set " + i);
	    int setId = (int) db.insert(TalkingIntervalTimerSQLiteOpenHelper.TBL_INTERVAL_SET, null, values);

	    for (int n = 0; n < 10; n++) {

		ContentValues values2 = new ContentValues();
		values2.put(TalkingIntervalTimerSQLiteOpenHelper.INTERVAL_SET_ID, setId);
		values2.put(TalkingIntervalTimerSQLiteOpenHelper.INTERVAL_NAME, "Interval name " + n);
		values2.put(TalkingIntervalTimerSQLiteOpenHelper.INTERVAL_MINUTES, "0");
		values2.put(TalkingIntervalTimerSQLiteOpenHelper.INTERVAL_SECONDS, "12");
		values2.put(TalkingIntervalTimerSQLiteOpenHelper.INTERVAL_ALERT_HALF, 1);
		values2.put(TalkingIntervalTimerSQLiteOpenHelper.INTERVAL_ALERT_COUNTDOWN, 0);
		values2.put(TalkingIntervalTimerSQLiteOpenHelper.INTERVAL_ALERT_MINUTES, 1);
		values2.put(TalkingIntervalTimerSQLiteOpenHelper.INTERVAL_ALERT_NAME, 1);

		values2.put(TalkingIntervalTimerSQLiteOpenHelper.INTERVAL_COLOR, 3);
		values2.put(TalkingIntervalTimerSQLiteOpenHelper.INTERVAL_ORDER, n);

		long insertId = db.insert(TalkingIntervalTimerSQLiteOpenHelper.TBL_INTERVAL, null, values2);

	    }
	}
    }
}
