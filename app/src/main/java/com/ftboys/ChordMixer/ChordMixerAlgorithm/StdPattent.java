package com.ftboys.ChordMixer.ChordMixerAlgorithm;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by OYZH on 16/8/5.
 */
public class StdPattent {

    public int defPattentNum = 5;
    public int defChordNum = 7;

    /*
    *C D E F G A  B
    *0 2 4 5 7 9 11
    *C  D  E  F  G  A  B  C
    *12 14 16 17 19 21 23 24
     */
    // int pitch, int duration, int octave, int dot, String name

    /*
    * 1维:选择伴奏型
    * 2维:选择和弦
    * 3维:音符串
    *
     */
    public StdNote[][][] defChordPat = new StdNote[][][]{
            //正常型节奏
            {
                    {new StdNote(0,6,2,0,"C"), new StdNote(4 ,6,2,0,"E"), new StdNote(7, 6,2,0,"G"),new StdNote(0,6,3,0,"C") },
                    {new StdNote(2,6,2,0,"D"), new StdNote(5 ,6,2,0,"F"), new StdNote(9, 6,2,0,"A"),new StdNote(2,6,3,0,"D") },
                    {new StdNote(4,6,2,0,"E"), new StdNote(7 ,6,2,0,"G"), new StdNote(11,6,2,0,"B"),new StdNote(4,6,3,0,"E") },
                    {new StdNote(5,6,2,0,"F"), new StdNote(9 ,6,2,0,"A"), new StdNote(0 ,6,3,0,"C"),new StdNote(5,6,3,0,"F") },
                    {new StdNote(7,6,2,0,"G"), new StdNote(11,6,2,0,"B"), new StdNote(2, 6,3,0,"D"),new StdNote(7,6,3,0,"G") },
                    {new StdNote(9,6,2,0,"A"), new StdNote(0 ,6,3,0,"C"), new StdNote(4, 6,3,0,"E"),new StdNote(9,6,3,0,"A") },
                    {new StdNote(7,6,2,0,"G"), new StdNote(11,6,2,0,"B"), new StdNote(2, 6,3,0,"D"),new StdNote(5,6,3,0,"F") },

            },
//            //推进型节奏
            {
                    {QuickMusic.strToNote("C25"), QuickMusic.strToNote("G25"), QuickMusic.strToNote("C35"), QuickMusic.strToNote("G25"),
                            QuickMusic.strToNote("E35"), QuickMusic.strToNote("G25"), QuickMusic.strToNote("C35"), QuickMusic.strToNote("G25"),},
                    {QuickMusic.strToNote("D25"), QuickMusic.strToNote("A25"), QuickMusic.strToNote("D35"), QuickMusic.strToNote("A25"),
                            QuickMusic.strToNote("F35"), QuickMusic.strToNote("A25"), QuickMusic.strToNote("D35"), QuickMusic.strToNote("A25"),},
                    {QuickMusic.strToNote("E25"), QuickMusic.strToNote("B25"), QuickMusic.strToNote("E35"), QuickMusic.strToNote("B25"),
                            QuickMusic.strToNote("G35"), QuickMusic.strToNote("B25"), QuickMusic.strToNote("E35"), QuickMusic.strToNote("B25"),},
                    {QuickMusic.strToNote("F15"), QuickMusic.strToNote("C25"), QuickMusic.strToNote("F25"), QuickMusic.strToNote("C25"),
                            QuickMusic.strToNote("A25"), QuickMusic.strToNote("C25"), QuickMusic.strToNote("F25"), QuickMusic.strToNote("C25"),},

                    {QuickMusic.strToNote("G15"), QuickMusic.strToNote("D25"), QuickMusic.strToNote("G25"), QuickMusic.strToNote("D25"),
                            QuickMusic.strToNote("B25"), QuickMusic.strToNote("D25"), QuickMusic.strToNote("G25"), QuickMusic.strToNote("D25"),},
                    {QuickMusic.strToNote("A15"), QuickMusic.strToNote("E25"), QuickMusic.strToNote("A25"), QuickMusic.strToNote("E25"),
                            QuickMusic.strToNote("C25"), QuickMusic.strToNote("E25"), QuickMusic.strToNote("A25"), QuickMusic.strToNote("E25"),},
                    {QuickMusic.strToNote("G15"), QuickMusic.strToNote("D25"), QuickMusic.strToNote("F25"), QuickMusic.strToNote("D25"),
                            QuickMusic.strToNote("G25"), QuickMusic.strToNote("D25"), QuickMusic.strToNote("F25"), QuickMusic.strToNote("D25"),},
            },

            //深情型节奏
            {
                    {new StdNote(0,7,1,0,"C"),new StdNote(0,7,2,0,"C")},
                    {QuickMusic.strToNote("D27"), QuickMusic.strToNote("D37")},
                    {QuickMusic.strToNote("E27"), QuickMusic.strToNote("E37")},
                    {QuickMusic.strToNote("F27"), QuickMusic.strToNote("F37")},
                    {QuickMusic.strToNote("G27"), QuickMusic.strToNote("G37")},
                    {QuickMusic.strToNote("A27"), QuickMusic.strToNote("A37")},
                    {QuickMusic.strToNote("G27"), QuickMusic.strToNote("F37")},
            },

            //活泼型节奏
            {
                    {       new StdNote(0,6,2,0,"C"), new StdNote(4,5,2,0,"E"), new StdNote(7,5,2,0,"G"),
                            new StdNote(0,5,2,0,"Z"), new StdNote(4,5,2,0,"E"), new StdNote(7,5,2,0,"Z"),new StdNote(0,5,3,0,"C")},
                    {       QuickMusic.strToNote("D26"), QuickMusic.strToNote("F25"), QuickMusic.strToNote("A25"),
                            QuickMusic.strToNote("Z25"), QuickMusic.strToNote("F25"), QuickMusic.strToNote("Z25"), QuickMusic.strToNote("D35"),},
                    {       QuickMusic.strToNote("E26"), QuickMusic.strToNote("G25"), QuickMusic.strToNote("B25"),
                            QuickMusic.strToNote("Z25"), QuickMusic.strToNote("G25"), QuickMusic.strToNote("Z25"), QuickMusic.strToNote("E35"),},

                    {       QuickMusic.strToNote("F16"), QuickMusic.strToNote("A15"), QuickMusic.strToNote("C25"),
                            QuickMusic.strToNote("Z15"), QuickMusic.strToNote("A15"), QuickMusic.strToNote("Z25"), QuickMusic.strToNote("F25"),},
                    {       QuickMusic.strToNote("G16"), QuickMusic.strToNote("B15"), QuickMusic.strToNote("D25"),
                            QuickMusic.strToNote("Z15"), QuickMusic.strToNote("B15"), QuickMusic.strToNote("Z25"), QuickMusic.strToNote("G25"),},
                    {       QuickMusic.strToNote("A16"), QuickMusic.strToNote("C15"), QuickMusic.strToNote("E25"),
                            QuickMusic.strToNote("A15"), QuickMusic.strToNote("C15"), QuickMusic.strToNote("E25"), QuickMusic.strToNote("A25"),},
                    {       QuickMusic.strToNote("G16"), QuickMusic.strToNote("B15"), QuickMusic.strToNote("D25"),
                            QuickMusic.strToNote("Z15"), QuickMusic.strToNote("B15"), QuickMusic.strToNote("Z25"), QuickMusic.strToNote("F25"),},
            }



    };





    public StdPattent() {
        defPattentNum = defChordPat.length;

    }

    //为曲谱加入伴奏

    public StdScore scoreChordHandler(StdScore inputScore){


        for(int noteTrackJ=1; noteTrackJ<inputScore.musicTrack.size(); noteTrackJ++){
            //先清空原本的所有伴奏轨道的内容
            inputScore.musicTrack.get(noteTrackJ).noteTrack.clear();
            //开始读取伴奏轨道的id
            int ptId = inputScore.musicTrack.get(noteTrackJ).trackPattent;

            for(int i=0; i<inputScore.chordTrack.size(); i++){
                StdChord tmpChord = inputScore.chordTrack.get(i);
                inputScore.musicTrack.get(noteTrackJ).noteTrack.addAll( traslateChordWithPattent(tmpChord,ptId) );
            }

        }

        return inputScore;
    }

    //翻译和弦成音符

    public ArrayList<StdNote> traslateChordWithPattent(StdChord chordValue, int pattentId){

        int chordId = 0;
        switch(chordValue.chordName){
            case "C":
                chordId = 0; break;
            case "Dm":
                chordId = 1; break;
            case "Em":
                chordId = 2; break;
            case "F":
                chordId = 3; break;
            case "G":
                chordId = 4; break;
            case "Am":
                chordId = 5; break;
            case "G7":
                chordId = 6; break;
        }
        ArrayList<StdNote> tmpArr = new ArrayList<>(Arrays.asList( defChordPat[pattentId][chordId] ));
        return tmpArr;
    }




//    public ArrayList<StdNote>[] defChordPat;
//    public int defPattentNum = 6;
//
//    public StdPattent() {
//
//        defChordPat = new ArrayList [defPattentNum];
//        for(int i = 0; i < defPattentNum; i++){
//            defChordPat[i] = new ArrayList< StdNote >();
//        }
//    }
//    ArrayList<StdNote> pat1 = new ArrayList<>(Arrays.asList(
//            new StdNote(0,6,2,0,"C"), new StdNote(4,6,2,0,"E"), new StdNote(7,6,2,0,"E"),new StdNote(0,6,3,0,"C")
//    ));

}
