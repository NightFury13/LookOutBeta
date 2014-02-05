package com.example.lookout2;

import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.media.AudioFormat;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;



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
					if(Math.abs(sample) > 20*overallAvg)
						{
							System.out.println("123very loud");	
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
							
							else { Vibrator v = (Vibrator)con.getSystemService(Context.VIBRATOR_SERVICE);
							v.vibrate(400); }
						}
					
				}
		
			}//else recorder started

		} //while recording
				
		if (recorder.getState()==android.media.AudioRecord.RECORDSTATE_RECORDING) recorder.stop(); //stop the recorder before ending the thread
		
		recorder.release(); //release the recorders resources
		recorder=null; //set the recorder to be garbage collected.

    }//run



}//RecorderThread