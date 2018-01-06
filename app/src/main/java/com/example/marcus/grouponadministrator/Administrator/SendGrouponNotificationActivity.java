package com.example.marcus.grouponadministrator.Administrator;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marcus.grouponadministrator.Config.AdminDatabaseVariable;
import com.example.marcus.grouponadministrator.Config.DatabaseVariable;
import com.example.marcus.grouponadministrator.Database.PostUrlLoader;
import com.example.marcus.grouponadministrator.R;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class SendGrouponNotificationActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>{
    //must be unique in activity/fragment
    private static final int LOADER_ID = 0;
    private boolean firstTimeLoading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //set color of status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.administrator_activity_send_groupon_notification);

        //gets toolbar/action bar
        Toolbar toolbar = (Toolbar)findViewById(R.id.sendGrouponNotification_toolbar);
        //set support action bar to the newly created toolbar
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar(); // Get a support ActionBar corresponding to this toolbar
        //ab.setElevation(0); //no elevation
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); //set default back icon to another icon
        ab.setDisplayHomeAsUpEnabled(true);
    }

    public void sendBroadcast(View view) {
        // Prepare the loader.  Either re-connect with an existing one, or start a new one.
        // fragmentNumber is the loader's unique id. Loader ids are specific to the Activity or
        // Fragment in which they reside.
        if(firstTimeLoading) {
            getLoaderManager().initLoader(LOADER_ID, null, this);
            firstTimeLoading = false;
        } else {
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }
    }

    @Override
    public Loader<String> onCreateLoader(int i, Bundle bundle) {

        TextView dealIDTextView = (TextView) findViewById(R.id.sendGrouponNotification_id);
        String dealID = dealIDTextView.getText().toString();

        HashMap<String, String> params = new HashMap<>();
        params.put(DatabaseVariable.ID, dealID);

        try
        {
            return new PostUrlLoader(this, new URL(AdminDatabaseVariable.FIREBASE_SEND_GROUPON_NOTIFICATION_URL), params);
        }
        catch(MalformedURLException e)
        {
            e.printStackTrace();
            Toast.makeText(this, "Error: MalformedURLException Caught", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String s) {
        TextView responseTextView = (TextView) findViewById(R.id.sendGrouponNotification_response_TV);
        responseTextView.setText(s);
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}
