package com.ftboys.ChordMixer.ChordMixerAlgorithm;

/**
 * @author OYZH
 * 标准和弦信息
 */
public class StdChord {


	public StdNote[] keyNotes;
	public int numOfNotes;
	public int chordId;
	public int priority;
	public static int chordN = 0;
	public int frequency;
	public int counter;
	public String chordName;

	/**
	 * @param note1 		根音
	 * @param note2 		三音
	 * @param note3 		五音
	 * @param numOfNotes    3个音
	 * @param chordId 		和弦序号
	 * @param priority		优先级
	 * @param frequency		使用频率
	 * @param counter		命中次数
	 * @param chordName		和弦名字
	 */
	public StdChord(String note1, String note2, String note3, int numOfNotes, int chordId, int priority,
					int frequency, int counter, String chordName) {
		StdNote[] noteSeq = {
				new StdNote(note1),
				new StdNote(note2),
				new StdNote(note3)
		};
		this.keyNotes = noteSeq;
		this.numOfNotes = 3;
		this.chordId = chordId;
		this.priority = priority;
		this.frequency = frequency;
		this.counter = counter;
		this.chordName = new String(chordName);
		this.chordN++;

	}


	/**快速初始化三和弦，默认参数全为0，和弦个数为3
	 * @param note1 		根音
	 * @param note2 		三音
	 * @param note3 		五音
	 * @param chordName		和弦名字
	 * @param priority		和弦优先级
	 */
	public StdChord(String note1, String note2, String note3, String chordName, int priority) {
		StdNote[] noteSeq = {
				new StdNote(note1),
				new StdNote(note2),
				new StdNote(note3)
		};
		this.keyNotes = noteSeq;
		this.numOfNotes = 3;

		this.priority = priority;
		this.frequency = 0;
		this.counter = 0;
		this.chordName = new String(chordName);

		chordId = chordN;
		this.chordN++;
	}

	//和弦名初始化StdChord
	public StdChord(String chordName){

		this("C","E","G",chordName,0);
		StdNote note1,note2,note3;
		note1 = new StdNote("C"); note2 = new StdNote("E"); note3 = new StdNote("G");
		switch(chordName){
			case "C":
				note1 = new StdNote("C"); note2 = new StdNote("E"); note3 = new StdNote("G"); break;
			case "Dm":
				note1 = new StdNote("D"); note2 = new StdNote("F"); note3 = new StdNote("A"); break;
			case "Em":
				note1 = new StdNote("E"); note2 = new StdNote("G"); note3 = new StdNote("B"); break;
			case "F":
				note1 = new StdNote("F"); note2 = new StdNote("A"); note3 = new StdNote("C"); break;
			case "G":
				note1 = new StdNote("G"); note2 = new StdNote("B"); note3 = new StdNote("D"); break;
			case "Am":
				note1 = new StdNote("A"); note2 = new StdNote("C"); note3 = new StdNote("E"); break;
			case "G7":
				note1 = new StdNote("G"); note2 = new StdNote("B"); note3 = new StdNote("F"); break;

		}

		this.keyNotes = new StdNote[] {note1,note2,note3};
	}

	public int CompareTo(Object obj)
	{
		StdChord p = (StdChord)obj ;

		if (this.counter < p.counter) // More counter is better
			return 1;
		else if (this.counter == p.counter)
		{
			if (this.priority < p.priority) // higher priority
				return 1;
			else
				return -1;
		}
		else
			return -1;

	}

}

