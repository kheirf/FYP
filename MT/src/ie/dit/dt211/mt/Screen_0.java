package ie.dit.dt211.mt;


import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Screen_0 extends Activity implements OnClickListener
{
	

	private Button create, list, quit, about, ok;
	private Dialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_screen_0);
		
		create = (Button)findViewById(R.id.button2);
		list = (Button)findViewById(R.id.button3);
		quit = (Button)findViewById(R.id.button4);
		about = (Button)findViewById(R.id.button1);
		
		create.setOnClickListener(this);
		list.setOnClickListener(this);
		quit.setOnClickListener(this);
		about.setOnClickListener(this);
		
	}
	
	@Override
	public void onClick(View v) 
	{
		if(v.getId() == create.getId())
		{
			Intent intent = new Intent(this, Screen_3.class);
			startActivity(intent);
			//finish();
		}
		
		if(v.getId() == list.getId())
		{
			Intent intent = new Intent(getBaseContext(), Screen_1.class);
			startActivity(intent);
			//finish();
		}
		
		if(v.getId() == quit.getId())
		{
			promptClose();
		}
		
		if(v.getId() == about.getId())
		{
			ShowAbout ab = new ShowAbout();
			try 
			{ab.showAbout(this);} 
			catch (IOException e) 
			{e.printStackTrace();}
		}
	}
	
	
	
	private void promptClose() 
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Quit");
		alert.setMessage("Close application?");
		
		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() 
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				closeApplication();
			}
		});
		
		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{}
		});
		
		alert.show();
	}

	
	public void closeApplication()
	{
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		android.os.Process.killProcess(android.os.Process.myPid());
		finish();
	}
	
	public void showAbout() throws IOException
	{
		dialog = new Dialog(this);
		dialog.setContentView(R.layout.about_dialog);
		
		TextView txt = (TextView)dialog.findViewById(R.id.aboutTextView);
		txt.setMovementMethod(new ScrollingMovementMethod());
		
		ok = (Button)dialog.findViewById(R.id.dialogDismiss);
		ok.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{ dialog.dismiss(); }
		});
		
		StringBuffer contents = new StringBuffer("");
		byte[] buffer = new byte[1024];
		
		InputStream bit = null;
			
		try 
		{
			bit = this.getAssets().open("texts/about.txt");
			while(bit.read(buffer) != -1)
				contents.append(new String(buffer));
		} 
		catch (IOException e)
		{e.printStackTrace();}
			
		txt.setText(contents.toString());
		txt.setTextSize(13.0f);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setTitle("About");
		dialog.show();
		bit.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.screen_0, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
		{
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}

	
}
