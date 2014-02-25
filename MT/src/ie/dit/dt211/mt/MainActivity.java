package ie.dit.dt211.mt;

//testing github
import android.os.Bundle;
import android.app.Activity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnSignalsDetected
{
	private DetectThread dt;
	private CaptureThread ct;
	
	static MainActivity m;
	private Button b;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		m = this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		b = (Button) this.findViewById(R.id.button1);
		b.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if (v == b)
				{
					ct = new CaptureThread();
					ct.start();
					dt = new DetectThread(ct);
					dt.setOnSignalsDetectedListener(MainActivity.m);
					dt.start();
					setContentView(R.layout.activity_main);
					// TODO Auto-generated method stub
				}
				
			}
		});
		
		/*Handler handler = new Handler();
		handler.postDelayed(new Runnable(){

			@Override
			public void run() 
			{
				// TODO Auto-generated method stub
				
			}}, 10000);
			*/
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
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	public void keyBack(int keycode, KeyEvent ke)
	{
		if (keycode == KeyEvent.KEYCODE_BACK && ke.getRepeatCount() == 0)
		{
			if (ct != null)
			{
				ct.stopRecord();
				ct = null;
			}
			if (dt != null)
			{
				dt.stop_thread();
				dt = null;
			}
		}
	}

	@Override
	public void onDetected() 
	{
		runOnUiThread(new Runnable() 
		{
			@Override
			public void run() 
			{
				TextView text = (TextView) MainActivity.m.findViewById(R.id.Draw);
				text.setText(dt.getCurrentNote());
				// TODO Auto-generated method stub
				
			}
		});
		// TODO Auto-generated method stub
		
	}

}
