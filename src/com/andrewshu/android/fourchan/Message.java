package com.andrewshu.android.fourchan;

public class Message {

    public String author = null;
    public String date = null;
    public String id = null;
    public String imageURL = null;      // Can be null
    public String thumbnailURL = null;  // Can be null
    public String messageText = "";     // Can be ""
    public String title = "";           // Can be ""
    public String tripcode = "";        // Can be ""

    @Override
    public String toString()
    {
//            return "("+author+") : " + messageText;
    	return id+":"+author+" ("+tripcode+") : "+date+" : "+imageURL+" : "+title+" : "+messageText;
    }
}
