package ie.dit.dt211.mt;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;

public class DrawStaff 
{
	private static int canvasWidth;
	private static int canvasHeight;
	
	public DrawStaff(int canvasWidth, int canvasHeight)
	{
		DrawStaff.canvasHeight = canvasHeight;
		DrawStaff.canvasWidth = canvasWidth;
	}
	
	public static Bitmap drawStaff()
	{
		float distance = 0.150f;
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
}
