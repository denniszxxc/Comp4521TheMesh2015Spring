package com.comp4521.bookscan;

import java.util.HashMap;

import com.example.bookscan.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class ScannerSound {
	private SoundPool mSoundPool;
    private HashMap<Integer, Integer> mSoundPoolMap;
    private AudioManager mAudioManager;
    private Context mContext;
    int streamVolume;
    
	@SuppressWarnings("deprecation")
	@SuppressLint("UseSparseArrays") 
	public ScannerSound(Context context) {
		mContext = context;
	    mSoundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
	    mSoundPoolMap = new HashMap<Integer, Integer>();
	    mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
	    mSoundPoolMap.put(1, mSoundPool.load(mContext, R.raw.bit, 1));
	    streamVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	}
	
	public void playSound() {
		mSoundPool.play(mSoundPoolMap.get(1), streamVolume, streamVolume, 100, 0, 1f);
	}
}
