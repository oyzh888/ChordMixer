package com.ftboys.ChordMixer;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pdrogfer.simplemidiplayer.R;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements  View.OnClickListener, OnNoteSavedToStorage {

    private static final String TAG = "MainActivity";
    private final int MAXSIZE=88;
    Button btnPlayNote, btnChangeInstrument,btnPlayWhole,btnTest;
    TextView testTextView;
    AudioManager audioManager;
    float volume;
    private SoundPool soundPool;
    ExternalStorageOperations externalStorageOperations;
    //int[] soundPooldIDs = new int[89];
    private boolean soundPoolIsLoaded = false;
    HashMap<Integer,Integer> soundPoolMap = new HashMap<Integer, Integer>();
    private MediaPlayer mp ;
    final File MIDI_NOTE_FILE = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_MUSIC) + "/MIDI_NOTES/");
    final File MIDI_MUSIC_FILE = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_MUSIC) + "/MIDI_MUSIC/");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        initGUI();
        initMusicDevice();

    }

    private void initMusicDevice(){
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        volume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        externalStorageOperations = new ExternalStorageOperations(this,this);
        //createAllNoteMidiFiles(32); // some default instrument
        initSoundPool();
        buildSingleMIDIFile(0);
        loadSoundPool();
        externalStorageOperations.createTestMusic();
    }
    private void initGUI() {

        btnPlayNote = (Button) findViewById(R.id.btn_play_note);
        btnChangeInstrument = (Button) findViewById(R.id.btnChangeInstrument);
        //FJC添加
        btnPlayWhole=(Button)findViewById(R.id.btn_play_whole_music);
        btnTest = (Button)findViewById(R.id.testButton);

        testTextView = (TextView)findViewById(R.id.textView);


        btnPlayNote.setOnClickListener(this);
        btnChangeInstrument.setOnClickListener(this);
        btnPlayWhole.setOnClickListener(this);
        btnTest.setOnClickListener(this);



    }
    //如果你写事件  你一定会看到这里  留给OYZH的HINT
    //void createMidiMuiscInExternalStorage(StdScore stdScore)创建自己的*.mid文件  这里面最怕你的stdScore中信息不全  发生空引用 虽然我已经做好防备了
    //上面已经声明了操作的对象externalStorageOperations  所以你直接使用externalStorageOperations.createMidiMuiscInExternalStorage(StdScore stdScore);
    //创建好了会有LOG的提示   文件名+ 创建成功
    //测试你创建的文件能否播放  用的是public void playWholeMusic(String musicName) 这个是直接写在MainActivity里面的  可以直接调用

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_play_note:
                final int newNoteMidiCode = 60;
                Thread MyThread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        playMIDINote(newNoteMidiCode);
                    }
                });
                MyThread.start();

                break;
            case R.id.btnChangeInstrument:
                int newInstrumentMidiCode = (int) (Math.random()*126+1);
                changeMidiNotesInstrument(newInstrumentMidiCode);
                Toast.makeText(this, "change instrument successfully", Toast.LENGTH_SHORT).show();
//                Log.i("ExternalStorage", "onClick: Instrument Change to " + newInstrumentMidiCode);
                break;
            case R.id.btn_play_whole_music:
                playWholeMusic("mynote");

                break;
            case R.id.testButton:

                testTextView.setText(MIDI_NOTE_FILE.toString()+"\n"+MIDI_MUSIC_FILE+ "\n"+Environment.getExternalStorageState());

                playMIDINote(65);

                break;




        }
    }



    public void playWholeMusic(String musicName){
        //播放整首乐曲  默认路径为/sdcard/Music/MIDI_MUSIC/*.mid
        try {
            mp = new MediaPlayer();
            mp.setDataSource( MIDI_MUSIC_FILE +"/"+ musicName + ".mid");
            mp.prepare();
            mp.start();

        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mp.release();
            }
        });

    }

    private void initSoundPool() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }

        soundPool = new SoundPool(16, AudioManager.STREAM_MUSIC,100);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder().build();
        }
    }



    public void buildSingleMIDIFile(int instrument){
        //生成midi单音文件
        initSoundPool();
        for(int i=0;i<MAXSIZE;i++){
            externalStorageOperations.createMidiNoteInExternalStorage(String.valueOf(i),i,instrument);
        }
    }

    public void loadSoundPool(){
        for(int i=0;i<MAXSIZE;i++){
            soundPoolMap.put(i,soundPool.load(MIDI_NOTE_FILE.toString()+"/"+i+".mid",1));
            if(i==MAXSIZE-1){
                soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                        soundPoolIsLoaded = true;
                        Log.i(TAG, "loadSoundPool: onLoadComplete:soundPoolIsLoaded");
                    }
                });
            }
        }

    }

    public void playMIDINote(int noteMidiCode) {
        // TODO: 20/07/2016 PROBLEM!!! ASSOCIATE NOTE MIDI CODES TO MY SOUNDS IDS
        if (soundPoolIsLoaded) {
            soundPool.play(soundPoolMap.get(noteMidiCode), volume, volume, 1, 0, 1f);
        } else {
            Log.i(TAG, "playNote: SoundPool not loaded");
        }
    }

    public void changeMidiNotesInstrument(int newInstrument) {
        // Call buildSingleMIDIFile with the new instrument
        // Call reLoadSoundPool to reload
        buildSingleMIDIFile(newInstrument);
        loadSoundPool();
    }

//    public void createAllNoteMidiFiles(int instrument) {
//        // TODO: 21/07/2016 first delete old notes if exist
//
//        // destroy and restart soundPool
//        initSoundPool();
//        // loop over Utils.midiNotes and create all
//        for (int midiCode = 0; midiCode < Utils.midiNotes.size(); midiCode++) {
//            externalStorageOperations.createMidiNoteFileInExternalStorage(midiCode, instrument);
//        }
//
//
//    }



    @Override
    public void onNoteSaved(String pathToNote, final int noteMidiCode) {
        //目前仅仅作为接口保留  以下功能已被重写
//        // soundPool load happens here, once every note has been saved to external storage
//        // soundPool.load has an overload that takes the path, and the priority (default, 1)
//        soundPooldIDs[noteMidiCode] = soundPool.load(pathToNote, 1);
//        Log.i(TAG, "onNoteSaved: loading note " + noteMidiCode);
//        if (noteMidiCode == 87) {
//        }
//        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
//            @Override
//            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
//                soundPoolIsLoaded = true;
//                Log.i(TAG, "onLoadComplete: soundPool loaded note" + noteMidiCode);
//            }
//        });
    }

//    public void playNote(int noteMidiCode) {
//        // TODO: 20/07/2016 PROBLEM!!! ASSOCIATE NOTE MIDI CODES TO MY SOUNDS IDS
//        if (soundPoolIsLoaded) {
//            soundPool.play(soundPooldIDs[noteMidiCode], volume, volume, 1, 0, 1f);
//        } else {
//            Log.i(TAG, "playNote: SoundPool not loaded");
//        }
//    }
//
//
//    private void loadSoundsToSoundPool() {
//        // The actual loading takes place in 'onNoteSaved' (interface callback)
//    }




}

