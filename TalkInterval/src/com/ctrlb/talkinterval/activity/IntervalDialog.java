package com.ctrlb.talkinterval.activity;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.Engine;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.ctrlb.talkinterval.R;
import com.ctrlb.talkinterval.model.Interval;
import com.ctrlb.talkinterval.model.TalkingTimer;
import com.ctrlb.talkinterval.model.TalkingTimer.TimerListener;

public class IntervalDialog extends DialogFragment implements OnClickListener, TimerListener,
	TextToSpeech.OnUtteranceCompletedListener {

    private static final String INTERVAL_START_UTTERANCE = "INTERVAL_START";
    private static final String INTERVAL_COMPLETED_UTTERANCE = "INTERVAL_COMPLETED";
    private static final String INTERVAL_SET_COMPLETED_UTTERANCE = "INTERVAL_SET_COMPLETED";

    private Interval mCurrentInterval;
    private TalkingTimer mtimer;

    private TextView txtVw_intrvlNm;
    private TextView txtVw_min;
    private TextView txtVw_sec;
    private TextView txtVw_milli;
    private ViewAnimator mBottomBarViewAnimator;
    private TextToSpeech mTts;
    private boolean stopped = false;
    
    private WakeLock mWakeLock;

    public interface IntervalList {
	Interval getNextInterval();
    }

    public IntervalDialog() {
	super();

    }

    public void setTts(TextToSpeech mTts) {
	this.mTts = mTts;
	mTts.setOnUtteranceCompletedListener(this);
    }

//    @Override
//    public void onActivityCreated(Bundle arg0) {
//	super.onActivityCreated(arg0);
//	// intrvl = nextInterval();
//
//    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        PowerManager mgr = (PowerManager)getActivity().getSystemService(Context.POWER_SERVICE);
	mWakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
    }

    @Override
    public void onStart() {
	// TODO Auto-generated method stub
	mCurrentInterval = nextInterval();
	mWakeLock.acquire();
	playInterval();
	super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
	setStyle(STYLE_NO_FRAME, R.style.MyDialog);
	
	
	
	super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

	View view = inflater.inflate(R.layout.interval_dialog, container);

	((Button) view.findViewById(R.id.btn_start)).setOnClickListener(this);
	((Button) view.findViewById(R.id.btn_stop)).setOnClickListener(this);
	((Button) view.findViewById(R.id.btn_reset)).setOnClickListener(this);

	txtVw_intrvlNm = (TextView) view.findViewById(R.id.tv_interval_name);
	txtVw_min = (TextView) view.findViewById(R.id.tv_min);
	txtVw_sec = (TextView) view.findViewById(R.id.tv_sec);
	txtVw_milli = (TextView) view.findViewById(R.id.tv_milli);

	mBottomBarViewAnimator = (ViewAnimator) view.findViewById(R.id.va_buttons_interval_dialog);
	// Animation fadeOut =
	// AnimationUtils.loadAnimation(IntervalListActivity.this,
	// R.anim.fade_out);
	// TODO
	// Animation fadeIn = AnimationUtils.loadAnimation(,
	// R.anim.view_animator_in);
	// va.setOutAnimation(fadeOut);
	// va.setInAnimation(fadeIn);

	return view;
    }

    @Override
    public void onClick(View v) {
	switch (v.getId()) {
	case R.id.btn_start:
	    playInterval();
	    break;
	case R.id.btn_stop:

	    stop();
	    // show the start & reset buttons
	    mBottomBarViewAnimator.setDisplayedChild(1);

	    break;
	case R.id.btn_reset:
	    // *Reset* button click
	    this.dismiss();
	    break;
	}
    }

    private void stop() {
	mTts.stop();
	stopped = true;
	
	if (mWakeLock != null && mWakeLock.isHeld()) mWakeLock.release();

	if (mtimer != null)
	    mtimer.stop();
    }

    @Override
    public void onTick(int min, int sec, int milli) {
	updateCountdownText(min, sec, milli);
    }

    @Override
    public void onCountDownFinish(int reason) {

	if (reason == TalkingTimer.FINISHED) {
	    updateCountdownText(0, 0, 0);
	    HashMap<String, String> args = new HashMap<String, String>();
	    args.put(Engine.KEY_PARAM_UTTERANCE_ID, INTERVAL_COMPLETED_UTTERANCE);
	    
	    if (mCurrentInterval.getNameAlert()) {
		mTts.speak(mCurrentInterval.getName(), TextToSpeech.QUEUE_ADD, null);
	    }
	    mTts.speak("Completed", TextToSpeech.QUEUE_ADD, args);

	}

    }

    private void setCompleted() {
	
	HashMap<String, String> args = new HashMap<String, String>();
	args.put(Engine.KEY_PARAM_UTTERANCE_ID, INTERVAL_SET_COMPLETED_UTTERANCE);
	
	mTts.speak("Set completed !", TextToSpeech.QUEUE_ADD, args);
	
    }

    private void playInterval() {

	if (mCurrentInterval != null) {

	    mBottomBarViewAnimator.setDisplayedChild(0);

	    stopped = false;

	    mtimer = new TalkingTimer(mCurrentInterval.getMinutes(), mCurrentInterval.getSeconds(),
		    mCurrentInterval.getHalfwayAlert(), mCurrentInterval.getCountdownAlert(), false,
		    mCurrentInterval.getMinutesAlert());

	    
	    txtVw_intrvlNm.setText(mCurrentInterval.getName());
	    updateCountdownText(mCurrentInterval.getMinutes(), mCurrentInterval.getSeconds(), 0);

	    mtimer.setTimerListener(this);

	    // TODO say some stuff
	    mTts.setSpeechRate((float) 1.0);

	    HashMap<String, String> args = new HashMap<String, String>();
	    args.put(Engine.KEY_PARAM_UTTERANCE_ID, INTERVAL_START_UTTERANCE);

	    StringBuilder utteranceString = new StringBuilder();

	    if (mCurrentInterval.getMinutes() != 0) {
		utteranceString.append(mCurrentInterval.getMinutes());
		utteranceString.append(" minutes");
	    }
	    if (mCurrentInterval.getSeconds() != 0) {
		utteranceString.append(mCurrentInterval.getSeconds());
		utteranceString.append(" seconds");
	    }
	    
	    if (mCurrentInterval.getNameAlert()) {
		mTts.speak(mCurrentInterval.getName(), TextToSpeech.QUEUE_ADD, null);
	    }
	    mTts.speak(utteranceString.toString(), TextToSpeech.QUEUE_ADD, null);
	    mTts.speak("Start", TextToSpeech.QUEUE_ADD, args);
	}
    }

    private void updateCountdownText(int min, int sec, int milli) {
	txtVw_min.setText(String.format("%02d", min));
	txtVw_sec.setText(String.format("%02d", sec));
	txtVw_milli.setText(String.format("%02d", milli));
    }

    private Interval nextInterval() {
	IntervalList activity = (IntervalList) getActivity();
	return activity.getNextInterval();
    }

    @Override
    public void onUtteranceCompleted(String utteranceId) {

	if (utteranceId.equals(INTERVAL_START_UTTERANCE)) {

	    getActivity().runOnUiThread(new Runnable() {
		public void run() {
		    if (!stopped) {
			mtimer.start();
		    }
		}
	    });
	} else if (utteranceId.equals(INTERVAL_COMPLETED_UTTERANCE)) {
	    getActivity().runOnUiThread(new Runnable() {
		public void run() {
		    if (!stopped) {
			mCurrentInterval = nextInterval();
			if (mCurrentInterval != null) {
			    playInterval();
			} else {
			    setCompleted();
			}
		    }
		}
	    });
	}
	else if (utteranceId.equals(INTERVAL_SET_COMPLETED_UTTERANCE)) {
	    dismiss();
	}
    }

    @Override
    public void onMessageToTalk(String msg) {

	mTts.speak(msg, TextToSpeech.QUEUE_ADD, null);

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
	stop();
	super.onDismiss(dialog);
    }

}
