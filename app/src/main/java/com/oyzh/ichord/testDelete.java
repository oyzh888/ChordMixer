//package com.oyzh.ichord;
//
//import android.content.Context;
//import android.content.Intent;
//import android.media.AudioManager;
//import android.media.MediaPlayer;
//import android.media.SoundPool;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Environment;
//
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.View;
//import android.support.design.widget.NavigationView;
//import android.support.v4.view.GravityCompat;
//import android.support.v4.widget.DrawerLayout;
//import android.support.v7.app.ActionBarDrawerToggle;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.ftboys.ChordMixer.ChordMixerAlgorithm.*;
//import com.pdrogfer.simplemidiplayer.R;
//
//import com.ftboys.ChordMixer.*;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.HashMap;
//
//import cn.sharesdk.framework.ShareSDK;
//import cn.sharesdk.onekeyshare.OnekeyShare;
//
//public class MainActivity extends AppCompatActivity
//        implements NavigationView.OnNavigationItemSelectedListener,OnNoteSavedToStorage {
//
//
//    //OYZH Added
//    /*
//    调用midi部分代码
//     */
//    private static final String TAG = "MainActivity";
//    private final int MAXSIZE=88;
//    AudioManager audioManager;
//    float volume;
//    private SoundPool soundPool;
//    ExternalStorageOperations externalStorageOperations;
//    //int[] soundPooldIDs = new int[89];
//    private boolean soundPoolIsLoaded = false;
//    HashMap<Integer,Integer> soundPoolMap = new HashMap<Integer, Integer>();
//    private MediaPlayer mp ;
//    final File MIDI_NOTE_FILE = new File(Environment.getExternalStoragePublicDirectory(
//            Environment.DIRECTORY_MUSIC) + "/MIDI_NOTES/");
//    final File MIDI_MUSIC_FILE = new File(Environment.getExternalStoragePublicDirectory(
//            Environment.DIRECTORY_MUSIC) + "/MIDI_MUSIC/");
//
//
//    ChordCalculator chordMixerCalculator = new ChordCalculator();
//    //界面音乐变量
//    private StdScore mainScore = new StdScore();
//    private StdPattent mainPattent = new StdPattent();
//
//    private int mainInputDuration = 6;
//
////    private int mainVolume = 100;
////    private float mainTempo = 120;
//
//    //界面控件
//    private final int pianoKeyNum = 36;
//    ImageView pianoImage[] = new ImageView[pianoKeyNum];
//    TextView inputTextMain, textMainChord;
//
//
//    //基本函数
//    public void clickPianoKey(int btnId){
//        int bias = 50;
//        btnId += bias;
//
//        //播放钢琴单音
//        playMIDINote(btnId );
//
//        StdNote addedNote = new StdNote(btnId);
//
//        addedNote.duration = mainInputDuration;
//        mainScore.musicTrack.get(0).noteTrack.add(addedNote);
//        //刷新界面TextView
//        refreshInputTextMain();
//
//    }
//
//    //更换轨道和弦节奏型
//    public void changeTrackPattent(int trackId, int pattentId) throws Exception{
//
//        if(trackId > mainScore.musicTrack.size()|| trackId < 0){
//            throw new Exception("The trackId is Out of Range");
//        }
//        if(pattentId > mainPattent.defChordNum || pattentId < 0){
//            throw new Exception("The pattentId is Out of Range");
//        }
//        mainScore.musicTrack.get(trackId).trackPattent = pattentId;
//
//    }
//
//
//    //TODO
//    //更换乐器
//    public void changeInstrument(int trackId, int insId) throws Exception{
//        if(trackId > mainScore.musicTrack.size() || trackId < 0){
//            throw new Exception("The trackId is Out of Range");
//        }
////        if(insId > mainScore.instrument.length || insId < 0){
////            throw new Exception("The instrumentId is Out of Range");
////        }
//        mainScore.musicTrack.get(trackId).instrument=insId;
//
//    }
//    //改变音量
//    public void changeVolume(int volumeValue){
//        mainScore.setVolume(volumeValue);
//    }
//    //改变速度
//    public void changeTempo(float tempoValue){
//        mainScore.tempo = tempoValue;
//    }
//    //播放
//
//
//
//
//
//    //刷新显示inputTextView界面
//    public void refreshInputTextMain(){
//        String str = "";
//        for(StdNote tmp : mainScore.musicTrack.get(0).noteTrack){
//            str += tmp.name + tmp.absulutePosition % 12 +  "  " ;
//        }
//        inputTextMain.setText(str);
//    }
//    public void refreshTextMainChord(){
//        String str = "";
//        for(StdChord tmp : mainScore.chordTrack){
//            str += tmp.chordName + " ";
//        }
//        textMainChord.setText(str);
//    }
//    //处理曲谱,得到和弦 及 伴奏演奏模式
//    public void chordMixerScoreHandle(){
//        //得到和弦
//        mainScore = chordMixerCalculator.scoreHandler(mainScore);
//        //解释和弦,生成伴奏
//        mainScore = mainPattent.scoreChordHandler(mainScore);
//        //刷新和弦显示界面
//        refreshTextMainChord();
//    }
//
//
//    //Button Event:
//    class MyClickListener implements View.OnClickListener {
//
//        @Override
//        public void onClick(View v) {
//            // TODO Auto-generated method stub
//            switch (v.getId()) {
//                //Function added by OYZH
//                case R.id.s00_btn:
//                    Toast.makeText( MainActivity.this,"OYZH's Test",Toast.LENGTH_SHORT).show();
//                default:
//                    break;
//            }
//        }
//    }
//
//    public void setButtonEvent(){
//        for(int i= 0; i < pianoKeyNum; i++){
//            pianoImage[i].setTag(i);
//
//            pianoImage[i].setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View view, MotionEvent motionEvent) {
//
//                    if (motionEvent == null || motionEvent.getAction() == MotionEvent.ACTION_DOWN){
//                        clickPianoKey( (int)view.getTag() );
//                        ImageView tmp = (ImageView)view;
//                    }
//                    return false;
//                }
//            });
//
////            pianoImage[i].setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View view) {
////                    clickPianoKey( (int)view.getTag() );
////                    ImageView tmp = (ImageView)view;
////                }
////            });
//        }
//
//        //休止符事件
//        pianoImage[0].setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                chordMixerScoreHandle();//获取和弦
//
//                //mainScore = mainPattent.scoreChordHandler(mainScore);
//                //externalStorageOperations.createMidiMuiscInExternalStorage(mainScore);
//            }
//        });
//
//        //临时测试按钮
//
//        findViewById(R.id.testButton1).setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                chordMixerScoreHandle();//获取和弦
//
//                for(int i=1; i<mainScore.musicTrack.size(); i++){
//                    mainScore.musicTrack.get(i).trackVolume = 50;
//                }
//
//                externalStorageOperations.createMidiMuiscInExternalStorage(mainScore);
//
//            }
//        });
//
//
//        findViewById(R.id.testButton2).setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(MainActivity.this,"HAHAHAHHA",Toast.LENGTH_SHORT).show();
//                //playWholeMusic(mainScore.scoreName);
//                playWholeMusic("UnKnown");
//            }
//        });
//        //测试清除所有曲谱
//        pianoImage[35].setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view){
//                mainScore.initScore();
//                refreshTextMainChord();
//                refreshInputTextMain();
//            }
//        });
//
//        //测试曲谱和弦生成的音符
//        pianoImage[34].setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view){
//                mainScore = mainPattent.scoreChordHandler(mainScore);
//                String str = "";
//                for(StdNote tmp : mainScore.musicTrack.get(1).noteTrack){
//                    str += tmp.name + tmp.absulutePosition/12 + " ";
//                }
//                textMainChord.setText(str);
//            }
//
//        });
//
//        //测试微信分享
//        pianoImage[33].setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(),"successfully",Toast.LENGTH_SHORT).show();
//                showShare();
//            }
//        });
////        //测试播放
////        pianoImage[32].setOnClickListener(new View.OnClickListener(){
////            @Override
////            public void onClick(View view){
////
////                //externalStorageOperations.createMidiMuiscInExternalStorage(mainScore);
////                playWholeMusic(mainScore.scoreName);
////            }
////        });
//    }
//
//    //绑定界面按钮
//    public void initWhenCreate(){
//
//        inputTextMain = (TextView)findViewById(R.id.textViewForInput);
//        textMainChord = (TextView)findViewById(R.id.textViewForChord);
//        //Piano Keys
//        pianoImage[0]  = (ImageView)findViewById(R.id.s00_btn);
//        pianoImage[1]  = (ImageView)findViewById(R.id.s31_btn);
//        pianoImage[2]  = (ImageView)findViewById(R.id.s32_btn);
//        pianoImage[3]  = (ImageView)findViewById(R.id.s33_btn);
//        pianoImage[4]  = (ImageView)findViewById(R.id.s34_btn);
//        pianoImage[5]  = (ImageView)findViewById(R.id.s35_btn);
//        pianoImage[6]  = (ImageView)findViewById(R.id.s36_btn);
//        pianoImage[7]  = (ImageView)findViewById(R.id.s37_btn);
//        pianoImage[8]  = (ImageView)findViewById(R.id.s38_btn);
//        pianoImage[9]  = (ImageView)findViewById(R.id.s39_btn);
//        pianoImage[10] = (ImageView)findViewById(R.id.s40_btn);
//        pianoImage[11] = (ImageView)findViewById(R.id.s41_btn);
//        pianoImage[12] = (ImageView)findViewById(R.id.s42_btn);
//        pianoImage[13] = (ImageView)findViewById(R.id.s43_btn);
//        pianoImage[14] = (ImageView)findViewById(R.id.s44_btn);
//        pianoImage[15] = (ImageView)findViewById(R.id.s45_btn);
//        pianoImage[16] = (ImageView)findViewById(R.id.s46_btn);
//        pianoImage[17] = (ImageView)findViewById(R.id.s47_btn);
//        pianoImage[18] = (ImageView)findViewById(R.id.s48_btn);
//        pianoImage[19] = (ImageView)findViewById(R.id.s49_btn);
//        pianoImage[20] = (ImageView)findViewById(R.id.s50_btn);
//        pianoImage[21] = (ImageView)findViewById(R.id.s51_btn);
//        pianoImage[22] = (ImageView)findViewById(R.id.s52_btn);
//        pianoImage[23] = (ImageView)findViewById(R.id.s53_btn);
//        pianoImage[24] = (ImageView)findViewById(R.id.s54_btn);
//        pianoImage[25] = (ImageView)findViewById(R.id.s55_btn);
//        pianoImage[26] = (ImageView)findViewById(R.id.s56_btn);
//        pianoImage[27] = (ImageView)findViewById(R.id.s57_btn);
//        pianoImage[28] = (ImageView)findViewById(R.id.s58_btn);
//        pianoImage[29] = (ImageView)findViewById(R.id.s59_btn);
//        pianoImage[30] = (ImageView)findViewById(R.id.s60_btn);
//        pianoImage[31] = (ImageView)findViewById(R.id.s61_btn);
//        pianoImage[32] = (ImageView)findViewById(R.id.s62_btn);
//        pianoImage[33] = (ImageView)findViewById(R.id.s63_btn);
//        pianoImage[34] = (ImageView)findViewById(R.id.s64_btn);
//        pianoImage[35] = (ImageView)findViewById(R.id.s65_btn);
//
//        setButtonEvent();
//    }
//
//
//
//    //微信分享
//    private void showShare() {
//        ShareSDK.initSDK(this);
//        OnekeyShare oks = new OnekeyShare();
//        //关闭sso授权
//        oks.disableSSOWhenAuthorize();
//
//        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
//        oks.setTitle("标题");
//        // titleUrl是标题的网络链接，QQ和QQ空间等使用
//        oks.setTitleUrl("http://sharesdk.cn");
//        // text是分享文本，所有平台都需要这个字段
//        oks.setText("我是分享文本");
//        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
//        // url仅在微信（包括好友和朋友圈）中使用
//        oks.setUrl("http://sharesdk.cn");
//        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment("我是测试评论文本");
//        // site是分享此内容的网站名称，仅在QQ空间使用
//        oks.setSite(getString(R.string.app_name));
//        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//        oks.setSiteUrl("http://sharesdk.cn");
//
//        // 启动分享GUI
//        oks.show(this);
//    }
//
//
//
//    //FJC's Midi
//
//    private void initMusicDevice(){
//        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
//        volume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//        externalStorageOperations = new ExternalStorageOperations(this,this);
//        //createAllNoteMidiFiles(32); // some default instrument
//        initSoundPool();
//        buildSingleMIDIFile(0);
//        loadSoundPool();
//        externalStorageOperations.createTestMusic();
//    }
//
//    //如果你写事件  你一定会看到这里  留给OYZH的HINT
//    //void createMidiMuiscInExternalStorage(StdScore stdScore)创建自己的*.mid文件  这里面最怕你的stdScore中信息不全  发生空引用 虽然我已经做好防备了
//    //上面已经声明了操作的对象externalStorageOperations  所以你直接使用externalStorageOperations.createMidiMuiscInExternalStorage(StdScore stdScore);
//    //创建好了会有LOG的提示   文件名+ 创建成功
//    //测试你创建的文件能否播放  用的是public void playWholeMusic(String musicName) 这个是直接写在MainActivity里面的  可以直接调用
//
//
//    public void playWholeMusic(String musicName){
//        //播放整首乐曲  默认路径为/sdcard/Music/MIDI_MUSIC/*.mid
//        try {
//            mp = new MediaPlayer();
//            mp.setDataSource( MIDI_MUSIC_FILE +"/"+ musicName + ".mid");
//            mp.prepare();
//            mp.start();
//
//        }
//        catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mediaPlayer) {
//                mp.release();
//            }
//        });
//
//    }
//
//    private void initSoundPool() {
//        if (soundPool != null) {
//            soundPool.release();
//            soundPool = null;
//        }
//
//        soundPool = new SoundPool(16, AudioManager.STREAM_MUSIC,100);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            soundPool = new SoundPool.Builder().build();
//        }
//    }
//
//
//
//    public void buildSingleMIDIFile(int instrument){
//        //生成midi单音文件
//        initSoundPool();
//        for(int i=0;i<MAXSIZE;i++){
//            externalStorageOperations.createMidiNoteInExternalStorage(String.valueOf(i),i,instrument);
//        }
//    }
//
//    public void loadSoundPool(){
//        for(int i=0;i<MAXSIZE;i++){
//            soundPoolMap.put(i,soundPool.load(MIDI_NOTE_FILE.toString()+"/"+i+".mid",1));
//            if(i==MAXSIZE-1){
//                soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
//                    @Override
//                    public void onLoadComplete(SoundPool soundPool, int i, int i1) {
//                        soundPoolIsLoaded = true;
//                        Log.i(TAG, "loadSoundPool: onLoadComplete:soundPoolIsLoaded");
//                    }
//                });
//            }
//        }
//
//    }
//
//    public void playMIDINote(int noteMidiCode) {
//        // TODO: 20/07/2016 PROBLEM!!! ASSOCIATE NOTE MIDI CODES TO MY SOUNDS IDS
//        if (soundPoolIsLoaded) {
//            soundPool.play(soundPoolMap.get(noteMidiCode), volume, volume, 1, 0, 1f);
//        } else {
//            Log.i(TAG, "playNote: SoundPool not loaded");
//        }
//    }
//
//    public void changeMidiNotesInstrument(int newInstrument) {
//        // Call buildSingleMIDIFile with the new instrument
//        // Call reLoadSoundPool to reload
//        buildSingleMIDIFile(newInstrument);
//        loadSoundPool();
//    }
//
//    public void onNoteSaved(String pathToNote, final int noteMidiCode) {
//        //目前仅仅作为接口保留  以下功能已被重写
////        // soundPool load happens here, once every note has been saved to external storage
////        // soundPool.load has an overload that takes the path, and the priority (default, 1)
////        soundPooldIDs[noteMidiCode] = soundPool.load(pathToNote, 1);
////        Log.i(TAG, "onNoteSaved: loading note " + noteMidiCode);
////        if (noteMidiCode == 87) {
////        }
////        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
////            @Override
////            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
////                soundPoolIsLoaded = true;
////                Log.i(TAG, "onLoadComplete: soundPool loaded note" + noteMidiCode);
////            }
////        });
//    }
//    //End of FJC's Midi
//
//
//    //MGR's Code 2016/8/4
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        //Added by OYZH
//        initWhenCreate();
//        initMusicDevice();
//
//        //初始化微信分享
//        ShareSDK.initSDK(MainActivity.this);
//
//        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });*/
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
//    }
//
//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//        if(id==R.id.nav_chordsetting){
//            final Intent it = new Intent(this, com.ftboys.ChordMixer.MainActivity.class); //你要转向的Activity
//            this.startActivity(it);
//        }
//        /*
//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }*/
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }
//
//    //End of MGR's Code
//
//
//}
