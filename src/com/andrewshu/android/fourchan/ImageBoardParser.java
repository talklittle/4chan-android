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

    }


}
