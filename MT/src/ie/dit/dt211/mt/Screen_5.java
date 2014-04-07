package ie.dit.dt211.mt;


import ie.dit.dt211.mt.MySurfaceView.MyThread;
import ie.dit.dt211.mt.model.FileHandler;
import ie.dit.dt211.mt.model.NoteObject;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Screen_5 extends Activity 
{
	String filePath;
	String [] extras;
	Bundle intentExtras;
	
	MySurfaceView msv;
	MyThread thread;
	
	NoteObject n;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_screen_5);
		
		intentExtras = getIntent().getExtras();
		extras = intentExtras.getStringArray("compo_details");
		if(!extras[5].isEmpty())
			filePath = extras[5];
		
		msv = (MySurfaceView)findViewById(R.id.surface3);
		msv.setArrayList(getFromFile(filePath));
		thread = msv.getThread();
		
		TextView title = (TextView) findViewById(R.id.s5Title);
		TextView tempo = (TextView) findViewById(R.id.s5Tempo);
		TextView measure = (TextView) findViewById(R.id.s5measure);
		
		if(!intentExtras.isEmpty())
		{
			
			if(!extras[1].isEmpty())
				title.setText(extras[1]);
			else
				title.setText("N/A");
			
			if(!extras[3].isEmpty())
				tempo.setText(extras[3]+"bpm");
			else
				tempo.setText("N/A");
			
			if(!extras[4].isEmpty())
				measure.setText(extras[4]);
			else
				measure.setText("N/A");
			
		}
		
		
		final Button back = (Button) findViewById(R.id.s5BackButton);
		back.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				msv.onDestroy();
				Intent intent = new Intent(getBaseContext(), Screen_2.class);
				intent.putExtra("compo_details", extras);
				intent.putExtra("activity_caller", "s5");
				startActivity(intent);
				finish();
			}
		});
	}

	

	@SuppressWarnings("unchecked")
	private ArrayList<NoteObject> getFromFile(String fileName)
	{
		String ser = FileHandler.read(this, fileName);
		if(ser!=null && !ser.equalsIgnoreCase(""))
		{
			Object obj = FileHandler.convertString(ser);
			if(obj instanceof ArrayList)
				return (ArrayList<NoteObject>) obj;
		}
		
		return null;
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.screen_5, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		msv = (MySurfaceView)findViewById(R.id.surface3);
	}



	@Override
	protected void onPause() 
	{
		super.onPause();
		msv.onPause();
	}



	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
		{
			msv.onDestroy();
			Intent intent = new Intent(getBaseContext(), Screen_2.class);
			intent.putExtra("compo_details", extras);
			startActivity(intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}



	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		thread.stopThread();
	}

}
