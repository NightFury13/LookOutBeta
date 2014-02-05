package com.example.lookout2;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class StartService extends Service {
	
	RecorderThread rec;
	
	public StartService() {
		System.out.println("aya kya?");
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
    public void onCreate() {
		rec = new RecorderThread(StartService.this);
		rec.recording = true;
		System.out.println("start service called");
    }

    @Override
    public void onStart(Intent intent, int startId) {
    	
    	rec.start();
    	System.out.println("service started");
    }

    @Override
    public void onDestroy() {
    	rec.recording = false;
		try {
			rec.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}