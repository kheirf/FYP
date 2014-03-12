package ie.dit.dt211.mt;

//testing github
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class MainActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Handler handler = new Handler();
		handler.postDelayed(new Runnable()
		{
			@Override
			public void run() 
			{
				finish();
				Intent intent = new Intent(MainActivity.this, Screen_1.class);
				startActivity(intent);
			}}, 3000);
			
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
