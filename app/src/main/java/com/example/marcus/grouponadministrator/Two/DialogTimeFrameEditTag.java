package com.example.marcus.grouponadministrator.Two;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.marcus.grouponadministrator.R;

/**
 * Created by Marcus Chiu on 4/24/2016.
 */
public class DialogTimeFrameEditTag  extends Dialog implements View.OnClickListener
{
    private EditText tagNameEditText;
    private Context context;
    private OnDialogTimeFrameEditTag mListener;

    public DialogTimeFrameEditTag(Context context)
    {
        super(context);

        this.context = context;

        setContentView(R.layout.two_dialog_time_frame_edit_tag);
        //setTitle("This is my custom dialog box");
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        //there are a lot of settings, for dialog, check them all out!
        //http://developer.android.com/reference/android/app/Dialog.html

        tagNameEditText = (EditText)findViewById(R.id.dialogTimeFrameEditTag_editText);

        TextView cancel = (TextView) findViewById(R.id.dialogTimeFrameEditTag_cancelButton);
        cancel.setOnClickListener(this);

        TextView okay = (TextView) findViewById(R.id.dialogTimeFrameEditTag_OKButton);
        okay.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        int id = view.getId();

        switch(id)
        {
            case R.id.dialogTimeFrameEditTag_OKButton:
                mListener.onDialogTimeFrameEditTagOkay(tagNameEditText.getText().toString());
                tagNameEditText.setText("");
                dismiss();
                break;

            case R.id.dialogTimeFrameEditTag_cancelButton:
                dismiss();
                break;
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (context instanceof OnDialogTimeFrameEditTag)
        {
            mListener = (OnDialogTimeFrameEditTag) context;
        }
        else
        {
            throw new RuntimeException(context.toString() + " must implement OnDialogOrderBy");
        }
    }
    @Override
    protected void onStop()
    {
        super.onStop();
        mListener = null;
    }

    public interface OnDialogTimeFrameEditTag
    {
        void onDialogTimeFrameEditTagOkay(String text);
    }

    @Override
    public void show()
    {
        super.show();
    }
}