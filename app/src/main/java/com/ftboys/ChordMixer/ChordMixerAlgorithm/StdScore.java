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

	//乐曲其他信息
	private int volume = 100;
	public int tempo = 120;
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


	//region encoder：
	public String scoreToStringNotes(){
		String str = "";
		for(StdNote tNote : musicTrack.get(0).noteTrack){
			str +=  tNote.name + tNote.getOctave() + 0 + (tNote.duration-2)  + " " ;
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
	//0-2absoluteposition 3是否升降 4时长 5附点 6-7占位符 8分隔符

	public String scoreTo8BitsStringNotes(){
		String str = "", placeholder = "00",seperator = " ";
		for(StdNote tNote : musicTrack.get(0).noteTrack){

			str += String.format("%03d",tNote.absolutePosition)
				+  tNote.downFlatSharp
				+  tNote.duration
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

	//保留方法
	public String scoreToHeadInfo(){
		String str = "";
		return str;
	}

	public String scoreToFileString(){
		String str = "", seperator = "$";

		//头信息
		str += 	  author + seperator
				+ scoreName + seperator
				+ tempo + seperator
				+ volume + seperator
				+ toneChenge + seperator
				+ musicTrack.size() + seperator
				//主旋律信息和和弦信息
				+ scoreTo8BitsStringNotes() + seperator
				+ scoreToChord();
		return str;
	}
	//endregion

	//region decoder:

	public StdScore fileToScore(String fileStr){
		String seperator = "\\$", author, scoreName;
		int tempo, volume, toneChange, musicTrackSize;

		String[] splitedStr = fileStr.split(seperator);

		author 		   = splitedStr[0];
		scoreName      = splitedStr[1];
		tempo		   = Integer.valueOf(splitedStr[2]);
		volume 		   = Integer.valueOf(splitedStr[3]);
		toneChange     = Integer.valueOf(splitedStr[4]);
		musicTrackSize = Integer.valueOf(splitedStr[5]);

		StdScore scoreRet = new StdScore();
		//赋值头部信息
		scoreRet.scoreName 	 = scoreName;
		scoreRet.author	     = author;
		scoreRet.tempo       = tempo;
		scoreRet.volume      = volume;
		scoreRet. toneChenge = toneChange;

		scoreRet.musicTrack.clear();
		for(int i = 0; i<musicTrackSize; i++)
			scoreRet.musicTrack.add(new StdTrack());

		String strArr[];
		//加入主旋律
		strArr = splitedStr[6].split(" ");
		for(String tmpStr : strArr){
			scoreRet.musicTrack.get(0).noteTrack.add( stringToNote(tmpStr) );
		}
		//加入和弦
		strArr = splitedStr[7].split(" ");//chord的分隔符
		for(String tmpStr : strArr){
			scoreRet.chordTrack.add( stringToChord(tmpStr));
		}
		return scoreRet;
	}

	public StdChord stringToChord(String strChord){
		return new StdChord((strChord));
	}

	public StdNote stringToNote(String strNote){

//        08805100 08805100 08805100
//        C Dm Em C4 Cadd
		//0-2absoluteposition 3是否升降 4时长 5附点 6-7占位符 8分隔符
		StdNote noteRet 	   = new StdNote(Integer.valueOf(strNote.substring(0,3)) );

		noteRet.downFlatSharp  = strNote.charAt(3) - '0';
		noteRet.duration       = strNote.charAt(4) - '0';
		noteRet.dot  		   = strNote.charAt(5) - '0';


		//System.out.println(noteRet.description());
		return noteRet;
	}
	//endregion

	public String description(){
		String str = "";
		str += "numOfTrarck = " + musicTrack.size() + "\n";
		str += "MainMelody:\n";
		for(StdNote tmp : musicTrack.get(0).noteTrack){
			str += tmp.name + tmp.getOctave() + tmp.duration + " ";
		}
		str += "\n";
		str += "ChordTrack:\n";
		for(StdChord tmp :chordTrack){
			str += tmp.chordName + " ";
		}
		return str;
	}
}



