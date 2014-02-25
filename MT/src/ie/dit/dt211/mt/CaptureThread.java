 /***********************************************************************
 * 	Author: 		Kheir Antony Fernandez								*
 *  References:		Jacquet Wong (2012) on WhistleAPI					*
 *  Description:	This class which extends "Thread" is used for 		*
 *  				recording audio. The concept is referenced to		*
 *  				Wong's implementation of WhistleAPI					*
 ***********************************************************************/

package ie.dit.dt211.mt;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class CaptureThread extends Thread
{
	
	protected int buffSize;
	private AudioRecord audioRec;
	private int channelConf = AudioFormat.CHANNEL_IN_MONO; //single channel
	private int encoding = AudioFormat.ENCODING_PCM_16BIT;
	private int sampleRate = 44100; //sample rate must be large enough to determine the frequency
	private int frameSize = 4096; /**original: 2048*/
	private boolean flag; //flag to know if recording has stopped or currently running
	byte [] buffer;
	
	//Constructor
	public CaptureThread() //initialize the values of the audio format
	{
		//record buff size needs to be larger than the size of the frame (frameSize)
		buffSize = AudioRecord.getMinBufferSize(sampleRate, channelConf, encoding);
		audioRec = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConf, encoding, buffSize);
		buffer = new byte[frameSize];
		
		
		
	}
	
	public AudioRecord getAudioRecord()
	{
		return audioRec;
	}
	
	//method to start recording
	public void startRecord()
	{
		try{
			audioRec.startRecording();
			flag = true;
			}
		catch (Exception e){
			e.printStackTrace();}
	}
	
	//method to stop recording
	public void stopRecord()
	{
		try{
			audioRec.stop();
			flag = false;
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//method to get the audio data
	public byte [] getFrameByte()
	{
		audioRec.read(buffer, 0, frameSize); //write the recorded audio data to "buffer" array, starting from 0, and with 2042 size
		
		//convert 2 bytes into single value
		int z = 0;
		short y = 0;
		double x = 0.0f;
		for (int i = 0; i < frameSize; i+=2)
		{
			y = (short) ((buffer[i]) | buffer[i + 1] << 8);
			z += Math.abs(y);
		}
		x = z / frameSize / 2; //average absolute value
		
		if (x < 30) //check if there is input available
		{
			return null;
		}
		else
			return buffer;
	}
	
	//method to check the progress
	public boolean progress()
	{
		return this.isAlive() && flag;
	}
	
	public void run()
	{
		startRecord();
	}
}
