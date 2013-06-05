package com.ctrlb.talkinterval.model;

import java.util.ArrayList;
import android.os.CountDownTimer;
import android.util.Log;

public class TalkingTimer {

    public static final int STOPPED = 1;
    public static final int FINISHED = 0;

    private TimerListener mTimerListener;
    private CountDownTimer mTimer;
    private int mMinutes;
    private int mSeconds;

    // private int mXMinutes;

    private boolean mNotifyHalfWay;
    private boolean mNotifyCount5;
    private boolean mNotifyCount10;
    private boolean mNotifyMinutes;

    /**
     * 
     * @param minutes
     * @param seconds
     * @param notifyHalfWay
     * @param notifyCount10
     * @param notifyCount5
     * @param mNotifyMinutes
     */

    public TalkingTimer(int minutes, int seconds, boolean notifyHalfWay, boolean notifyCount10, boolean notifyCount5,
	    boolean mNotifyMinutes) {
	this.mMinutes = minutes;

	this.mSeconds = seconds;
	this.mNotifyHalfWay = notifyHalfWay;
	this.mNotifyCount10 = notifyCount10;
	this.mNotifyCount5 = notifyCount5;
	this.mNotifyMinutes = mNotifyMinutes;
    }

    public long getCountTimeMillis() {
	return ((mMinutes * 60) + mSeconds) * 1000;
    }

    public long getHalfwayMillis() {
	return getCountTimeMillis() / 2;
    }

    boolean mHalfwayMsgDelivered;

    long mCheckForMsgAtMillisUntilFinished;

    long mLastNotifiedMins;

    public void start() {

	mCheckForMsgAtMillisUntilFinished = getCountTimeMillis();
	mLastNotifiedMins = mMinutes;

	mTimer = new CountDownTimer(getCountTimeMillis(), 25) {

	    @Override
	    public void onTick(long millisUntilFinished) {
		
		
		//Log.v("talkinginterval", "millisUntilFinished"+millisUntilFinished);
		
		

		long mins = (millisUntilFinished / 1000) / 60;

		long secs = (millisUntilFinished / 1000) - (mins * 60);

		long millis = (millisUntilFinished - mins * 60 * 1000 - secs * 1000) / 10;

		mTimerListener.onTick((int) mins, (int) secs, (int) millis);

		if (millisUntilFinished < mCheckForMsgAtMillisUntilFinished) {

		    if (mNotifyMinutes && mins < mLastNotifiedMins) {

			mTimerListener.onMessageToTalk((mins + 1) + " minutes remaining");

			// deliverMessage(mins + " minuts remaining", (int)
			// secs);
			mLastNotifiedMins = mins;
		    }

		    else if (mNotifyHalfWay && (mHalfwayMsgDelivered == false)
			    && millisUntilFinished < getHalfwayMillis()) {
			mTimerListener.onMessageToTalk("halfway!");

			// deliverMessage("halfway!", (int) secs);
			mHalfwayMsgDelivered = true;
		    }

		    else if ((mNotifyCount10 && millisUntilFinished < 10 * 1000)
			    || (mNotifyCount5 && millisUntilFinished < 5 * 1000)) {

			mTimerListener.onMessageToTalk("" + (1 + millisUntilFinished / 1000));

		    }
		    mCheckForMsgAtMillisUntilFinished -= 1000;

		}

		// if (millisUntilFinished <= 11000 && millisUntilFinished > 9)
		// {
		// deliverMessage("ten");
		// }
		// if (millisUntilFinished <= 10 * 1000) {
		// deliverMessage("nine");
		// }
		// if (millisUntilFinished <= 9 * 1000) {
		// deliverMessage("eight");
		// }
		// if (millisUntilFinished <= 8 * 1000) {
		// deliverMessage("seven");
		// }
		// if (millisUntilFinished <= 7 * 1000) {
		// deliverMessage("six");
		// }
		// if (millisUntilFinished <= 6 * 1000) {
		// deliverMessage("five");
		// }
		// if (millisUntilFinished <= 5 * 1000) {
		// deliverMessage("four");
		// }
		// if (millisUntilFinished <= 4 * 1000) {
		// deliverMessage("three");
		// }
		// if (millisUntilFinished <= 3 * 1000) {
		// deliverMessage("two");
		// }
		// if (millisUntilFinished <= 2 * 1000) {
		// deliverMessage("one");
		// }

	    }

	    @Override
	    public void onFinish() {

		if (mNotifyCount10 || mNotifyCount5) {
		    mTimerListener.onMessageToTalk("0");
		}

		mTimerListener.onCountDownFinish(TalkingTimer.FINISHED);
	    }

	}.start();

    }

    public void stop() {
	if (mTimer != null)
	    mTimer.cancel();
    }

    // ArrayList<String> mDeliveredEvents = new ArrayList<String>();
    //
    // private int mLastMsgDeliveredOnSecond;
    //
    // private boolean deliverMessage(String message, int second) {
    //
    // if (mLastMsgDeliveredOnSecond == second)
    // return false;
    // else {
    // mTimerListener.onMessageToTalk(message);
    // mLastMsgDeliveredOnSecond = second;
    // }
    //
    // // for (String s : mDeliveredEvents)
    // // if (s.equals(message)) {
    // // return false;
    // // }
    // //
    // // mDeliveredEvents.add(message);
    //
    // return true;
    // }

    public void setTimerListener(TimerListener timerListener) {
	this.mTimerListener = timerListener;
    }

    // *********************************************************************************************************************
    // INTERFACE
    // *********************************************************************************************************************

    public interface TimerListener {
	void onCountDownFinish(int reason);

	void onTick(int min, int sec, int milli);

	void onMessageToTalk(String msg);
    }

}
