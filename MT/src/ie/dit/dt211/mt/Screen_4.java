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
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.widget.ToggleButton;

public class Screen_4 extends Activity implements Runnable
{
	//Database manager
	DBManager dbmgr;
	
	//Data from previous intent
	String songTitle, timeSig, dateTime, dirPath;
	int bps;
	
	
	//Set up the surfaceview
	Bitmap noteBitmap, scaledNoteBitmap, restBitmap, scaledRestBitmap;
	float distance = 0.150f;
	private SurfaceView surface;
	private SurfaceHolder holder;
	Thread t;
	int canvasHeight = 0, canvasWidth = 0;
	boolean locker = true;
	
	
	//Note objects
	NoteObject n;
	ArrayList<NoteObject> noteObjects;
	Iterator<NoteObject> noteIterator1, noteIterator2;
	int curXpos = 150;
	int xDist = 150;
	int hold = 0;
	
	//set up the detector thread and capture thread and other related variable
	private DetectThread dt;
	private CaptureThread ct;
	String currentNote;
	int curr;
	
	private ToggleButton b;
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
				
		b = (ToggleButton)findViewById(R.id.button1);
		surface = (SurfaceView) findViewById(R.id.surface);
		
		holder = surface.getHolder();
		
		noteBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.note);
		restBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.rest);
		
		
		n = new NoteObject(this);
		Log.d("BP", "Here");
		
		noteObjects = new ArrayList<NoteObject>();
		
		
		t = new Thread(this);
		b.setOnClickListener(buttonListener);
	}

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
					ct = new CaptureThread();
					ct.start();
					dt = new DetectThread(ct);
					dt.start();
					t.start();
					
				}
				else
				{
					b.setBackgroundResource(R.drawable.stop);
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
						promptDialog();		
				}
			}
		}
	};
	

	private void promptDialog()
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Recording stopped");
		alert.setMessage("Would you like to save this?");
		
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
				
				Intent intent = new Intent(Screen_4.this, Screen_1.class);
				startActivity(intent);
			}
		}); 
		
		
		alert.setNegativeButton("No", new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				//Go back to screen 3 with extras.
			}
		});
		alert.show();
	}
	
	private boolean saveToDB(String songTitle, String dateTime, String bps, String timeSig, String dirPath)
	{
		dbmgr.open();
		if(dbmgr.insert(songTitle, dateTime, bps, timeSig, dirPath) > 0)
		{
			dbmgr.close();
			return true;
		}
		return false;
	}
	
	
	private boolean saveDataToFile()
	{
		String ser = FileHandler.convertObject(noteObjects);
		if(ser != null && !ser.equalsIgnoreCase(""))
		{
			String dirHolder = FileHandler.write(this, ser, "saved.dat");
			if(!dirHolder.equals(""))
			{
				dirPath = dirHolder;
				return true;
			}
		}
		return false;
	}
	
	//Running Thread
	@Override
	public void run() 
	{
		Canvas canvas;
		Rect dimension = holder.getSurfaceFrame();
		canvasHeight = dimension.height();
		canvasWidth = dimension.width();
		Bitmap background = drawStaff();
		scaledNoteBitmap = Bitmap.createScaledBitmap(noteBitmap, canvasWidth/15, canvasHeight/9, true);
		scaledRestBitmap = Bitmap.createScaledBitmap(restBitmap, canvasWidth/15, (canvasHeight/9)/2, true);
		
		n.setCanvasSize(canvasWidth, canvasHeight);
		n.init();
		
		while(locker)
		{
			if(!holder.getSurface().isValid())
				continue;
			
			canvas = holder.lockCanvas();
			canvas.drawBitmap(background, 0, 0, null);
			update();
			draw(canvas);
			holder.unlockCanvasAndPost(canvas);
		}
	}
	
	
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
				//while(System.currentTimeMillis()!=wait){
				//Log.d("Breakpoint", "here");
			//};
		}
		//When in rest - code goes inside else clause
		hold = dt.getCurrentNote();
	}
	
	public void draw(Canvas c)
	{
		
		noteIterator2 = noteObjects.iterator();
		while(noteIterator2.hasNext())
		{
			NoteObject curNO = noteIterator2.next();
			curNO.draw(c);
		}	
		
	}
	
	
	//Method to draw white background with lines
	public Bitmap drawStaff()
	{
		Paint boxColor = new Paint();
		boxColor.setARGB(255, 255, 255, 255);
		boxColor.setStyle(Paint.Style.FILL);
		RectF rect;
		rect = new RectF(0, 0, canvasWidth, canvasHeight);
		
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
		return staff;
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
	
	

}
