package com.andrewshu.android.fourchan;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.InputSource;

import android.os.AsyncTask;
import android.util.Log;

public abstract class LoadBoardTask extends AsyncTask< String, Long, List<Message> >
		implements PropertyChangeListener {
	
	private static final String TAG = "LoadBoardTask";
	
	public long mContentLength = -1;

	@Override
	public List<Message> doInBackground(String... boardURLs) {
    	DefaultHttpClient client = CommonClient.getGzipHttpClient();
    	
    	HttpGet request = null;
    	HttpResponse response = null;
    	HttpEntity entity = null;
    	InputStream content = null;
    	
    	ArrayList<Message> allMessages = new ArrayList<Message>();
    	
    	try {
    		for (String boardURL : boardURLs) {
		    	request = new HttpGet(boardURL);
		        response = client.execute(request);
		    	entity = response.getEntity();
		    	content = entity.getContent();

		    	// 4chan doesn't return Content-Length headers. Use slurped data instead.
		    	String data = Util.slurp(content);
		    	mContentLength = data.length();
		    	ByteArrayInputStream byteContent = new ByteArrayInputStream(data.getBytes());
		    	
		        ProgressInputStream pin = new ProgressInputStream(byteContent, mContentLength);
		        pin.addPropertyChangeListener(LoadBoardTask.this);
		        InputSource in = new InputSource(pin);
		        
		        SingleThreadContentHandler singleHandler = new SingleThreadContentHandler();
		        Parser parser = new Parser();
		        parser.setContentHandler(singleHandler);
		        parser.parse(in);
		        
		        allMessages.addAll(singleHandler.getMessages());
		        
		        try { byteContent.close(); } catch (Exception ignore) {}
		        try { content.close(); } catch (Exception ignore) {}
	    		try { entity.consumeContent(); } catch (Exception ignore) {}
    		}
	        
	        return allMessages;

    	} catch (Exception e) {
    		if (Constants.LOGGING) Log.e(TAG, "getPosts", e);
    	} finally {
    		try { content.close(); } catch (Exception ignore) {}
    		try { entity.consumeContent(); } catch (Exception ignore) {}
    	}
    	return null;
	}
	
	public void propertyChange(PropertyChangeEvent event) {
		publishProgress((Long) event.getNewValue());
	}
}
