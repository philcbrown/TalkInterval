package com.ctrlb.talkinterval.activity;

import com.ctrlb.talkinterval.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.view.ContextThemeWrapper;

public class IntervalSetDialog extends DialogFragment implements OnClickListener {

    private String mSetName;

    public interface IntervalSetDialogListener {
	public void onDialogSaveClick(DialogFragment dialog);

	public void onDialogCancelClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    IntervalSetDialogListener mListener;

    static IntervalSetDialog newInstance(String setName) {
	IntervalSetDialog f = new IntervalSetDialog();

	// Supply num input as an argument.
	Bundle args = new Bundle();
	args.putString("setName", setName);
	f.setArguments(args);

	return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
	setStyle(STYLE_NO_TITLE, R.style.MyDialog);
	super.onCreate(savedInstanceState);

	mSetName = getArguments().getString("setName");

    }

    //@Override
    //public void onSaveInstanceState(Bundle arg0) {
	// TODO Auto-generated method stub
//	super.onSaveInstanceState(arg0);

	// EditText edtTxtName = (EditText)
	// getView().findViewById(R.id.edtTxt_set_name);
	// arg0.put("name", );
   // }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

	AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(),
		R.style.TalkingIntervalDialog));
	AlertDialog rtn = builder.create();

	LayoutInflater mInflater = LayoutInflater.from(getActivity());
	View v = mInflater.inflate(R.layout.interval_set_dialog, null, false);

	((Button) v.findViewById(R.id.btnSave)).setOnClickListener(this);
	((Button) v.findViewById(R.id.BtnCancel)).setOnClickListener(this);

	EditText edtTxtName = (EditText) v.findViewById(R.id.edtTxt_set_name);
	edtTxtName.setText(mSetName);
	edtTxtName.requestFocus();

	rtn.setView(v, 0, 0, 0, 0);

	// builder.setIcon(R.drawable.ic_interval_set_add);
	// builder.setTitle(R.string.interval_set);

	// builder.setPositiveButton(R.string.save, new
	// DialogInterface.OnClickListener() { public void
	// onClick(DialogInterface
	// dialog, int id) {

	// getActivity()

	// mListener.onDialogSaveClick(IntervalSetDialog.this);

	// dialog.

	// EditText et = //
	// (EditText)getView().findViewById(R.id.edtTxt_set_name); //
	// et.setText(setName);

	// } });

	// builder.setNegativeButton(R.string.cancel, new
	// DialogInterface.OnClickListener() {

	// /@Override public void onClick(DialogInterface dialog, int which) {
	// dismiss();

	// } });

	// mIntrvlSetDialog = builder.create();

	rtn.setCanceledOnTouchOutside(false);

	return rtn;

    }

    /*
     * @Override public View onCreateView(LayoutInflater inflater, ViewGroup
     * container, Bundle savedInstanceState) {
     * 
     * View view = inflater.inflate(R.layout.interval_set_dialog, container);
     * 
     * // ((Button) view.findViewById(R.id.btnSave)).setOnClickListener(this);
     * // ((Button) //
     * view.findViewById(R.id.BtnCancel)).setOnClickListener(this); // ((Button)
     * //
     * view.findViewById(R.id.btn_reset_interval_dialog)).setOnClickListener(this
     * );
     * 
     * return view; }
     */

    // public void setName(String name) {
    // this.name = name;
    // }

    @Override
    public void onAttach(Activity activity) {
	super.onAttach(activity);
	// Verify that the host activity implements the callback interface
	try {
	    // Instantiate the NoticeDialogListener so we can send events to the
	    // host
	    mListener = (IntervalSetDialogListener) activity;
	} catch (ClassCastException e) {
	    // The activity doesn't implement the interface, throw exception
	    throw new ClassCastException(activity.toString() + " must implement IntervalSetDialogListener");
	}
    }

    @Override
    public void onStart() {
	super.onStart();

    }

    @Override
    public void onClick(View v) {
	switch (v.getId()) {
	case R.id.btnSave:
	    mListener.onDialogSaveClick(this);
	    dismiss();
	    break;
	case R.id.BtnCancel:
	    mListener.onDialogCancelClick(this);
	    dismiss();
	    break;
	}

    }
}
