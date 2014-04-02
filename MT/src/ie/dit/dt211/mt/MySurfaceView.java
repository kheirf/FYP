package ie.dit.dt211.mt;



import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.support.v4.view.VelocityTrackerCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.VelocityTracker;
import android.view.View;

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback
{
	String TAG="MySurfaceView";
	MyThread thread;
	float x, y;
	//boolean okay = true;
	int canvasWidth, canvasHeight;
	float distance = 0.150f;
	float xDist = 200;
	Paint linePaint;
	
	int scrollSpeed;
	private VelocityTracker velocity = null;
	float xVel;
	
	Bitmap background;
	boolean flag = false;
	
	NoteObject n;
	ArrayList<NoteObject> objects = new ArrayList<NoteObject>();
	ArrayList<NoteObject> objectsTemp = new ArrayList<NoteObject>();
	Iterator<NoteObject> iterator;
	
	public MySurfaceView(Context context) 
	{
		super(context);
		init();
	}
	
	public MySurfaceView(Context context, AttributeSet attr) 
	{
		super(context, attr);
		init();
		
	}
	
	
	public MySurfaceView(Context context, AttributeSet attr, int defStyle) 
	{
		super(context, attr, defStyle);
		init();
	}
	
	public void setArrayList(ArrayList<NoteObject> a)
	{
		objects = a;
		float pos = 0;
		Iterator<NoteObject> iterator = objects.iterator();
		while(iterator.hasNext())
		{
			pos += xDist;
			NoteObject curr = iterator.next();
			curr.setXpos((int)pos);
		}
		
		if(objects.size() <= 20)
			scrollSpeed = 10;
		if(objects.size() >= 20 && objects.size() <= 50)
			scrollSpeed = 20;
		if(objects.size() >= 50)
			scrollSpeed = 30;
		
	}
	
	private void init() 
	{
		thread = new MyThread(getHolder(), this);
		getHolder().addCallback(this);
		setFocusable(true);
		
	}
	
	public MyThread getThread()
	{
		return thread;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) 
	{
		Rect dimension = holder.getSurfaceFrame();
		canvasWidth = dimension.width();
		canvasHeight = dimension.height();
		linePaint = new Paint();
		linePaint.setStrokeWidth(canvasHeight*0.004f);
		linePaint.setARGB(255, 0, 0, 0);
		background = drawStaff();
		thread.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) 
	{
		boolean retry = true;
		thread.setOkay(false);
		while(retry)
		{
			try
			{
				thread.join();
				retry = false;
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void draw(Canvas canvas) 
	{
		Log.d("BP", "here");
		canvas.drawBitmap(background, 0, 0, null);
		
		iterator = objects.iterator();	
		int counter = 0;
		while(iterator.hasNext())
		{
			NoteObject curr = iterator.next();

			if(curr.displayable())
				curr.draw(canvas);
			if((counter % 4) == 0)
				canvas.drawLine(curr.getXpos() - 50, canvasHeight*distance, curr.getXpos() - 50, canvasHeight*(distance * 5), linePaint);
			
			counter++;
		}
	}
	
	
	public void update()
	{
		iterator = objects.iterator();
		if(Math.abs(xVel) > 3)
		{
			while(iterator.hasNext())
			{
				NoteObject curr = iterator.next();
				curr.setXpos((int)(curr.getXpos() + xVel));
			}
		}
	}
	
	
	public Bitmap drawStaff()
	{
		Paint boxColor = new Paint();
		Paint paint1 = new Paint();
		paint1.setTextSize(canvasHeight/12);
		paint1.setStrokeWidth(5);
		paint1.setColor(Color.rgb(195, 195, 170));
		
		boxColor.setARGB(255, 255, 255, 255);
		boxColor.setStyle(Paint.Style.FILL);
		RectF rect;
		rect = new RectF(0, 0, canvasWidth, canvasHeight);
		
		
		Canvas canvas = new Canvas();
		Bitmap staff = Bitmap.createBitmap(canvasWidth, canvasHeight, Config.ARGB_8888);
		canvas.setBitmap(staff);
		
		canvas.drawRect(rect, boxColor);
		
		canvas.drawText("G", 30, canvasHeight * (distance*0.5f) + (canvasHeight/53), paint1);
		canvas.drawLine(0, canvasHeight*distance, canvasWidth, canvasHeight * distance, linePaint);
		canvas.drawText("F", 5, canvasHeight * distance + (canvasHeight/53), paint1);
		canvas.drawText("E", 30, canvasHeight * (distance*1.5f) + (canvasHeight/53), paint1);
		canvas.drawLine(0, canvasHeight*(distance*2) , canvasWidth, canvasHeight*(distance*2), linePaint);
		canvas.drawText("D", 5, canvasHeight * (distance*2) + (canvasHeight/53), paint1);
		canvas.drawText("C", 30, canvasHeight * (distance*2.5f) + (canvasHeight/53), paint1);
		canvas.drawLine(0, canvasHeight*(distance*3), canvasWidth, canvasHeight*(distance*3), linePaint);
		canvas.drawText("B", 5, canvasHeight * (distance*3) + (canvasHeight/53), paint1);
		canvas.drawText("A", 30, canvasHeight * (distance*3.5f) + (canvasHeight/53), paint1);
		canvas.drawLine(0, canvasHeight*(distance*4), canvasWidth, canvasHeight*(distance*4), linePaint);
		canvas.drawText("G", 5, canvasHeight * (distance*4) + (canvasHeight/53), paint1);
		canvas.drawText("F", 30, canvasHeight * (distance*4.5f) + (canvasHeight/53), paint1);
		canvas.drawLine(0, canvasHeight*(distance*5), canvasWidth, canvasHeight*(distance*5) , linePaint);
		canvas.drawText("E", 5, canvasHeight * (distance*5) + (canvasHeight/53), paint1);
		canvas.drawText("D", 30, canvasHeight * (distance*5.5f) + (canvasHeight/53), paint1);
		canvas.drawText("G", 5, canvasHeight * (distance*6.0f) + (canvasHeight/53), paint1);
		return staff;
	}
	
	public void onResume()
	{
		thread = new MyThread(this.getHolder(), this);
		thread.setOkay(true);
		thread.start();
	}
	
	public void onPause()
	{
		thread.setOkay(false);
		boolean b = true;
		while(b)
		{
			try 
			{
				thread.join();
				b = false;
			} 
			catch (InterruptedException e) 
			{e.printStackTrace();}
		}
	}
	
	public void onDestroy()
	{
		thread.setOkay(false);
		thread.stopThread();
	}
	
	
	
	
	
	
	/****************************************************************************************************
	 * 
	 * @author Kheir
	 *	Thread class
	 ****************************************************************************************************/
	public class MyThread extends Thread implements OnTouchListener
	{

		SurfaceHolder holder;
		MySurfaceView surface;
		boolean okay = true;
		
		public MyThread(SurfaceHolder sh, MySurfaceView msv) 
		{
			holder = sh;
			surface = msv;
			surface.setOnTouchListener(this);
		}
		
		@Override
		public boolean onTouch(View v, MotionEvent e) 
		{
			int index = e.getActionIndex();
	        int action = e.getActionMasked();
	        int pointerId = e.getPointerId(index);

	        switch(action) 
	        {
	            case MotionEvent.ACTION_DOWN:
	                if(velocity != null) 
	                	 velocity.clear();
	                    
	                velocity = VelocityTracker.obtain();
	                velocity.addMovement(e);
	                break;
	            case MotionEvent.ACTION_MOVE:
	                velocity.addMovement(e);
	                velocity.computeCurrentVelocity(scrollSpeed);
	                xVel =  VelocityTrackerCompat.getXVelocity(velocity, 
	                        pointerId);
	                break;
	            case MotionEvent.ACTION_UP:
	            	xVel = 0;
	            	break;
	            case MotionEvent.ACTION_CANCEL:
	            	if(velocity != null)
	            		velocity.recycle();
	                break;
	        }
			return true;
			
		}
		
		
		public void setOkay(boolean b)
		{
			okay = b;
		}
		
		@Override
		public void run() 
		{
			Canvas canvas = null;
			n = new NoteObject(getContext());
			n.setCanvasSize(canvasWidth, canvasHeight);
			
			while(this.okay)
			{
				try 
				{
					canvas = holder.lockCanvas();
					synchronized (holder) 
					{surface.draw(canvas);
					surface.update();}
					Thread.sleep(1);
				} 
				catch (InterruptedException e) 
				{e.printStackTrace();}
				finally
				{
					if(canvas != null)
						holder.unlockCanvasAndPost(canvas);
				}
			}
			
		}//end of run

		
		
		
		public void stopThread()
		{
			this.setOkay(false);

				try 
				{
					this.join();
				} 
				catch (InterruptedException e) 
				{e.printStackTrace();}
			
		}
		
		
		
	}//end of MyThread
	
}//end of MySurfaceView
