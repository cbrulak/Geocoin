/*
Copyright (c) 2011, Chris Brulak
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

    Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT 
HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED 
TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.brulak.androidtraining.geocoin;

//import com.brulak.androidtraining.util.ClientUtils;
import com.brulak.nfcutils.NFCReader;
import com.brulak.nfcutils.NFCWriter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
	static final String TAG = "GeoCoin";
	String messageToLeave;

	static final int ACTIVITY_TIMEOUT_MS = 1 * 1000;

	static final int OUT_CONTACTS = 0;
	static final int IN_CONTACTS = 1;
	
	private boolean bLeaveMessage = false;
	private final int ENTER_GIFT_AMOUNT_ACTIVITY = 0;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        messageToLeave = "Gift card is not initialized";
        
		Intent intent = getIntent();
		NFCReader reader = new NFCReader();
		String message = reader.ReadMessageFromTag(intent);
		updateStatusArea(message);
		//ClientUtils.showToast(this, "Message " + message);
		
		if(bLeaveMessage) 
		{
			leaveMessage();
		}
    }
    
    public void onReadMessage(View v) {
    	
		Intent intent = getIntent();
		
		NFCReader reader = new NFCReader();
		
		String message = reader.ReadMessageFromTag(intent);
	
		updateStatusArea(message);
		
    }
    
    public void onLeaveMessage(View v) 
    {

    	
    	NFCWriter writer = new NFCWriter();
    	
    	if(!writer.isTagPresent(getIntent()))
    	{
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setMessage("Tag is not in range. Please put the tag in contact with your phone and try again");
    		AlertDialog alert = builder.create();
    		alert.show();
    		return;
    	}
    	
    	Intent intent = new Intent(this,EnterMessageActivity.class);
    	startActivityForResult(intent,0);

    }
    
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == ENTER_GIFT_AMOUNT_ACTIVITY && resultCode == Activity.RESULT_OK)
		{
			Bundle extras = data.getExtras();
			
			String message = extras.getString("message");
			
	    	this.bLeaveMessage = true;
	    	updateStatusArea("writing....." + message);
	    	
	    	messageToLeave = message;
	    	
	    	leaveMessage();
			
		}
		
	}
    	
    	
    private void leaveMessage() 
    {
    	Intent intent = getIntent();
    	
    	NFCWriter writer = new NFCWriter();
    	
    	if (writer.doesTagNeedFormatting(intent))
    	{
    		Log.d(TAG,"Formating...");
    		if(!writer.formatTag(intent, this.messageToLeave))
    		{
           		Log.d(TAG, "FormatTag FAILED");
    			//Log.d(TAG, "LeaveMessageException:" + e.getCause());
        		//Log.d(TAG, "LeaveMessageException:" + e.getStackTrace());
    		}
    		Log.d(TAG,"Exiting from Formatting...");
        	finish();
        	return;
    	}
    	writer.writeMessage(intent, messageToLeave);
    }

    private void updateStatusArea(String message)
    {
    	TextView tv = (TextView) findViewById(R.id.statusText);
    	
    	tv.setText(message);
    
    }
}