package com.oyzh.ichord;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.*;

import com.ftboys.ChordMixer.ChordMixerAlgorithm.*;
import com.mp3player.ScanMusic;
import com.pdrogfer.simplemidiplayer.R;

import com.ftboys.ChordMixer.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnNoteSavedToStorage {


    //region 界面控件 UI Components
    private final int pianoKeyNum = 36;
    ImageView pianoImage[] = new ImageView[pianoKeyNum];
    TextView inputTextMain, textMainChord, volumeValue, speedValue;
    //东西们
    //MGR 2016-8-11
    //Fragment Project
    private RelativeLayout ly_content;
    private TextView tab1, tab2, tab3, tab4;
    private View contentView;
    //主音轨按钮
    private Button instrumentMain;
    //MGR 2016-8-13
    //设置们
    private SeekBar volumeSetting,speedSetting;

    private Button tonedown, toneup;

    //endregion s

    //region 界面依赖 UI reliances
    //曲库部分
    private ScanMusic scanMusic;
    private ArrayList<HashMap<String, String>> musicList = new ArrayList<HashMap<String, String>>();
    ArrayAdapter<String> adapter;
    ArrayList<String> songs=new ArrayList<>();
    ArrayList<String> paths=new ArrayList<>();
    ListView listView;

    private String str = null;


    //OYZH Added
    /*
    调用midi部分代码
     */
    private static final String TAG = "MainActivity";
    private final int MAXSIZE=88;
    AudioManager audioManager;
    float volume;
    private SoundPool soundPool;
    ExternalStorageOperations externalStorageOperations;
    private boolean soundPoolIsLoaded = false;
    HashMap<Integer,Integer> soundPoolMap = new HashMap<Integer, Integer>();
    private MediaPlayer mp ;
    final File MIDI_NOTE_FILE = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_MUSIC) + "/MIDI_NOTES/");
    final File MIDI_MUSIC_FILE = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_MUSIC) + "/MIDI_MUSIC/");


    ChordCalculator chordMixerCalculator = new ChordCalculator();
    //界面音乐变量
    private StdScore mainScore = new StdScore();
    private StdPattent mainPattent = new StdPattent();

    private int mainInputDuration = 6;

    //基本函数
    public void clickPianoKey(int btnId){
        int bias = 50;
        btnId += bias;
        //播放钢琴单音
        playMIDINote(btnId );
        StdNote addedNote = new StdNote(btnId);
        addedNote.duration = mainInputDuration;
        mainScore.musicTrack.get(0).noteTrack.add(addedNote);

        //setMelody(addedNote.stdToStringNote());
        //刷新界面TextView
        refreshInputTextMain();
        //inputTextMain.setText("SADDSDSADSADSADSA");

    }

    //更换轨道和弦节奏型
    public void changeTrackPattent(int trackId, int pattentId) throws Exception{

        if(trackId > mainScore.musicTrack.size()|| trackId < 0){
            throw new Exception("The trackId is Out of Range");
        }
        if(pattentId > mainPattent.defChordNum || pattentId < 0){
            throw new Exception("The pattentId is Out of Range");
        }
        mainScore.musicTrack.get(trackId).trackPattent = pattentId;

    }

    //endregion

    //region 界面方法 UI methods
    //TODO
    //更换乐器
    public void changeInstrument(int trackId, int insId){
        mainScore.musicTrack.get(trackId).instrument=insId;
    }
    //改变音量
    public void changeVolume(int volumeValue){
        mainScore.setVolume(volumeValue);
    }
    //改变速度
    public void changeTempo(float tempoValue){
        mainScore.tempo = tempoValue;
    }
    //刷新显示inputTextView界面
    public void refreshInputTextMain(){
        String str = "";
        for(StdNote tmp : mainScore.musicTrack.get(0).noteTrack){
            str += tmp.name + tmp.octave +  "  " ;
        }
        inputTextMain.setText(str);
    }
    public void refreshTextMainChord(){
        String str = "";
        for(StdChord tmp : mainScore.chordTrack){
            str += tmp.chordName + " ";
        }
        textMainChord.setText(str);
    }
    //处理曲谱,得到和弦 及 伴奏演奏模式
    public void chordMixerScoreHandle(){
        //得到和弦
        mainScore = chordMixerCalculator.scoreHandler(mainScore);
        //解释和弦,生成伴奏
        mainScore = mainPattent.scoreChordHandler(mainScore);
        //刷新和弦显示界面
        //refreshTextMainChord();
    }

    //微信分享
    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle("标题");
        // titleUrl是标题的网络链接，QQ和QQ空间等使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

        // 启动分享GUI
        oks.show(this);
    }
    //endregion

    //region 界面绑定相关
    //按钮事件绑定
    public void setButtonEvent(){
        for(int i= 1; i < pianoKeyNum; i++){
            pianoImage[i].setTag(i);

            pianoImage[i].setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    if (motionEvent == null || motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                        clickPianoKey( (int)view.getTag() );

                    }
                    return false;
                }
            });
        }

        //休止符事件
        pianoImage[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StdNote addedNote = new StdNote(-1);
                addedNote.duration = mainInputDuration;
                mainScore.musicTrack.get(0).noteTrack.add(addedNote);
                //setMelody(addedNote.stdToStringNote());
            }
        });

        //Play ImageButton
        findViewById(R.id.btn_play_whole_music).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                //Toast.makeText(MainActivity.this,"Test for play",Toast.LENGTH_SHORT).show();
                chordMixerScoreHandle();//获取和弦
                //自动强制降低伴奏轨音量
                for(int i=1; i<mainScore.musicTrack.size(); i++){
                    mainScore.musicTrack.get(i).trackVolume = 30;
                }
                refreshTextMainChord();
                //setChord(mainScore.scoreToChord());
                externalStorageOperations.createMidiMuiscInExternalStorage(mainScore);
                playWholeMusic(mainScore.scoreName);

            }
        });

        //Pause ImageButton
        findViewById(R.id.btn_pause).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"Hi Tpai!~",Toast.LENGTH_SHORT);
                //setChord("Am C G7 Dm Emmmm oyzh");
            }
        });

        //Stop Button
        findViewById(R.id.btn_stop).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mainScore.initScore();
                //clearAll();
                //showShare();
            }
        });

        //临时测试按钮------------------------!!!!
        findViewById(R.id.testButton1).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                chordMixerScoreHandle();//获取和弦

                for(int i=1; i<mainScore.musicTrack.size(); i++){
                    mainScore.musicTrack.get(i).trackVolume = 30;
                }

                externalStorageOperations.createMidiMuiscInExternalStorage(mainScore);

            }
        });


        findViewById(R.id.testButton2).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //String str1 =  mainScore.scoreToStringNotes(), str2 = mainScore.scoreToChord();
                //setMelody(str1);
                //setChord(str2);
//                setMelody("A504 A504");
//                setChord("C C");

            }
        });
        //测试清除所有曲谱
        pianoImage[35].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                mainScore.initScore();
                //clearAll();
                refreshTextMainChord();
                refreshInputTextMain();
            }
        });

        //测试曲谱和弦生成的音符
//        pianoImage[34].setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view){
//                mainScore = mainPattent.scoreChordHandler(mainScore);
//                String str = "";
//                for(StdNote tmp : mainScore.musicTrack.get(1).noteTrack){
//                    str += tmp.name + tmp.octave() + " ";
//                }
//                textMainChord.setText(str);
//            }
//
//        });

//        //测试微信分享
//        pianoImage[33].setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(),"successfully",Toast.LENGTH_SHORT).show();
//                showShare();
//            }
//        });

    }

    //绑定界面按钮->变量
    public void initWhenCreate(){

        inputTextMain = (TextView)findViewById(R.id.textViewForInput);
        textMainChord = (TextView)findViewById(R.id.textViewForChord);
        //Piano Keys
        pianoImage[0]  = (ImageView)findViewById(R.id.s00_btn);
        pianoImage[1]  = (ImageView)findViewById(R.id.s31_btn);
        pianoImage[2]  = (ImageView)findViewById(R.id.s32_btn);
        pianoImage[3]  = (ImageView)findViewById(R.id.s33_btn);
        pianoImage[4]  = (ImageView)findViewById(R.id.s34_btn);
        pianoImage[5]  = (ImageView)findViewById(R.id.s35_btn);
        pianoImage[6]  = (ImageView)findViewById(R.id.s36_btn);
        pianoImage[7]  = (ImageView)findViewById(R.id.s37_btn);
        pianoImage[8]  = (ImageView)findViewById(R.id.s38_btn);
        pianoImage[9]  = (ImageView)findViewById(R.id.s39_btn);
        pianoImage[10] = (ImageView)findViewById(R.id.s40_btn);
        pianoImage[11] = (ImageView)findViewById(R.id.s41_btn);
        pianoImage[12] = (ImageView)findViewById(R.id.s42_btn);
        pianoImage[13] = (ImageView)findViewById(R.id.s43_btn);
        pianoImage[14] = (ImageView)findViewById(R.id.s44_btn);
        pianoImage[15] = (ImageView)findViewById(R.id.s45_btn);
        pianoImage[16] = (ImageView)findViewById(R.id.s46_btn);
        pianoImage[17] = (ImageView)findViewById(R.id.s47_btn);
        pianoImage[18] = (ImageView)findViewById(R.id.s48_btn);
        pianoImage[19] = (ImageView)findViewById(R.id.s49_btn);
        pianoImage[20] = (ImageView)findViewById(R.id.s50_btn);
        pianoImage[21] = (ImageView)findViewById(R.id.s51_btn);
        pianoImage[22] = (ImageView)findViewById(R.id.s52_btn);
        pianoImage[23] = (ImageView)findViewById(R.id.s53_btn);
        pianoImage[24] = (ImageView)findViewById(R.id.s54_btn);
        pianoImage[25] = (ImageView)findViewById(R.id.s55_btn);
        pianoImage[26] = (ImageView)findViewById(R.id.s56_btn);
        pianoImage[27] = (ImageView)findViewById(R.id.s57_btn);
        pianoImage[28] = (ImageView)findViewById(R.id.s58_btn);
        pianoImage[29] = (ImageView)findViewById(R.id.s59_btn);
        pianoImage[30] = (ImageView)findViewById(R.id.s60_btn);
        pianoImage[31] = (ImageView)findViewById(R.id.s61_btn);
        pianoImage[32] = (ImageView)findViewById(R.id.s62_btn);
        pianoImage[33] = (ImageView)findViewById(R.id.s63_btn);
        pianoImage[34] = (ImageView)findViewById(R.id.s64_btn);
        pianoImage[35] = (ImageView)findViewById(R.id.s65_btn);


        //MGR 2016-8-11 Added
        //底部菜单栏
//        tab1 = (TextView) findViewById(R.id.tab1);
//        tab2 = (TextView) findViewById(R.id.tab2);
//        tab3 = (TextView) findViewById(R.id.tab3);
//        tab4 = (TextView) findViewById(R.id.tab4);

//        tab1.setOnClickListener(this);
//        tab2.setOnClickListener(this);
//        tab3.setOnClickListener(this);
//        tab4.setOnClickListener(this);

        //顶部按键

        //切换Fragment用
//        ly_content = (RelativeLayout) findViewById(R.id.ly_content);
//        contentView = findViewById(R.id.content_main);

        //MGR 2016-8-12 Added
        //绑定主音轨音色按钮
        //instrumentMain = (Button) findViewById(R.id.track_0_instrument);
        //mainTrackSetInstrument();
        setButtonEvent();
    }

    //Button Event备用写法:
    class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                //Function added by OYZH
                case R.id.s00_btn:
                    Toast.makeText( MainActivity.this,"OYZH's Test",Toast.LENGTH_SHORT).show();
                default:
                    break;
            }
        }
    }
    //MGR 2016-8-13 Added
    //页面切换相关
    private void hideAllShows(){
        findViewById(R.id.screen_content).setVisibility(View.INVISIBLE);
        findViewById(R.id.screen_chordsetting).setVisibility(View.INVISIBLE);
        findViewById(R.id.screen_music_control).setVisibility(View.INVISIBLE);
        findViewById(R.id.screen_gallery).setVisibility(View.INVISIBLE);
    }

    //MGR 2016-8-13 Added
    //设置相关
    //各项设置的默认值
    private void defaultSetting(){
        //MGR 2016-8-13 Added
        //绑定设置页面
        volumeSetting = (SeekBar) findViewById(R.id.seekBar_volume);
        volumeValue = (TextView) findViewById(R.id.txtlinkto_seekBar_volume);
        speedSetting = (SeekBar) findViewById(R.id.seekBar_speed);
        speedValue = (TextView) findViewById(R.id.txtlinkto_seekBar_speed);
        tonedown = (Button) findViewById(R.id.button_leveldown);
        toneup = (Button) findViewById(R.id.button_levelup);

        //音量设置
        volumeSetting.setMax(100);//最大值
        volumeSetting.setProgress(100);//默认值
        volumeValue.setText(Integer.toString(volumeSetting.getProgress()));
        volumeSetting.setOnSeekBarChangeListener(new VolumeSettingChangedListener());

        //速度设置
        speedSetting.setMax(180);//最大值
        speedSetting.setProgress(100);//默认值
        speedValue.setText(Integer.toString(speedSetting.getProgress()));
        speedSetting.setOnSeekBarChangeListener(new SpeedSettingChangedListener());

        //调式设置
        //还没有弄23333
    }
    //音量设置相关
    public class VolumeSettingChangedListener implements SeekBar.OnSeekBarChangeListener{
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            volumeValue.setText(Integer.toString(progress));
            //这里写设置最终音量的具体方法调用，progress为具体进度值，还没写
            mainScore.setVolume(progress);

        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //啥都不写
        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //啥都不写
        }
    }

    public class SpeedSettingChangedListener implements SeekBar.OnSeekBarChangeListener{
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            speedValue.setText(Integer.toString(progress));
            //这里写设置速度值的具体方法调用，progress为具体进度值，还没写
            mainScore.tempo = (float) progress;
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            //啥都不写
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            //啥都不写
        }
    }

    //End of MGR's Code
    //endregion

    //region Midi初始化
    //FJC's Midi
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
    //End of FJC's Midi
    //endregion

    //region 界面默认初始化函数
    //MGR's Code 2016/8/4
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Added by OYZH
        initWhenCreate();
        initMusicDevice();

        //初始化微信分享
        ShareSDK.initSDK(MainActivity.this);

        //MGR 2016-8-13 Added
        //绑定设置页面相关
        defaultSetting();


        //Oyk's code

        //init();
        //test();
        //WIDTH = contentView.getWidth()/64;

        //曲库显示
        listView = (ListView) findViewById(R.id.listView2);
        scanMusic = new ScanMusic();
        musicList = scanMusic.getSong();

        for (int i = 0; i < musicList.size(); i++) {
            songs.add(i, musicList.get(i).get("songTitle"));
            paths.add(i, musicList.get(i).get("songPath"));
        }

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, songs);

        //TODO Delete
        //MGR 底部按钮切换
       // setSelected();
       // tab1.performClick();

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        hideAllShows();
        int id = item.getItemId();
        if(id==R.id.nav_playsetting){
            findViewById(R.id.screen_music_control).setVisibility(View.VISIBLE);
        }
        else if(id==R.id.nav_import){
            findViewById(R.id.screen_content).setVisibility(View.VISIBLE);
        }
        else if(id==R.id.nav_chordsetting){
            findViewById(R.id.screen_chordsetting).setVisibility(View.VISIBLE);
        }
        else if(id==R.id.nav_gallery){
            findViewById(R.id.screen_gallery).setVisibility(View.VISIBLE);
//            scanMusic = new ScanMusic();
//            musicList = scanMusic.getSong();
//
//            for (int i = 0; i < musicList.size(); i++) {
//                songs.add(i, musicList.get(i).get("songTitle"));
//                paths.add(i, musicList.get(i).get("songPath"));
//            }

//            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, songs);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    str = musicList.get(position).get("songPath");

                    Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();

//                    showShare();
                }
            });
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //endregion

    //region 曲谱显示 OYK's Code


    //----test
    EditText et1;
    EditText et2;
    Button btn;

    //----default const
    int HEIGHT=40;//default msp and csp height
    int UHEIGHT=0;
    int DHEIGHT=0;

    int WIDTH = 18;//default label width
    LinearLayout.LayoutParams forsp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,HEIGHT);
    LinearLayout.LayoutParams forubp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,UHEIGHT);
    LinearLayout.LayoutParams fordbp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,DHEIGHT);
    LinearLayout.LayoutParams forlb=new LinearLayout.LayoutParams(WIDTH, ViewGroup.LayoutParams.FILL_PARENT);
    LinearLayout.LayoutParams forimage=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
    int MAX = 100;
    int pr = 4;
    int MAXCNT_MELODY = pr * 16 + pr - 1;
    int MAXCNT_CHORD = 15 * pr + pr - 1;
    double width = 320;//default
    double melodyheight = 27;//default旋律图片高度
    double chordheight = 27;//default
    double height = 27;
    float Tsize=11;
    double upheight = 6;//旋律图片上压缩距离
    double downheight = 0;
    int chordfontsize = 15;//和弦字母大小

    //----locationflag
    private int[] cnt_melody = new int[MAX];
    private int[] cnt_chord = new int[MAX];
    private int melodyloc;
    private int chordloc;
    private int sploc;
    private int hlmelody1 = 0;//locate high light melody location
    private int hlmelody2 = 0;
    public int thlmelody;//location of the high light melody for transferring
    public int thlchord;
    private int hlchord1;//locate high light chord location
    private int hlchord2;
    private int chordchoice=0;

    TextView[][] chordhl=new TextView[MAX][MAXCNT_MELODY];
    TextView[][] melodyhl=new TextView[MAX][MAXCNT_MELODY];
    TextView[][] melodylabel=new TextView[MAX][MAXCNT_MELODY];
    TextView[][] chordlabel=new TextView[MAX][MAXCNT_MELODY];
    ImageView[] imagepic=new ImageView[1000];
    Drawable[] dpic=new Drawable[1000];

    private LinearLayout[] msp;
    private LinearLayout[] csp;
    private LinearLayout[] blanksp;
    private GridLayout[] mg=new GridLayout[MAX];
    private GridLayout[] cg=new GridLayout[MAX];

    String curstr;

    LinearLayout mainsp;

    private void test()
    {
//        et1=(EditText)findViewById(R.id.text1);
//        et2=(EditText)findViewById(R.id.text2);
//        btn=(Button)findViewById(R.id.btn);
//
//        btn.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v){
//                String str1 = et1.getText().toString();
//                String str2 = et2.getText().toString();
//                //clearAll();
//                setMelody(str1);
//                setChord(str2);
//
//            }
//        });
    }



    private void init()
    {
        initData();
        mainsp=(LinearLayout)findViewById(R.id.mainsp);
        msp = new LinearLayout[MAX];
        csp = new LinearLayout[MAX];
        blanksp = new LinearLayout[2 * MAX];
        for (int i = 0; i < MAX; i++)
        {
            msp[i] = new LinearLayout(this);
            //height
            csp[i] = new LinearLayout(this);
            //height
            blanksp[2 * i] = new LinearLayout(this);
            blanksp[2 * i + 1] = new LinearLayout(this);
            //height
        }
        for (int i = 0; i < MAX; i++)
            for (int j = 0; j < MAXCNT_MELODY; j++)
            {
                melodylabel[i][j] = new TextView(this);
                //melodylabel[i][j].HorizontalContentAlignment = HorizontalAlignment.Center;
                //melodylabel[i][j].VerticalContentAlignment = VerticalAlignment.Center;
                //melodylabel[i][j].Height = melodyheight;
                //melodylabel[i][j].HorizontalAlignment = HorizontalAlignment.Stretch;
                //melodylabel[i][j].VerticalAlignment = VerticalAlignment.Stretch;
                //melodylabel[i][j].Margin = new Thickness(0, 0, 0, 0);
                melodyhl[i][j] = new TextView(this);
                //melodyhl[i][j].MouseDown += melodyhl_MouseDown;
                //melodyhl[i][j].Background = new SolidColorBrush(highlight);
                //melodyhl[i][j].Opacity = 0;
            }
        for (int i = 0; i < MAX; i++)
            for (int j = 0; j < MAXCNT_MELODY; j++)
            {
                chordlabel[i][j]=new TextView(this);
                chordhl[i][j]=new TextView(this);
                chordlabel[i][j].setTextSize(Tsize);
                /*
                chordcb[i, j] = new ComboBox();
                chordcb[i, j].HorizontalContentAlignment = HorizontalAlignment.Left;
                chordcb[i, j].VerticalContentAlignment = VerticalAlignment.Center;
                chordcb[i, j].Height = chordheight;
                chordcb[i, j].HorizontalAlignment = HorizontalAlignment.Stretch;
                chordcb[i, j].VerticalAlignment = VerticalAlignment.Stretch;
                chordcb[i, j].Margin = new Thickness(0, 0, 0, 0);
                chordcb[i, j].FontSize = chordfontsize;
                chordcb[i, j].MouseEnter += chordcb_MouseEnter;
                chordcb[i, j].MouseLeave += chordcb_MouseLeave;
                */
                //chordcb[i, j].ContextMenuClosing += chordcb_ContextMenuClosing;
                //chordcb[i, j].SelectionChanged += chordcb_SelectionChanged;

                    /*
                    chordcb[i, j].Background=new SolidColorBrush(Color.FromRgb(189,189,189));
                    chordcb[i, j].MouseDown += chordcb_MouseDown;
                    chordcb[i, j].Foreground = new SolidColorBrush(Color.FromRgb(207, 207, 207));
                    chordcb[i, j].BorderThickness = new Thickness(0, 0, 0, 0);
                    chordcb[i, j].MouseEnter += chordcb_MouseEnter;
                    */
                //chordcb[i, j].Style = Resources["ComboBox-ChordShow"] as Style;
                /*
                chordcb[i, j].Style = Resources["ComboBox"] as Style;

                chordcb[i, j].Items.Add("C");
                chordcb[i, j].Items.Add("Dm");
                chordcb[i, j].Items.Add("Em");
                chordcb[i, j].Items.Add("F");
                chordcb[i, j].Items.Add("G");
                chordcb[i, j].Items.Add("Am");
                chordcb[i, j].Items.Add("G7");
                chordcb[i, j].SelectedIndex = 0;
                */
            }

        for (int i = 0; i < 1000; i++)
        {
            imagepic[i] = new ImageView(this);
            imagepic[i].setLayoutParams(forimage);
            /*
            String filename=String.format("a%1$d.png",i);
            Bitmap bm= BitmapFactory.decodeResource(this.getResources(),filename);
            imagepic[i].setImageBitmap(bm);
            */
            //imagepic[i].ImageSource = new BitmapImage(new Uri(String.Format("images/{0}.png", i), UriKind.Relative));
            //index first 22 for basic melody pic.22 for 8. 22 for 16;then 66 for - then 67 for 0 68 for 0/ 69 for 0// 70 for _ 71 for blank 72 for |
        }
        imagepic[0].setImageResource(R.drawable.a0);
        imagepic[1].setImageResource(R.drawable.a1);
        imagepic[2].setImageResource(R.drawable.a2);
        imagepic[3].setImageResource(R.drawable.a3);
        imagepic[4].setImageResource(R.drawable.a4);
        imagepic[5].setImageResource(R.drawable.a5);
        imagepic[6].setImageResource(R.drawable.a6);
        imagepic[7].setImageResource(R.drawable.a7);
        imagepic[8].setImageResource(R.drawable.a8);
        imagepic[9].setImageResource(R.drawable.a9);
        imagepic[10].setImageResource(R.drawable.a10);
        imagepic[11].setImageResource(R.drawable.a11);
        imagepic[12].setImageResource(R.drawable.a12);
        imagepic[13].setImageResource(R.drawable.a13);
        imagepic[14].setImageResource(R.drawable.a14);
        imagepic[15].setImageResource(R.drawable.a15);
        imagepic[16].setImageResource(R.drawable.a16);
        imagepic[17].setImageResource(R.drawable.a17);
        imagepic[18].setImageResource(R.drawable.a18);
        imagepic[19].setImageResource(R.drawable.a19);
        imagepic[20].setImageResource(R.drawable.a20);
        imagepic[21].setImageResource(R.drawable.a21);
        imagepic[22].setImageResource(R.drawable.a22);
        imagepic[23].setImageResource(R.drawable.a23);
        imagepic[24].setImageResource(R.drawable.a24);
        imagepic[25].setImageResource(R.drawable.a25);
        imagepic[26].setImageResource(R.drawable.a26);
        imagepic[27].setImageResource(R.drawable.a27);
        imagepic[28].setImageResource(R.drawable.a28);
        imagepic[29].setImageResource(R.drawable.a29);
        imagepic[30].setImageResource(R.drawable.a30);
        imagepic[31].setImageResource(R.drawable.a31);
        imagepic[32].setImageResource(R.drawable.a32);
        imagepic[33].setImageResource(R.drawable.a33);
        imagepic[34].setImageResource(R.drawable.a34);
        imagepic[35].setImageResource(R.drawable.a35);
        imagepic[36].setImageResource(R.drawable.a36);
        imagepic[37].setImageResource(R.drawable.a37);
        imagepic[38].setImageResource(R.drawable.a38);
        imagepic[39].setImageResource(R.drawable.a39);
        imagepic[40].setImageResource(R.drawable.a40);
        imagepic[41].setImageResource(R.drawable.a41);
        imagepic[42].setImageResource(R.drawable.a42);
        imagepic[43].setImageResource(R.drawable.a43);
        imagepic[44].setImageResource(R.drawable.a44);
        imagepic[45].setImageResource(R.drawable.a45);
        imagepic[46].setImageResource(R.drawable.a46);
        imagepic[47].setImageResource(R.drawable.a47);
        imagepic[48].setImageResource(R.drawable.a48);
        imagepic[49].setImageResource(R.drawable.a49);
        imagepic[50].setImageResource(R.drawable.a50);
        imagepic[51].setImageResource(R.drawable.a51);
        imagepic[52].setImageResource(R.drawable.a52);
        imagepic[53].setImageResource(R.drawable.a53);
        imagepic[54].setImageResource(R.drawable.a54);
        imagepic[55].setImageResource(R.drawable.a55);
        imagepic[56].setImageResource(R.drawable.a56);
        imagepic[57].setImageResource(R.drawable.a57);
        imagepic[58].setImageResource(R.drawable.a58);
        imagepic[59].setImageResource(R.drawable.a59);
        imagepic[60].setImageResource(R.drawable.a60);
        imagepic[61].setImageResource(R.drawable.a61);
        imagepic[62].setImageResource(R.drawable.a62);
        imagepic[63].setImageResource(R.drawable.a63);
        imagepic[64].setImageResource(R.drawable.a64);
        imagepic[65].setImageResource(R.drawable.a65);
        imagepic[66].setImageResource(R.drawable.a66);
        imagepic[67].setImageResource(R.drawable.a67);
        imagepic[68].setImageResource(R.drawable.a68);
        imagepic[69].setImageResource(R.drawable.a69);
        imagepic[70].setImageResource(R.drawable.a70);
        imagepic[71].setImageResource(R.drawable.a71);
        imagepic[72].setImageResource(R.drawable.a72);
        dpic[0]=getResources().getDrawable(R.drawable.a0);
        dpic[1]=getResources().getDrawable(R.drawable.a1);
        dpic[2]=getResources().getDrawable(R.drawable.a2);
        dpic[3]=getResources().getDrawable(R.drawable.a3);
        dpic[4]=getResources().getDrawable(R.drawable.a4);
        dpic[5]=getResources().getDrawable(R.drawable.a5);
        dpic[6]=getResources().getDrawable(R.drawable.a6);
        dpic[7]=getResources().getDrawable(R.drawable.a7);
        dpic[8]=getResources().getDrawable(R.drawable.a8);
        dpic[9]=getResources().getDrawable(R.drawable.a9);
        dpic[10]=getResources().getDrawable(R.drawable.a10);
        dpic[11]=getResources().getDrawable(R.drawable.a11);
        dpic[12]=getResources().getDrawable(R.drawable.a12);
        dpic[13]=getResources().getDrawable(R.drawable.a13);
        dpic[14]=getResources().getDrawable(R.drawable.a14);
        dpic[15]=getResources().getDrawable(R.drawable.a15);
        dpic[16]=getResources().getDrawable(R.drawable.a16);
        dpic[17]=getResources().getDrawable(R.drawable.a17);
        dpic[18]=getResources().getDrawable(R.drawable.a18);
        dpic[19]=getResources().getDrawable(R.drawable.a19);
        dpic[20]=getResources().getDrawable(R.drawable.a20);
        dpic[21]=getResources().getDrawable(R.drawable.a21);
        dpic[22]=getResources().getDrawable(R.drawable.a22);
        dpic[23]=getResources().getDrawable(R.drawable.a23);
        dpic[24]=getResources().getDrawable(R.drawable.a24);
        dpic[25]=getResources().getDrawable(R.drawable.a25);
        dpic[26]=getResources().getDrawable(R.drawable.a26);
        dpic[27]=getResources().getDrawable(R.drawable.a27);
        dpic[28]=getResources().getDrawable(R.drawable.a28);
        dpic[29]=getResources().getDrawable(R.drawable.a29);
        dpic[30]=getResources().getDrawable(R.drawable.a30);
        dpic[31]=getResources().getDrawable(R.drawable.a31);
        dpic[32]=getResources().getDrawable(R.drawable.a32);
        dpic[33]=getResources().getDrawable(R.drawable.a33);
        dpic[34]=getResources().getDrawable(R.drawable.a34);
        dpic[35]=getResources().getDrawable(R.drawable.a35);
        dpic[36]=getResources().getDrawable(R.drawable.a36);
        dpic[37]=getResources().getDrawable(R.drawable.a37);
        dpic[38]=getResources().getDrawable(R.drawable.a38);
        dpic[39]=getResources().getDrawable(R.drawable.a39);
        dpic[40]=getResources().getDrawable(R.drawable.a40);
        dpic[41]=getResources().getDrawable(R.drawable.a41);
        dpic[42]=getResources().getDrawable(R.drawable.a42);
        dpic[43]=getResources().getDrawable(R.drawable.a43);
        dpic[44]=getResources().getDrawable(R.drawable.a44);
        dpic[45]=getResources().getDrawable(R.drawable.a45);
        dpic[46]=getResources().getDrawable(R.drawable.a46);
        dpic[47]=getResources().getDrawable(R.drawable.a47);
        dpic[48]=getResources().getDrawable(R.drawable.a48);
        dpic[49]=getResources().getDrawable(R.drawable.a49);
        dpic[50]=getResources().getDrawable(R.drawable.a50);
        dpic[51]=getResources().getDrawable(R.drawable.a51);
        dpic[52]=getResources().getDrawable(R.drawable.a52);
        dpic[53]=getResources().getDrawable(R.drawable.a53);
        dpic[54]=getResources().getDrawable(R.drawable.a54);
        dpic[55]=getResources().getDrawable(R.drawable.a55);
        dpic[56]=getResources().getDrawable(R.drawable.a56);
        dpic[57]=getResources().getDrawable(R.drawable.a57);
        dpic[58]=getResources().getDrawable(R.drawable.a58);
        dpic[59]=getResources().getDrawable(R.drawable.a59);
        dpic[60]=getResources().getDrawable(R.drawable.a60);
        dpic[61]=getResources().getDrawable(R.drawable.a61);
        dpic[62]=getResources().getDrawable(R.drawable.a62);
        dpic[63]=getResources().getDrawable(R.drawable.a63);
        dpic[64]=getResources().getDrawable(R.drawable.a64);
        dpic[65]=getResources().getDrawable(R.drawable.a65);
        dpic[66]=getResources().getDrawable(R.drawable.a66);
        dpic[67]=getResources().getDrawable(R.drawable.a67);
        dpic[68]=getResources().getDrawable(R.drawable.a68);
        dpic[69]=getResources().getDrawable(R.drawable.a69);
        dpic[70]=getResources().getDrawable(R.drawable.a70);
        dpic[71]=getResources().getDrawable(R.drawable.a71);
        dpic[72]=getResources().getDrawable(R.drawable.a72);


        addNewSP();
        //disableAll();
    }

    private void initData()
    {
        for(int i=0;i<MAX;i++)
        {
            cnt_chord[i]=0;
            cnt_melody[i]=0;
        }
        sploc=0;
        melodyloc=0;
        chordloc=0;
        curstr="0";
    }

    private boolean judgeNewSP()
    {
        if (chordloc > sploc - 1 || melodyloc > sploc - 1)
        {
            addNewSP();
        }
        return true;
    }


    private boolean addNewSP()
    {
        //upblank
        mainsp.addView(blanksp[sploc * 2],forubp);
        //melodypart
        mg[sploc] = new GridLayout(this);
        mg[sploc].setColumnCount(MAXCNT_MELODY);
        for(int i=0;i<MAXCNT_MELODY;i++)
        {
            GridLayout.Spec cs=GridLayout.spec(i);
            GridLayout.Spec rs=GridLayout.spec(0);
            GridLayout.LayoutParams params=new GridLayout.LayoutParams(rs,cs);
            params.width= WIDTH;
            mg[sploc].addView(melodylabel[sploc][i],params);
        }
        /*
        for (int i = 16; i < MAXCNT_MELODY; i += 17)
        {
            Drawable bgd=getResources().getDrawable(R.drawable.a72);
            melodylabel[sploc][i].setBackgroundDrawable(bgd); //= imagepic[72];
            /*
            GridLayout.Spec cs=GridLayout.spec(i);
            GridLayout.Spec rs=GridLayout.spec(0);
            GridLayout.LayoutParams params=new GridLayout.LayoutParams(cs,rs);
            params.width=WIDTH;
            params.height=GridLayout.LayoutParams.FILL_PARENT;
            mg[sploc].addView(melodylabel[sploc][i],params);


            //melodylabel[sploc][i].setVisibility(View.INVISIBLE);
        }
        */
        msp[sploc].addView(mg[sploc]);
        mainsp.addView(msp[sploc],forsp);
        //downblank
        mainsp.addView(blanksp[sploc * 2 + 1],fordbp);
        //chordpart
        cg[sploc] = new GridLayout(this);
        //cg[sploc].setBackgroundColor(Color.rgb(189,189,189));
        cg[sploc].setColumnCount(MAXCNT_CHORD);
        for(int i=0;i<MAXCNT_CHORD;i++)
        {
            if(i%16==0)
            {
                GridLayout.Spec cs=GridLayout.spec(i);
                GridLayout.Spec rs=GridLayout.spec(0);
                GridLayout.LayoutParams params=new GridLayout.LayoutParams(rs,cs);
                params.width=2*WIDTH;
                cg[sploc].addView(chordlabel[sploc][i],params);
            }
            else {
                GridLayout.Spec cs = GridLayout.spec(i);
                GridLayout.Spec rs = GridLayout.spec(0);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams(rs, cs);
                params.width = WIDTH;
                cg[sploc].addView(chordlabel[sploc][i], params);
            }
        }
        /*
        for (int i = 16; i < MAXCNT_CHORD; i += 17)
        {
            Drawable bgd=getResources().getDrawable(R.drawable.a72);
            chordlabel[sploc][i].setBackgroundDrawable(bgd); //= imagepic[72];
            /*
            GridLayout.Spec cs=GridLayout.spec(i);
            GridLayout.Spec rs=GridLayout.spec(0);
            GridLayout.LayoutParams params=new GridLayout.LayoutParams(rs,cs);
            cg[sploc].addView(chordlabel[sploc][i],params);

            //chordlabel[sploc][i].setVisibility(View.INVISIBLE);
        }
        */
        csp[sploc].addView(cg[sploc]);
        csp[sploc].setBackgroundColor(Color.rgb(228,228,228));//189
        mainsp.addView(csp[sploc],forsp);
        sploc++;
        return true;
    }

    public void setChord(String str)//str和弦，空格分开。
    {
        String[] strSplitted = str.split(" ");
        int len = strSplitted.length;
        for (int i = 0; i < len; i++)
        {
            addChord(strSplitted[i]);
        }
    }

    public void setMelody(String str)
    {
        str = str.replace(",", "");
        str=str.trim();
        String[] strSplitted = str.split(" ");
        int len = strSplitted.length;
        for (int i = 0; i < len; i++)
        {
            addMelody(strSplitted[i]);
        }
    }

    private void addChord(String str)
    {
        if(str=="") return ;
        chordlabel[chordloc][cnt_chord[chordloc]].setText(str);
        /*
        GridLayout.Spec rs=GridLayout.spec(0);
        GridLayout.Spec cs=GridLayout.spec(cnt_chord[chordloc]);
        GridLayout.LayoutParams params=new GridLayout.LayoutParams(rs,cs);
        params.width=WIDTH;
        cg[chordloc].addView(chordlabel[chordloc][cnt_chord[chordloc]],params);
        */
        if(cnt_chord[chordloc]!=0)
        {
            Drawable bgd = getResources().getDrawable(R.drawable.a72);
            chordlabel[chordloc][cnt_chord[chordloc]-1].setBackgroundDrawable(bgd);
        }
        cnt_chord[chordloc] += 16;
        if (cnt_chord[chordloc] >= MAXCNT_CHORD)
            chordloc++;
        judgeNewSP();
    }

    private void audiJudgeLine()
    {
        if (cnt_melody[melodyloc] % 17 == 16)
        {
            cnt_melody[melodyloc]++;
        }

    }

    private void audiAddMelody(boolean flag)
    {

        /*
        GridLayout.Spec cs=GridLayout.spec(cnt_melody[melodyloc]);
        GridLayout.Spec rs=GridLayout.spec(0);
        GridLayout.LayoutParams params=new GridLayout.LayoutParams(rs,cs);
        params.width=WIDTH;
        mg[melodyloc].addView(melodylabel[melodyloc][cnt_melody[melodyloc]],params);
        */
        if (!flag) return;//no action for them
        //mg[melodyloc].addView(melodyhl[melodyloc][cnt_melody[melodyloc]],params);
    }

    private void audiJudgeMelody(int num)
    {
        switch (num)
        {
            case 2:
                cnt_melody[melodyloc]++;
                break;
            case 3:
                for (int i = 0; i < 2; i++)
                    audiJudgeLine();
                cnt_melody[melodyloc] += 2;
                break;
            case 4:
                for (int i = 0; i < 4; i++)
                    audiJudgeLine();
                cnt_melody[melodyloc] += 4;
                break;
        }
        for (int i = 16; i < MAXCNT_MELODY; i += 17)
        {
            if (cnt_melody[melodyloc] > i)
            {
                Drawable bgd = getResources().getDrawable(R.drawable.a72);
                melodylabel[melodyloc][i].setBackgroundDrawable(bgd);
            }
        }
        if (cnt_melody[melodyloc] >= MAXCNT_MELODY)
        {
            int tmp = cnt_melody[melodyloc] - MAXCNT_MELODY;
            cnt_melody[melodyloc] = MAXCNT_MELODY;
            melodyloc++;
            cnt_melody[melodyloc] += tmp;
        }
        judgeNewSP();
    }

    private void addMelody(String str)
    {
        curstr += " ";
        curstr += str;
        if(str=="") return;
        switch (str.charAt(0))
        {
            case 'C':
                switch (str.charAt(1))
                {
                    case '4':
                        switch (str.charAt(3))
                        {
                            case '2':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[44]);
                                audiAddMelody(true);
                                audiJudgeMelody(2);
                                break;
                            case '3':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[22]);
                                audiAddMelody(true);
                                audiJudgeMelody(2); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[70]);audiAddMelody(false); audiJudgeMelody(2);
                                break;
                            case '4':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[0]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                break;
                            case '5':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[0]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                            case '6':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[0]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                        }
                        break;
                    case '5':
                        switch (str.charAt(3))
                        {
                            case '2':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[51]);
                                audiAddMelody(true);
                                audiJudgeMelody(2);
                                break;
                            case '3':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[29]);
                                audiAddMelody(true);
                                audiJudgeMelody(2); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[70]); audiAddMelody(false); audiJudgeMelody(2);
                                break;
                            case '4':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[7]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                break;
                            case '5':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[7]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                            case '6':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[7]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                        }
                        break;
                    case '6':
                        switch (str.charAt(3))
                        {
                            case '2':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[58]);
                                audiAddMelody(true);
                                audiJudgeMelody(2);
                                break;
                            case '3':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[36]);
                                audiAddMelody(true);
                                audiJudgeMelody(2); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[70]); audiAddMelody(false); audiJudgeMelody(2);
                                break;
                            case '4':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[14]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                break;
                            case '5':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[14]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                            case '6':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[14]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                        }
                        break;
                    case '7':
                        switch (str.charAt(3))
                        {
                            case '2':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[65]);
                                audiAddMelody(true);
                                audiJudgeMelody(2);
                                break;
                            case '3':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[43]);
                                audiAddMelody(true);
                                audiJudgeMelody(2); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[70]); audiAddMelody(false); audiJudgeMelody(2);
                                break;
                            case '4':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[21]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                break;
                            case '5':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[21]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                            case '6':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[21]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                        }
                        break;
                }
                break;
            case 'D':
                switch (str.charAt(1))
                {
                    case '4':
                        switch (str.charAt(3))
                        {
                            case '2':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[45]);
                                audiAddMelody(true);
                                audiJudgeMelody(2);
                                break;
                            case '3':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[23]);
                                audiAddMelody(true);
                                audiJudgeMelody(2); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[70]); audiAddMelody(false); audiJudgeMelody(2);
                                break;
                            case '4':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[1]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                break;
                            case '5':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[1]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                            case '6':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[1]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                        }
                        break;
                    case '5':
                        switch (str.charAt(3))
                        {
                            case '2':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[52]);
                                audiAddMelody(true);
                                audiJudgeMelody(2);
                                break;
                            case '3':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[30]);
                                audiAddMelody(true);
                                audiJudgeMelody(2); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[70]); audiAddMelody(false); audiJudgeMelody(2);
                                break;
                            case '4':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[8]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                break;
                            case '5':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[8]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                            case '6':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[8]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                        }
                        break;
                    case '6':
                        switch (str.charAt(3))
                        {
                            case '2':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[59]);
                                audiAddMelody(true);
                                audiJudgeMelody(2);
                                break;
                            case '3':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[37]);
                                audiAddMelody(true);
                                audiJudgeMelody(2); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[70]); audiAddMelody(false); audiJudgeMelody(2);
                                break;
                            case '4':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[15]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                break;
                            case '5':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[15]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                            case '6':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[15]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                        }
                        break;
                }
                break;
            case 'E':
                switch (str.charAt(1))
                {
                    case '4':
                        switch (str.charAt(3))
                        {
                            case '2':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[46]);
                                audiAddMelody(true);
                                audiJudgeMelody(2);
                                break;
                            case '3':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[24]);
                                audiAddMelody(true);
                                audiJudgeMelody(2); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[70]); audiAddMelody(false); audiJudgeMelody(2);
                                break;
                            case '4':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[2]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                break;
                            case '5':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[2]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                            case '6':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[2]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                        }
                        break;
                    case '5':
                        switch (str.charAt(3))
                        {
                            case '2':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[53]);
                                audiAddMelody(true);
                                audiJudgeMelody(2);
                                break;
                            case '3':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[31]);
                                audiAddMelody(true);
                                audiJudgeMelody(2); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[70]); audiAddMelody(false); audiJudgeMelody(2);
                                break;
                            case '4':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[9]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                break;
                            case '5':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[9]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                            case '6':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[9]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                        }
                        break;
                    case '6':
                        switch (str.charAt(3))
                        {
                            case '2':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[60]);
                                audiAddMelody(true);
                                audiJudgeMelody(2);
                                break;
                            case '3':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[38]);
                                audiAddMelody(true);
                                audiJudgeMelody(2); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[70]); audiAddMelody(false); audiJudgeMelody(2);
                                break;
                            case '4':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[16]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                break;
                            case '5':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[16]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                            case '6':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[16]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                        }
                        break;
                }
                break;
            case 'F':
                switch (str.charAt(1))
                {
                    case '4':
                        switch (str.charAt(3))
                        {
                            case '2':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[47]);
                                audiAddMelody(true);
                                audiJudgeMelody(2);
                                break;
                            case '3':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[25]);
                                audiAddMelody(true);
                                audiJudgeMelody(2); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[70]); audiAddMelody(false); audiJudgeMelody(2);
                                break;
                            case '4':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[3]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                break;
                            case '5':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[3]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                            case '6':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[3]);
                                audiAddMelody(true);
                                audiJudgeMelody(2);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(2); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[70]); audiAddMelody(false); audiJudgeMelody(2);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                        }
                        break;
                    case '5':
                        switch (str.charAt(3))
                        {
                            case '2':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[54]);
                                audiAddMelody(true);
                                audiJudgeMelody(2);
                                break;
                            case '3':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[32]);
                                audiAddMelody(true);
                                audiJudgeMelody(2); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[70]); audiAddMelody(false); audiJudgeMelody(2);
                                break;
                            case '4':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[10]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                break;
                            case '5':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[10]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                            case '6':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[10]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                        }
                        break;
                    case '6':
                        switch (str.charAt(3))
                        {
                            case '2':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[61]);
                                audiAddMelody(true);
                                audiJudgeMelody(2);
                                break;
                            case '3':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[39]);
                                audiAddMelody(true);
                                audiJudgeMelody(2); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[70]); audiAddMelody(false); audiJudgeMelody(2);
                                break;
                            case '4':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[17]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                break;
                            case '5':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[17]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                            case '6':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[17]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                        }
                        break;
                }
                break;
            case 'G':
                switch (str.charAt(1))
                {
                    case '4':
                        switch (str.charAt(3))
                        {
                            case '2':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[48]);
                                audiAddMelody(true);
                                audiJudgeMelody(2);
                                break;
                            case '3':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[26]);
                                audiAddMelody(true);
                                audiJudgeMelody(2); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[70]); audiAddMelody(false); audiJudgeMelody(2);
                                break;
                            case '4':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[4]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                break;
                            case '5':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[4]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                            case '6':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[4]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                        }
                        break;
                    case '5':
                        switch (str.charAt(3))
                        {
                            case '2':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[55]);
                                audiAddMelody(true);
                                audiJudgeMelody(2);
                                break;
                            case '3':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[33]);
                                audiAddMelody(true);
                                audiJudgeMelody(2); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[70]); audiAddMelody(false); audiJudgeMelody(2);
                                break;
                            case '4':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[11]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                break;
                            case '5':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[11]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                            case '6':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[11]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                        }
                        break;
                    case '6':
                        switch (str.charAt(3))
                        {
                            case '2':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[62]);
                                audiAddMelody(true);
                                audiJudgeMelody(2);
                                break;
                            case '3':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[40]);
                                audiAddMelody(true);
                                audiJudgeMelody(2); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[70]); audiAddMelody(false); audiJudgeMelody(2);
                                break;
                            case '4':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[18]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                break;
                            case '5':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[18]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                            case '6':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[18]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                        }
                        break;
                }
                break;
            case 'A':
                switch (str.charAt(1))
                {
                    case '4':
                        switch (str.charAt(3))
                        {
                            case '2':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[49]);
                                audiAddMelody(true);
                                audiJudgeMelody(2);
                                break;
                            case '3':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[27]);
                                audiAddMelody(true);
                                audiJudgeMelody(2); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[70]); audiAddMelody(false); audiJudgeMelody(2);
                                break;
                            case '4':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[5]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                break;
                            case '5':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[5]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                            case '6':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[5]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                        }
                        break;
                    case '5':
                        switch (str.charAt(3))
                        {
                            case '2':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[56]);
                                audiAddMelody(true);
                                audiJudgeMelody(2);
                                break;
                            case '3':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[34]);
                                audiAddMelody(true);
                                audiJudgeMelody(2); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[70]); audiAddMelody(false); audiJudgeMelody(2);
                                break;
                            case '4':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[12]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                break;
                            case '5':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[12]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                            case '6':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[12]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                        }
                        break;
                    case '6':
                        switch (str.charAt(3))
                        {
                            case '2':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[63]);
                                audiAddMelody(true);
                                audiJudgeMelody(2);
                                break;
                            case '3':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[41]);
                                audiAddMelody(true);
                                audiJudgeMelody(2); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[70]); audiAddMelody(false); audiJudgeMelody(2);
                                break;
                            case '4':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[19]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                break;
                            case '5':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[19]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                            case '6':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[19]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                        }
                        break;
                }
                break;
            case 'B':
                switch (str.charAt(1))
                {
                    case '4':
                        switch (str.charAt(3))
                        {
                            case '2':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[50]);
                                audiAddMelody(true);
                                audiJudgeMelody(2);
                                break;
                            case '3':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[28]);
                                audiAddMelody(true);
                                audiJudgeMelody(2); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[70]); audiAddMelody(false); audiJudgeMelody(2);
                                break;
                            case '4':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[6]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                break;
                            case '5':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[6]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                            case '6':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[6]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                        }
                        break;
                    case '5':
                        switch (str.charAt(3))
                        {
                            case '2':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[57]);
                                audiAddMelody(true);
                                audiJudgeMelody(2);
                                break;
                            case '3':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[35]);
                                audiAddMelody(true);
                                audiJudgeMelody(2); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[70]); audiAddMelody(false); audiJudgeMelody(2);
                                break;
                            case '4':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[13]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                break;
                            case '5':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[13]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                            case '6':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[13]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                        }
                        break;
                    case '6':
                        switch (str.charAt(3))
                        {
                            case '2':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[64]);
                                audiAddMelody(true);
                                audiJudgeMelody(2);
                                break;
                            case '3':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[42]);
                                audiAddMelody(true);
                                audiJudgeMelody(2); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[70]); audiAddMelody(false); audiJudgeMelody(2);
                                break;
                            case '4':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[20]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                break;
                            case '5':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[20]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                            case '6':
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[20]);
                                audiAddMelody(true);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[66]);
                                audiAddMelody(false);
                                audiJudgeMelody(4);
                                break;
                        }
                        break;
                }
                break;
            case 'Z':
                switch (str.charAt(3))
                {
                    case '2':
                        audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[69]);
                        audiAddMelody(true);
                        audiJudgeMelody(2);
                        break;
                    case '3':
                        audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[68]);
                        audiAddMelody(true);
                        audiJudgeMelody(2); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[70]); audiAddMelody(false); audiJudgeMelody(2);
                        break;
                    case '4':
                        audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[67]);
                        audiAddMelody(true);
                        audiJudgeMelody(4);
                        break;
                    case '5':
                        audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[67]);
                        audiAddMelody(true);
                        audiJudgeMelody(4);
                        audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[67]);
                        audiAddMelody(false);
                        audiJudgeMelody(4);
                        break;
                    case '6':
                        audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[67]);
                        audiAddMelody(true);
                        audiJudgeMelody(4);
                        audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[67]);
                        audiAddMelody(false);
                        audiJudgeMelody(4);
                        audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[67]);
                        audiAddMelody(false);
                        audiJudgeMelody(4);
                        audiJudgeLine(); melodylabel[melodyloc][cnt_melody[melodyloc]].setBackgroundDrawable(dpic[67]);
                        audiAddMelody(false);
                        audiJudgeMelody(4);
                        break;
                }
                break;
        }
    }

    private int min(int a, int b)
    {
        return a < b ? a : b;
    }

    private boolean clearAll()
    {
        mainsp.removeAllViews();
        init();
        return true;
    }
    //endregion
}
