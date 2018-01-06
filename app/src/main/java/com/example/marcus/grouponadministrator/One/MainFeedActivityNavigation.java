package com.example.marcus.grouponadministrator.One;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marcus.grouponadministrator.R;
import com.example.marcus.grouponadministrator.User.LoginRegisterActivity;
import com.example.marcus.grouponadministrator.User.PersonalizeAccountActivity;
import com.example.marcus.grouponadministrator.User.User;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by Marcus on 8/30/2016.
 */
public class MainFeedActivityNavigation {
    private Context context;
    private NavigationView navigationView;

    private View background;
    private TextView usernameOrCompanyTextView;
    private TextView loginLogoutTextView;
    private ImageView avatarImageView;

    public MainFeedActivityNavigation(Context context, NavigationView navigationView) {
        this.context = context;
        this.navigationView = navigationView;

        background = navigationView.getHeaderView(0);
        usernameOrCompanyTextView = (TextView) background.findViewById(R.id.mainFeedNavHeader_CompanyNameOrUsername_TV);
        loginLogoutTextView = (TextView) background.findViewById(R.id.mainFeedNavHeader_loginRegisterOrLogout_TV);
        avatarImageView = (ImageView) background.findViewById(R.id.mainFeedNavHeader_avatar_IV);
    }

    public void setAsLogin(String username, String avatar_url) {
        setHeaderAsLoggedIn(username, avatar_url);
        setContentAsLoggedIn();
    }

    public void setAsNotLogin() {
        setHeaderAsNotLoggedIn();
        setContentAsNotLoggedIn();
    }

    private void setHeaderAsLoggedIn(String username, String avatar_url) {
        usernameOrCompanyTextView.setText(username);
        loginLogoutTextView.setText(context.getString(R.string.mainFeedNavHeader_logout));
        loginLogoutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //logout
                User.logout();

                //call to reload navigation view
                setAsNotLogin();
            }
        });

        //load image externally into ImageView
        Picasso.with(context)
                .load(avatar_url)
                .centerCrop() //for some reason it crashes without resize()
                .resize(200,200)
                //.memoryPolicy(MemoryPolicy.NO_CACHE ) //
                .networkPolicy(NetworkPolicy.NO_CACHE)// no network cache, so new image will be uploaded. invalidate() does not clear network cache
                .placeholder(R.drawable.ghoomo)
                .error(R.drawable.no_image)
                .transform(new CropCircleTransformation())
                .into(avatarImageView);

        //make avatar image view visibility visible
        avatarImageView.setVisibility(View.VISIBLE);

        //remove the on click listener
        background.setOnClickListener(null);
    }

    private void setHeaderAsNotLoggedIn() {
        usernameOrCompanyTextView.setText(context.getString(R.string.company_name));
        loginLogoutTextView.setText(context.getString(R.string.mainFeedNavHeader_loginOrRegister));
        loginLogoutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, LoginRegisterActivity.class);
                context.startActivity(intent);
            }
        });

        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(context, LoginRegisterActivity.class);
                context.startActivity(intent);
            }
        });

        //make avatar image view visibility gone
        avatarImageView.setVisibility(View.GONE);
    }

    private void setContentAsLoggedIn()
    {
        navigationView.setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener()
            {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem)
                {
                    int id = menuItem.getItemId();

                    switch(id)
                    {
                        case R.id.mainFeedDrawerView_nav_personalizeAccount:
                            Intent intent = new Intent(context, PersonalizeAccountActivity.class);context.startActivity(intent);
                            break;

                        case R.id.mainFeedDrawerView_nav_messages:
                            Toast.makeText(context, "messages", Toast.LENGTH_SHORT).show();
                            break;

                        case R.id.mainFeedDrawerView_nav_friends:
                            Toast.makeText(context, "friends", Toast.LENGTH_SHORT).show();
                            break;

                        case R.id.mainFeedDrawerView_nav_discussion:
                            Toast.makeText(context, "discussions", Toast.LENGTH_SHORT).show();
                            break;

                        case R.id.mainFeedDrawerView_nav_subItemOne:
                            Toast.makeText(context, "sub item 1", Toast.LENGTH_SHORT).show();
                            break;

                        case R.id.mainFeedDrawerView_nav_subItemTwo:
                            Toast.makeText(context, "sub item 2", Toast.LENGTH_SHORT).show();
                            break;

                        default:
                            break;
                    }

                    //menuItem.setChecked(true);

                    //close the drawer
                    //mDrawerLayout.closeDrawers();
                    return true;
                }
            }
        );
    }

    private void setContentAsNotLoggedIn()
    {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener()
                {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem)
                    {
                        int id = menuItem.getItemId();

                        switch(id)
                        {
                            case R.id.mainFeedDrawerView_nav_personalizeAccount:
                                Intent intent = new Intent(context, PersonalizeAccountActivity.class);
                                context.startActivity(intent);
                                break;

                            case R.id.mainFeedDrawerView_nav_messages:
                                Toast.makeText(context, "messages", Toast.LENGTH_SHORT).show();
                                break;

                            case R.id.mainFeedDrawerView_nav_friends:
                                Toast.makeText(context, "friends", Toast.LENGTH_SHORT).show();
                                break;

                            case R.id.mainFeedDrawerView_nav_discussion:
                                Toast.makeText(context, "discussions", Toast.LENGTH_SHORT).show();
                                break;

                            case R.id.mainFeedDrawerView_nav_subItemOne:
                                Toast.makeText(context, "sub item 1", Toast.LENGTH_SHORT).show();
                                break;

                            case R.id.mainFeedDrawerView_nav_subItemTwo:
                                Toast.makeText(context, "sub item 2", Toast.LENGTH_SHORT).show();
                                break;

                            default:
                                break;
                        }

                        //menuItem.setChecked(true);

                        //close the drawer
                        //mDrawerLayout.closeDrawers();
                        return true;
                    }
                }
        );
    }
}
