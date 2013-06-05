package com.ctrlb.talkinterval.model;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.ctrlb.draggablelist.*;
import com.ctrlb.android.utils.ObjectLoader;
import com.ctrlb.android.utils.ObjectLoader.LoadableObject;

public class IntervalSet implements LoadableObject, MoveableDataProvider {

    
    public int mSetId;

    private ArrayList<Interval> mData;
    private TalkingIntervalTimerSQLiteOpenHelper mDatabaseOpenHelper;

    private ObjectLoader mLoader;

    public IntervalSet() {
	super();
    }

    @Override
    public void loadData(Context context, Object[] args, ObjectLoader loader) {
	mLoader = loader;
	mSetId = ((Integer) args[0]).intValue();
	mDatabaseOpenHelper = new TalkingIntervalTimerSQLiteOpenHelper(context);

	Log.v("talkinginterval", "!!!!!!!! mSetId " + mSetId);

	mData = new ArrayList<Interval>();

	SQLiteDatabase database = mDatabaseOpenHelper.getReadableDatabase();

	Cursor cursor = database.query(TalkingIntervalTimerSQLiteOpenHelper.TBL_INTERVAL, new String[] {
		BaseColumns._ID,
		TalkingIntervalTimerSQLiteOpenHelper.INTERVAL_NAME,
		TalkingIntervalTimerSQLiteOpenHelper.INTERVAL_MINUTES,
		TalkingIntervalTimerSQLiteOpenHelper.INTERVAL_SECONDS,
		TalkingIntervalTimerSQLiteOpenHelper.INTERVAL_ALERT_MINUTES,
		TalkingIntervalTimerSQLiteOpenHelper.INTERVAL_ALERT_HALF,
		TalkingIntervalTimerSQLiteOpenHelper.INTERVAL_ALERT_COUNTDOWN,
		TalkingIntervalTimerSQLiteOpenHelper.INTERVAL_ALERT_NAME,
		TalkingIntervalTimerSQLiteOpenHelper.INTERVAL_COLOR,
		TalkingIntervalTimerSQLiteOpenHelper.INTERVAL_ORDER
	},
		TalkingIntervalTimerSQLiteOpenHelper.INTERVAL_SET_ID + "=?", new String[] { Integer.toString(mSetId) },
		null, null, TalkingIntervalTimerSQLiteOpenHelper.INTERVAL_ORDER+" ASC", null);

	if (cursor != null) {
	    if (cursor.moveToFirst()) {
		do {

		    Interval intrvl = new Interval();
		    intrvl.setId(cursor.getInt(0));
		    intrvl.setName(cursor.getString(1));
		    intrvl.setMinutes(cursor.getInt(2));
		    intrvl.setSeconds(cursor.getInt(3));
		    intrvl.setMinutesAlert(cursor.getInt(4) == 1 ? true : false);
		    intrvl.setHalfwayAlert(cursor.getInt(5) == 1 ? true : false);
		    intrvl.setCountdownAlert(cursor.getInt(6) == 1 ? true : false);
		    intrvl.setNameAlert(cursor.getInt(7) == 1 ? true : false);
		    intrvl.setColor(cursor.getInt(8));
		    intrvl.setOrder(cursor.getInt(9));

		    mData.add(intrvl);

		} while (cursor.moveToNext());
	    }
	}
	cursor.close();
	database.close();
    }

    @Override
    public HashMap<String, String> getItem(int position) {
	return mData.get(position).getHashMap();
    }

    public Interval getInterval(int position) {
	
	if (position < 0 || position > getCount()-1) return null;
	
	
	return mData.get(position);
    }

    @Override
    public int getCount() {
	if (mData == null)
	    return 0;
	return mData.size();
    }

    @Override
    public long getItemId(int position) {
	Interval i = mData.get(position);
	return i.getId();
    }

    @Override
    protected void finalize() throws Throwable {
	mDatabaseOpenHelper.close();
	super.finalize();
    }

    public void delete(int position) {
	SQLiteDatabase database = mDatabaseOpenHelper.getWritableDatabase();
	database.delete(TalkingIntervalTimerSQLiteOpenHelper.TBL_INTERVAL, BaseColumns._ID + "=?",
		new String[] { Long.toString(getItemId(position)) });
	database.close();

	mData.remove(position);
	mLoader.onContentChanged();

    }

    public void newInterval(Interval interval) {
	SQLiteDatabase database = mDatabaseOpenHelper.getWritableDatabase();

	ContentValues values = new ContentValues();
	values.put(TalkingIntervalTimerSQLiteOpenHelper.INTERVAL_SET_ID, mSetId);
	values.put(TalkingIntervalTimerSQLiteOpenHelper.INTERVAL_NAME, interval.getName());
	values.put(TalkingIntervalTimerSQLiteOpenHelper.INTERVAL_MINUTES, interval.getMinutes());
	values.put(TalkingIntervalTimerSQLiteOpenHelper.INTERVAL_SECONDS, interval.getSeconds());
	values.put(TalkingIntervalTimerSQLiteOpenHelper.INTERVAL_ALERT_HALF, interval.getHalfwayAlert() ? 1 : 0);
	values.put(TalkingIntervalTimerSQLiteOpenHelper.INTERVAL_ALERT_COUNTDOWN, interval.getCountdownAlert() ? 1 : 0);
	values.put(TalkingIntervalTimerSQLiteOpenHelper.INTERVAL_ALERT_MINUTES, interval.getMinutesAlert() ? 1 : 0);
	values.put(TalkingIntervalTimerSQLiteOpenHelper.INTERVAL_ALERT_NAME, interval.getNameAlert() ? 1 : 0);
	values.put(TalkingIntervalTimerSQLiteOpenHelper.INTERVAL_ORDER, interval.getOrder());
	values.put(TalkingIntervalTimerSQLiteOpenHelper.INTERVAL_COLOR, interval.getColor());
	

	if (interval.getId() == -1) {
	    
	    values.put(TalkingIntervalTimerSQLiteOpenHelper.INTERVAL_ORDER, getCount()+1);
	    
	    
	    
	    long insertId = database.insert(TalkingIntervalTimerSQLiteOpenHelper.TBL_INTERVAL, null, values);
	} else {
	    database.update(TalkingIntervalTimerSQLiteOpenHelper.TBL_INTERVAL, values, "_id=?",
		    new String[] { Long.toString(interval.getId()) });
	}

	database.close();

	mLoader.onContentChanged();
    }

    @Override
    public void move(int fromPosition, int toPosition) {
	
	Log.v("talkinginterval", "from "+fromPosition+" to "+toPosition);
	
	Interval from = mData.get(fromPosition);
	Interval to = mData.get(toPosition);
	
	int fromOrder = from.getOrder();
	int toOrder = to.getOrder();
	
	from.setOrder(toOrder);
	to.setOrder(fromOrder);
	
	mData.set(toPosition, from);
	mData.set(fromPosition, to);
	
    }
    
    public void saveOrder() {
	
	SQLiteDatabase database = mDatabaseOpenHelper.getWritableDatabase();
	
	for(Interval intrvl : mData) {
	      
	    updateOrder(intrvl.getId(), intrvl.getOrder(), database);
	    
	}

	
	database.close();
	
    }
    
    private void updateOrder(int id, int order, SQLiteDatabase database) {
	ContentValues values = new ContentValues();
	values.put(TalkingIntervalTimerSQLiteOpenHelper.INTERVAL_ORDER, order);
	
	database.update(TalkingIntervalTimerSQLiteOpenHelper.TBL_INTERVAL, values, "_id=?",
		    new String[] { Integer.toString(id) });
	
	
    }
}
