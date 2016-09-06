package com.ftboys.ChordMixer.ChordMixerAlgorithm;

public class NoteException extends Exception {

	private String name;
	public NoteException(String name){
		this.name = name;
	}
	
	public String getExceptionName(){
		return name;
	}
}
