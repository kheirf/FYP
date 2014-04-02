package ie.dit.dt211.mt;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.view.Menu;
import android.widget.ImageView;

public class MainActivity extends Activity
{
	int duration = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final ImageView img = (ImageView)findViewById(R.id.image1);
		img.setBackgroundResource(R.drawable.animation);

		final AnimationDrawable frameAnimation = (AnimationDrawable) img.getBackground();
		frameAnimation.start();
		 
		for(int i = 0; i < frameAnimation.getNumberOfFrames(); i++)
			 duration += frameAnimation.getDuration(i);
	
		Handler handler = new Handler();
		handler.postDelayed(new Runnable()
		{
			@Override
			public void run() 
			{
				frameAnimation.stop();
				Intent intent = new Intent(MainActivity.this, Screen_0.class);
				startActivity(intent);
				finish();
			}}, duration + 1000);
			
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//android.os.Process.killProcess(android.os.Process.myPid());
	}

}
