package ie.dit.dt211.mt;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class Screen_2 extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_screen_2);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.screen_2, menu);
		return true;
	}

}
