package com.ctrlb.talkinterval.activity;

import android.content.Intent;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.ctrlb.talkinterval.R;

public class TalkingIntervalShared extends SherlockFragmentActivity {
    
    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
	getSupportMenuInflater().inflate(R.menu.shared, menu);
	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {

	switch (item.getItemId()) {
	case R.id.menu_about:
	    Intent intnt = new Intent(this, AboutActivity.class);
	    startActivity(intnt);
	    return true;
	default:
	    return super.onOptionsItemSelected(item);
	}
    }
}
