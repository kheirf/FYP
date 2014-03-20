package ie.dit.dt211.mt;

import android.util.Log;

import com.musicg.math.rank.ArrayRankDouble;
import com.musicg.math.statistics.StandardDeviation;
import com.musicg.math.statistics.ZeroCrossingRate;
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
		
		//Log.d("Wave length", String.valueOf(wav.getBytes().length));
		
		Spectrogram spectrogram = wav.getSpectrogram(fftSampleSize, 0); //Try changing the fftsample size to 1024 or 2048 (java error)
		//Log.d("FPS", String.valueOf(spectrogram.getFramesPerSecond()));
		//Log.d("Total frams", String.valueOf(spectrogram.getNumFrames()));
		//Log.d("wav timeStamp: ", wav.timestamp());
		double [][] data = spectrogram.getAbsoluteSpectrogramData();
		double [] data2 = data[0];
		double [][] normalized = normalizeSpectrogram(data);
		double [] spectrum = normalized[0];
		
		
		
		unitFreq = (double) waveH.getSampleRate() / 2 / numFreqUnit; //try 512 or 1024
		ArrayRankDouble ard = new ArrayRankDouble();
		double robustFreq = ard.getMaxValueIndex(spectrum) * unitFreq;
		
		//testing zerocrossingrate
		ZeroCrossingRate zcr = new ZeroCrossingRate(wav.getSampleAmplitudes(), 1);
		double z = zcr.evaluate();
		//Log.d("ZCR", String.valueOf(z));
		//Log.d("Frequency", String.valueOf(robustFreq));
		if (robustFreq >= 516.79)
		{
			
		}
		/*if(robustFreq > 320)
		{
			double checkZCR = zeroCross(wav.getSampleAmplitudes());
			if(checkZCR < 195)
			{
				robustFreq *= .05;
			}
		}
		*/
		//testing standard deviation
		double nthVal = ard.getNthOrderedValue(spectrum, 10, false);
		double rf[] = new double [10];
		int count = 0;
		for (int i = 0; i < spectrum.length; i++)
		{
			if(spectrum[i] >= nthVal)
			{
				rf[count++] = spectrum[i];
				if(count >= 10)
					break;
			}
		}
		StandardDeviation sd = new StandardDeviation();
		sd.setValues(rf);
		double tt = sd.evaluate();
		//Log.d("SD", String.valueOf(tt));
		//testing intensity check
		
		double intensity = 0;
		for(int i = 0; i < data2.length; i++)
		{
			intensity += data2[i];
		}
		intensity /= data2.length;
		//Log.d("Intensity", String.valueOf(intensity));
		
		//Log.d("standard dev", String.valueOf(tt));
		//Log.d("Zero Crossing", String.valueOf(robustFreq));
		
		
		
		return robustFreq;
		
		
		
	}
	
	
	public double zeroCross(short [] amplitudes)
	{
		ZeroCrossingRate zcr = new ZeroCrossingRate(amplitudes, 1);
		double zcreval = zcr.evaluate();
		//Log.d("ZCR", String.valueOf(zcreval));
		return zcreval;
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
