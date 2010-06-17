package com.andrewshu.android.fourchan;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class ImageBoardParser {
	
	private static final String TAG = "ImageBoardParser";
	
    String _4ChanThread = "http://boards.4chan.org/a/res/36406990";

    class Message
    {
        public String author;
        public String date;
        public String imageURL;
        public String messageText;

        @Override
        public String toString()
        {
            return "("+author+") : " + messageText;
        }
    }

    public static void Main()
    {
        ImageBoardParser ibp = new ImageBoardParser();
        ibp.entryPoint();
    }

    public void entryPoint()
    {
        List<Message> messages = getPosts(_4ChanThread);
        for (Message message : messages) {
        	Log.d(TAG, message.author+":"+message.date+":"+message.imageURL+":"+message.messageText);
        }
        
    }

    List<Message> getPosts(String thrd)
    {
    	DefaultHttpClient client = CommonClient.getGzipHttpClient();
    	
    	HttpGet request = null;
    	HttpResponse response = null;
    	HttpEntity entity = null;
    	InputStream content = null;
    	
    	List<Message> messages = null;
    	
    	try {
	    	request = new HttpGet(thrd);
	        response = client.execute(request);
	    	entity = response.getEntity();
	    	content = entity.getContent();
	    	
	    	String data = Util.slurp(content);
	        String[] splitted = data.split("<hr>");
	        data = splitted[3];
	        splitted = data.split("<table>");
	
	        messages = new ArrayList<Message>();
	        for (String s : splitted)
	        {
	            Message mess = new Message();
	            for (String str : s.split("<span "))
	            {
	                if (str.contains("form"))
	                    continue;
	                if (str.contains("File"))
	                    mess.imageURL = str.split("<a href=\"")[1].split("\"")[0];
	                if (str.contains("postername"))
	                {
	                    mess.author = str.split(">")[1].split("<")[0];
	                    mess.date = str.split("</span>")[1];
	                }
	                if (str.contains("blockquote"))
	                {
	                    String message = str.split("blockquote")[1];
	                    if (str.contains("quotelink"))
	                        message = message.split("</font>")[1];
	                    mess.messageText = message.replaceAll("<br />", "").replaceAll("</", "").replaceAll("<", "").replaceAll(">", "");
	                }
	            }
	            messages.add(mess);
	        }
    	} catch (Exception e) {
    		if (Constants.LOGGING) Log.e(TAG, "getPosts", e);
    	} finally {
    		try { content.close(); } catch (Exception ignore) {}
    		try { entity.consumeContent(); } catch (Exception ignore) {}
    	}
        return messages;
    }


}
