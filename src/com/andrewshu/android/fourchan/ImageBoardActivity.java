package com.andrewshu.android.fourchan;

import java.beans.PropertyChangeListener;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Window;

public class ImageBoardActivity extends ListActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        ImageBoardParser.Main();
    }
    
    private class MyLoadBoardTask extends LoadBoardTask
    		implements PropertyChangeListener {
    	
    	protected final String TAG = "ImageBoardActivity$MyLoadBoardTask";

    	@Override
    	public void onPreExecute() {
    		
    	}
    	
    	@Override
    	public void onPostExecute(List<Message> messages) {
    		
    	}

    	@Override
    	public void onProgressUpdate(Long... progress) {
    		// 0-9999 is ok, 10000 means it's finished
    		getWindow().setFeatureInt(Window.FEATURE_PROGRESS, progress[0].intValue() * 9999 / (int) mContentLength);
    	}
    }
}