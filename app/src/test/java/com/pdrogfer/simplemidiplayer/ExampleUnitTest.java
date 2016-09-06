package com.pdrogfer.simplemidiplayer;

import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.ftboys.ChordMixer.ChordMixerAlgorithm.ChordCalculator;
import com.ftboys.ChordMixer.ChordMixerAlgorithm.QuickMusic;
import com.ftboys.ChordMixer.ChordMixerAlgorithm.StdChord;
import com.ftboys.ChordMixer.ChordMixerAlgorithm.StdNote;
import com.ftboys.ChordMixer.ChordMixerAlgorithm.StdPattent;
import com.ftboys.ChordMixer.ChordMixerAlgorithm.StdScore;

import org.junit.Test;

import static org.junit.Assert.*;
import com.ftboys.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */

/*
*Test for StdPattent
*
*
*/
public class ExampleUnitTest {

    ChordCalculator chordMixerCalculator = new ChordCalculator();
    //界面音乐变量
    private StdScore mainScore = new StdScore();
    private StdPattent mainPattent = new StdPattent();

    private int mainInputDuration = 6;


    TextView inputTextMain, textMainChord;

    private StdNote noteToAdd[] =  new StdNote[]{
        QuickMusic.strToNote("C36"),  QuickMusic.strToNote("C36"),  QuickMusic.strToNote("C36"),  QuickMusic.strToNote("C36"),
            QuickMusic.strToNote("D36"),  QuickMusic.strToNote("C36"),  QuickMusic.strToNote("D36"),  QuickMusic.strToNote("C36"),
            QuickMusic.strToNote("C36")
    };
    @Test
    public void addition_isCorrect() throws Exception {
        //assertEquals(3, 2 + 2);

       System.out.println();
       for(StdNote tmp : noteToAdd){
           mainScore.musicTrack.get(0).noteTrack.add(tmp);
       }

        mainScore.musicTrack.get(1).trackPattent = 2 ;

        mainScore = chordMixerCalculator.scoreHandler(mainScore);
        mainScore = mainPattent.scoreChordHandler(mainScore);

        String str = "";
        for(StdNote tmp :mainScore.musicTrack.get(2).noteTrack){
            str += tmp.name + tmp.octave + " ";
        }
        System.out.println(str);

        System.out.println(mainScore.scoreToChord());



    }
}