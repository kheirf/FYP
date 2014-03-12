package ie.dit.dt211.mt;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

public class Screen_4 extends Activity implements OnSignalsDetected
{

	static Screen_4 s4;
	private DetectThread dt;
	private CaptureThread ct;
	
	private ToggleButton b;
	//private Button c;
	private boolean flag = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		//s4 = this;	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_screen_4);
		b = (ToggleButton)findViewById(R.id.button1);
		b.setBackgroundResource(R.drawable.recbutton);
		//b.setBackgroundResource(R.drawable.recbutton);
		//c = (Button)findViewById(R.id.button2);
		
		
		
		b.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if (b.isChecked())
				{
					//b.setBackgroundResource(R.drawable.record);
					flag = true;
					//flag = true;
					ct = new CaptureThread();
					ct.start();
					dt = new DetectThread(ct);
					dt.setOnSignalsDetectedListener(Screen_4.this);
					dt.start();
					setContentView(R.layout.activity_screen_4);
					
				}
					
				// TODO Auto-generated method stub
				
			}
		});
		
		/*
		c.setOnClickListener(new View.OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				if(v == c)
				if(ct.isAlive())
				{
					ct.stopRecord();
					dt.stop_thread();
				}
				Intent intent = new Intent(Screen_4.this, Screen_1.class);
				startActivity(intent);
			}
		});*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.screen_4, menu);
		return true;
	}


	@Override
	public void onDetected() 
	{
		runOnUiThread(new Runnable() 
		{
			@Override
			public void run() 
			{
				/*if (flag)
				{
					b.setBackgroundResource(R.drawable.record);
				}
				else
					b.setBackgroundResource(R.drawable.stop);*/
				TextView tv = (TextView) Screen_4.this.findViewById(R.id.Draw);
				tv.setText(dt.getCurrentNote());
				
			}
		});
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onDestroy()
	{
		ct.stopRecord();
		dt.stop_thread();
		super.onDestroy();
	}

}
