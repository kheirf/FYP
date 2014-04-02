package ie.dit.dt211.mt;

import java.io.IOException;
import java.io.InputStream;

import android.app.Dialog;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ShowAbout 
{
	Dialog dialog;
	Button ok;
	
	public void showAbout(Context context) throws IOException
	{
		dialog = new Dialog(context);
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
			bit = context.getAssets().open("texts/about.txt");
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

}
