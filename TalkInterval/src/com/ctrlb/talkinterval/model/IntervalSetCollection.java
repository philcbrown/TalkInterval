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

public class IntervalSetCollection implements LoadableObject, GenericDataProvider {

    public static String INTERVAL_SET_ID = "id";
    public static String INTERVAL_SET_NAME = "name";

    private ArrayList<HashMap<String, String>> mData;
    private TalkingIntervalTimerSQLiteOpenHelper mDatabaseOpenHelper;
    private Context mContext;
    private ObjectLoader mLoader;

    public IntervalSetCollection() {
    }

    /**
     * Upate the Interval Set name or create a new Interval set if the position
     * does not exist
     * 
     * @param position
     * @param name
     * @return the id of the inserted / updated interval
     */

    public int updateSet(int position, String name) {

	int insertId = -1;
	SQLiteDatabase database = mDatabaseOpenHelper.getWritableDatabase();

	ContentValues values = new ContentValues();
	values.put(TalkingIntervalTimerSQLiteOpenHelper.INTERVAL_SET_NAME, name);

	if (position == -1) {
	    insertId = (int) database.insert(TalkingIntervalTimerSQLiteOpenHelper.TBL_INTERVAL_SET, null, values);
	} else {
	    insertId = (int) getItemId(position);
	    database.update(TalkingIntervalTimerSQLiteOpenHelper.TBL_INTERVAL_SET, values, "_id=?",
		    new String[] { Integer.toString(insertId) });
	}
	mLoader.onContentChanged();

	database.close();

	return insertId;

    }

    /**
     * Deletes the Interval Set and all associated Intervals form the database
     * 
     * @param position
     *            The position in the dataset of the Interval Set to delete
     */

    public void delete(int position) {

	SQLiteDatabase database = mDatabaseOpenHelper.getWritableDatabase();

	int id = (int) getItemId(position);
	database.delete(TalkingIntervalTimerSQLiteOpenHelper.TBL_INTERVAL,
		TalkingIntervalTimerSQLiteOpenHelper.INTERVAL_SET_ID + "=?", new String[] { Long.toString(id) });
	database.delete(TalkingIntervalTimerSQLiteOpenHelper.TBL_INTERVAL_SET, BaseColumns._ID + "=?",
		new String[] { Long.toString(id) });

	mData.remove(position);

	database.close();

    }

    /**
     * 
     * @param position
     * @return
     */

    public String getName(int position) {

	Log.v("talkinginterval", "get name position = " + position);

	HashMap<String, String> hm = mData.get(position);
	// Integer i =

	return hm.get(INTERVAL_SET_NAME);
    }

    /**
     * @param context
     * @param args
     * @param loader
     *            the {@link ObjectLoader}
     */
    @Override
    public void loadData(Context context, Object[] args, ObjectLoader loader) {
	mLoader = loader;
	mContext = context;
	mDatabaseOpenHelper = new TalkingIntervalTimerSQLiteOpenHelper(mContext);

	SQLiteDatabase database = mDatabaseOpenHelper.getReadableDatabase();

	Cursor cursor = database.query(TalkingIntervalTimerSQLiteOpenHelper.TBL_INTERVAL_SET, new String[] {
		BaseColumns._ID,
		TalkingIntervalTimerSQLiteOpenHelper.INTERVAL_SET_NAME }, null, null, null, null, null, null);

	mData = new ArrayList<HashMap<String, String>>();

	if (cursor != null) {
	    if (cursor.moveToFirst()) {
		do {

		    HashMap<String, String> hm = new HashMap<String, String>();

		    hm.put(INTERVAL_SET_ID, "" + cursor.getInt(0));
		    hm.put(INTERVAL_SET_NAME, cursor.getString(1));

		    mData.add(hm);

		    Log.v("talkinginterval", "Load data " + hm.get(INTERVAL_SET_ID) + " " + hm.get(INTERVAL_SET_NAME));

		} while (cursor.moveToNext());
	    }
	}
	cursor.close();
	database.close();

    }

    @Override
    protected void finalize() throws Throwable {
	if (mDatabaseOpenHelper != null)
	    mDatabaseOpenHelper.close();
	super.finalize();
    }

    /**
     * Gets the data for the Interval Set at a given position in the data set
     * 
     * @param position the position in the data set
     * @return a {@link HashMap<String, String>} containing the data for the
     *         Interval Set at the given position in the data set
     */
    @Override
    public HashMap<String, String> getItem(int position) {
	return mData.get(position);
    }

    /**
     * 
     * @return the number of Interval Sets in the data set
     */
    @Override
    public int getCount() {
	if (mData == null)
	    return 0;
	return mData.size();
    }

    /**
     * 
     * @param position
     *            the position in the data set
     * @return the id for the Interval Set at the position in the data set or
     *         -1L if the position does not exist
     */
    @Override
    public long getItemId(int position) {

	if (position == -1) {
	    return -1L;
	} else {
	    HashMap<String, String> hm = mData.get(position);
	    return Integer.parseInt(hm.get(INTERVAL_SET_ID));
	}
    }

}
