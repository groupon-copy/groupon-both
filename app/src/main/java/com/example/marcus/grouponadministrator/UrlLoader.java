package com.example.marcus.grouponadministrator;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.net.URL;

/**
 * Created by Marcus on 7/1/2016.
 */
public class UrlLoader extends AsyncTaskLoader<String>
{
    private static final String TAG = "UrlLoader";
    private static final boolean DEBUG = false;

    //list of current deals displayed
    private String str;
    //string of tags
    private URL url;

    public UrlLoader(Context context, URL url)
    {
        //Loaders may be used across multiple Activities
        //(assuming they aren't bound to the LoaderManager)
        //so never reference a context directly. doing so will cause you to leak an entire Activity's context.
        //the super class constructor will store a reference to the Application Context
        //instead, and can be retrieved with a call to getContext()
        super(context);
        this.url = url;
    }

    //1. A task that performs the asynchronous load
    @Override
    public String loadInBackground()
    {
        if(DEBUG) Log.i(TAG, "loadInBackground");

        if(url == null) return null;

        // This method is called on a background thread and should generate a
        // new set of data to be delivered back to the client.
        str = "";
        try
        {
            String dataString = GetProcessor.getIt(url);

            str = dataString;
            //convert JSON String into Deal Objects
            //JSONObject jsonObject = new JSONObject(dataString);
        }
        catch (Exception e)
        {
            if(DEBUG) e.printStackTrace();

            Log.e(TAG, "error " + url.toString());

            //either return empty list or what we have in the dealList so far
            //return new ArrayList<>();
            return str;
        }

        //get image for each deal
        /*for (Deal de: dealList)
        {
            try
            {
                InputStream is = (InputStream)(new URL(de.getImg_ref()).getContent());
                Drawable d = Drawable.createFromStream(is, "src");
                de.setImage(d);
            }
            catch(Exception e)
            {
                if(DEBUG) e.printStackTrace();

                de.setImage(ContextCompat.getDrawable(getContext(), R.drawable.no_image));
            }
        }*/

        return str;
    }

    //2. Deliver the results to the registered listener
    @Override
    public void deliverResult(String data)
    {
        if(DEBUG) Log.i("DealListLoader", "deliverResult");

        if(isReset())
        {
            //Loader has been reset; ignore the result and invalidate the data
            releaseResources(str);
            return;
        }

        //Hold a reference to the old data so it doesn't get garbage collected
        //we must protect it until new data has been delivered
        String oldStr = str;
        str = data;

        if(isStarted())
        {
            //if loader is in a started state, deliver results to the client
            //superclass method does this for us
            super.deliverResult(data);
        }

        if(oldStr != null && oldStr != str)
        {
            releaseResources(oldStr);
        }

        super.deliverResult(data);
    }

    //3. Implement the Loader's state-dependent behavior
    @Override
    protected void onStartLoading()
    {
        if(DEBUG) Log.i("DealListLoader", "onStartLoading");

        if(str != null)
        {
            //deliver any previously loaded data immediately
            deliverResult(str);
        }

        //begin monitoring the underlying data source
        /*if(mObserver == null)
        {
            mObserver = new SampleObserver();
            //TODO register the observer
        }*/

        if(takeContentChanged() || str == null)
        {
            //when the observer detects a change, it should call onContentChanged()
            //on the Loader, which cause the next call to takeContentChanged()
            //to return true. If this is ever the case (or if the current data is null)
            //we force a new load
            forceLoad();
        }

        super.onStartLoading();
    }

    @Override
    protected void onStopLoading()
    {
        if(DEBUG) Log.i("DealListLoader", "onStopLoading");

        //The Loader is in a stopped state, so we should attempt to cancel the current load
        //if there is one
        cancelLoad();

        //note we should leave the observer as it is. Loaders in a stopped state
        //should still monitor the data for changes so that the Loader
        //will know to force a new load if it is ever started again
        super.onStopLoading();
    }

    @Override
    protected void onReset()
    {
        if(DEBUG) Log.i("DealListLoader", "onReset");

        //ensure the Loader has been stoped
        onStopLoading();

        //at this point we can release the resources associated with 'dealList'
        if(str != null)
        {
            releaseResources(str);
            str = null;
        }

        //the Loader is being reset, so we should stop monitoring for changes
        /*if(mObserver != null)
        {
            //TODO: unregister the observer
            mObserver = null;
        }*/

        super.onReset();
    }

    @Override
    public void onCanceled(String data)
    {
        if(DEBUG) Log.i("DealListLoader", "onCanceled");

        //attempt to cancel the current asynchronous Load
        super.onCanceled(data);

        //the Load has been canceled, so we should release the resources
        releaseResources(str);
    }

    private void releaseResources(String data)
    {
        // For a simple List, there is nothing to do. For something like a Cursor, we
        // would close it in this method. All resources associated with the Loader
        // should be released here.
    }

    /*********************************************************************/
    /** (4) Observer which receives notifications when the data changes **/
    /*********************************************************************/

    // NOTE: Implementing an observer is outside the scope of this post (this example
    // uses a made-up "SampleObserver" to illustrate when/where the observer should
    // be initialized).

    // The observer could be anything so long as it is able to detect content changes
    // and report them to the loader with a call to onContentChanged(). For example,
    // if you were writing a Loader which loads a list of all installed applications
    // on the device, the observer could be a BroadcastReceiver that listens for the
    // ACTION_PACKAGE_ADDED intent, and calls onContentChanged() on the particular
    // Loader whenever the receiver detects that a new application has been installed.
    // Please don’t hesitate to leave a comment if you still find this confusing! :)
    //private SampleObserver mObserver;
}
