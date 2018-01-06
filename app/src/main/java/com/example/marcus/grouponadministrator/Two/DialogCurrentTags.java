package com.example.marcus.grouponadministrator.Two;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.marcus.grouponadministrator.R;

import java.util.ArrayList;

/**
 * Created by Marcus Chiu on 4/24/2016.
 */
public class DialogCurrentTags extends Dialog implements View.OnClickListener
{
    //Unique identifier for this Dialog; used in the log
    private static final String TAG = "DialogCurrentTags";
    private static final boolean DEBUG = false;

    private Context context;

    private LinearLayout tagsLayout;
    private EditText editText;

    public DialogCurrentTags(Context context)
    {
        super(context);
        this.context = context;

        setContentView(R.layout.two_dialog_current_tags);
        setTitle("This is my custom dialog box");
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        //there are a lot of settings, for dialog, check them all out!
        //http://developer.android.com/reference/android/app/Dialog.html

        setTitle("TAGS"); //for previous version

        setView();
    }

    @Override
    public void onClick(View view)
    {
        int id = view.getId();

        switch(id)
        {
            case R.id.dialogCurrentTags_backTextView:
                dismiss();
                break;

            case R.id.dialogCurrentTags_tagsAdd:
                //get string name of Edit Text
                String tag = editText.getText().toString();
                //add to file if does not exist, then create button
                if(TagsCurrentlyBeingSearch.addTagToFile(context, tag))
                    addTagButton(tag);
                editText.setText("");
                break;
        }

        if(view.getTag() == "dialogCurrentTags_tagButton")
        {
            //remove tag button
            TagsCurrentlyBeingSearch.removeTagFromFile(context, ((Button) view).getText().toString());
            ((ViewManager) view.getParent()).removeView(view);
        }
    }

    private void setView()
    {
        TextView back = (TextView) findViewById(R.id.dialogCurrentTags_backTextView);
        back.setOnClickListener(this);

        editText = (EditText)findViewById(R.id.dialogCurrentTags_editText);

        TextView add = (TextView) findViewById(R.id.dialogCurrentTags_tagsAdd);
        add.setOnClickListener(this);

        tagsLayout = (LinearLayout)findViewById(R.id.dialogCurrentTags_tagsLinearLayout);
    }

    private void addTagButton(String tag)
    {
        //set params for all buttons
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //params.weight = 1.0f;
        params.gravity = Gravity.CENTER_HORIZONTAL;

        Button b = new Button(context);
        b.setText(tag);

        b.setOnClickListener(this);
        b.setLayoutParams(params);
        b.setTag("dialogCurrentTags_tagButton");

        tagsLayout.addView(b);
    }

    @Override
    public void show()
    {
        super.show();

        tagsLayout.removeAllViews();

        ArrayList<String> tags = TagsCurrentlyBeingSearch.getTagsFromFile(context);

        //set params for all buttons
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //params.weight = 1.0f;
        params.gravity = Gravity.CENTER_HORIZONTAL;

        for(String s : tags)
        {
            Button b = new Button(context);
            b.setText(s);

            b.setOnClickListener(this);
            b.setLayoutParams(params);
            b.setTag("dialogCurrentTags_tagButton");

            tagsLayout.addView(b);
        }
    }
}
