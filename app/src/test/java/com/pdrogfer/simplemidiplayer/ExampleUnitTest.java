package com.pdrogfer.simplemidiplayer;

import android.provider.Settings;
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


    //曲谱样例
    private StdScore mainScore = new StdScore();

    //音符序列
    private StdNote noteToAdd[] =  new StdNote[]{
        QuickMusic.strToNote("E56"),  QuickMusic.strToNote("C35"),  QuickMusic.strToNote("C36"),  QuickMusic.strToNote("C36"),
            QuickMusic.strToNote("D36"),  QuickMusic.strToNote("C36"),  QuickMusic.strToNote("D36"),  QuickMusic.strToNote("C36"),
            QuickMusic.strToNote("C36")
    };
    //以下测试会自动运行
    @Test
    public void addition_isCorrect() throws Exception {
        //assertEquals(3, 2 + 2);

        //把音符序列依次加入到score的第一轨中
       for(StdNote tmp : noteToAdd){
           mainScore.musicTrack.get(0).noteTrack.add(tmp);
       }
        //添加两个和弦到和弦轨中
        mainScore.chordTrack.add(new StdChord("C"));
        mainScore.chordTrack.add(new StdChord("Dm"));

        System.out.println(mainScore.description());
        System.out.println(mainScore.scoreTo8BitsStringNotes());
        System.out.println(mainScore.scoreToChord());

    }
}