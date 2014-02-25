package ie.dit.dt211.mt;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import com.musicg.wave.WaveHeader;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.util.Log;


public class DetectThread extends Thread
{
	private CaptureThread ct;
	private Analyse analyzer;
	private WaveHeader wh;
	private volatile Thread _thread;
	protected String note = "hello";
	private static Hashtable<String, Double> notes = new Hashtable<String, Double>();
	
	private OnSignalsDetected onSignalsDetectedListener;
	
	public DetectThread(CaptureThread rec)
	{
		notes.put("C4", 261.6);
		notes.put("C4#", 277.2);
		notes.put("D4", 293.6);
		notes.put("D4#", 311.1);
		notes.put("E4", 329.6);
		notes.put("F4", 349.2);
		notes.put("F4#", 369.9);
		notes.put("G4", 391.9);
		notes.put("G4#", 415.3);
		notes.put("A4", 440.0);
		notes.put("A4#", 466.1);
		notes.put("B4", 493.8);
		notes.put("C5", 523.25);
		notes.put("C5#", 554.36);
		notes.put("D5", 587.3);
		notes.put("D5#", 622.254);
		notes.put("E5", 659.25);
		notes.put("F5", 698.45);
		notes.put("F5#", 739.98);
		notes.put("G5", 783.9);
		
		ct = rec;
		AudioRecord ar = rec.getAudioRecord();
		int bps = 0;
		
		
		if(ar.getAudioFormat() == AudioFormat.ENCODING_PCM_16BIT)
		{
			bps = 16;
		}
		else
			if(ar.getAudioFormat() == AudioFormat.ENCODING_PCM_8BIT)
			{
				bps = 8;
			}
		
		int channel = 0;
		if (ar.getChannelConfiguration() == AudioFormat.CHANNEL_IN_MONO)
		{
			channel = 1;
		}
		
		wh = new WaveHeader();
		wh.setChannels(channel);
		wh.setBitsPerSample(bps);
		wh.setSampleRate(ar.getSampleRate());
		
		analyzer = new Analyse(wh);
		
	}
	
	public void start()
	{
		_thread = new Thread(this);
		_thread.start();
	}
	
	public void stop_thread()
	{
		_thread = null;
	}
	
	public void run()
	{
		try
		{
			double frequency = 0.0;
			byte [] buffer;
			//String note = "hello";
			
			Thread t = Thread.currentThread();
			while(_thread == t)
			{
				
				buffer = ct.getFrameByte();
				
				
				if (buffer != null)
				{
					frequency = analyzer.robustFrequency(buffer);
					note = closestNote(frequency);
					//note = closestNote(analyzer.robustFrequency(buffer));
					Log.d("Note", note);
					if (!note.equals("hello"))
						 onDetected();
				}
				//else
					//Log.d("Buffer","no buffer");
				
				
				
			}
		}
		catch (Exception e) {e.printStackTrace();}
	}
	

	public String closestNote(double frequency)
	{
		double f = frequency;
		double minDist = Double.MAX_VALUE;
		double val = 0, x;
		double dist;
		String s = "hello";
		
		
			
		while (f < 261.6)
			f = f * 2.0;
		
		while (f > 783.9)
			f = f * 0.5;
		
		
		
		
		for (Enumeration<Double> e = notes.elements(); e.hasMoreElements();)
		{
			x = e.nextElement();
			dist = Math.abs(x - f);
			if (dist < minDist)
			{
				minDist = dist;
				val = x;
			}
		}
		
		for(@SuppressWarnings("rawtypes") Map.Entry entry: notes.entrySet())
		{
			if(entry.getValue().equals(val))
			{
				s = (String) entry.getKey();
				break;
			}
		}
		
		return s;
	}
	
	public String getCurrentNote()
	{
		return note;
	}
	
	private void onDetected()
	{
		if (onSignalsDetectedListener != null){
			onSignalsDetectedListener.onDetected();
		}
	}
	
	public void setOnSignalsDetectedListener(OnSignalsDetected listener)
	{
		onSignalsDetectedListener = listener;
	}
	
}
