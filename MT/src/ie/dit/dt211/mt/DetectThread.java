package ie.dit.dt211.mt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import com.musicg.wave.Wave;
import com.musicg.wave.WaveHeader;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTimestamp;
import android.util.Log;


public class DetectThread extends Thread
{
	ByteArrayOutputStream bas;
	
	protected Wave wave;
	protected AudioTimestamp audioTime;
	
	private CaptureThread ct;
	private Analyse analyzer;
	private WaveHeader wh;
	private volatile Thread _thread;
	protected int note = 0;
	private static Hashtable<String, Double> notes = new Hashtable<String, Double>();
	
	private OnSignalsDetected onSignalsDetectedListener;
	
	public DetectThread(CaptureThread rec)
	{
		initHashTable();
		bas = new ByteArrayOutputStream();
		ct = rec;
		AudioRecord ar = rec.getAudioRecord();
		int bps = 0;
		
		
		if(ar.getAudioFormat() == AudioFormat.ENCODING_PCM_16BIT)
			bps = 16;
		else
			if(ar.getAudioFormat() == AudioFormat.ENCODING_PCM_8BIT)
				bps = 8;
		
		int channel = 0;
		if (ar.getChannelConfiguration() == AudioFormat.CHANNEL_IN_MONO)
		{
			channel = 1;
		}
		
		wh = new WaveHeader();
		wh.setChannels(channel);
		wh.setBitsPerSample(bps);
		wh.setSampleRate(ar.getSampleRate());
		wave = new Wave();
		analyzer = new Analyse(wh);
	}
	
	public void start()
	{
		_thread = new Thread(this);
		_thread.start();
	}
	
	public void stopDetect()
	{
		//onSignalsDetectedListener = null;
		_thread = null;
	}
	
	protected double getAverageLoudness(byte [] data)
	{
		int frameSize = ct.getFrameSize();
		int z = 0;
		short y = 0;
		double x = 0.0f;
		
		for (int i = 0; i < frameSize; i+=2)
		{
			y = (short)(data[i] | data[i + 1] << 8);
			z += Math.abs(y);
		}
		x = z / frameSize / 2;
		
		return x;	
	}
	
	
	
	
	public void collect(byte [] bb) throws IOException
	{
		bas.write(bb);
	}
	
	
	@Override
	public void run()
	{
		try
		{
			double frequency = 0.0;
			byte [] buffer = new byte[2048];
			Thread t = Thread.currentThread();
			while(_thread == t)
			{
				
				buffer = ct.getFrameByte();
				if(getAverageLoudness(buffer) > 45) //original 30
				{
					frequency = analyzer.robustFrequency(buffer);
					if((frequency > 257) && (frequency < 783.9))
					{
						note = closestNote(frequency);
						onDetected();
					}
				}
				else
					note = 0;
			}
		}
		catch (Exception e) {e.printStackTrace();}
	}
	

	public int closestNote(double frequency)
	{
		double f = frequency;
		double minDist = Double.MAX_VALUE;
		double val = 0, x;
		double dist;
		int s = 0;
	

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
				s = Integer.parseInt((String) entry.getKey());
				break;
			}
		}
		
		return s;
	}
	
	public int getCurrentNote()
	{
		int count = 0;
		int nota = 0;
		while(count < 10)
		{
			nota = note;
			count++;
		}
		return nota;
	}
	
	private void initHashTable()
	{
		notes.put("1", 261.6);
		notes.put("2", 277.2);
		notes.put("3", 293.6);
		notes.put("4", 311.1);
		notes.put("5", 329.6);
		notes.put("6", 349.2);
		notes.put("7", 369.9);
		notes.put("8", 391.9);
		notes.put("9", 415.3);
		notes.put("10", 440.0);
		notes.put("11", 466.1);
		notes.put("12", 493.8);
		notes.put("13", 523.25);
		notes.put("14", 554.36);
		notes.put("15", 587.3);
		notes.put("16", 622.254);
		notes.put("17", 659.25);
		notes.put("18", 698.45);
		notes.put("19", 739.98);
		notes.put("20", 783.9);
	}
	
	private void onDetected()
	{
		if (onSignalsDetectedListener != null)
		{
			onSignalsDetectedListener.onDetected();
		}
	}
	
	public void setOnSignalsDetectedListener(OnSignalsDetected listener)
	{
		onSignalsDetectedListener = listener;
	}
	
}
