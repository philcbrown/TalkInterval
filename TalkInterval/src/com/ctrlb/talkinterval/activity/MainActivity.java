package com.ctrlb.talkinterval.activity;

import com.ctrlb.android.utils.ObjectLoader;
import com.ctrlb.talkinterval.R;
import com.ctrlb.talkinterval.activity.IntervalSetDialog.IntervalSetDialogListener;
import com.ctrlb.talkinterval.model.*;
import com.ctrlb.draggablelist.GenericAdapter;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends TalkingIntervalShared implements LoaderCallbacks<Object>,
	IntervalSetDialogListener {

    private IntervalSetCollection mIntervalSetCollection;
    private GenericAdapter mAdapter;
    private int mSelectedPosition = -1;
    private AlertDialog mMoreThan3Dialog;
    private AlertDialog mIntrvlSetDialog;

    public static final String SELECTED_SET_ID = "SELECTED_SET_ID";
    private static final int EDIT_INTERVAL_REQUEST_CODE = 0;
    
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onRestoreInstanceState(savedInstanceState);
        mSelectedPosition = savedInstanceState.getInt("mSelectedPosition");
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        outState.putInt("mSelectedPosition", mSelectedPosition);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);

	ListView intervalSetListView = (ListView) findViewById(R.id.lv_intervalSet);
	registerForContextMenu(intervalSetListView);

	final String from[] = { IntervalSetCollection.INTERVAL_SET_NAME };
	final int to[] = { R.id.tv_set_name };
	mAdapter = new GenericAdapter(this, null, R.layout.intrvl_set_list_item, from, to);

	intervalSetListView.setAdapter(mAdapter);

	getSupportLoaderManager().initLoader(0, null, this);

	final Context c = this;

	intervalSetListView.setOnItemClickListener(new OnItemClickListener() {

	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		//Toast.makeText(getApplicationContext(), "" + id, Toast.LENGTH_SHORT).show();

		Intent i = new Intent(c, IntervalListActivity.class);
		i.putExtra(IntervalListActivity.SET_ID, mIntervalSetCollection.getItemId(position));
		i.putExtra(IntervalListActivity.SET_NAME, mIntervalSetCollection.getName(position));
		startActivityForResult(i, EDIT_INTERVAL_REQUEST_CODE);

	    }
	});
    }

    @Override
    protected void onStart() {
	super.onStart();

    }

    private void moreThan3Dialog() {

	if (mMoreThan3Dialog == null) {

	    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this,
			R.style.TalkingIntervalDialog));
	    
	    mMoreThan3Dialog = builder.create();
	    
	    
	    
	    LayoutInflater mInflater = LayoutInflater.from(this);
	    View v = mInflater.inflate(R.layout.morethan3_dialog, null, false);
	    
	    Button btn = (Button)v.findViewById(R.id.btn_ok);
		
		btn.setOnClickListener(new OnClickListener() {
		    
		    @Override
		    public void onClick(View v) {
			// TODO Auto-generated method stub
			mMoreThan3Dialog.dismiss();
		    }
		});
	    
	    mMoreThan3Dialog.setView(v, 0, 0, 0, 0); 
	}
	
	mMoreThan3Dialog.show();
	
	

    }
    
    private void displayHelp() {
	
	if (mIntervalSetCollection == null || mIntervalSetCollection.getCount() == 0) {
	    View v = findViewById(R.id.lv_help);
	    v.setVisibility(View.VISIBLE);
	}
	else {
	    View v = findViewById(R.id.lv_help);
	    v.setVisibility(View.GONE);
	}
    }

    @Override
    protected void onResume() {
	super.onResume();
    }

    @Override
    protected void onPause() {
	super.onPause();
    }

    @Override
    protected void onStop() {
	super.onStop();
    }

    @Override
    protected void onRestart() {
	super.onRestart();
    }

    @Override
    protected void onDestroy() {
	mMoreThan3Dialog = null;
	mIntervalSetCollection = null;
	super.onDestroy();
    }

    private void showSetDialog(String setName) {
	FragmentManager fm = getSupportFragmentManager();
	IntervalSetDialog intrvlSetDialog = IntervalSetDialog.newInstance(setName);
	intrvlSetDialog.show(fm, "interval_set");
    }
    
    /*private void showSetDialog2(String setName) {
	if (mIntrvlSetDialog == null) {

	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    LayoutInflater mInflater = LayoutInflater.from(this);
	    View v = mInflater.inflate(R.layout.interval_set_dialog, null, false);
	    builder.setView(v);
	    
	    builder.setIcon(R.drawable.ic_interval_set_add);
	    builder.setTitle("xxxx");
	    

	    builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int id) {
		    
		    //dialog.
		    
		    //EditText et = (EditText)dialog.findViewById(R.id.edtTxt_set_name);
		    //et.setText(setName);
		    
		    
		}
	    });

	    mIntrvlSetDialog = builder.create();
	    
	    
	    
	    EditText et = (EditText)mIntrvlSetDialog.findViewById(R.id.edtTxt_set_name);
	    et.setText(setName);
	}
	mMoreThan3Dialog.show();
    }*/
    
    

    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
	return new ObjectLoader((Context) this, IntervalSetCollection.class, null);
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object object) {
	mIntervalSetCollection = (IntervalSetCollection) object;
	mAdapter.reloadData(mIntervalSetCollection);
	displayHelp();
    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {
	mAdapter.reloadData(null);
    }

    @Override
    public void onDialogSaveClick(DialogFragment dialogFrag) {
	
	String name = ((EditText) dialogFrag.getDialog().findViewById(R.id.edtTxt_set_name)).getText().toString();
	
	if (name.equals("")) name = "New Interval Set";
	
	mIntervalSetCollection.updateSet(mSelectedPosition, name);

	mAdapter.notifyDataSetChanged();

	Toast.makeText(getApplicationContext(), name + " Saved", Toast.LENGTH_LONG).show();
    }
    
    /*public void onDialogSaveClick() {
	String name = ((EditText) mIntrvlSetDialog.findViewById(R.id.edtTxt_set_name)).getText().toString();
	mIntervalSetCollection.updateSet(mSelectedPosition, name);

	mAdapter.notifyDataSetChanged();

	Toast.makeText(getApplicationContext(), "Saved " + name, Toast.LENGTH_LONG).show();
    }*/

    @Override
    public void onDialogCancelClick(DialogFragment dialog) {
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	super.onCreateContextMenu(menu, v, menuInfo);
	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.main_activity_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

	switch (item.getItemId()) {
	case R.id.context_menu_delete_set:

	    mIntervalSetCollection.delete(info.position);
	    mAdapter.notifyDataSetChanged();

	    Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
	    displayHelp();
	    return true;
	case R.id.context_menu_edit_set:

	    mSelectedPosition = info.position;

	    String setName = mIntervalSetCollection.getName(info.position);
	    showSetDialog(setName);

	    // Toast.makeText(this, , Toast.LENGTH_SHORT).show();

	    return true;
	default:
	    return super.onContextItemSelected(item);
	}
    }

    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
	getSupportMenuInflater().inflate(R.menu.main_activity_main, menu);
	return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {

	switch (item.getItemId()) {
	// case android.R.id.home:
	// Toast.makeText(this, "Home !", Toast.LENGTH_SHORT).show();
	// return true;
	case R.id.menu_new_set:

	    if (!(mIntervalSetCollection.getCount() + 1 <= 3)) {

		moreThan3Dialog();

		// Toast.makeText(this, "can only make 3",
		// Toast.LENGTH_SHORT).show();
	    } else {
		showSetDialog("");
		mSelectedPosition = -1;
	    }
	    return true;
	default:
	    return super.onOptionsItemSelected(item);
	}
    }
}
