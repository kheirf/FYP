package ie.dit.dt211.mt;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Screen_2 extends Activity 
{
	String filePath, _id, compoTitle;
	String [] extras;
	Bundle intentExtras = null;
	Button review, delete, back;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_screen_2);
		
		TextView comp_title = (TextView) findViewById(R.id.compo_title);
		TextView date_created = (TextView) findViewById(R.id.compo_dateCreated);
		TextView comp_tempo = (TextView) findViewById(R.id.compo_tempo);
		TextView time_sig = (TextView) findViewById(R.id.compo_time_signature);
	
		review = (Button) findViewById(R.id.viewScoreButton);
		delete = (Button) findViewById(R.id.deleteScoreButton);
		back = (Button) findViewById(R.id.s2backButton);
		
		back.setOnClickListener(new clickListener());
		review.setOnClickListener(new clickListener());
		delete.setOnClickListener(new clickListener());
		
		
		intentExtras = getIntent().getExtras();
		//String [] extras;
		if(!intentExtras.isEmpty())
		{
			extras = intentExtras.getStringArray("compo_details");
			if(!extras[0].isEmpty())
				_id = extras[0];
			
			if(!extras[1].isEmpty())
			{
				comp_title.setText(extras[1]);
				compoTitle = extras[1];
			}
			else
			{
				comp_title.setText("N/A");
				compoTitle = "N/A";
			}
			
			if(!extras[2].isEmpty())
				date_created.setText(extras[2]);
			
			else
				date_created.setText("N/A");
			
			if(!extras[3].isEmpty())
				comp_tempo.setText(extras[3]);
			else
				comp_tempo.setText("N/A");
			
			if(!extras[5].isEmpty())
				time_sig.setText(extras[4]);
			else
				time_sig.setText("N/A");
			
			if(!extras[5].isEmpty())
				filePath = extras[5];
			else
				filePath = "N/A";
		}
		
	}
	
	private class clickListener implements View.OnClickListener
	{

		@Override
		public void onClick(View v) 
		{
			if (v == back)
			{
				Intent intent = new Intent(getBaseContext(), Screen_1.class);
				startActivity(intent);
			}
			
			if (v == review)
			{
				Intent intent = new Intent(getBaseContext(), Screen_5.class);
				intent.putExtra("compo_details", extras);
				startActivity(intent);
			}
			
			if (v == delete)
			{
				prompt();
			}
			
		}
		
	}
	
	private void prompt()
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(compoTitle);
		alert.setMessage("Are you sure you want to delete this?");
		
		alert.setPositiveButton("No", new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) {}
		});
		
		alert.setNegativeButton("Yes", new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				delete();
			}
		});
		alert.show();
	}
	
	private void delete()
	{
		DBManager dbmgr = new DBManager(this);
		dbmgr.open();
		if(dbmgr.deleteRow(Long.parseLong(_id)) > 0)
			Toast.makeText(this, compoTitle + " is deleted", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(this, "Sum Ting Wong", Toast.LENGTH_SHORT).show();
		dbmgr.close();
		
		Intent intent = new Intent(getBaseContext(), Screen_1.class);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.screen_2, menu);
		return true;
	}

}
