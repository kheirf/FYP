package ie.dit.dt211.mt.model;




import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import com.musicg.wave.Wave;
import com.musicg.wave.WaveFileManager;
import com.musicg.wave.WaveHeader;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTimestamp;
import android.media.AudioTrack;
import android.util.Log;


public class DetectThread extends Thread
{	
	protected Wave wave;
	protected AudioTimestamp audioTime;
	
	CaptureThread ct;
	Analyse analyzer;
	private WaveHeader wh;
	private volatile Thread _thread;
	protected int note = 0;
	private static Hashtable<String, Double> notes = new Hashtable<String, Double>();
	
	
	AudioTrack at;
	ByteArrayOutputStream bas;
	
	
	public DetectThread(CaptureThread rec)
	{
		initHashTable();
		bas = new ByteArrayOutputStream();
		ct = rec;
		AudioRecord ar = rec.getAudioRecord();
		int bps = 0; //bits per sample
		int channel = 0;
		
		if(ar.getAudioFormat() == AudioFormat.ENCODING_PCM_16BIT)
			bps = 16; //16 bits per sample
		else
			if(ar.getAudioFormat() == AudioFormat.ENCODING_PCM_8BIT)
				bps = 8; //8 bits per sample
		
		if (ar.getChannelConfiguration() == AudioFormat.CHANNEL_IN_MONO)
			channel = 1;
		
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
		_thread = null;
	}
	
	protected double getAverageLoudness(byte[] data)
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
	
	
	
	@Override
	public void run()
	{
		try
		{
			double frequency = 0.0;
			byte [] buffer;
			
			Thread t = Thread.currentThread();
			while(_thread == t)
			{
				buffer = ct.getFrameByte();
				collect(buffer);
				if(getAverageLoudness(buffer) > 50)
				{
					frequency = analyzer.robustFrequency(buffer);
					if((frequency > 257) && (frequency < 783.9))
					{note = closestNote(frequency);}
				}
				else
					note = 0;
				
			}
			
		}
		catch (Exception e) {e.printStackTrace();}
	}
	

	private void collect(byte[] buffer) 
	{
		bas.write(buffer, 0, buffer.length);
		Log.d("BAS size", String.valueOf(bas.size()));
	}
	
	public String writeToFile(String fileName, Context context) 
	{
		Log.d("Writing wav", "here");
		// TODO Auto-generated method stub
		byte [] data = bas.toByteArray();
		wave = new Wave(wh, data);
		//String filePath = Environment.getExternalStorageDirectory().getPath() + "/mtRecord1.wav";
		String filePath = context.getFilesDir().getPath() + "/" + fileName;
		File filename = new File(filePath);
		try 
		{
			filename.createNewFile();
			bas.flush();
			bas.close();
		} 
		catch (IOException e) 
		{e.printStackTrace();}
		
		WaveFileManager wfm = new WaveFileManager();
		wfm.setWave(wave);
		wfm.saveWaveAsFile(filePath);
		
		return filePath;
	}

	private static int closestNote(double frequency)
	{
		double f = frequency;
		double minDist = Double.MAX_VALUE;
		double val = 0, x; //x = temporary holder of values from table
		double dist; //distance
		int s = 0; //return value
	
		//loop through each element of the hash table
		for (Enumeration<Double> e = notes.elements(); e.hasMoreElements();)
		{
			x = e.nextElement(); //x holds the value of current element
			dist = Math.abs(x - f); //distance is calculated by subtracting the value of
									// current element and the given frequency
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
		notes.put("1", 261.6); //C
		notes.put("2", 277.2); //C#
		notes.put("3", 293.6); //D
		notes.put("4", 311.1); //D#
		notes.put("5", 329.6); //E
		notes.put("6", 349.2); //F
		notes.put("7", 369.9); //F#
		notes.put("8", 391.9); //G
		notes.put("9", 415.3); //G#
		notes.put("10", 440.0); //A
		notes.put("11", 466.1); //A#
		notes.put("12", 493.8); //B
		notes.put("13", 523.25); //C
		notes.put("14", 554.36);
		notes.put("15", 587.3);
		notes.put("16", 622.254);
		notes.put("17", 659.25);
		notes.put("18", 698.45);
		notes.put("19", 739.98);
		notes.put("20", 783.9);
	}
	
	
	
}
