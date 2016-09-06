package com.ftboys.ChordMixer.ChordMixerAlgorithm;

import java.util.ArrayList;

public class ChordCalculator {


    private int[][] chordRelationTable = new int[][]//和弦表，控制和弦连接
            {
                    { 5, 0, 7,10, 8, 8, 5},
                    { 0, 5, 8, 0,10, 6, 6},
                    { 6, 0, 5, 8, 0, 8, 6},
                    { 8, 6, 8, 5, 9, 5, 8},
                    {10, 0, 6, 8, 5, 5, 6},
                    { 0, 8, 6, 6, 8, 5, 6},
                    { 9, 8, 6, 6, 9, 6, 5},

            };
    int totalChord = chordRelationTable.length;//Total chord
    StdChord[] myChord = new StdChord[totalChord]; //软件支持的主和弦数（一般7个）

    public int preChordID = 4;//前置和弦（上一个和弦），影响和弦连接。初始值赋值属和弦
    public String preChord = "G";//前置和弦序号

    public ChordCalculator()
    {
        chordInit();
        init();
    }
    //初始化

    public void init()
    {
        counterInit();
        preChord = "G";
        preChordID = 4;
    }
    public void chordInit()
    {
        //(String note1, String note2, String note3,int numOfNotes, int chordId, int priority,
        //	int frequency, int counter, String chordName)
        myChord[0] = new StdChord("C","E","G",3, 0, 7, 0, 0, "C");
        //myChord[1] = new StdChord(1, 3, 5, "C", 0, 7);
        myChord[1] = new StdChord("D", "F", "A", "Dm", 2);
        myChord[2] = new StdChord("E", "G", "B", "Em", 3);
        myChord[3] = new StdChord("F", "A", "C", "F", 5);
        myChord[4] = new StdChord("G", "B", "D", "G", 6);
        myChord[5] = new StdChord("A", "C", "E", "Am", 4);
        myChord[6] = new StdChord("F", "D", "B", "G7", 1);
        //构造函数：StdChord(int a, int b, int c, String name, int counter, int priority)
    }

    public void counterInit()
    {
        for (int i = 0; i < totalChord; i++)
            myChord[i].counter = 0;
    }
    public int checkChordTable(StdChord chordInput[])
    {
        int n = preChordID, m=0;// m is the result chordID,从第一个和弦开始
        //首先需要满足chordtable，其次选出counter最优，再选择table值最优。
        int j = 0;//当前扫描的chordInput的序号
        int tableValue = 0;
        int moveId = 0;
        /* 1.先寻找所有chordRelationTable值相等的元素，取出最大值
         * 2.查询该最大值是否合法
         * 3.如果合法则返回否则继续查找
         *
         *
         */
        while( j< totalChord){
            tableValue = chordRelationTable[n][ chordInput[j].chordId];//先给默认值
            m = j;
            moveId = 0;
            while (j + 1 < totalChord && chordInput[j].counter == chordInput[j + 1].counter)//counter一样时
            {
                if(chordRelationTable[n][ chordInput[j+1].chordId] > tableValue){
                    tableValue = chordRelationTable[n][ chordInput[j+1].chordId];
                    m = j + 1;
                }
                j++;
            }

            if (chordRelationTable[n][ chordInput[m].chordId ] > 0){
                return m;
            }
            j++;

        }
        return m;
    }

    public void chordCounter(StdNote[] inputNotes)//接收音符的name输入得到字符
    {
        //char[] input = inputStr.toCharArray();// 输入string转char数组
        //根据输入音符信息 计算和弦命中率
        int n = inputNotes.length;
        for (int i = 0; i < n; i++)//扫描所有音符
        {
            for (int j = 0; j < totalChord; j++)//扫描所有和弦
            {
                for(int k = 0; k < myChord[j].numOfNotes; k++)//扫描所有弦内音
                {
                    if ( inputNotes[i].compareNote( myChord[j].keyNotes[k]) )
                        myChord[j].counter++;
                }
            }
        }
    }
    //综合算法，将音符用数学进行处理。
    public String chordFeedback(StdNote[] inputNotes)  //throws ProjectException
    {
        int selectedChordID;//（最后计算得到的）和弦序号，1234567依次对应CDEFGAB。 和弦序号方便查表，真实名字用于反馈，所以这里将两者分开记录。
        String selectedChord;//（最后计算得到的）和弦名字
        chordCounter(inputNotes);//统计和弦命中
        optimizedChordSort(myChord);//对和弦命中情况排序
        selectedChordID = checkChordTable(myChord);// 查表后可得到此次应该输出的和弦
        //printInfo(); // 输出此次进行处理的所有数据信息
        counterInit(); //renew the counter for note analysis
        selectedChord = myChord[selectedChordID].chordName; //需要反馈的和弦名称
        preChord = selectedChord;//前置和弦定义为当前和弦
        preChordID = myChord[selectedChordID].chordId;//前置和弦定义为当前和弦（序号0
        return selectedChord;
    }

    public String chordFeedback(ArrayList<StdNote> arrayListInput)  //throws ProjectException
    {
        StdNote[] inputNotes = (StdNote[]) arrayListInput.toArray(new StdNote[ arrayListInput.size()] );
//
//    	for(StdNote tmp : inputNotes){
//    		System.out.println(tmp.description() + "!!!!!!!!!!!!!!!");
//    	}
//
        //将ArrayList转化为数组
        int selectedChordID;//（最后计算得到的）和弦序号，1234567依次对应CDEFGAB。 和弦序号方便查表，真实名字用于反馈，所以这里将两者分开记录。
        String selectedChord;//（最后计算得到的）和弦名字
        chordCounter(inputNotes);//统计和弦命中
        optimizedChordSort(myChord);//对和弦命中情况排序
        selectedChordID = checkChordTable(myChord);// 查表后可得到此次应该输出的和弦
        //printInfo(); // 输出此次进行处理的所有数据信息
        counterInit(); //renew the counter for note analysis
        selectedChord = myChord[selectedChordID].chordName; //需要反馈的和弦名称
        preChord = selectedChord;//前置和弦定义为当前和弦
        preChordID = myChord[selectedChordID].chordId;//前置和弦定义为当前和弦（序号0
        return selectedChord;
    }

    public StdScore scoreHandler(StdScore inputScore){

        //先清空所有的和弦
        inputScore.chordTrack.clear();
        int maxTimeOfEachBar = 6 * 4;//定义一小节的时长
        int currentTime = 0;
        ArrayList<StdNote> tmpList = new ArrayList<>();
        for(int i = 0; i<inputScore.musicTrack.get(0).noteTrack.size(); i++){

            StdNote noteInforLoop = inputScore.musicTrack.get(0).noteTrack.get(i);
            currentTime += noteInforLoop.duration;
            tmpList.add(noteInforLoop);
            if(currentTime >= maxTimeOfEachBar ){
                inputScore.musicTrack.get(0).noteTrack.get(i).barPoint = 1;
                currentTime = 0;
                inputScore.chordTrack.add( new StdChord( chordFeedback(tmpList) ) );
                tmpList.clear();
            }
        }
        if(tmpList.size() > 0)
            inputScore.chordTrack.add( new StdChord( chordFeedback(tmpList) ) );
        return inputScore;

    }
    //快速排序
    void quick_sort(StdChord[] s, int l, int r)
    {
        if (l < r)
        {
            //Swap(s[l], s[(l + r) / 2]); //将中间的这个数和第一个数交换 参见注1
            int i = l, j = r;
            StdChord x = s[l];
            while (i < j)
            {
                while(i < j && s[j].CompareTo(x) == 1 ) // 从右向左找第一个小于x的数
                    j--;
                if(i < j)
                    s[i++] = s[j];

                while(i < j && s[i].CompareTo(x) == -1) // 从左向右找第一个大于等于x的数
                    i++;
                if(i < j)
                    s[j--] = s[i];
            }
            s[i] = x;
            quick_sort(s, l, i - 1); // 递归调用
            quick_sort(s, i + 1, r);
        }
    }
    //重载快排
    void quick_sort(StdChord[] s){
        quick_sort(s, 0, s.length - 1 );
    }

    //利用到了排序
    public void optimizedChordSort(StdChord[] a)
    {
        //Array.Sort(a);
        quick_sort(a);
    }

    //机器学习过程
    public Boolean recieveNewAlgorithm(int [][] newTable, StdChord [] newChord){
        if(newTable.length > 0){
            chordRelationTable = newTable;
            myChord = newChord;
            return true;
        }
        else
            return false;
    }

    //打印结果
    public void printInfo()
    {
        System.out.println("Basic info:");

        System.out.println("Name:");
        for (int i = 0; i < totalChord; i++)
            System.out.print(myChord[i].chordName + " ");
        System.out.println();

        System.out.println("Counter:");
        for (int i = 0; i < totalChord; i++)
            System.out.print(myChord[i].counter + " ");
        System.out.println();
        /*
        System.out.println("Frequency:");
        for (int i = 0; i < totalChord; i++)
            System.out.print(myChord[i].freq + " ");
        System.out.println();
        */
    }

}

