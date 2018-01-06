package com.example.marcus.grouponadministrator.User;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.example.marcus.grouponadministrator.R;

/**
 * Created by Marcus on 8/1/2016.
 */

public class ResendVerifyEmailDialog extends Dialog
{
    private Fragment fragment;
    private OnDialogResendVerifyEmail mListener;

    public ResendVerifyEmailDialog(Context context, Fragment fragment)
    {
        super(context);
        this.fragment = fragment;

        setContentView(R.layout.user_resend_verify_email_dialog);
        setTitle(context.getString(R.string.resendVerifyEmailDialog_title)); //for previous version
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        //there are a lot of settings, for dialog, check them all out!
        //http://developer.android.com/reference/android/app/Dialog.html

        TextView resend = (TextView) findViewById(R.id.dialogResendVerifyEmail_resend_TV);
        resend.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mListener.resendVerificationEmail();
                dismiss();
            }
        });

        TextView cancel = (TextView) findViewById(R.id.dialogResendVerifyEmail_cancel_TV);
        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dismiss();
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (fragment instanceof OnDialogResendVerifyEmail)
        {
            mListener = (OnDialogResendVerifyEmail) fragment;
        }
        else
        {
            throw new RuntimeException(fragment.toString() + " must implement OnDialogResendVerifyEmail");
        }
    }
    @Override
    protected void onStop()
    {
        super.onStop();
        mListener = null;
    }

    public interface OnDialogResendVerifyEmail
    {
        void resendVerificationEmail();
    }
}
