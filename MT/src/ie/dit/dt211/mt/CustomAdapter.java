package ie.dit.dt211.mt;

import ie.dit.dt211.mt.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class CustomAdapter extends CursorAdapter // implements OnItemClickListener
{
	LayoutInflater inflater;
	private Cursor cursor;
	private Context ctx;
	int row_id;
	DBManager dbmgr;
	
	public CustomAdapter(Context context, Cursor c, int flags) 
	{
		super(context, c, flags);
		this.inflater = LayoutInflater.from(context);
		this.cursor = c;
		this.ctx = context;
	}

	@Override
	public void bindView(View view, Context context,Cursor cursor) 
	{
		final int row_id = Integer.parseInt(cursor.getString(0));
		Button delButton = (Button) view.findViewById(R.id.del_image);
		TextView compoTitle = (TextView) view.findViewById(R.id.compo_title);
		TextView compoNumber = (TextView) view.findViewById(R.id.list_number);
		compoNumber.setText(String.valueOf(row_id));
		compoTitle.setText(cursor.getString(1));
		delButton.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				promptDialog(row_id);
			}
		});
		
	}
	
	@Override
	public Cursor getCursor() {
		// TODO Auto-generated method stub
		return super.getCursor();
	}
	
	
	@Override
	public void changeCursor(Cursor cursor) 
	{
		// TODO Auto-generated method stub
		super.changeCursor(cursor);
		this.cursor = cursor;
	}

	private void promptDialog(final int row_id)
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
		alert.setTitle("Delete Composition");
		alert.setMessage("Are you sure you want to delete this?");
		alert.setPositiveButton("No", new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{}
		}); 
		
		alert.setNegativeButton("Yes", new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				doTrue(row_id);
			}
		});
		alert.show();
	}
	
	private void doTrue(int row_id)
	{
		dbmgr = new DBManager(ctx);
		dbmgr.open();
		
		Log.d("ImageButton", String.valueOf(row_id));
		
		if(dbmgr.deleteRow(row_id)> 0)
			Toast.makeText(ctx, "Deleted..", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(ctx, "Error. Contact Dev", Toast.LENGTH_SHORT).show();
		
		Cursor cursor = dbmgr.getAllRowsDesc();
		changeCursor(cursor);
		dbmgr.close();
	}
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) 
	{
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.rowlayout, parent, false);
		bindView(v, context, cursor);
		// TODO Auto-generated method stub
		return v;
	}
	
	@Override
	public Object getItem(int position)
	{
		int row_id = Integer.parseInt(cursor.getString(0));
		return row_id;
	}
	
	@Override
	public long getItemId(int position)
	{
		cursor.moveToPosition(position);
		int row_id = Integer.parseInt(cursor.getString(0));
		return row_id;
	}

	/*
	@Override
	public void onItemClick(AdapterView<?> view, View arg1, int position, long arg3) 
	{
		
		Log.d("Item clicked", String.valueOf(getItemId(position)));
	}
	*/
	
	
}
