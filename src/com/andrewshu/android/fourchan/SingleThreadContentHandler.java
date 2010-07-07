package com.andrewshu.android.fourchan;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class SingleThreadContentHandler implements ContentHandler {

	static final Pattern DATE_PATTERN = Pattern.compile("\\d\\d/\\d\\d/\\d\\d\\([A-Z][a-z][a-z]\\)\\d\\d:\\d\\d");

	private ArrayList<Message> mMessages = null;
	private Message mCurrentMessage = null;
	
	private int mNumHRs = 0;
	
	// States affecting upcoming parse
	private boolean mIsAuthor = false;
	private boolean mIsMessageText = false;
	private boolean mIsOP = false;
	private boolean mIsTitle = false;
	private boolean mIsTripcode = false;

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (mCurrentMessage != null) {
			// postername span
			if (mIsAuthor) {
				mCurrentMessage.author = String.valueOf(ch, start, length);
			}
			// blockquote
			else if (mIsMessageText) {
				// May be split up by sub-elements
				// like fonts (including spoiler tags), links to other messages, etc.
				// so append.
				mCurrentMessage.messageText += String.valueOf(ch, start, length);
			}
			// title span
			else if (mIsTitle) {
				mCurrentMessage.title = String.valueOf(ch, start, length);
			}
			// tripcode span
			else if (mIsTripcode) {
				mCurrentMessage.tripcode = String.valueOf(ch, start, length);
			}
			// date has no span. use first matching date pattern.
			else if (mCurrentMessage.date == null){
		        Matcher m = DATE_PATTERN.matcher(String.valueOf(ch, start, length));
		        if (m.find()) {
		        	mCurrentMessage.date = m.group();
		        }
			}
		}
	}

	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		
		// Skip everything before first 4 horizontal rules on page.
		if (mNumHRs < 4)
			return;
		
		if ("span".equals(localName)) {
			mIsAuthor = false;
			mIsTitle = false;
			mIsTripcode = false;
		}
		else if ("blockquote".equals(localName)) {
			mIsMessageText = false;
		}
		// Each reply (not OP) is contained in <table></table>
		else if ("table".equals(localName)) {
			if (mCurrentMessage != null) {
				mMessages.add(mCurrentMessage);
				mCurrentMessage = null;
			}
		}
	}

	public void endPrefixMapping(String prefix) throws SAXException {
		// TODO Auto-generated method stub
		
	}
	
	public ArrayList<Message> getMessages() {
		return mMessages;
	}

	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void processingInstruction(String target, String data)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void setDocumentLocator(Locator locator) {
		// TODO Auto-generated method stub
		
	}

	public void skippedEntity(String name) throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void startDocument() throws SAXException {
		mMessages = new ArrayList<Message>();
		mCurrentMessage = new Message();
		mIsOP = true;
		
		mIsAuthor = false;
		mIsMessageText = false;
		mIsTitle = false;
		mIsTripcode = false;
	}

	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		
		if ("hr".equals(localName)) {
			mNumHRs++;
			mIsOP = true;
			return;
		}
		
		// Skip everything before first 4 horizontal rules on page.
		if (mNumHRs < 4)
			return;
		
		// Each reply begins with <a name="NUMERICAL_ID"></a>
		if ("a".equals(localName) && atts.getValue("name") != null) {
			// For OP, the <a name=...> comes after image, in middle of message.
			if (mIsOP) {
				// mCurrentMessage should always be non-null immediately after OP, since OP doesn't have </table>
				if (mCurrentMessage != null)
					mMessages.add(mCurrentMessage);
				mIsOP = false;
			}
			// <a name=...> after the OP signifies the start of a reply.
			else {
				mCurrentMessage = new Message();
				mCurrentMessage.id = atts.getValue("name");
				return;
			}
		}
		
		if (mCurrentMessage != null) {
			// The only <img> elements in threads are thumbnails.
			// (Outside of threads there are ads and stuff.)
			if ("a".equals(localName)) {
				// Images have target="_blank"
				if ("_blank".equals(atts.getValue("target"))) {
					mCurrentMessage.imageURL = atts.getValue("href");
				}
			}
			else if ("img".equals(localName)) {
				mCurrentMessage.thumbnailURL = atts.getValue("src");
			}
			else if ("span".equals(localName)) {
				String spanClass = atts.getValue("class");
				// OP is class="postername", replies are class="commentpostername"
				if (spanClass != null && spanClass.endsWith("postername")) {
					mIsAuthor = true;
				}
				// class="replytitle" Title may be empty.
				else if (spanClass != null && spanClass.endsWith("title")) {
					mIsTitle = true;
				}
				else if (spanClass != null && spanClass.equals("postertrip")) {
					mIsTripcode = true;
				}
			}
			else if ("blockquote".equals(localName)) {
				mIsMessageText = true;
			}
		}
	}

	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

	
}
