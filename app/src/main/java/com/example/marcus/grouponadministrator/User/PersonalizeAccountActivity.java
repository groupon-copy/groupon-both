package com.example.marcus.grouponadministrator.User;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Bitmap;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.marcus.grouponadministrator.Config.DatabaseVariable;
import com.example.marcus.grouponadministrator.Database.PostUrlLoader;
import com.example.marcus.grouponadministrator.R;
import com.example.marcus.grouponadministrator.Utility;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class PersonalizeAccountActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>{
    //specific request codes for onActivityResult
    private static final int PICK_PHOTO_FOR_AVATAR = 0;
    //for loader manager
    private static final int LOADER_UPLOAD_AVATAR_IMAGE = 0;
    private boolean loadingFirstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!User.isLoggedIn()) {
            Toast.makeText(PersonalizeAccountActivity.this, "Need to Log in", Toast.LENGTH_SHORT).show();
            finish();
        }

        //set color of status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity_personalize_account);

        //gets toolbar/action bar
        Toolbar toolbar = (Toolbar)findViewById(R.id.userUpdateActivity_toolbar);
        //set support action bar to the newly created toolbar
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar(); // Get a support ActionBar corresponding to this toolbar
        //ab.setElevation(0); //no elevation
        //ab.setHomeAsUpIndicator(R.drawable.ic_menu); //set default back icon to another icon
        ab.setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(""); //similar to setTitle()
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));
        //collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(primary));
        //collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(primaryDark));

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.userUpdateActivity_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseOrTakePicture();
            }
        });

        //if user is logged in set avatar image, else finish() activity
        setAvatarImage();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setAvatarImage() {
        //get image view
        ImageView avatar = (ImageView) findViewById(R.id.userUpdateActivity_avatar_IV);

        //load image externally into ImageView
        Picasso.with(this)
                .load(User.getUser_avatar_img_full_url())
                .centerCrop() //for some reason it crashes without resize()
                .resize(600,500)
                //.memoryPolicy(MemoryPolicy.NO_CACHE )
                .networkPolicy(NetworkPolicy.NO_CACHE) // no network cache, so new image will be uploaded
                .placeholder(R.drawable.ghoomo)
                .error(R.drawable.no_image)
                .into(avatar);
    }

    //ToolBar menu method Interactions
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_personalize_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch(item.getItemId()) {
            case R.id.userUpdateToolbar_settings:
                Toast.makeText(this, "settings", Toast.LENGTH_SHORT).show();
                break;
            case R.id.userUpdateToolbar_help:
                Toast.makeText(this, "help", Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void chooseOrTakePicture() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, PICK_PHOTO_FOR_AVATAR);
        //Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //startActivityForResult(takePicture, PICK_PHOTO_FOR_AVATAR);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
            //get bitmap from intent data
            Bitmap bm = onSelectFromGalleryResult(data);
            if(bm != null) {
                //convert an bitmap image into base64 string
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 20, bao);
                byte[] ba = bao.toByteArray();
                String ba1 = Base64.encodeToString(ba, Base64.DEFAULT);

                Bundle bundle = new Bundle();
                bundle.putString("avatar_image", ba1);
                // Prepare the loader.  Either re-connect with an existing one, or start a new one.
                // fragmentNumber is the loader's unique id. Loader ids are specific to the Activity or
                // Fragment in which they reside.
                if(loadingFirstTime) {
                    getLoaderManager().initLoader(LOADER_UPLOAD_AVATAR_IMAGE, bundle, this);
                    loadingFirstTime = false;
                } else {
                    getLoaderManager().restartLoader(LOADER_UPLOAD_AVATAR_IMAGE, bundle, this);
                }
            } else {
                Toast.makeText(PersonalizeAccountActivity.this, "Error getting bitmap from Intent data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Bitmap onSelectFromGalleryResult(Intent data) {
        if (data != null) {
            try {
                //two ways to get bitmap from Intent data
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                //or
                //InputStream input = getContentResolver().openInputStream(data.getData());
                //input = new BufferedInputStream(input);
                //Bitmap bitmap = BitmapFactory.decodeStream(input);

                return bm;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("UserUpdate", "error data is null");
        }
        return null;
    }

    //dealing with keyboard and search icon
    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if(Utility.isPointerInsideEditTextOrSearchView(this, motionEvent)) {
            //hides the soft keyboard
            Utility.hideSoftKeyboard(this);

            //collapse the search icon in Toolbar
            getSupportActionBar().collapseActionView();
        }

        /*if(Utility.isSwipeLeftOrRight(motionEvent)) {
            String txt = "Ad Panel/Fragment "; //+ Integer.toString(currentAdFragment) + " SWIPED";
            Toast.makeText(this, txt, Toast.LENGTH_SHORT).show();
        }*/

        return super.dispatchTouchEvent(motionEvent);
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        try{
            if(id == LOADER_UPLOAD_AVATAR_IMAGE){
                HashMap<String, String> params = new HashMap<>();
                params.put(DatabaseVariable.ID, Integer.toString(User.getUser_id()));
                params.put(DatabaseVariable._POST_AVATAR_IMAGE, args.getString("avatar_image"));
                params.put(DatabaseVariable.API_KEY, User.getUser_api_key());

                return new PostUrlLoader(this, new URL(DatabaseVariable.UPLOAD_AVATAR_IMAGE_URL), params);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        try {
            if(loader.getId() == LOADER_UPLOAD_AVATAR_IMAGE) {
                JSONObject jo = new JSONObject(data);

                //if no error
                if(!jo.getBoolean(DatabaseVariable.ERROR)) {
                    //notify avatar image has changed
                    User.avatarImageChanged(this);

                    //update to new avatar image
                    setAvatarImage();
                } else {
                    Toast.makeText(PersonalizeAccountActivity.this, jo.getString(DatabaseVariable.MESSAGE), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}
