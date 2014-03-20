package ie.dit.dt211.mt;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
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
	private boolean isPressed = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		//s4 = this;	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_screen_4);
		b = (ToggleButton)findViewById(R.id.button1);
		

		OnClickListener cl = new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				if (v == b)
				{
					
					isPressed = !isPressed;
					//isPressed = true;
					
					if(isPressed)
					{
						b.setBackgroundResource(R.drawable.record);			
						ct = new CaptureThread();
						ct.start();
						dt = new DetectThread(ct);
						dt.setOnSignalsDetectedListener(Screen_4.this);
						dt.start();
						
					}
					
					if(!isPressed)
					{
						b.setBackgroundResource(R.drawable.stop);
						if(ct.progress())
						{
							ct.stopRecord();
							dt.setOnSignalsDetectedListener(null);
							try {
								dt.stop_thread();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					
							ct = null;
							dt = null;
						}
					}
					Log.d("IsPressed", String.valueOf(isPressed));
					//isPressed = !isPressed;
					//isPressed = !isPressed;
						//setContentView(R.layout.activity_screen_4);
				}
					
				
				
			}
		};
		
		b.setOnClickListener(cl);
		
		/*
		b.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if (isPressed)
				{
					
					//b.setBackgroundResource(R.drawable.record);
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
		});*/
		
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

	
	public void ClickEvent ()
	{
		
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
				TextView tv = (TextView) Screen_4.this.findViewById(R.id.Draw);
				
				if(isPressed)
				{
					tv.setText(dt.getCurrentNote());
				}
				else
					tv.setText("Click button");
			}
		});
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onDestroy()
	{
		ct.stopRecord();
		try {
			dt.stop_thread();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onDestroy();
	}

}
