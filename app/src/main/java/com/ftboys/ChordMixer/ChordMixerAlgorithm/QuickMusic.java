package com.ftboys.ChordMixer.ChordMixerAlgorithm;

/**
 * Created by OYZH on 16/8/6.
 */
public  class QuickMusic {

    //音名 八度 时长
    public static StdNote strToNote(String str){
        //C46
        String name = String.valueOf(str.charAt(0)) ;
        int octave = str.charAt(1) - '0' ;
        int duration = str.charAt(2) - '0';
        StdNote noteReturn = new StdNote(name);

        //biasForOctave = 2
        //noteReturn.absolutePosition += (octave + noteReturn.biasForOctave) *12;
        noteReturn.absolutePosition += (octave + 2) *12;
        noteReturn.duration = duration;
        return noteReturn;
    }
}
