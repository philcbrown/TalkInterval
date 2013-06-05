package com.ctrlb.talkinterval.activity;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.ctrlb.talkinterval.R;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends SherlockFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_about);
	
	ActionBar ab = getSupportActionBar();
	ab.setDisplayHomeAsUpEnabled(true);
	
	
	try {
	    
	    TextView tv = (TextView)findViewById(R.id.tv_version);
	
	    String version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
	    tv.setText(version);
	
	}catch (NameNotFoundException ex) {
	    
	}
	
	
    }
    
    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {

	switch (item.getItemId()) {
	case android.R.id.home:
	    finish();
	    return true;
	}

	return true;
    }

}
