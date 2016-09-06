package com.ftboys.ChordMixer.ChordMixerAlgorithm;

import java.util.ArrayList;

/**
 * Created by OYZH on 16/8/10.
 */
public class StdTrack {

    public ArrayList<StdNote> noteTrack;
    public int trackPattent = 0;
    public int instrument = 0;

    public int trackVolume = 100;

    public StdTrack(){
        noteTrack = new ArrayList<StdNote>();
    }

}
