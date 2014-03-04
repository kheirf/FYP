package ie.dit.dt211.mt;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterViewFlipper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;

public class Screen_3 extends Activity 
{
	private String timeSig;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_screen_3);
		
		Button start = (Button)findViewById(R.id.start);
		Button cancel = (Button)findViewById(R.id.cancel1);
		
		EditText title = (EditText)findViewById(R.id.title);
		Spinner meter = (Spinner)findViewById(R.id.spinner1);
		
		
		meter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() 
		{

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) 
			{
				int item_id = (int)parent.getItemIdAtPosition(pos);
				switch(item_id)
				{
				case 0:
					timeSig = "4/4";
					break;
				case 1:
					timeSig = "2/4";
					break;
					default:
						timeSig="4/4";
						break;		
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
			
		});
		
		NumberPicker np = (NumberPicker)findViewById(R.id.numberPicker1);
		String [] nums = {"60", "80", "120"};
		np.setMaxValue(20);
		np.setMinValue(0);
		np.setDisplayedValues(nums);
		np.setValue(0);
		
		cancel.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(Screen_3.this, Screen_1.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.screen_3, menu);
		return true;
	}

}
