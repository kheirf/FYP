package ie.dit.dt211.mt;

import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class Screen_1 extends ListActivity
{
	DBManager dbMgr = new DBManager(this);
	Cursor cursor;
	ListView listV;
	private Button addNew;
	private Button back;
	CustomAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_screen_1);
		listV = (ListView) findViewById(android.R.id.list);
		addNew = (Button)findViewById(R.id.addButton);
		back = (Button)findViewById(R.id.s1quitButton);
		addNew.setText("Compose");
		back.setText("Quit");
		
		dbMgr.open();
		//testAddItem();
		cursor = dbMgr.getAllRowsDesc();
		cursor.moveToFirst();
		
		
		adapter = new CustomAdapter(this, cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		listV.setAdapter((ListAdapter) adapter);
		//listV.setOnItemClickListener(adapter);
		
		addNew.setOnClickListener(new View.OnClickListener() 
		{	
			@Override
			public void onClick(View v) 
			{
				cursor.close();
				dbMgr.close();
				Intent intent = new Intent(Screen_1.this, Screen_3.class);
				startActivity(intent);
			}
		});
		
		back.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
					cursor.close();
					dbMgr.close();
					//finish();
			}
		});
		
		
	}
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) 
	{
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Cursor cursor = adapter.getCursor();
		Log.d("Item clicked", String.valueOf(cursor.getString(0)));
		String [] extra = new String [cursor.getColumnCount()];
		for(int i = 0; i < cursor.getColumnCount(); i++)
		{
			extra[i] = cursor.getString(i);
		}
		Intent intent = new Intent(getBaseContext(), Screen_2.class);
		intent.putExtra("compo_details", extra);
		startActivity(intent);
	}


	public void testAddItem()
	{
		for(int i = 0; i < 20; i++)
		{
			dbMgr.insert("Test 1", "015", "80", "4/4", "data/data/ie/");
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.screen_1, menu);
		return true;
	}
	

	public void onDestroy()
	{
		super.onDestroy();
	}
	
	public void onStop()
	{
		dbMgr.close();
		cursor.close();
		super.onStop();
	}

}
