package ie.dit.dt211.mt.model;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;

import android.content.Context;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;
import android.util.Log;

public class FileHandler 
{
	private final static String TAG = "SerializeObject";
	
	
	//Method for converting an object into a string
	public static String convertObject(Serializable object)
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try
		{
			new ObjectOutputStream(out).writeObject(object);
			byte [] data = out.toByteArray();
			out.close();
			
			out = new ByteArrayOutputStream();
			Base64OutputStream b64 = new Base64OutputStream(out, 0);
			b64.write(data);
			b64.close();
			out.close();
			
			return new String(out.toByteArray());
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return null;
		
	}
	
	
	//Method for converting string into object
	public static Object convertString(String object) 
	{
		try 
		{
			return new ObjectInputStream(new Base64InputStream(new ByteArrayInputStream(object.getBytes()), 0)).readObject();
	    } 
		catch (Exception e) 
		{ e.printStackTrace();}
	        
		return null;
	}
	 
	 
	 //Method to write to a file
	 public static String write(Context ctx, String data, String filename)
	 { 
		 String ret = "";
	     FileOutputStream fo = null; 
	     OutputStreamWriter fos = null;

	        try
	        {
	            fo = ctx.openFileOutput(filename, Context.MODE_PRIVATE);       
	            fos = new OutputStreamWriter(fo); 
	            fos.write(data); 
	            fos.flush();
	            ret = filename;//String.valueOf(ctx.getFilesDir());
	            Log.d("File Path", filename);
	        } 
	        catch (Exception e) 
	        { e.printStackTrace();} 
	        finally 
	        { 
	            try 
	            { 
	                if(fos!=null)
	                    fos.close();
	                if (fo != null)
	                    fo.close(); 
	            } 
	            catch (IOException e) 
	            { e.printStackTrace(); } 
	        }
	        
	        return ret;
	    }
	 
	 public static String read(Context ctx, String filename)
	 { 
	        StringBuffer dataBuffer = new StringBuffer();
	        try
	        {
	            InputStream instream = ctx.openFileInput(filename);
	            if (instream != null) 
	            {
	                InputStreamReader inputreader = new InputStreamReader(instream);
	                BufferedReader buffreader = new BufferedReader(inputreader);

	                String newLine;
	                while (( newLine = buffreader.readLine()) != null) 
	                {
	                    dataBuffer.append(newLine);
	                }
	                instream.close();
	            }

	        } 
	        catch (java.io.FileNotFoundException f) 
	        {
	            Log.e(TAG, "FileNot Found in ReadSettings filename = " + filename);
	            try 
	            {
	                ctx.openFileOutput(filename, Context.MODE_PRIVATE);
	            } 
	            catch (FileNotFoundException e) {
	                e.printStackTrace();
	            }
	        } 
	        catch (IOException e) 
	        { Log.e(TAG, "IO Error in ReadSettings filename = " + filename); }

	        return dataBuffer.toString();
	    }

}
