package ie.dit.dt211.mt.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

public class NoteObject implements Serializable
{
	/**
	 *  Generated Serial ID
	 */
	private static final long serialVersionUID = -3504520557931067017L;
	
	private int type;
	private static int canvasHeight, canvasWidth;
	private static Bitmap noteBitmap, scaledNoteBitmap, sharpBitmap, scaledSharpBitmap;
	private static Context ctx;
	private float distance = 0.150f, x, y;
	private static final Hashtable<Integer, Float> multiplier = new Hashtable<Integer, Float>();
	
	
	// Default Constructor;
	public NoteObject(Context ctx) 
	{
		NoteObject.ctx = ctx;
	}
	
	// Creating new NoteObject. The default constructor must be initiated first
	public NoteObject(int type, int x) 
	{
		this(ctx);
		this.x = x;
		this.type = type;
		this.y = calcPos(type);
	}
	
	// Checks if the position of the note is within the dimension of the screen
	public boolean displayable()
	{
		if(x < -50 || x > canvasWidth + 50)
			return false;
		
		return true;
	}
	
	//Initialize some variables for calculating the position
	public void init()
	{
		initializeBitmap();
		multiplier.put(1, 6.0f);
		multiplier.put(2, 6.0f);
		multiplier.put(3, 5.5f);
		multiplier.put(4, 5.5f);
		multiplier.put(5, 5.0f);
		multiplier.put(6, 4.5f);
		multiplier.put(7, 4.5f);
		multiplier.put(8, 4.0f);
		multiplier.put(9, 4.0f);
		multiplier.put(10, 3.5f);
		multiplier.put(11, 3.5f);
		multiplier.put(12, 3.0f);
		multiplier.put(13, 2.5f);
		multiplier.put(14, 2.5f);
		multiplier.put(15, 2.0f);
		multiplier.put(16, 2.0f);
		multiplier.put(17, 1.5f);
		multiplier.put(18, 1.0f);
		multiplier.put(19, 1.0f);
		multiplier.put(20, 0.5f);
		
	}
	
	public float getXpos()
	{
		return this.x;
	}
	
	public float getYpos()
	{
		return this.y;
	}
	
	public float calcPos(int note)
	{
		float ret = 0f;
		
		for(@SuppressWarnings("rawtypes") Map.Entry entry: multiplier.entrySet())
		{
			if(entry.getKey().equals(note))
			{
				ret = (Float) entry.getValue();
				break;
			}
		}
		return (canvasHeight * (distance*ret)) - (canvasHeight/10)/2;
	}
	
	public void setXpos(int x)
	{
		this.x = x;
	}
	public void setYpos(int y)
	{
		this.y = y;
	}
	
	public int getType()
	{
		return this.type;
	}
	
	public boolean isSharp(int note)
	{
		if(note == 2)
			return true;
		if(note == 4)
			return true;
		if(note == 7)
			return true;
		if(note == 9)
			return true;
		if(note == 11)
			return true;
		if(note == 14)
			return true;
		if(note == 16)
			return true;
		if(note == 19)
			return true;
		return false;
	}
	
	private void initializeBitmap()
	{
		InputStream bit = null;
		noteBitmap = null;
		sharpBitmap = null;
		
		try
		{
			
			bit = ctx.getAssets().open("bitmaps/note.png");
			noteBitmap = BitmapFactory.decodeStream(bit);
			bit = ctx.getAssets().open("bitmaps/sharp.png");
			sharpBitmap = BitmapFactory.decodeStream(bit);
			bit.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			if(noteBitmap != null)
			{
				noteBitmap.recycle();
				noteBitmap = null;
			}
			if(sharpBitmap != null)
			{
				sharpBitmap.recycle();
				sharpBitmap = null;
			}
		}
		
	}
	
	public void draw(Canvas c)
	{
		if(noteBitmap == null)
			initializeBitmap();
		
		scaledNoteBitmap = Bitmap.createScaledBitmap(noteBitmap, canvasWidth/15, canvasHeight/9, true);
		scaledSharpBitmap = Bitmap.createScaledBitmap(sharpBitmap, canvasWidth/20, canvasHeight/9, true);
		
		
		if(type == 1 || type == 2)
		{
			Paint p = new Paint();
			p.setARGB(255, 0, 0, 0);
			p.setStrokeWidth(canvasHeight*0.008f);
			c.drawLine(x - 20, canvasHeight*(0.150f * 6), x + scaledNoteBitmap.getWidth() + 20, canvasHeight*(0.150f * 6), p);
		}
		
		if(isSharp(type))
			c.drawBitmap(scaledSharpBitmap, x - (scaledSharpBitmap.getWidth()/1.5f), y - 15, null);
		
		c.drawBitmap(scaledNoteBitmap, x, y, null);
	}
	
	public void setCanvasSize(int w, int h)
	{
		canvasWidth = w;
		canvasHeight = h;
	}
}
