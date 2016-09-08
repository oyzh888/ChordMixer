package com.ftboys.ChordMixer;

import com.ftboys.ChordMixer.ChordMixerAlgorithm.QuickMusic;
import com.ftboys.ChordMixer.ChordMixerAlgorithm.StdChord;
import com.ftboys.ChordMixer.ChordMixerAlgorithm.StdNote;
import com.ftboys.ChordMixer.ChordMixerAlgorithm.StdScore;

import org.junit.Test;

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

        String str1 = mainScore.scoreToFileString();
        str1 = "UnKnown$UnKnown$120$100$0$5$08416000 06015000 06016000 06016000 06016000 06016000 06016000 06016000 06016000$C Dm";

        mainScore.initScore();
        System.out.println(str1);
        System.out.println("----------------------------");

        StdScore newScore = mainScore.fileToScore(str1);
        System.out.println(newScore.description());


       // System.out.println(mainScore.scoreToChord());

    }
}