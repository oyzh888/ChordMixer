package com.ftboys.ChordMixer;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import    java.text.SimpleDateFormat;

import com.ftboys.ChordMixer.ChordMixerAlgorithm.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import midi.midi.MidiFile;
import midi.midi.MidiTrack;
import midi.midi.event.ProgramChange;
import midi.midi.event.meta.Tempo;
import midi.midi.event.meta.TimeSignature;

public class ExternalStorageOperations {

    private static final String TAG = "ExternalStorage";

    final File MIDI_NOTE_FILE = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_MUSIC) + "/MIDI_NOTES/");
    final File MIDI_MUSIC_FILE = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_MUSIC) + "/MIDI_MUSIC/");


    Context context;
    OnNoteSavedToStorage noteSavedListener;

    public ExternalStorageOperations(Context context, OnNoteSavedToStorage noteSavedListener) {
        this.context = context;
        this.noteSavedListener = noteSavedListener;
    }

    public void createTestMusic() {

        MIDI_MUSIC_FILE.mkdirs(); // create folder if not exists

        //以下为节拍信息 BPM beat per min  不过好像没啥子卵用  控制速度还是用我们的一套吧

        MidiTrack tempoTrack = new MidiTrack();
        TimeSignature timeSignature = new TimeSignature();
        timeSignature.setTimeSignature(4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION);

        Tempo tempo = new Tempo();
        tempo.setBpm(120);

        //改变轨道音色  时间标记 轨道 音色
        ProgramChange programChange = new ProgramChange(0, 0, 0);

        tempoTrack.insertEvent(timeSignature);
        tempoTrack.insertEvent(tempo);
        tempoTrack.insertEvent(programChange);

        ArrayList<MidiTrack> tracks = new ArrayList<>();
        tracks.add(tempoTrack);

        //新建轨道这么操作 new MidiTrack（）
        //ProgramChange（时间标记，轨道，音色）设置轨道音色
        MidiTrack noteTrack = new MidiTrack();
        //.insertNote(channel, pitch, velocity, tick, shortNoteDuration);
        //频道 音 音量 时间标记 延时
        programChange = new ProgramChange(0, 0, 0);
        //时间标记 轨道 音色
        noteTrack.insertEvent(programChange);

        noteTrack.insertNote(0, 43, 100, 0, 500);
        noteTrack.insertNote(0, 43, 100, 500, 500);
        noteTrack.insertNote(0, 44, 100, 1000, 500);
        noteTrack.insertNote(0, 46, 100, 1500, 500);
        noteTrack.insertNote(0, 46, 100, 2000, 500);
        noteTrack.insertNote(0, 44, 100, 2500, 500);
        noteTrack.insertNote(0, 43, 100, 3000, 500);
        noteTrack.insertNote(0, 41, 100, 3500, 500);
        noteTrack.insertNote(0, 39, 100, 4000, 500);
        noteTrack.insertNote(0, 39, 100, 4500, 500);
        noteTrack.insertNote(0, 41, 100, 5000, 500);
        noteTrack.insertNote(0, 43, 100, 5500, 500);
        noteTrack.insertNote(0, 43, 100, 6000, 750);
        noteTrack.insertNote(0, 41, 100, 6750, 250);
        noteTrack.insertNote(0, 41, 100, 7000, 500);

        noteTrack.insertNote(0, 43, 100, 7500 + 0, 500);
        noteTrack.insertNote(0, 43, 100, 7500 + 500, 500);
        noteTrack.insertNote(0, 44, 100, 7500 + 1000, 500);
        noteTrack.insertNote(0, 46, 100, 7500 + 1500, 500);
        noteTrack.insertNote(0, 46, 100, 7500 + 2000, 500);
        noteTrack.insertNote(0, 44, 100, 7500 + 2500, 500);
        noteTrack.insertNote(0, 43, 100, 7500 + 3000, 500);
        noteTrack.insertNote(0, 41, 100, 7500 + 3500, 500);
        noteTrack.insertNote(0, 39, 100, 7500 + 4000, 500);
        noteTrack.insertNote(0, 39, 100, 7500 + 4500, 500);
        noteTrack.insertNote(0, 41, 100, 7500 + 5000, 500);
        noteTrack.insertNote(0, 43, 100, 7500 + 5500, 500);
        noteTrack.insertNote(0, 41, 100, 7500 + 6000, 750);
        noteTrack.insertNote(0, 39, 100, 7500 + 6750, 250);
        noteTrack.insertNote(0, 39, 100, 7500 + 7000, 500);

        MidiTrack noteTrack1 = new MidiTrack();
        programChange = new ProgramChange(0, 1, 60);
        //时间标记 轨道 音色
        noteTrack1.insertEvent(programChange);
        noteTrack1.insertNote(1, 43, 100, 0, 500);
        noteTrack1.insertNote(1, 43, 100, 500, 500);
        noteTrack1.insertNote(1, 44, 100, 1000, 500);
        noteTrack1.insertNote(1, 46, 100, 1500, 500);
        noteTrack1.insertNote(1, 46, 100, 2000, 500);
        noteTrack1.insertNote(1, 44, 100, 2500, 500);
        noteTrack1.insertNote(1, 43, 100, 3000, 500);
        noteTrack1.insertNote(1, 41, 100, 3500, 500);
        noteTrack1.insertNote(1, 39, 100, 4000, 500);
        noteTrack1.insertNote(1, 39, 100, 4500, 500);
        noteTrack1.insertNote(1, 41, 100, 5000, 500);
        noteTrack1.insertNote(1, 43, 100, 5500, 500);
        noteTrack1.insertNote(1, 43, 100, 6000, 750);
        noteTrack1.insertNote(1, 41, 100, 6750, 250);
        noteTrack1.insertNote(1, 41, 100, 7000, 500);


        tracks.add(noteTrack);
        tracks.add(noteTrack1);

        //在存储上创建MIDI文件（*.mid）
        MidiFile newMidiNoteFile = new MidiFile(MidiFile.DEFAULT_RESOLUTION, tracks);

        // File file = new File(ABSOLUTE_EXTERNAL_PATH, "note_c4.mid");
        //String pathToMidiNoteFile = MIDI_MUSIC_FILE + "/" +   "mynote.mid";
        final File output = new File(MIDI_MUSIC_FILE, "mynote.mid");
        try {
            newMidiNoteFile.writeToFile(output);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(e);
        }


    }

    public void createMidiMuiscInExternalStorage(StdScore stdScore){
        //Edit your music
        MIDI_MUSIC_FILE.mkdirs(); // create folder if not exists

        //以下为节拍信息 BPM beat per min  不过好像没啥子卵用  控制速度还是用我们的一套吧

        MidiTrack tempoTrack = new MidiTrack();
        TimeSignature timeSignature = new TimeSignature();
        timeSignature.setTimeSignature(4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION);

        Tempo tempo = new Tempo();
        if(stdScore != null){
            tempo.setBpm(stdScore.tempo);
        }
        else{
            tempo.setBpm(120);

        }










        //改变轨道音色  时间标记 轨道 音色
        ProgramChange programChange = new ProgramChange(0, 0,0);

        tempoTrack.insertEvent(timeSignature);
        tempoTrack.insertEvent(tempo);
        tempoTrack.insertEvent(programChange);

        ArrayList<MidiTrack> tracks = new ArrayList<>();
        tracks.add(tempoTrack);

        double perBeatTime_1_64 = 60000.0/(double)tempo.getBpm()/16;
        if(stdScore != null){
            for(int i=0;i<stdScore.musicTrack.size();i++) {
                //每插入一条轨道i++
                MidiTrack tempMidiTrack = new MidiTrack();
                ProgramChange tempProgramChange = new ProgramChange(0, i, stdScore.musicTrack.get(i).instrument);
                tempMidiTrack.insertEvent(tempProgramChange);

                int tempDuration = 0;
                for (int j = 0; j < stdScore.musicTrack.get(i).noteTrack.size(); j++) {
                    //每插入一个音符 j++
//                    int tempPitch = stdScore.musicTrack.get(i).noteTrack.get(j).pitch + 12 * stdScore.musicTrack.get(i).noteTrack.get(j).octave + 3;
                    int tempPitch = stdScore.musicTrack.get(i).noteTrack.get(j).absolutePosition;
                    int thisDuration =(int) (perBeatTime_1_64 * Math.pow( 2,stdScore.musicTrack.get(i).noteTrack.get(j).duration-2));
                    //tempMidiTrack.insertNote(i,tempPitch,stdScore.getVolume(),tempDuration,thisDuration);
                    tempMidiTrack.insertNote(i,tempPitch,stdScore.musicTrack.get(i).trackVolume,tempDuration,thisDuration);
                    tempDuration += thisDuration ;
                }
                //MIDI文件的一个轨道编辑结束 插入编辑好的轨道
                tracks.add(tempMidiTrack);
                Log.i(TAG,"stdScore.musicTrack["+i+"]add successfully" );
            }
        }
        else{
            Log.i(TAG,"stdNote Null References");
        }

  /*
        double perBeatTime_1_64 = 60000.0/(double)tempo.getBpm()/16;
        if(stdScore != null){
            for(int i=0;i<stdScore.numOfTrack;i++) {
                //每插入一条轨道i++
                MidiTrack tempMidiTrack = new MidiTrack();
                ProgramChange tempProgramChange = new ProgramChange(0, i, stdScore.instrument[i]);
                tempMidiTrack.insertEvent(tempProgramChange);

                int tempDuration = 0;
                for (int j = 0; j < stdScore.noteTrack[i].length; j++) {
                    //每插入一个音符 j++
                    int tempPitch = stdScore.noteTrack[i][j].pitch + 12 * stdScore.noteTrack[i][j].octave + 3;
                    int thisDuration =(int) (perBeatTime_1_64 * Math.pow( 2,stdScore.noteTrack[i][j].duration-2));
                    tempMidiTrack.insertNote(i,tempPitch,stdScore.getVolume(),tempDuration,thisDuration);
                    tempDuration += thisDuration ;
                }
                //MIDI文件的一个轨道编辑结束 插入编辑好的轨道
                tracks.add(tempMidiTrack);
            }
        }
        else{
            Log.i(TAG,"stdNote Null References");
        }
*/



        //在存储上创建MIDI文件（*.mid）
        MidiFile newMidiNoteFile = new MidiFile(MidiFile.DEFAULT_RESOLUTION, tracks);

        // File file = new File(ABSOLUTE_EXTERNAL_PATH, "note_c4.mid");
        String fileName;
        if(stdScore.scoreName != null){
            fileName = stdScore.scoreName;
        }
        else{
            SimpleDateFormat sdf = new SimpleDateFormat("hh-mm-ss");
            fileName = sdf.format(new java.util.Date());

        }
        String pathToMidiNoteFile = MIDI_MUSIC_FILE + "/" + fileName + ".mid";
        final File output = new File(MIDI_MUSIC_FILE,fileName + ".mid");
        try {
            newMidiNoteFile.writeToFile(output);
        }
        catch(IOException e) {
            e.printStackTrace();
            System.err.println(e);
        }
        Log.i(TAG,"createMidiMuiscInExternalStorage:"+fileName + "  create succcessfully!");


    }

    public void createMidiNoteInExternalStorage(String name,int noteMidiCode, int newInstrument) {

        // Create a path where we will place our midi files in the user's
        // MIDI_NOTES directory.  Note that you should be careful about
        // what you place here, since the user often manages these files.  For
        // pictures and other media owned by the application, consider
        // Context.getExternalMediaDir().
        MIDI_NOTE_FILE.mkdirs(); // create folder if not exists

        final String noteName = name;

        MidiTrack tempoTrack = new MidiTrack();
        TimeSignature timeSignature = new TimeSignature();
        timeSignature.setTimeSignature(4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION);

        Tempo tempo = new Tempo();
        tempo.setBpm(120);

        ProgramChange programChange = new ProgramChange(0, 0, newInstrument);

        tempoTrack.insertEvent(timeSignature);
        tempoTrack.insertEvent(tempo);
        tempoTrack.insertEvent(programChange);
        int pitch;
        int channel;
        int velocity = 100;
        long tick = 0;
        long duration = 5 * 60000; //5 minutes midi length
        long shortNoteDuration = 1000; // one second?

        ArrayList<MidiTrack> tracks = new ArrayList<>();
        tracks.add(tempoTrack);

        pitch = noteMidiCode;
        channel = 0;
        MidiTrack noteTrack = new MidiTrack();
        noteTrack.insertNote(channel, pitch, velocity, tick, shortNoteDuration);
        tracks.add(noteTrack);
        MidiFile newMidiNoteFile = new MidiFile(MidiFile.DEFAULT_RESOLUTION, tracks);

        // File file = new File(ABSOLUTE_EXTERNAL_PATH, "note_c4.mid");
        //String pathToMidiNoteFile = TESTFILE + "/" + noteName + ".mid";
        final File output = new File(MIDI_NOTE_FILE, noteName + ".mid");
        try {
            newMidiNoteFile.writeToFile(output);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(e);
        }

    }


    MidiFile getMidiNoteFileFromExternalStorage(String fileName) {
        // Create a path where we will place our midi file in the user's
        // MIDI_NOTES directory and return the file.  If external
        // storage is not currently mounted this will fail.

        MidiFile midiFile = null;
        // fileName example: "external_note_c4.mid"
        File file_to_get = new File(MIDI_NOTE_FILE, fileName);
        try {
            midiFile = new MidiFile(file_to_get);
        } catch (IOException e) {
            System.err.println("Error retrieving MIDI file: ");
            e.printStackTrace();
        }
        return midiFile;
    }

    void deleteMidiNoteFileFromExternalStorage(String fileName) {
        // Create a path where we will place our midi file in the user's
        // MIDI_NOTES directory and delete the file.  If external
        // storage is not currently mounted this will fail.

        // fileName example: "external_note_c4.mid"
        File file_to_delete = new File(MIDI_NOTE_FILE, fileName);
        file_to_delete.delete();
    }

    boolean existsMidiNoteFileInExternalStorage(String fileName) {
        // Create a path where we will place our midi file in the user's
        // MIDI_NOTES directory and check if the file exists.  If
        // external storage is not currently mounted this will think the
        // file doesn't exist.

        // fileName example: "external_note_c4.mid"
        File file_to_check = new File(MIDI_NOTE_FILE, fileName);
        return file_to_check.exists();
    }

    // Ex: 60 is c4
//    void createMidiNoteFileInExternalStorage(int noteMidiCode, int newInstrument) {
//        if(noteMidiCode!=88){
//            // Create a path where we will place our midi files in the user's
//            // MIDI_NOTES directory.  Note that you should be careful about
//            // what you place here, since the user often manages these files.  For
//            // pictures and other media owned by the application, consider
//            // Context.getExternalMediaDir().
//            ABSOLUTE_EXTERNAL_PATH.mkdirs(); // create folder if not exists
//
//            final String noteName = (Utils.midiNotes.get(noteMidiCode));
//
//            MidiTrack tempoTrack = new MidiTrack();
//            TimeSignature timeSignature = new TimeSignature();
//            timeSignature.setTimeSignature(4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION);
//
//            Tempo tempo = new Tempo();
//            tempo.setBpm(120);
//
//            ProgramChange programChange = new ProgramChange(0, 0, newInstrument);
//
//            tempoTrack.insertEvent(timeSignature);
//            tempoTrack.insertEvent(tempo);
//            tempoTrack.insertEvent(programChange);
//            int pitch;
//            int channel;
//            int velocity = 100;
//            long tick =  0;
//            long duration = 5 * 60000; //5 minutes midi length
//            long shortNoteDuration = 1000; // one second?
//
//            ArrayList<MidiTrack> tracks = new ArrayList<>();
//            tracks.add(tempoTrack);
//
//            pitch = noteMidiCode;
//            channel = 0;
//            MidiTrack noteTrack = new MidiTrack();
//            noteTrack.insertNote(channel, pitch, velocity, tick, shortNoteDuration);
//            tracks.add(noteTrack);
//            MidiFile newMidiNoteFile = new MidiFile(MidiFile.DEFAULT_RESOLUTION, tracks);
//
//            // File file = new File(ABSOLUTE_EXTERNAL_PATH, "note_c4.mid");
//            String pathToMidiNoteFile = ABSOLUTE_EXTERNAL_PATH + "/" + noteName + ".mid";
//            final File output = new File(ABSOLUTE_EXTERNAL_PATH, noteName + ".mid");
//            try {
//                newMidiNoteFile.writeToFile(output);
//            }
//            catch(IOException e) {
//                e.printStackTrace();
//                System.err.println(e);
//            }
//            // callback interface to load the note to soundPool
//            noteSavedListener.onNoteSaved(pathToMidiNoteFile, noteMidiCode);
//            Log.i(TAG, "createMidiNoteFileInExternalStorage: after NoteSavedListener");
//        }
//        else if(noteMidiCode==88){
//            // Create a path where we will place our midi files in the user's
//            // MIDI_NOTES directory.  Note that you should be careful about
//            // what you place here, since the user often manages these files.  For
//            // pictures and other media owned by the application, consider
//            // Context.getExternalMediaDir().
//            MUSIC_PATH.mkdirs(); // create folder if not exists
//
//            final String noteName = (Utils.midiNotes.get(noteMidiCode));
//
//            MidiTrack tempoTrack = new MidiTrack();
//            TimeSignature timeSignature = new TimeSignature();
//            timeSignature.setTimeSignature(4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION);
//
//            Tempo tempo = new Tempo();
//            tempo.setBpm(120);
//
//            ProgramChange programChange = new ProgramChange(0, 0, 0);
//
//            tempoTrack.insertEvent(timeSignature);
//            tempoTrack.insertEvent(tempo);
//            tempoTrack.insertEvent(programChange);
//            int pitch;
//            int channel;
//            int velocity = 100;
//            long tick =  0;
//            long duration = 5 * 600000; //5 minutes midi length
//            long shortNoteDuration = 1000; // one second?
//
//            ArrayList<MidiTrack> tracks = new ArrayList<>();
//            tracks.add(tempoTrack);
//
//
//            //自己添加的逻辑
//
//            pitch = noteMidiCode;
//            channel = 0;
//
//            MidiTrack noteTrack = new MidiTrack();
//            //noteTrack.insertNote(channel, pitch, velocity, tick, shortNoteDuration);
//            //频道 音 音量 时间标记 延时
//
//
//            programChange=new ProgramChange(0,0,0);
//            //时间标记 轨道 音色
//            noteTrack.insertEvent(programChange);
//
//
//
//            noteTrack.insertNote(0,43,100,0,500);
//            noteTrack.insertNote(0,43,100,500,500);
//            noteTrack.insertNote(0,44,100,1000,500);
//            noteTrack.insertNote(0,46,100,1500,500);
//            noteTrack.insertNote(0,46,100,2000,500);
//            noteTrack.insertNote(0,44,100,2500,500);
//            noteTrack.insertNote(0,43,100,3000,500);
//            noteTrack.insertNote(0,41,100,3500,500);
//            noteTrack.insertNote(0,39,100,4000,500);
//            noteTrack.insertNote(0,39,100,4500,500);
//            noteTrack.insertNote(0,41,100,5000,500);
//            noteTrack.insertNote(0,43,100,5500,500);
//            noteTrack.insertNote(0,43,100,6000,750);
//            noteTrack.insertNote(0,41,100,6750,250);
//            noteTrack.insertNote(0,41,100,7000,500);
//
//            noteTrack.insertNote(0,43,100,7500+0,500);
//            noteTrack.insertNote(0,43,100,7500+500,500);
//            noteTrack.insertNote(0,44,100,7500+1000,500);
//            noteTrack.insertNote(0,46,100,7500+1500,500);
//            noteTrack.insertNote(0,46,100,7500+2000,500);
//            noteTrack.insertNote(0,44,100,7500+2500,500);
//            noteTrack.insertNote(0,43,100,7500+3000,500);
//            noteTrack.insertNote(0,41,100,7500+3500,500);
//            noteTrack.insertNote(0,39,100,7500+4000,500);
//            noteTrack.insertNote(0,39,100,7500+4500,500);
//            noteTrack.insertNote(0,41,100,7500+5000,500);
//            noteTrack.insertNote(0,43,100,7500+5500,500);
//            noteTrack.insertNote(0,41,100,7500+6000,750);
//            noteTrack.insertNote(0,39,100,7500+6750,250);
//            noteTrack.insertNote(0,39,100,7500+7000,500);
//
//            MidiTrack noteTrack1 = new MidiTrack();
//            programChange=new ProgramChange(1,1,60);
//            //时间标记 轨道 音色
//            noteTrack1.insertEvent(programChange);
//            noteTrack1.insertNote(1,43,100,0,500);
//            noteTrack1.insertNote(1,43,100,500,500);
//            noteTrack1.insertNote(1,44,100,1000,500);
//            noteTrack1.insertNote(1,46,100,1500,500);
//            noteTrack1.insertNote(1,46,100,2000,500);
//            noteTrack1.insertNote(1,44,100,2500,500);
//            noteTrack1.insertNote(1,43,100,3000,500);
//            noteTrack1.insertNote(1,41,100,3500,500);
//            noteTrack1.insertNote(1,39,100,4000,500);
//            noteTrack1.insertNote(1,39,100,4500,500);
//            noteTrack1.insertNote(1,41,100,5000,500);
//            noteTrack1.insertNote(1,43,100,5500,500);
//            noteTrack1.insertNote(1,43,100,6000,750);
//            noteTrack1.insertNote(1,41,100,6750,250);
//            noteTrack1.insertNote(1,41,100,7000,500);
//
//
//
//            tracks.add(noteTrack);
//            tracks.add(noteTrack1);
//
//
//            MidiFile newMidiNoteFile = new MidiFile(MidiFile.DEFAULT_RESOLUTION, tracks);
//
//            // File file = new File(ABSOLUTE_EXTERNAL_PATH, "note_c4.mid");
//            String pathToMidiNoteFile = MUSIC_PATH + "/" + noteName + ".mid";
//            final File output = new File(MUSIC_PATH, noteName + ".mid");
//            try {
//                newMidiNoteFile.writeToFile(output);
//            }
//            catch(IOException e) {
//                e.printStackTrace();
//                System.err.println(e);
//            }
//            // callback interface to load the note to soundPool
////            noteSavedListener.onNoteSaved(pathToMidiNoteFile, noteMidiCode);
////            Log.i(TAG, "createMidiNoteFileInExternalStorage: after NoteSavedListener");
//
//        }
//
//
//    }

}

