package com.andrewshu.android.fourchan;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.InputSource;

import android.util.Log;

public class ImageBoardParser {
	
	private static final String TAG = "ImageBoardParser";
	
    String _4ChanThread = "http://boards.4chan.org/v/res/65842698";

    public static void Main()
    {
        ImageBoardParser ibp = new ImageBoardParser();
        ibp.entryPoint();
    }

    public void entryPoint()
    {
        List<Message> messages = getPosts(_4ChanThread);
        if (messages != null) {
	        for (Message message : messages) {
	        	Log.d(TAG, message.toString());
	        }
        } else {
        	Log.w(TAG, "messages null. Thread expired?");
        }
        
    }

    List<Message> getPosts(String thrd)
    {
    	DefaultHttpClient client = CommonClient.getGzipHttpClient();
    	
    	HttpGet request = null;
    	HttpResponse response = null;
    	HttpEntity entity = null;
    	InputStream content = null;
    	long contentLength = -1;
    	
    	try {
	    	request = new HttpGet(thrd);
	        response = client.execute(request);
	    	entity = response.getEntity();
	    	content = entity.getContent();
	    	// entity.getContentLength() seems to always return -1, but it's ok
	    	// because ProgressInputStream doesn't seem to use the maxNumBytes parameter anyway.
	    	contentLength = entity.getContentLength();
	    	
	        SingleThreadContentHandler singleHandler = new SingleThreadContentHandler();
	        Parser parser = new Parser();
	        parser.setContentHandler(singleHandler);
	        InputSource in = new InputSource(new ProgressInputStream(content, contentLength));
	        parser.parse(in);
	        
	        return singleHandler.getMessages();

    	} catch (Exception e) {
    		if (Constants.LOGGING) Log.e(TAG, "getPosts", e);
    	} finally {
    		try { content.close(); } catch (Exception ignore) {}
    		try { entity.consumeContent(); } catch (Exception ignore) {}
    	}
    	return null;
    }


}
