package ie.dit.dt211.mt;

import android.util.Log;

import com.musicg.math.rank.ArrayRankDouble;
import com.musicg.wave.Wave;
import com.musicg.wave.WaveHeader;
import com.musicg.wave.extension.Spectrogram;

public class Analyse 
{
	protected WaveHeader waveH;
	protected int fftSampleSize;
	protected int numFreqUnit;
	protected double unitFreq;
	protected int t;

	public Analyse(WaveHeader wh)
	{
		if (wh.getChannels() == 1)
			this.waveH = wh;
		
		
	}
	
	public double checkClarity(double [] normalizedSpectrum)
	{
		
		return 1.0;
	}
	
	public double robustFrequency(byte[] audioBytes)
	{
		/**
		 * probably get the amplitude first and then the fftsamplesize will be determined depending on the intensity of the sound
		 * 
		 * */
		if (audioBytes == null)
		{
			return 0;
		}
		
		int bps = waveH.getBitsPerSample() / 8; //bits per sample = 16 divided by 8 = 2 bytes per sample
		int samples = audioBytes.length / bps; //samples = 2048 / 2 = 1024 samples
		fftSampleSize = samples; //fft = 1024
		numFreqUnit = fftSampleSize / 2; //frequency unit = 512
		//Log.d("numFreqUnit", String.valueOf(numFreqUnit));
		Wave wav = new Wave(waveH, audioBytes);
		
		Spectrogram spectrogram = wav.getSpectrogram(2048, 1); //Try changing the fftsample size to 1024 or 2048 (java error)
		//Log.d("FPS", String.valueOf(spectrogram.getFramesPerSecond()));
		//Log.d("Total frams", String.valueOf(spectrogram.getNumFrames()));
		//Log.d("wav timeStamp: ", wav.timestamp());
		double [][] data = spectrogram.getAbsoluteSpectrogramData();
		double [][] normalized = normalizeSpectrogram(data);
		double [] spectrum = normalized[0];
		
		
		
		unitFreq = (double) waveH.getSampleRate() / 2 / numFreqUnit; //try 512 or 1024
		ArrayRankDouble ard = new ArrayRankDouble();
		double robustFreq = ard.getMaxValueIndex(spectrum) * unitFreq;
		
		Log.d("robust frequency", String.valueOf(robustFreq));
		
		return robustFreq;
	}
	
	public int getTime()
	{
		return t;
	}
	
	private static double [][] normalizeSpectrogram(double[][] spectrogram)
	{
		double [][] normalized = spectrogram;
        // normalization of absoultSpectrogram
        // set max and min amplitudes
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;    
        for (int i=0; i<spectrogram.length; i++)
        {
        	for (int j = 0; j < spectrogram[i].length; j++)
        	{
        		if (normalized[i][j] > max)
        			max = normalized[i][j];
        	
        		else 
        			if(normalized[i][j] < min)
        				min = normalized[i][j];
        	}
                
        }// end set max and min amplitudes
       
        // normalization
        // avoiding divided by zero
        double minValidAmp = 0.00000000001F;
        if (min == 0)
                min = minValidAmp;
       
        double diff = Math.log10(max / min);  // perceptual difference
        for (int i = 0; i < normalized.length; i++)
        {
        	for (int j = 0; j < normalized[i].length; j++)
        	{
                if (normalized[i][j] < minValidAmp)
                        normalized[i][j] = 0;
                else
                        normalized[i][j] = (Math.log10(normalized[i][j] / min))/diff;
        	}
        }
        
        return normalized;
	}
	
	
}
