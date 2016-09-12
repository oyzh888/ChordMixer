package com.ftboys.ChordMixer.ChordMixerAlgorithm;

/**
 * @author OYZH
 *定义标准音符信息
 */
public class StdNote {

	/**
	 * @param absolutePosition 绝对音高
	 * @param duration 持续时长，2^n * k秒。6 为四分音符长度   6表示四分音符 5表示八分音符 2表示六十四分之音符
	 * @param dot 是否有附点 1为有，0为没有
	 * @param name 音符的名字CDEFGAB 可以加#b表示升降
	 * @param barPoint 是否为小节末尾
	 *
	 *
	 */

	public int absolutePosition;
	public int duration;
	public int dot;
	public String name;
	public int barPoint = 0;

	public int downFlatSharp = 1;

	private int biasForOctave = 2;
	public StdNote(int pitch, int duration, int octave, int dot, String name) {
		if(!(pitch > -1 && pitch < 12
				   && duration > -1
				   && octave > -1
				   && name.length() < 3)
		  )
			return;
		this.duration = duration;
		this.dot = dot;
		this.name = name;
		absolutePosition = pitch + (octave + biasForOctave) * 12;


		if(name.length() > 1){
			if(name.charAt(1) == '#') downFlatSharp++;
			else if(name.charAt(1) == 'b')downFlatSharp--;
		}

	}


	//使用absolutePosition初始化  60是中央C即C3
	public StdNote(int absolutePosition){

		this(0,5,0,0,"C");
		this.absolutePosition = absolutePosition;
		int pitch = getPitch();
		String tmp = null;
		switch(pitch)
		{
			case 0: tmp = "C"; break;
			case 1: tmp = "C#";break;
			case 2: tmp = "D"; break;
			case 3: tmp = "D#";break;
			case 4: tmp = "E"; break;
			case 5: tmp = "F"; break;
			case 6: tmp = "F#";break;
			case 7: tmp = "G"; break;
			case 8: tmp = "G#";break;
			case 9: tmp = "A"; break;
			case 10:tmp = "A#";break;
			case 11:tmp = "B"; break;
			default:tmp = "Z"; break;
		}
		this.name = tmp;

	}


	//只是用name初始化，建立一个没有八度的音
	public StdNote(String name) {
		this(60);
		this.name = name;
		int pitch = 0;
		//根据pitch初始化name
		switch(name.charAt(0))
		{
			case 'C': pitch = 0; break;
			case 'D': pitch = 2; break;
			case 'E': pitch = 4; break;
			case 'F': pitch = 5; break;
			case 'G': pitch = 7; break;
			case 'A': pitch = 9;break;
			case 'B': pitch = 11;break;

		}

		if(name.length() > 1){
			if(name.charAt(1) == '#') pitch++;
			else if(name.charAt(1) == 'b')pitch--;
		}
		absolutePosition += pitch;

	}

	//建议使用本构造函数构造完整的音符
	/**
	 * @param duration 持续时长，2^n * k 秒。6 为四分音符长度
	 * @param barPoint 是否是小节分割
	 * @param dot 是否有附点 1为有，0为没有
	 * @param absolutePosition 绝对音高
	 */
	public StdNote(int absolutePosition, int duration, int dot, int barPoint) {
		this(absolutePosition);
		this.duration = duration;
		this.dot = dot;
		this.barPoint = barPoint;
		//octave = getOctave();
	}

	public int getOctave(){
		return absolutePosition / 12 - biasForOctave;
	}

	public int getPitch(){
		return absolutePosition % 12;
	}

	//控制台打印音符信息
	public String description(){
		String tmp = "pitch: "   + getPitch()				   				 + '\n'
				   + "duration:" + duration 								 + '\n'
				   + "octave:"   + getOctave()							     + '\n'
				   + "dot:"		 + dot 										 + '\n'
				   + "name:"	 + name 									 + '\n'
				   + "absPosition"+ absolutePosition;
		return tmp;
	}

	//ToStringNote code
	public String stdToStringNote(){
		String str = "";
		str +=  name + (getOctave()+1) + 0 + (duration-2)  + " " ;
		//if(barPoint == 1) str += ',';
		return str;
	}

	//	C4030999
	//  8位一个音，1音名；2八度；3升降＋－0；4音长；5附点1、0；6－8无意义的9占位；
	public String stdTo8BitsStringNote(){
		String str = "";
		str +=  name + (getOctave()) + 0 + (duration-2)  + " " ;
		//if(barPoint == 1) str += ',';
		return str;
	}
	
	//比较两个音符是否相同的函数
	public Boolean compareNote(StdNote otherNote){
		if(otherNote.absolutePosition % 12 == this.absolutePosition % 12) return true;
		else return false;
	}
	
}

