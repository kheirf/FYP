package ie.dit.dt211.mt;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class Screen_4 extends Activity implements Runnable
{

	//Database manager
	private DBManager dbmgr;
	String [] extras;
	
	//Data from previous intent
	private String songTitle, timeSig, dateTime, dirPath;
	private int bps;
	
	
	//Surface view variables for drawing
	private float distance = 0.150f;
	private SurfaceView surface;
	private SurfaceHolder holder;
	Thread t;
	int canvasHeight = 0, canvasWidth = 0;
	boolean locker = false;
	
	
	//Note objects
	private NoteObject n;
	private ArrayList<NoteObject> noteObjects;
	private Iterator<NoteObject> noteIterator1, noteIterator2;
	int curXpos = 150, xDist = 150, hold = 0;
	
	//set up the detector thread and capture thread and other related variable
	private DetectThread dt;
	private CaptureThread ct;
	String currentNote;
	int curr;
	
	private ToggleButton b;
	private ImageView led;
	private AnimationDrawable frameAnimation;
	private boolean isPressed = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_screen_4);
		
		dbmgr = new DBManager(this);
		
		Bundle extras = getIntent().getExtras();
		if(extras != null)
		{
			songTitle = extras.getString("songTitle");
			timeSig = extras.getString("timeSig");
			bps = extras.getInt("bps");
		}
		
		
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd:MMM:yyyy HH:mm", Locale.ENGLISH);
		dateTime = sdf.format(c.getTime());
				
		b = (ToggleButton)findViewById(R.id.startButton);
		b.setOnClickListener(buttonListener);
		led = (ImageView)findViewById(R.id.ledView);

		setupLED();
		
		surface = (SurfaceView) findViewById(R.id.surface);
		holder = surface.getHolder();
		n = new NoteObject(this);
		noteObjects = new ArrayList<NoteObject>();
		
		t = new Thread(this);
		
		//led.setBackgroundResource(R.drawable.animation_led_60);
		//frameAnimation = (AnimationDrawable)led.getBackground();
		
	}//End of onCreate


	final OnClickListener buttonListener = new OnClickListener() 
	{
		
		@Override
		public void onClick(View v) 
		{
			if (v.getId() == b.getId())
			{
				isPressed = !isPressed;

				if(isPressed)
				{
					Log.d("Button", "Pressed");
					b.setBackgroundResource(R.drawable.record);
					led.post(new Runnable() 
					{
						@Override
						public void run() 
						{frameAnimation.start();}
					});
					
							
					ct = new CaptureThread();
					ct.start();
					dt = new DetectThread(ct);
					dt.start();
					locker = true;
					t.start();
				}
				else
				{
					b.setBackgroundResource(R.drawable.stop);
					frameAnimation.stop();
					locker = false;
					t = null;
					if(ct != null)
					{
						ct.stopRecord();
						ct = null;
					}
					if(dt != null)
					{
						dt.stopDetect();
						dt = null;
					}
					promptDialog("Recording finished", "Would you like to save this?");		
				}
			}
		}
	};//End of OnClickListener 
	

	private void promptDialog(String title, String message)
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setCancelable(false);
		alert.setTitle(title);
		alert.setMessage(message);
		
		alert.setPositiveButton("Save", new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				if(saveDataToFile())
				{
					if(saveToDB(songTitle, dateTime, String.valueOf(bps), timeSig, dirPath))
						Toast.makeText(Screen_4.this, "Saved Successfully..", Toast.LENGTH_SHORT).show();
				}
				else
					Toast.makeText(Screen_4.this, "Error..Data not saved", Toast.LENGTH_LONG).show();
				
				Intent intent = new Intent(Screen_4.this, Screen_2.class);
				intent.putExtra("compo_details", extras);
				startActivity(intent);
				finish();
			}
		}); 
		
		
		alert.setNegativeButton("No", new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				Intent intent = new Intent(getBaseContext(), Screen_0.class);
				startActivity(intent);
				finish();
			}
		});
		
		alert.show();
	}//End of promptDialog()
	
	private boolean saveToDB(String songTitle, String dateTime, String bps, String timeSig, String dirPath)
	{
		dbmgr.open();
		long id = dbmgr.insert(songTitle, dateTime, bps, timeSig, dirPath);
		if(id > 0)
		{
			Cursor c = dbmgr.getRow(id);
			extras = new String[c.getColumnCount()];
			if(c != null)
			{
				for (int i = 0; i < c.getColumnCount(); i++)
					extras[i] = c.getString(i);
				
				dbmgr.close();
				return true;
			}
		}
		dbmgr.close();
		return false;
	}//end of saveToDB()
	
	
	private boolean saveDataToFile()
	{
		String ser = FileHandler.convertObject(noteObjects);
		Log.d("NoteObjects size", String.valueOf(noteObjects.size()));
		if(ser != null && !ser.equalsIgnoreCase(""))
		{
			dbmgr = new DBManager(this);
			dbmgr.open();
			String highestID = String.valueOf(dbmgr.nextAutoincrement() + 1);
			String dirHolder = FileHandler.write(this, ser, highestID+".dat");// + "/" + highestID + ".dat";
			Log.d("dirHolder", dirHolder);
			dbmgr.close();
			if(!dirHolder.equals(""))
			{
				dirPath = dirHolder;
				return true;
			}
		}
		return false;
	}//End of saveDataToFile
	
	//Running Thread
	@Override
	public void run() 
	{
		Canvas canvas;
		Log.d("Valid surface", String.valueOf(holder.getSurface().isValid()));
		Rect dimension = holder.getSurfaceFrame();
		canvasHeight = dimension.height();
		canvasWidth = dimension.width();
		Bitmap background = drawStaff();

		n.setCanvasSize(canvasWidth, canvasHeight);
		n.init();
		
		while(locker)
		{
			Log.d("canvas animation", "here");
			if(!holder.getSurface().isValid())
				continue;

			canvas = holder.lockCanvas();
			canvas.drawBitmap(background, 0, 0, null);
			update();
			draw(canvas);
			holder.unlockCanvasAndPost(canvas);
		}
	}//End of Run()
	
	
	public void update()
	{
		//long wait = System.currentTimeMillis() + 500;
		if(dt.getCurrentNote() > 0)
		{
			if(dt.getCurrentNote() != hold)
			{
				if((curXpos + xDist) < canvasWidth)
				{
					curXpos += xDist;
				}
				else
				{
					noteIterator1 = noteObjects.iterator();
					while(noteIterator1.hasNext())
					{
						NoteObject cur = noteIterator1.next();
						cur.setXpos((int)cur.getXpos() - xDist);
					}
				}
				noteObjects.add(new NoteObject(dt.getCurrentNote(), curXpos));
			}
		}
		//When in rest - code goes inside else clause
		hold = dt.getCurrentNote();
	}//End of update()
	
	public void draw(Canvas c)
	{
		
		noteIterator2 = noteObjects.iterator();
		while(noteIterator2.hasNext())
		{
			NoteObject curNO = noteIterator2.next();
			curNO.draw(c);
		}	
	}//End of draw();
	
	
	//Method to draw white background with lines
	public Bitmap drawStaff()
	{
		Paint boxColor = new Paint();
		boxColor.setARGB(255, 255, 255, 255);
		boxColor.setStyle(Paint.Style.FILL);
		RectF rect;
		rect = new RectF(0, 0, canvasWidth, canvasHeight);
		
		Paint paint1 = new Paint();
		paint1.setColor(Color.rgb(196, 195, 170));
		paint1.setTextSize(canvasHeight/12);
		
		Paint linePaint = new Paint();
		linePaint.setStrokeWidth(canvasHeight*0.008f);
		linePaint.setARGB(255, 0, 0, 0);
		
		Canvas canvas = new Canvas();
		Bitmap staff = Bitmap.createBitmap(canvasWidth, canvasHeight, Config.ARGB_8888);
		canvas.setBitmap(staff);
		
		canvas.drawRect(rect, boxColor);
		canvas.drawLine(0, canvasHeight*distance, canvasWidth, canvasHeight * distance, linePaint);
		canvas.drawLine(0, canvasHeight*(distance*2) , canvasWidth, canvasHeight*(distance*2), linePaint);
		canvas.drawLine(0, canvasHeight*(distance*3), canvasWidth, canvasHeight*(distance*3), linePaint);
		canvas.drawLine(0, canvasHeight*(distance*4), canvasWidth, canvasHeight*(distance*4), linePaint);
		canvas.drawLine(0, canvasHeight*(distance*5), canvasWidth, canvasHeight*(distance*5) , linePaint);
		
		canvas.drawText("G", 30, canvasHeight * (distance*0.5f) + (canvasHeight/53), paint1);
		canvas.drawText("F", 5, canvasHeight * distance + (canvasHeight/53), paint1);
		canvas.drawText("E", 30, canvasHeight * (distance*1.5f) + (canvasHeight/53), paint1);
		canvas.drawText("D", 5, canvasHeight * (distance*2) + (canvasHeight/53), paint1);
		canvas.drawText("C", 30, canvasHeight * (distance*2.5f) + (canvasHeight/53), paint1);
		canvas.drawText("B", 5, canvasHeight * (distance*3) + (canvasHeight/53), paint1);
		canvas.drawText("A", 30, canvasHeight * (distance*3.5f) + (canvasHeight/53), paint1);
		canvas.drawText("G", 5, canvasHeight * (distance*4) + (canvasHeight/53), paint1);
		canvas.drawText("F", 30, canvasHeight * (distance*4.5f) + (canvasHeight/53), paint1);
		canvas.drawText("E", 5, canvasHeight * (distance*5) + (canvasHeight/53), paint1);
		canvas.drawText("D", 30, canvasHeight * (distance*5.5f) + (canvasHeight/53), paint1);
		canvas.drawText("G", 5, canvasHeight * (distance*6.0f) + (canvasHeight/53), paint1);
		
		return staff;
	}
	
	
	@Override
	protected void onRestart() 
	{
		// TODO Auto-generated method stub
		Intent intent = new Intent(getBaseContext(), Screen_3.class);
		startActivity(intent);
		finish();
		super.onRestart();
	}

	/*
	@Override
	protected void onResume() 
	{
		// TODO Auto-generated method stub
		super.onResume();
		Intent intent = new Intent(getBaseContext(), Screen_0.class);
		startActivity(intent);
		finish();
	}
	*/
	@Override
	protected void onPause() 
	{
		// TODO Auto-generated method stub
		super.onPause();
		if(frameAnimation.isRunning())
			frameAnimation.stop();
		
		if(locker)
			locker = false;
		if(t != null)
			t = null;
		
		if(ct != null)
		{
			ct.stopRecord();
			ct = null;
		}
		if(dt != null)
		{
			dt.stopDetect();
			dt = null;
			Toast.makeText(this, "Interrupted. No data saved", Toast.LENGTH_LONG).show();
		}
		
		//finish();
	}
	
	@Override
	public void onDestroy()
	{
		if(ct != null)
		{
			ct.stopRecord();
			ct = null;
		}
		if(dt != null)
		{
			dt.stopDetect();
			dt = null;
		}
		super.onDestroy();
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.screen_4, menu);
		return true;
	}
	
	
	
	private void setupLED() 
	{
		// TODO Auto-generated method stub
		switch(bps)
		{
			case 60:
				led.setBackgroundResource(R.drawable.animation_led_60);
				break;
			case 80:
				led.setBackgroundResource(R.drawable.animation_led_80);
				break;
			case 120:
				led.setBackgroundResource(R.drawable.animation_led_80);
				break;	
		}
		
		Log.d("BPS", String.valueOf(bps));
		
		frameAnimation = (AnimationDrawable)led.getBackground();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
		{
			b.setBackgroundResource(R.drawable.stop);
			if(frameAnimation.isRunning())
				frameAnimation.stop();
			
			if(locker)
			{
				promptDialog("Recording interrupted", "Save recording?");	
				locker = false;
			}
				
			t = null;
			if(ct != null)
			{
				ct.stopRecord();
				ct = null;
			}
			if(dt != null)
			{
				dt.stopDetect();
				dt = null;
			}
			
		}
		
		if (keyCode == KeyEvent.KEYCODE_HOME && event.getRepeatCount() == 0)
		{
			if(frameAnimation.isRunning())
				frameAnimation.stop();
			if(locker)
				locker = false;
			if(t != null)
				t = null;
			if(ct != null)
			{
				ct.stopRecord();
				ct = null;
			}
			if(dt != null)
			{
				dt.stopDetect();
				dt = null;
			}
			Toast.makeText(this, "Interrupted. No data saved", Toast.LENGTH_LONG).show();
		}
		return super.onKeyDown(keyCode, event);
	}


}
