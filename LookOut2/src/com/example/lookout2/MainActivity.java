package com.example.lookout2;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	Button recB;
	Button stopB;
	RecorderThread rec;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.d("mainLOG", "inside MainActivity onCreate");
		
		
		recB = (Button) findViewById(R.id.recBut);
		stopB = (Button) findViewById(R.id.stopBut);
		
		recB.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				stopB.setEnabled(true);
				recB.setEnabled(false);
				System.out.println("start clicked");
				// Start the  service
				Intent act = new Intent(MainActivity.this, StartService.class);
				startService(act);
				System.out.println("after start click");
			
			}
		});
		
		stopB.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
						
					recB.setEnabled(true);
					stopB.setEnabled(false);
					Intent act = new Intent(MainActivity.this, StartService.class);
					stopService(act);
					System.out.println("service killed");
			}
		});
	}
      

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


}
