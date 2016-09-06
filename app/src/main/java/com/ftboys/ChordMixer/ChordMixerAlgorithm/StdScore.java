package com.ftboys.ChordMixer.ChordMixerAlgorithm;
//所有的音乐信息 都使用StdScore在各个类之间传递

import java.util.ArrayList;

/**
 * @author OYZH
 * 曲谱类
 */

public class StdScore {
	public int defNumOfTrack = 5;

	public ArrayList< StdChord > chordTrack;
	public ArrayList<StdTrack> musicTrack  ;

	public int toneChenge = 0;

	//ADD BY FJC
	public int instrument[];

	//乐曲其他信息
	private int volume = 100;
	public float tempo = 120;
	public String scoreName = "UnKnown";
	public String author = "UnKnown";

	public StdScore(){
		super();
		//noteTrack = new ArrayList [maxNumOfTrack];//注意泛型数组的使用（泛型声明时不能实例化）
		musicTrack = new ArrayList<StdTrack>();
		for(int i=0; i<defNumOfTrack; i++){
			musicTrack.add(new StdTrack());
		}

		chordTrack = new ArrayList< StdChord >();

		//给所有的轨道分配空间
		//给和弦模式分配默认值
//		for(int i = 0; i < defNumOfTrack; i++){
//			noteTrack.get(i).noteTrack = new ArrayList< StdNote >();
//			noteTrack.get(i).instrument = 0;
//		}


	}


	public void initScore() {
		musicTrack.get(0).noteTrack.clear();
		for(int i=0; i<musicTrack.size(); i++){
			musicTrack.get(i).noteTrack.clear();
		}
		chordTrack.clear();
	}


	//ADD BY FJC
	//oyzh has deleted FJC's code since the structure of StdScore was changed.

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public int getVolume() {
		return volume;
	}

	public String scoreToStringNotes(){
		String str = "";
		for(StdNote tNote : musicTrack.get(0).noteTrack){
			str +=  tNote.name + tNote.octave + 0 + (tNote.duration-2)  + " " ;
			if(tNote.barPoint == 1) str += ',';
		}
		return str.trim();//去掉前后空格
	}

	//        088 0 5 1 00
//        100 1 6 1 00
//
//        name
//                author
//
//        08805100 08805100 08805100
//        C Dm Em C4 Cadd
	public String scoreTo8BitsStringNotes(){
		String str = "", placeholder = "00",seperator = " ";
		for(StdNote tNote : musicTrack.get(0).noteTrack){

			str += String.format("%03d",tNote.absolutePosition)
				+  tNote.downFlatSharp
				+  tNote.octave
				+  tNote.dot
				+  placeholder
				+  seperator;
			//这里要这样写么？？
			if(tNote.barPoint == 1) str += ',';
		}
		return str.trim();//去掉前后空格
	}


	public String scoreToChord(){
		String str = "";
		for(StdChord tChord : chordTrack){
			str +=  tChord.chordName + " " ;
		}
		return str.trim();//去掉了前后空格
	}


	public String description(){
		String str = "";
		str += "numOfTrarck = " + musicTrack.size() + "\n";
		str += "MainMelody:\n";
		for(StdNote tmp : musicTrack.get(0).noteTrack){
			str += tmp.name + tmp.octave + tmp.duration + " ";
		}
		str += "\n";
		str += "ChordTrack:\n";
		for(StdChord tmp :chordTrack){
			str += tmp.chordName + " ";
		}
		return str;
	}
}



