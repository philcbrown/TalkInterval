package com.ctrlb.android.utils;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

public class ObjectLoader extends AsyncTaskLoader<Object> {

    private Class<? extends LoadableObject> mClasssToload;
    private Context mContext;
    private Object[] mArgs;

    public ObjectLoader(Context context, Class<? extends LoadableObject> classToload, Object[] args) {
	super(context);
	mContext = context;
	mClasssToload = classToload;
	mArgs = args;
    }

    @Override
    public Object loadInBackground() {
	LoadableObject object = null;
	try {
	    object = mClasssToload.newInstance();
	    object.loadData(mContext, mArgs, this);
	} catch (Exception e) {
	    Log.v("talkinginterval", "ObjectLoader loadInBackground exception " +e.getMessage());
	}

	return object;
    }

    @Override
    public void deliverResult(Object data) {
	super.deliverResult(data);
    }

    @Override
    protected void onStartLoading() {
	forceLoad();
    }

    @Override
    public void onContentChanged() {
	super.onContentChanged();
    }

    public interface LoadableObject {
	public void loadData(Context context, Object[] args, ObjectLoader loader);
    }
}
