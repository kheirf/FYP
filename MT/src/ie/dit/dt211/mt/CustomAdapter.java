package ie.dit.dt211.mt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.musicg.wave.Wave;
import com.musicg.wave.WaveHeader;

import ie.dit.dt211.mt.R;
import ie.dit.dt211.mt.model.DBManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
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
	private static volatile Thread t;
	private static Button playButton;
	
	private static boolean clicked = false;
	
	private static AudioTrack at = null;
	
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
		
		playButton = (Button) view.findViewById(R.id.play_image);
		
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
		
		playButton.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(v.getId() == playButton.getId())
				{

					clicked = !clicked;
					if(clicked)
					{
						//playButton.setPressed(false);
						//playButton.setBackgroundResource(R.drawable.pause);
						t = new Thread(new Runnable() 
						{
							@Override
							public void run() 
							{
								playWav(row_id);
							}	
						});
						t.start();
					
					}
					else
					{
						//playButton.setBackgroundResource(R.drawable.play);
						at.stop();
						at.release();
						at = null;
					}
				}
			}
		});
	}
	
	private void playWav(int row_id) 
	{
		Log.d("row id", ""+row_id);
		
		dbmgr = new DBManager(ctx);
		dbmgr.open();
		Cursor c = dbmgr.getRow(row_id);
		//cursor.moveToPosition(row_id);
		String wavPath = c.getString(6);
		c.close();
		dbmgr.close();
		
		Wave wave = new Wave(wavPath);
		WaveHeader wh;
		wh = wave.getWaveHeader();
		
		
		byte [] data = null;
		File file = new File(wavPath);
		data = new byte[(int)file.length()];
		FileInputStream in = null;
		try 
		{
			in = new FileInputStream(file);
			in.read(data);
			in.close();
		} 
		catch (IOException e) 
		{e.printStackTrace();}
		
		int intSize = android.media.AudioTrack.getMinBufferSize(wh.getSampleRate(), AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
		at = new AudioTrack(AudioManager.STREAM_MUSIC, wh.getSampleRate(), AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
				intSize, AudioTrack.MODE_STREAM);
		
		
		if(at != null)
		{
			at.setNotificationMarkerPosition((int) ((data.length / 2)));
			at.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {
				
				@Override
				public void onPeriodicNotification(AudioTrack track) 
				{}
				
				@Override
				public void onMarkerReached(AudioTrack track) 
				{
					playButton.setBackgroundResource(R.drawable.play);
					Log.d("End", "here");
					//t = null;
					//at = null;
					//playButton.setPressed(false);
				}
			});
			at.play();
			at.write(data, 0, data.length);
		}
		
		
		
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
		
		Intent intent = new Intent(ctx.getApplicationContext(), Screen_1.class);
		ctx.startActivity(intent);
		((Activity) ctx).finish();
		
		if(!cursor.isClosed())
			cursor.close();
	}
	
	
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) 
	{
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.rowlayout, parent, false);
		bindView(v, context, cursor);
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
	
}
