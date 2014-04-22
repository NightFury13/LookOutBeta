package com.example.lookout2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.media.AudioFormat;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.widget.Button;
import android.widget.TextView;



public class RecorderThread extends Thread {
	
int arr[] = new int [50];
int overallAvg = 1500;
int arr_ctr = 0;	
public boolean recording; //variable to start or stop recording
public Context con;
public int frequency; //the public variable that contains the frequency value "heard", it is updated continually while the thread is running.
public RecorderThread (Context context) {
	this.con = context;
	recording = true;
	setDaemon(true);
}


@Override
public void run() {
	
	
	AudioRecord recorder;
	short audioData[];
	int bufferSize;
		
	bufferSize=AudioRecord.getMinBufferSize(8000,AudioFormat.CHANNEL_IN_STEREO,AudioFormat.ENCODING_PCM_16BIT)*3; //get the buffer size to use with this audio record

	System.out.println("before");
	recorder = new AudioRecord (AudioSource.MIC,8000,AudioFormat.CHANNEL_IN_STEREO,AudioFormat.ENCODING_PCM_16BIT,bufferSize); //instantiate the AudioRecorder 
	System.out.println("after");	
	recording=true; //variable to use start or stop recording
	audioData = new short [bufferSize]; //short array that pcm data is put into.


	while (recording)
	{  //loop while recording is needed
		
		if (recorder.getState()==android.media.AudioRecord.STATE_INITIALIZED) // check to see if the recorder has initialized yet.
		if (recorder.getRecordingState()==android.media.AudioRecord.RECORDSTATE_STOPPED)
			recorder.startRecording();  //check to see if the Recorder has stopped or is not recording, and make it record.
				
		else {
			
				recorder.read(audioData,0,bufferSize); //read the PCM audio data into the audioData array
	
				int totalAbsValue = 0;
				short sample = 0; 
				float averageAbsValue = 0.0f;
				float samMax = 0;
    
				///////////////////////////////////////////////
				int voiceFlag = 0;
				
				float[] Auto = new float[bufferSize];
				
				//auto-correlation.
				for (int i = 0; i < bufferSize; i += 1){
					int sum = 0;
					for (int j = 0; j < bufferSize-i; j += 1){
						sum += (int)(audioData[j]*audioData[j+i]);
					}
					Auto[i] = sum;
				}
				
				System.out.println("first iter done");
				
				int thresh = 50; // threshold to check peaks in voice signals
				int nPeaks = 0; // the number of peaks.
				
				for (int i = 1; i < bufferSize - 1; i += 1){
					if (Auto[i] > thresh*Auto[i-1] && Auto[i] > thresh*Auto[i+1]){
						nPeaks += 1;
					}
				}
				
				System.out.println("peaks : "+ nPeaks);
				
				///////////////////////////////////////////////
				
				for (int i = 0; i < bufferSize; i += 1) {
					sample = (short)((audioData[i]));
					totalAbsValue += Math.abs(sample);
				}
				averageAbsValue = totalAbsValue / bufferSize ;
				
				arr[arr_ctr] = (int) averageAbsValue;
				arr_ctr += 1;
				
				if (arr_ctr == 30)
				{
					overallAvg = 0;
					for (int j=0; j<arr_ctr ; j++)
						overallAvg += arr[j];
					
					overallAvg = overallAvg/arr_ctr;
					arr_ctr = 0;
				}
				System.out.println("avg : "+ overallAvg);
				System.out.println("val : "+ averageAbsValue);
				for (int i = 0; i < bufferSize; i += 1) {
					sample = (short)((audioData[i]));
					if(Math.abs(sample) > 20*overallAvg) //current scale factor is 20.
						{
							
							if (averageAbsValue > 3*nPeaks){
								voiceFlag = 1;
							}
							else{
								voiceFlag = 0;
							}						    
						
							System.out.println("loud input, Alert!");	
							AudioManager am1 = (AudioManager)con.getSystemService(Context.AUDIO_SERVICE);
							//Log.i("am1.isWiredHeadsetOn()", am1.isWiredHeadsetOn());
							if(am1.isWiredHeadsetOn())
							{
								System.out.println("yes connected");
								//Define Notification Manager
								NotificationManager notificationManager = (NotificationManager) con.getSystemService(Context.NOTIFICATION_SERVICE);

								//Define sound URI
								Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

								NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(con.getApplicationContext())
								        //.setSmallIcon(icon)
								        //.setContentTitle(title)
								        //.setContentText(message)
								        .setSound(soundUri); //This sets the sound to play

								//Display notification
								//notificationManager.notify(0, mBuilder.build());
							}
							
							else {
								NotificationManager notificationManager = (NotificationManager) con.getSystemService(Context.NOTIFICATION_SERVICE);
								NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(con.getApplicationContext()).setContentText("Hello"); 
							        
							     
								Vibrator v = (Vibrator)con.getSystemService(Context.VIBRATOR_SERVICE);
								if (voiceFlag == 1){
								v.vibrate(800); 
								}
								else {
									v.vibrate(100);
								}
							}
						}
					
				}
				
			}//else recorder started

		} //while recording
				
		if (recorder.getState()==android.media.AudioRecord.RECORDSTATE_RECORDING) recorder.stop(); //stop the recorder before ending the thread
		
		recorder.release(); //release the recorders resources
		recorder=null; //set the recorder to be garbage collected.

    }//run





}//RecorderThread
