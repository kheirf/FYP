package ie.dit.dt211.mt;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class Screen_1 extends Activity 
{

	DBManager dbMgr = new DBManager(this);
	Cursor cursor;
	ListView list;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_screen_1);
		
		list = (ListView)findViewById(R.id.list);
		Button addNew = (Button)findViewById(R.id.addButton);
		Button back = (Button)findViewById(R.id.s1quitButton);
		addNew.setText("New Composition");
		back.setText("Back");
		
		dbMgr.open();
		cursor = dbMgr.getAllRows();
		
		list.setAdapter(new CustomAdapter(cursor, this));
		
		addNew.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Screen_1.this, Screen_3.class);
				startActivity(intent);
			}
		});
		
		back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				dbMgr.close();
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.screen_1, menu);
		return true;
	}
	
	
	private class CustomAdapter extends BaseAdapter
	{
		
		Cursor data;
		Context context;
		
		CustomAdapter(Cursor c, Context ctx)
		{
			data = c;
			context = ctx;
		}
		
		@Override
		public int getCount() 
		{
			return data.getCount();
		}

		@Override
		public Object getItem(int pos) 
		{
			// TODO Auto-generated method stub
			data.moveToPosition(pos);
			String [] details = {};
			for(int i = 0; i < data.getColumnCount(); i++)
			{
				details[i] = data.getString(i);
			}
			return details;
		}

		@Override
		public long getItemId(int pos) 
		{
			data.moveToPosition(pos);
			return Long.valueOf(data.getString(0));
		}

		@Override
		public View getView(int pos, View convertView, ViewGroup parentView) 
		{
			View v = convertView;
			if (v == null)
			{
				LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = li.inflate(R.layout.list_item, null);
			}
			
			ImageButton b = (ImageButton) v.findViewById(R.id.del_image);
			TextView tv = (TextView) v.findViewById(R.id.compo_title);
			
			String [] s = (String[])this.getItem(pos);
			tv.setText(s[0]);
			b.setImageResource(R.drawable.delete);
			
			return v;
		}
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


/**
 * <RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"

    tools:context=".Screen_1" >

</RelativeLayout>
 */