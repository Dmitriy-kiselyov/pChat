package ru.pussy_penetrator.pchat.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.SparseIntArray;

import java.util.HashMap;
import java.util.Map;

import static android.media.SoundPool.*;

public class SoundManager {
    private static int MAX_STREAMS = 1;

    private Context mContext;
    private SoundPool mSoundPool;
    private SparseIntArray mResIds;

    public SoundManager(Context context) {
        this.mContext = context;

        mResIds = new SparseIntArray();
        mSoundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_NOTIFICATION, 1);
    }

    public void load(int resourceId) {
        int id = mSoundPool.load(mContext, resourceId, 10);
        mResIds.append(resourceId, id);
    }

    public void play(int resourceId) {
        int id = mResIds.get(resourceId);
        mSoundPool.play(id, 0.15f, 0.15f, 10, 0, 1);
    }

    public void release() {
        mSoundPool.release();
    }
}
