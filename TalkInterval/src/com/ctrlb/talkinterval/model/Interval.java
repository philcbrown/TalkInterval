package com.ctrlb.talkinterval.model;

import java.util.HashMap;
import android.os.Parcel;
import android.os.Parcelable;

public class Interval implements Parcelable {
    
    public static String INTERVAL_ID = "id";
    public static String INTERVAL_NAME = "name";
    public static String INTERVAL_MINS = "mins";
    public static String INTERVAL_SECS = "secs";
    public static String INTERVAL_COUNTDOWN_ALERT = "countdown_alert";
    public static String INTERVAL_HALFWAY_ALERT = "halfway_alert";
    public static String INTERVAL_MINUTES_ALERT = "minutes_alert";
    public static String INTERVAL_NAME_ALERT = "name_alert";
    public static String INTERVAL_COLOR = "color";
    public static String INTERVAL_ORDER = "order";

    private int id;
    private String name;
    private int minutes;
    private int seconds;
    private boolean mCountdownAlert;
    private boolean mHalfwayAlert;
    private boolean mMinutesAlert;
    private boolean mNameAlert;
    private int mColor;
    private int mOrder;
    
    public boolean getNameAlert() {
        return mNameAlert;
    }

    public void setNameAlert(boolean nameAlert) {
        this.mNameAlert = nameAlert;
    }
    
    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        this.mColor = color;
    }

    public int getOrder() {
        return mOrder;
    }

    public void setOrder(int order) {
        this.mOrder = order;
    }

    public boolean getCountdownAlert() {
	return mCountdownAlert;
    }

    public void setCountdownAlert(boolean countdownAlert) {
	this.mCountdownAlert = countdownAlert;
    }

    public boolean getHalfwayAlert() {
	return mHalfwayAlert;
    }

    public void setHalfwayAlert(boolean halfwayAlert) {
	this.mHalfwayAlert = halfwayAlert;
    }

    public boolean getMinutesAlert() {
	return mMinutesAlert;
    }

    public void setMinutesAlert(boolean minutesAlert) {
	this.mMinutesAlert = minutesAlert;
    }

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public int getMinutes() {
	return minutes;
    }
    
    public String getFormatedMinutes(){
	return String.format("%02d", getMinutes());
    }

    public void setMinutes(int minutes) {
	this.minutes = minutes;
    }

    public int getSeconds() {
	return seconds;
    }
    
    public String getFormatedSeconds(){
	return String.format("%02d", getSeconds());
    }

    public void setSeconds(int seconds) {
	this.seconds = seconds;
    }

    public Interval() {
    }

    public Interval(int id, String name, int minutes, int seconds) {
	this.id = id;
	this.name = name;
	this.minutes = minutes;
	this.seconds = seconds;
    }

    public Interval(Parcel in) {
	String[] data = new String[10];
	in.readStringArray(data);
	this.id = Integer.parseInt(data[0]);
	this.name = data[1];
	this.minutes = Integer.parseInt(data[2]);
	this.seconds = Integer.parseInt(data[3]);
	this.mHalfwayAlert = Boolean.parseBoolean(data[4]);
	this.mCountdownAlert = Boolean.parseBoolean(data[5]);
	this.mMinutesAlert = Boolean.parseBoolean(data[6]);
	this.mNameAlert = Boolean.parseBoolean(data[7]);
	this.mColor = Integer.parseInt(data[8]);
	this.mOrder = Integer.parseInt(data[9]);
    }

    @Override
    public int describeContents() {
	return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
	dest.writeStringArray(new String[] {
		Integer.toString(this.id),
		this.name,
		Integer.toString(this.minutes),
		Integer.toString(this.seconds),
		String.valueOf(mHalfwayAlert),
		String.valueOf(mCountdownAlert),
		String.valueOf(mMinutesAlert),
		String.valueOf(mNameAlert),
		Integer.toString(mColor),
		String.valueOf(mOrder)});
    }

    public static final Parcelable.Creator<Interval> CREATOR = new Parcelable.Creator<Interval>() {
	public Interval createFromParcel(Parcel in) {
	    return new Interval(in);
	}

	public Interval[] newArray(int size) {
	    return new Interval[size];
	}
    };
    
    public HashMap<String, String> getHashMap(){
	HashMap<String, String> hm  = new HashMap<String, String>();
	hm.put(INTERVAL_NAME, getName());
	hm.put(INTERVAL_MINS, ""+getFormatedMinutes());
	hm.put(INTERVAL_SECS, ""+getFormatedSeconds());
	hm.put(INTERVAL_COUNTDOWN_ALERT, ""+getCountdownAlert());
	hm.put(INTERVAL_HALFWAY_ALERT, ""+getHalfwayAlert());
	hm.put(INTERVAL_MINUTES_ALERT, ""+getMinutesAlert());
	hm.put(INTERVAL_NAME_ALERT, ""+getNameAlert());
	hm.put(INTERVAL_COLOR, ""+getColor());
	hm.put(INTERVAL_ORDER, ""+getOrder());
	return hm;
    }

}
