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
import android.util.Log;

public class CaptureThread extends Thread
{
	
	
	protected long startTime;
	protected int buffSize;
	private AudioRecord audioRec;
	private int channelConf = AudioFormat.CHANNEL_IN_MONO; //single channel
	private int encoding = AudioFormat.ENCODING_PCM_16BIT;
	private int sampleRate = 44100; //sample rate must be large enough to determine the frequency
	private int frameSize = 2048; /**original: 4096*/
	private boolean flag; //flag to know if recording has stopped or currently running
	private byte [] buffer;
	
	//Constructor
	public CaptureThread() //initialize the values of the audio format
	{
		//record buff size needs to be larger than the size of the frame (frameSize)
		buffSize = AudioRecord.getMinBufferSize(sampleRate, channelConf, encoding);
		Log.d("buffSize", String.valueOf(buffSize));
		audioRec = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConf, encoding, buffSize);
		buffer = new byte[frameSize];
		startTime = 0;
	}
	
	public AudioRecord getAudioRecord()
	{
		return audioRec;
	}
	
	public long getStartTime()
	{
		return startTime;
	}
	
	//method to start recording
	public void startRecord()
	{
		try
		{
			startTime = System.nanoTime();
			if(audioRec.getState() == AudioRecord.STATE_INITIALIZED)
			{
				audioRec.startRecording();
				if(audioRec.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING)
					flag = true;
			}
			//flag = true;
		}
		catch (Exception e){
			e.printStackTrace();}
	}
	
	
	//method to stop recording
	public void stopRecord()
	{
		if(audioRec.getState() == AudioRecord.STATE_INITIALIZED
				&& audioRec.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING)
		{
			try
			{
				audioRec.stop();
				flag = false;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		
	}
	
	public synchronized void release() throws InterruptedException
	{
		if(audioRec != null)
		{
			if(audioRec.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING)
			{
				join();
			}
			audioRec.release();
			audioRec = null;
		}
	}
	
	//method to get the audio data
	public byte [] getFrameByte()
	{
		audioRec.read(buffer, 0, frameSize); //write the recorded audio data to "buffer" array, starting from 0, and with 2042 size
		int x = 0;
        short y = 0; 
        float z = 0.0f;
        //Short to byte conversion
        for (int i = 0; i < frameSize; i += 2) {
            y = (short)((buffer[i]) | buffer[i + 1] << 8);
            x += Math.abs(y);
        }
        z = x / frameSize / 2;

        // no input
        if (z < 50)
        	return null;
        
		return buffer;
	}
	
	public int getFrameSize()
	{
		return frameSize;
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
