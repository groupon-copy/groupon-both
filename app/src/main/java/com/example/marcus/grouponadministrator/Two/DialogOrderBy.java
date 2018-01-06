package com.example.marcus.grouponadministrator.Two;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marcus.grouponadministrator.R;

/**
 * Created by Marcus Chiu on 4/17/2016.
 */
public class DialogOrderBy extends Dialog implements View.OnClickListener, AdapterView.OnItemSelectedListener
{
    public static final String dialogOrderBySpinnerKEY = "dialogOrderBy_SpinnerSelectionIndex";
    private Context context;
    private Spinner orderBySpinner;

    private OnDialogOrderBy mListener;

    public DialogOrderBy(Context context)
    {
        super(context);
        this.context = context;

        setContentView(R.layout.two_dialog_active_order_by);
        setTitle("This is my custom dialog box");
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        //there are a lot of settings, for dialog, check them all out!
        //http://developer.android.com/reference/android/app/Dialog.html

        //Load from File
        int orderBySelectedIndex = 0;
        String str = FileIO.readFromSharedPreferences(context, dialogOrderBySpinnerKEY);
        if(!str.equals(""))
            orderBySelectedIndex = Integer.parseInt(str);

        setTitle("ORDER BY"); //for previous version

        //set Views
        setView(orderBySelectedIndex);
    }

    @Override
    public void onClick(View view)
    {
        int id = view.getId();

        switch(id)
        {
            case R.id.dialogActiveOrderBy_backTextView:
                dismiss();
                break;
        }
    }

    private void setView(int selectionIndex) throws IndexOutOfBoundsException
    {
        orderBySpinner = (Spinner) findViewById(R.id.dialogActiveOrderBy_spinnerSortByType);
        orderBySpinner.setOnItemSelectedListener(this);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.sort_by_type, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        orderBySpinner.setAdapter(adapter);

        orderBySpinner.setSelection(selectionIndex);

        TextView cancel = (TextView) findViewById(R.id.dialogActiveOrderBy_backTextView);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        //save orderBy index to File
        FileIO.writeToSharedPreferences(context, dialogOrderBySpinnerKEY, Integer.toString(orderBySpinner.getSelectedItemPosition()));
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {
        Toast.makeText(context, "onNothingSelected", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onStart()
    {
        super.onStart();
        if (context instanceof OnDialogOrderBy)
        {
            mListener = (OnDialogOrderBy) context;
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
        mListener.onDialogOrderByStop(orderBySpinner.getSelectedItemPosition());
        mListener = null;
    }

    public interface OnDialogOrderBy
    {
        void onDialogOrderByStop(int orderBySelectedIndex);
    }
}
