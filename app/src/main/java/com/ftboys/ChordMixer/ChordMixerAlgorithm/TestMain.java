package com.ftboys.ChordMixer.ChordMixerAlgorithm;

import com.ftboys.ChordMixer.ChordMixerAlgorithm.*;

import java.util.Scanner;

public class TestMain {

	public static void main(String[] args)
	{
		ChordCalculator myC = new ChordCalculator();
		//默认建立一个5轨道的曲谱
		StdScore myScore = new StdScore();
		//可以直接访问tempo,volume等public变量,进行读写操作
		myScore.author = "OYZH";

		//构造音符,其中有众多构造函数,建议使用下面这个
		StdNote myNote = new StdNote(60,6,0,0);//一个四分音符,中央C,没浮点,不是小节末.
		//取出第一轨道写入一个音符
		myScore.musicTrack.get(0).noteTrack.add(  myNote );
		//写入一堆音符
		for(int i=0; i<10; i++)myScore.musicTrack.get(0).noteTrack.add(  myNote );
		//读出第一轨的音符
		myNote = myScore.musicTrack.get(0).noteTrack.get(0);
		//写入第一条伴奏轨
		myScore.musicTrack.get(1).noteTrack.add(myNote);
		//调用算法
		myScore = myC.scoreHandler(myScore);

		//打印主旋律音符midi音高和时长
		for (StdNote tmpNote: myScore.musicTrack.get(0).noteTrack) {
			System.out.println(tmpNote.absolutePosition + " " + tmpNote.duration );

		}
		//打印和弦
		for (StdChord tmpChord: myScore.chordTrack) {
			System.out.println(tmpChord.chordName);

		}

		//曲谱信息打印函数
		System.out.println(myScore.description());






//		System.out.println(myScore.scoreToStringNotes());
//		System.out.println(myScore.scoreToChord());
//		//建议所有的变量都要放到构造函数里面,无论是StdScore还是StdTrack,还是StdNote.确保信息完整
//

		/*
		* To FJC
		* 建议构造函数:
		* public StdNote(int absulutePosition, int duration, int dot, int barPoint)
		* public StdTrack(ArrayList<StdNote> noteTrack, int trackPattent, int instrument, int trackVolume)
		* public StdScore(ArrayList< StdChord > chordTrack,	ArrayList< StdTrack > musicTrack, int toneChange,
							int volume, float tempo, String scoreName, String author)
		* public StdChord(StdNote[] keyNotes, int chordId, int priority, int frequency, String chordName)
		*
		* 只考虑C调,那么StdChord可以用public StdChord(String chordName)快速建立.
		*
		* 文件存储规则你来决定吧
		*
		* OYZH 2016/8/12
		*
		*/

	}


}
