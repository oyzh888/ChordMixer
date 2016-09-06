package com.ftboys.ChordMixer.ChordMixerAlgorithm;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by OYZH on 16/8/10.
 */
public class ScoreLearnResult {

    private int[][] chordRelationTable = new int[][]//和弦表，控制和弦连接
            {
                    { 5, 0, 7,10, 8, 8, 5},
                    { 0, 5, 8, 0,10, 6, 6},
                    { 6, 0, 5, 8, 0, 8, 6},
                    { 8, 6, 8, 5, 9, 5, 8},
                    {10, 0, 6, 8, 5, 5, 6},
                    { 0, 8, 6, 6, 8, 5, 6},
                    { 9, 8, 6, 6, 9, 6, 5},

            };
    int totalChord = chordRelationTable.length;//Total chord
    StdChord[] myChord = new StdChord[totalChord]; //软件支持的主和弦数（一般7个）


    public void scoreHandler(StdScore myScore){

        //统计不同和弦出现次数,记录下其名字.
        Map<String, Integer> chordMap = new HashMap<String , Integer>();
        for(StdChord cTmp : myScore.chordTrack){
            if( chordMap.containsKey(cTmp.chordName) ){
                Integer x = chordMap.get(cTmp.chordName);
                x++;
                chordMap.put(cTmp.chordName, x);
            }
            else
                chordMap.put(cTmp.chordName, 1);
        }

        int mapSize = chordMap.size();
        //Integer table[] =  chordMap.values().toArray(new Integer [mapSize ]);
        int tableToRet [][] = new int[mapSize][mapSize];

        //myScore.chordTrack.get()

    }


    public class scoreInfo{
        //int
    }
}
