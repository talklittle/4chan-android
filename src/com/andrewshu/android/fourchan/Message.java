package com.andrewshu.android.fourchan;

public class Message {

    public String author = null;
    public String date = null;
    public String id = null;
    public String imageURL = null;
    public String thumbnailURL = null;
    public String messageText = null;
    public String title = null;
    public String tripcode = null;

    @Override
    public String toString()
    {
//            return "("+author+") : " + messageText;
    	return author+" ("+tripcode+") : "+date+" : "+imageURL+" : "+messageText;
    }
}
