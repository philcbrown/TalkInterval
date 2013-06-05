package com.ctrlb.talkinterval.activity;

import java.util.Locale;
import com.actionbarsherlock.app.ActionBar;
import com.ctrlb.android.utils.ObjectLoader;
import com.ctrlb.draggablelist.DraggableGenericAdapter;
import com.ctrlb.draggablelist.DraggableListView;
import com.ctrlb.draggablelist.GenericAdapter;
import com.ctrlb.talkinterval.R;
import com.ctrlb.talkinterval.model.Interval;
import com.ctrlb.talkinterval.model.IntervalSet;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

public class IntervalListActivity extends TalkingIntervalShared implements IntervalDialog.IntervalList,
	OnClickListener, TextToSpeech.OnInitListener, LoaderCallbacks<Object> {

    private static final String TAG = "TalkingIntervalTimer";
    private static final int EDIT_INTERVAL_REQUEST_CODE = 0;
    public static final String SELECTED_INTERVAL_ID = "SELECTED_INTERVAL_ID";

    public static String SET_ID = "INTERVAL_SET_ID";
    public static String SET_NAME = "INTERVAL_SET_NAME";

    private TextToSpeech mTts;
    private Cursor curs;
    private int cursPosition = -1;
    private long mSetId;
    private String mSetName;

    private DraggableGenericAdapter mAdapter;
    private IntervalSet mIntervalSet;

    private ViewAnimator mBottomBarViewAnimator;

    private DraggableListView mDraggableListView;
    
    private int currentIntervalPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.interval_list_activity);

	ActionBar ab = getSupportActionBar();
	ab.setDisplayHomeAsUpEnabled(true);

	mBottomBarViewAnimator = (ViewAnimator) findViewById(R.id.va_bottom_bar);

	((Button) findViewById(R.id.btn_start)).setOnClickListener(this);
	((Button) findViewById(R.id.btn_move_complete)).setOnClickListener(this);
	((Button) findViewById(R.id.btn_move_undo)).setOnClickListener(this);

	// create adapter for list view data. Data is loaded by Loader
	final String from[] = {
		Interval.INTERVAL_NAME,
		Interval.INTERVAL_MINS,
		Interval.INTERVAL_SECS,
		Interval.INTERVAL_HALFWAY_ALERT,
		Interval.INTERVAL_COUNTDOWN_ALERT,
		Interval.INTERVAL_MINUTES_ALERT,
		Interval.INTERVAL_NAME_ALERT,
		Interval.INTERVAL_COLOR };
	final int to[] = {
		R.id.tv_name_listitem,
		R.id.tv_min_listitem,
		R.id.tv_sec_listitem,
		R.id.iv_half,
		R.id.iv_countdown,
		R.id.iv_mins,
		R.id.iv_name,
		-1 };
	// Drawable upDrawable = getResources().getDrawable(R.drawable.ic_up);
	// Drawable downDrawable =
	// getResources().getDrawable(R.drawable.ic_down);
	mAdapter = new DraggableGenericAdapter(this, null, R.layout.listitem, from, to, R.drawable.bg_striped_img,
		R.drawable.ic_drag);

	mAdapter.setViewBinder(new GenericAdapter.ViewBinder() {

	    @Override
	    public boolean setViewValue(View parent, View view, String data, String from) {

		if (from == Interval.INTERVAL_HALFWAY_ALERT || from == Interval.INTERVAL_COUNTDOWN_ALERT
			|| from == Interval.INTERVAL_MINUTES_ALERT || from == Interval.INTERVAL_NAME_ALERT) {

		    if (Boolean.parseBoolean(data))
			view.setVisibility(View.VISIBLE);
		    else
			view.setVisibility(View.GONE);

		    return true;

		} else if (from == Interval.INTERVAL_COLOR) {

		    int id = Integer.parseInt(data);

		    switch (id) {
		    case 1:
			parent.setBackgroundResource(R.drawable.bg_list_item_color1);
			break;
		    case 2:
			parent.setBackgroundResource(R.drawable.bg_list_item_color2);
			break;
		    case 3:
			parent.setBackgroundResource(R.drawable.bg_list_item_color3);
			break;

		    case 4:
			parent.setBackgroundResource(R.drawable.bg_list_item_color4);
			break;
		    }

		    return true;
		}

		return false;
	    }
	});

	mDraggableListView = (DraggableListView) findViewById(R.id.lv_intervals);
	mDraggableListView.setAdapter(mAdapter);

	mDraggableListView.setHoverColor(Color.parseColor("#99cc00"));
	mDraggableListView.setHoverAlpha(0.75f);

	registerForContextMenu(mDraggableListView);

	mTts = new TextToSpeech(this, this);

	Bundle extras = getIntent().getExtras();

	// if the key is not found 0L & null is returned
	if (extras != null) {
	    mSetId = extras.getLong(IntervalListActivity.SET_ID);
	    mSetName = extras.getString(IntervalListActivity.SET_NAME);
	}

	if (mSetName != null) {
	    TextView tv = (TextView) findViewById(R.id.tv_set_name);
	    tv.setText(mSetName);
	}

	getSupportLoaderManager().initLoader(0, null, this);

    }

    @Override
    protected void onStart() {
	super.onStart();
    }

    @Override
    protected void onDestroy() {
	mTts.shutdown();
	super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
	getSupportMenuInflater().inflate(R.menu.interval_list_activity_main, menu);
	return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	super.onCreateContextMenu(menu, v, menuInfo);
	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.interval_list_activity_context, menu);
    }
    
    @Override
    public void openContextMenu(View view) {
        mDraggableListView.setDragStatus(false);
        super.openContextMenu(view);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	switch (item.getItemId()) {
	case R.id.ctx_menu_edit:
	    editInterval(info.position);
	    return true;
	case R.id.ctx_menu_delete:
	    mIntervalSet.delete(info.position);
	    mAdapter.notifyDataSetChanged();
	    displayHelp();
	    return true;
	case R.id.ctx_menu_start:
	    // *Start from this Interval* context menu click
	    startInterval((int) info.position);
	    return true;
	case R.id.ctx_menu_move:
	    mBottomBarViewAnimator.setDisplayedChild(1);
	    mDraggableListView.setDragStatus(true);
	    return true;
	case R.id.ctx_menu_duplicate:
	    Toast.makeText(this, "Feature not Implemented.", Toast.LENGTH_SHORT).show();
	    return true;
	default:
	    return super.onContextItemSelected(item);
	}
    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {

	switch (item.getItemId()) {
	case android.R.id.home:
	    Intent mainActivityIntent = new Intent(this, MainActivity.class);
	    mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
	    startActivity(mainActivityIntent);
	    return true;
	case R.id.menu_new_interval:
	    editInterval(-1);
	    return true;
	default:
	    return super.onOptionsItemSelected(item);
	}

    }

    private void editInterval(int position) {
	Interval intrvl = null;
	if (position == -1)
	    intrvl = new Interval(-1, "", 0, 0);
	else
	    intrvl = mIntervalSet.getInterval(position);

	Intent i = new Intent(this, EditIntervalActivity.class);
	i.putExtra(IntervalListActivity.SELECTED_INTERVAL_ID, intrvl);
	startActivityForResult(i, IntervalListActivity.EDIT_INTERVAL_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	if (requestCode == IntervalListActivity.EDIT_INTERVAL_REQUEST_CODE) {
	    if (resultCode == RESULT_OK) {

		Bundle extras = data.getExtras();

		// if the key is not found 0L & null is returned
		if (extras != null) {
		    Interval intrvl = (Interval) extras.getParcelable("INTERVAL_DATA");

		    mIntervalSet.newInterval(intrvl);
		    mAdapter.notifyDataSetChanged();

		}

	    } else if (resultCode == RESULT_CANCELED) {

	    }
	}
    }

    private void reorder(int startPosition, int endPosition) {
	Toast.makeText(this, "Feature not Implemented.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {

	switch (v.getId()) {
	case R.id.btn_start:
	    startInterval(0);
	    break;
	case R.id.btn_move_complete:
	    mIntervalSet.saveOrder();

	    mDraggableListView.setDragStatus(false);
	    mBottomBarViewAnimator.setDisplayedChild(0);
	    break;
	case R.id.btn_move_undo:
	    // mAdapter.moveUndo();
	    break;
	}
    }


   

    private final void startInterval(int startPosition) {
	FragmentManager fm = getSupportFragmentManager();
	IntervalDialog intrvlDialog = new IntervalDialog();
	intrvlDialog.setTts(mTts);
	intrvlDialog.show(fm, "interval");
	currentIntervalPosition = startPosition;

    }

    @Override
    public void onInit(int status) {
	mTts.setLanguage(Locale.UK);
	mTts.setSpeechRate((float) 2.0);
    }

    @Override
    public Interval getNextInterval() {
	return mIntervalSet.getInterval(currentIntervalPosition++);
    }

    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
	return new ObjectLoader((Context) this, IntervalSet.class, new Object[] { new Integer((int) mSetId) });
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object object) {
	mIntervalSet = (IntervalSet) object;
	mAdapter.reloadData(mIntervalSet);
	displayHelp();
    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {
    }

    private void displayHelp() {

	if (mIntervalSet == null || mIntervalSet.getCount() == 0) {
	    View v = findViewById(R.id.lv_help);
	    v.setVisibility(View.VISIBLE);
	} else {
	    View v = findViewById(R.id.lv_help);
	    v.setVisibility(View.GONE);
	}
    }
}
