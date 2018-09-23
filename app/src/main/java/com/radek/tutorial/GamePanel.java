package com.radek.tutorial;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import static android.R.attr.path;
import static android.provider.Telephony.Mms.Part.FILENAME;
import static android.provider.Telephony.Mms.Part._DATA;
import static com.radek.tutorial.Constants.HEIGHT_Y;
import static com.radek.tutorial.Constants.WIDTH_X;
import static com.radek.tutorial.MainThread.canvas;
import static java.lang.StrictMath.abs;

/**
 * Created by Radek on 2017-11-11.
 */

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {


    private MainThread thread;
    private Rect r = new Rect();

    private ArrayList<Obstacle> dots;
    private ArrayList<Obstacle> dots2;
    private ArrayList<Obstacle> dots3;
    private int newDotId;
    private int newDotIdBlue;
    private int newDotIdPurple;

    private RectPlayer player;
    private RectPlayer line;
    //private Point playerPoint;
    private Point linePoint;
    //private ObstacleManager obstacleManager;

    int marginLeft = 40*WIDTH_X/1080;
    int marginUp = 200*HEIGHT_Y/1920;

    private boolean gameOver = false;
    private int newLevel = -1;
    //private boolean movingPlayer = false;
    private long gameOverTime;
    private long newLevelTime;
    private long startGameTime;
    private boolean levelCompleted = false;
    private boolean nextLevel = false;

    private boolean levelIsScreen = false;

    private int currentLevelX1 = marginLeft;
    private int currentLevelX2 = marginLeft;
    private int currentLevelX3 = marginLeft;
    private int currentLevelY1 = 410*HEIGHT_Y/1920;
    private int currentLevelY2 = 710*HEIGHT_Y/1920;
    private int currentLevelY3 = 1010*HEIGHT_Y/1920;

    private int currentDotX = marginLeft, currentDotY = 410*HEIGHT_Y/1920;
    private int targetDotX=700*WIDTH_X/1080, targetDotY=600*HEIGHT_Y/1920;
    private int drawnX=marginLeft;
    private int drawnY;
    private boolean drawingEnabled = false;
    private int directionDrawingX = 1;//right
    private int directionDrawingY = 1; // up
    private int actualDotX = marginLeft;
    private int actualDotY = 410*HEIGHT_Y/1920;

    private int currentDotXBlue = marginLeft, currentDotYBlue = 710;
    private int targetDotXBlue=700, targetDotYBlue=600;
    private int drawnXBlue=marginLeft;
    private int drawnYBlue;
    private boolean drawingEnabledBlue = false;
    private int directionDrawingXBlue = 1;
    private int directionDrawingYBlue = 1;
    private int actualDotXBlue = marginLeft;
    private int actualDotYBlue = 710;

    private int currentDotXPurple = marginLeft, currentDotYPurple = 1010;
    private int targetDotXPurple=700, targetDotYPurple=600;
    private int drawnXPurple=marginLeft;
    private int drawnYPurple;
    private boolean drawingEnabledPurple = false;
    private int directionDrawingXPurple = 1;//right
    private int directionDrawingYPurple = 1; // up
    private int actualDotXPurple = marginLeft;
    private int actualDotYPurple = 1010;

    private int GRAYLINE = 1;
    private int BLUELINE = 2;
    private int PURPLELINE = 3;

    protected int selectedLine = 1;

    private RectPlayer lightBorder1;
    private RectPlayer lightBorder2;
    private RectPlayer lightBorder3;
    private RectPlayer lightBorder4;

    private RectPlayer line1;
    private RectPlayer line2;
    private RectPlayer line3;

    private int flickeringPhase = 1;

    protected ArrayList<Screen> obstacles;
    private int direction;

    private String loadedText;

    private long startTime;
    private long initTime;

    protected float score = 100;
    private float previousScore=100;
    protected float levelTime = 100;
    protected int gameLevel = 1;

    int pause = 0;// -1 pause OFF, 0 pause ON without message, 1 pause ON

    protected int [][] borders = new int [1080*WIDTH_X/1080] [1920*HEIGHT_Y/1920];
    protected int [] [] colors = new int [1080*WIDTH_X/1080] [1920*HEIGHT_Y/1920];

    private ArrayList<RectPlayer>scoringAreas;

    private RectPlayer border1;
    private RectPlayer border2;
    private RectPlayer border3;
    private RectPlayer border4;

    protected RectPlayer startLine1;
    protected RectPlayer startLine2;
    protected RectPlayer startLine3;



    protected boolean startedGame = false;
    private int elapsedPeriod = 0;

    private Screen screen;
    BitmapFactory.Options options = new BitmapFactory.Options();

    public static void Save (File file, String [] data){
        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(file);

        }catch (FileNotFoundException e) {e.printStackTrace();}
        try
        {
            try
            {
                for (int i = 0; i< data.length; i++){
                    fos.write(data[i].getBytes());
                    if(i<data.length-1){
                        fos.write("\n".getBytes());
                    }
                }
            }
            catch(IOException e){e.printStackTrace();}
        }
        finally{
            try{
                fos.close();
            }
            catch(IOException e){e.printStackTrace();}
        }
    }

    public static String [] Load (File file){
        FileInputStream fis = null;
        try{
            fis = new FileInputStream(file);

        }
        catch (FileNotFoundException e){e.printStackTrace();}
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);
        String test;
        int anzahl1=0;

        try{
            while((test=br.readLine()) != null){
                anzahl1++;
            }
        }
        catch(IOException e){e.printStackTrace();}

        try{
            fis.getChannel().position(0);
        }
        catch(IOException e ){e.printStackTrace();}

        String [] array = new String [anzahl1];
        String line;
        int i = 0;
        try{
            while ((line=br.readLine()) != null){
                array[i]=line;
                i++;
            }
        }
        catch(IOException e){e.printStackTrace();}
        return array;
    }

    public void writeToFile(String data)
    {
        // Get the directory for the user's public pictures directory.
       /* final File path =
                Environment.getExternalStoragePublicDirectory
                        (
                                //Environment.DIRECTORY_PICTURES
                                Environment.DIRECTORY_DCIM + "/YourFolder/"
                        );*/

        File path = Environment.getExternalStorageDirectory();

        // Make sure the path directory exists.
        if(!path.exists())
        {
            // Make it, if it doesn't exit
            path.mkdirs();
        }

        final File file = new File(path, "config.txt");

        // Save your stream, don't forget to flush() it before closing it.

        try
        {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);

            myOutWriter.close();

            fOut.flush();
            fOut.close();
        }
        catch (IOException e)
        {
            Log.e("Exception", "File write failed: " + e.toString());
        }


    }

    public GamePanel(Context context) {
        super(context);
        dots = new ArrayList<>();
        dots2 = new ArrayList<>();
        dots3 = new ArrayList<>();

        writeToFile("dupa123");
/*
        File sdcard = Environment.getExternalStoragePublicDirectory(
                //Environment.DIRECTORY_PICTURES
                Environment.DIRECTORY_DCIM + "/YourFolder/"
        );*/

        File sdcard = Environment.getExternalStorageDirectory();

//Get the text file
        File file = new File(sdcard,"config.txt");

//Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }

        System.out.println("dupa111" + text);
        loadedText = text.toString();

/*        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write("dupa1");
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }*/



        /*String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/aTutorial";

        File file = new File (path + "/savedFile.txt");
        String [] savedText = {"radek82s","BBBB"};

        Save (file, savedText);


        String [] loadText = Load(file);
        String finalString = "";

        for (int i=0; i<loadText.length;i++){
            finalString += loadText[i];
        }
        System.out.println(finalString);*/

        getHolder().addCallback(this);
        //thread = new MainThread(getHolder(), this);

        //obstacleManager = new ObstacleManager();
        obstacleManager();

        lightBorder1 = new RectPlayer(new Rect(20*WIDTH_X/1080,1440*HEIGHT_Y/1920,380*WIDTH_X/1080,1470*HEIGHT_Y/1920), Color.rgb(128,128,128));
        lightBorder2 = new RectPlayer(new Rect(20*WIDTH_X/1080,1530*HEIGHT_Y/1920,380*WIDTH_X/1080,1560*HEIGHT_Y/1920), Color.rgb(128,128,128));
        lightBorder3 = new RectPlayer(new Rect(20*WIDTH_X/1080,1470*HEIGHT_Y/1920,50*WIDTH_X/1080,1530*HEIGHT_Y/1920), Color.rgb(128,128,128));
        lightBorder4 = new RectPlayer(new Rect(350*WIDTH_X/1080,1470*HEIGHT_Y/1920,380*WIDTH_X/1080,1530*HEIGHT_Y/1920), Color.rgb(128,128,128));

        //line1 = new RectPlayer(new Rect((int)((float)50*scaleX),(int)((float)1470*scaleY),(int)((float)350*scaleX),(int)((float)1530*scaleY)), Color.rgb(0,255,255));
        /*line1 = new RectPlayer(new Rect(50*WIDTH/1080,1470*HEIGHT/1920,350*WIDTH/1080,1530*HEIGHT/1920), Color.rgb(0,255,255));
        line2 = new RectPlayer(new Rect(390*WIDTH/1080,1470*HEIGHT/1920,690*WIDTH/1080,1530*HEIGHT/1920), Color.rgb(255,255,0));
        line3 = new RectPlayer(new Rect(730*WIDTH/1080,1470*HEIGHT/1920,1030*WIDTH/1080,1530*HEIGHT/1920), Color.rgb(255,255,255));*/
        line1 = new RectPlayer(new Rect(50*WIDTH_X/1080,1470*HEIGHT_Y/1920,350*WIDTH_X/1080,1530*HEIGHT_Y/1920), Color.rgb(0,255,255));
        line2 = new RectPlayer(new Rect(390*WIDTH_X/1080,1470*HEIGHT_Y/1920,690*WIDTH_X/1080,1530*HEIGHT_Y/1920), Color.rgb(255,255,0));
        line3 = new RectPlayer(new Rect(730*WIDTH_X/1080,1470*HEIGHT_Y/1920,1030*WIDTH_X/1080,1530*HEIGHT_Y/1920), Color.rgb(255,255,255));


        border1 = new RectPlayer(new Rect(marginLeft-5*WIDTH_X/1080,marginUp-5*HEIGHT_Y/1920,marginLeft+1000*WIDTH_X/1080,marginUp*HEIGHT_Y/1920), Color.rgb(128,128,128));
        border2 = new RectPlayer(new Rect(marginLeft-5*WIDTH_X/1080,marginUp-5*HEIGHT_Y/1920,marginLeft*WIDTH_X/1080,marginUp+1000*HEIGHT_Y/1920), Color.rgb(128,128,128));
        border3 = new RectPlayer(new Rect(marginLeft+1000*WIDTH_X/1080,marginUp-5*HEIGHT_Y/1920,marginLeft+1005*WIDTH_X/1080,marginUp+1005*HEIGHT_Y/1920), Color.rgb(128,128,128));
        border4 = new RectPlayer(new Rect(marginLeft-5*WIDTH_X/1080,marginUp+1000*HEIGHT_Y/1920,marginLeft+1005*WIDTH_X/1080,marginUp+1005*HEIGHT_Y/1920), Color.rgb(128,128,128));

        startLine1 = new RectPlayer(new Rect(marginLeft-25*WIDTH_X/1080, 400*HEIGHT_Y/1920, marginLeft-5*WIDTH_X/1080, 420*HEIGHT_Y/1920), Color.rgb(0,255,255));      //cyan
        startLine2 = new RectPlayer(new Rect(marginLeft-25*WIDTH_X/1080, 700*HEIGHT_Y/1920, marginLeft-5*WIDTH_X/1080, 720*HEIGHT_Y/1920), Color.rgb(255,255,0));        //yellow
        startLine3 = new RectPlayer(new Rect(marginLeft-25*WIDTH_X/1080, 1000*HEIGHT_Y/1920, marginLeft-5*WIDTH_X/1080, 1020*HEIGHT_Y/1920), Color.rgb(255,255,255));      //white

        setFocusable(true);
    }

    public void obstacleManager (){
        startedGame = false;
        elapsedPeriod = 0;

        score = 100;
        gameLevel = 0;
        startTime = initTime = System.currentTimeMillis();

        obstacles = new ArrayList<>();

        //a1 = (ImageView)findViewById(R.id.a1);
        //populateObstacles();
        newLevelCreate();

        for (int x = 0; x < 1080*WIDTH_X/1080; x++)
            for (int y = 0; y < 1920*HEIGHT_Y/1920; y ++) {
                borders[x][y] = 0;

                if (x >= marginLeft+1000*WIDTH_X/1080 || y >= marginUp+1000*HEIGHT_Y/1920 || x <= marginLeft || y <= marginUp)
                    borders[x][y] =1;
            }
    }

    public void reset (){
        int limit;
        selectedLine = 1;
        pause = 0;
        startedGame = false;

        newDotId = 0;
        newDotIdBlue = 0;
        newDotIdPurple = 0;

        drawnX = currentDotX = actualDotX = targetDotX =currentLevelX1;
        drawnXBlue = currentDotXBlue = actualDotXBlue = targetDotXBlue = currentLevelX2;
        drawnXPurple = currentDotXPurple = actualDotXPurple =targetDotXPurple= currentLevelX3;
        currentDotY = actualDotY = targetDotY = currentLevelY1;
        currentDotYBlue = actualDotYBlue = targetDotYBlue=currentLevelY2;
        currentDotYPurple = actualDotYPurple = targetDotYPurple=currentLevelY3;

        limit = dots.size();
        for (int i = 0; i < limit; i ++) {
            dots.remove(0);
        }

        limit = dots2.size();
        for (int i = 0; i < limit; i ++) {
            dots2.remove(0);
        }

        limit = dots3.size();
        for (int i = 0; i < limit; i ++) {
            dots3.remove(0);
        }

        for (int x = 0; x < 1080*WIDTH_X/1080; x++)
            for (int y = 0; y < 1920*HEIGHT_Y/1920; y ++) {
                borders[x][y] = 0;

                if (x >= marginLeft+1000*WIDTH_X/1080 || y >= marginUp+1000*HEIGHT_Y/1920 || x <= marginLeft*WIDTH_X/1080 || y <= marginUp*HEIGHT_Y/1920)
                    borders[x][y] =1;
            }

        for( int x = 0; x < 1080*WIDTH_X/1080; x ++)
            for (int y = 0; y < 1920*HEIGHT_Y/1920; y ++) {
                colors[x][y] = 0;
            }

        if (gameOver){
            gameLevel = 0;
            //newLevel = 0;
            newLevelCreate();
            gameOver = false;
            System.out.println("gameOver");
            nextLevel = false;
            score = previousScore = 100;
            newLevelTime = System.currentTimeMillis();
        }else if (newLevel == 0 || !gameOver) {
            newLevelCreate();
        }
    }

    private void newLevelCreate(){
        gameLevel ++;
        scoringAreas = new ArrayList<>();
        levelTime = 400;
        levelIsScreen = false;

        int limit = obstacles.size();
        for (int i = 0; i < limit; i ++){
            obstacles.remove(0);
        }

        if (gameLevel == 3) {
          /*  addRectangleArea(0,0,250,250,3);
            addRectangleArea(400,0,600,400,3);
            addRectangleArea(750,0,1000,250,3);
            addRectangleArea(0,400,400,600,3);
            addRectangleArea(600,400,1000,600,3);
            addRectangleArea(0,750,250,1000,3);
            addRectangleArea(400,600,600,1000,3);
            addRectangleArea(750,750,1000,1000,3);

            addRectangleArea(0,0,150,150,1);
            addRectangleArea(850,0,1000,150,1);
            addRectangleArea(0,850,150,1000,1);
            addRectangleArea(850,850,1000,1000,1);
            //obstacles.add(new Obstacle(10, Color.rgb(220,220,220), 200, 400, 7, 1));
            instertBall (10, 220, 220, 220, 100, 500, 7, 1);*/
            options.inScaled = false;
            screen = new Screen(BitmapFactory.decodeResource(getResources(), R.drawable.paulina1, options), 40, 200, 1000, 1000, 1);

            levelIsScreen = true;

            //obstacles.add(new Obstacle(10, Color.rgb(220,220,220), 540, 1000, 7, 2));
            instertBall(10,220,220,220,500,500,7,2);
        }

        if (gameLevel == 2) {
            //addRectangleArea (300,300,600,600,2);
            //addRectangleArea (500,400,600,500,1);
            options.inScaled = false;
            screen = new Screen(BitmapFactory.decodeResource(getResources(), R.drawable.girl1, options), 40, 200, 1000, 1000, 1);

            levelIsScreen = true;

            //obstacles.add(new Obstacle(10, Color.rgb(220,220,220), 540, 1000, 7, 2));
            instertBall(10,220,220,220,500,500,7,2);
        }

        if (gameLevel == 1) {

            //addRectangleArea (300,300,700,700,2);
            //addRectangleArea (500,400,600,500,1);
            //addRectangleArea (500,400,600,500,1);
            //obstacles.add(new Obstacle(10, Color.rgb(220,220,220), 340, 1000, 7, 3));
            //obstacles.add(new Obstacle(10, Color.rgb(250,250,250), 540, 1000, 7, 2));
            //screen = new Screen(BitmapFactory.decodeResource(getResources(), R.drawable.rain2), 40, 200, 3000,3000, 1);

            options.inScaled = false;
            screen = new Screen(BitmapFactory.decodeResource(getResources(), R.drawable.face1, options), 40*WIDTH_X/1080, 200*HEIGHT_Y/1920, 1000,1000, 1);
            //System.out.println("width: "+screen.getSpritesheet().getWidth());
            //screen.getSpritesheet().setDensity(DisplayMetrics.DENSITY_560);

            levelIsScreen = true;
            instertBall(10,220,220,220,400,500,7,3);
            //instertBall(10,250,250,250,500,500,7,2);
        }

        if (gameLevel == 4) {

            addRectangleArea (300,300,700,700,2);
            //addRectangleArea (500,400,600,500,1);
            //addRectangleArea (500,400,600,500,1);
            //obstacles.add(new Obstacle(10, Color.rgb(220,220,220), 340, 1000, 7, 3));
            //obstacles.add(new Obstacle(10, Color.rgb(250,250,250), 540, 1000, 7, 2));

            instertBall(10,220,220,220,400,500,7,3);
        }
    }

    private void instertBall (int height, int colA, int colB, int colC, int startX, int startY, int size, int direction){
        //obstacles.add(new Obstacle(height, Color.rgb(colA,colB,colC), startX+marginLeft, startY+marginUp, size, direction));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        obstacles.add(new Screen(BitmapFactory.decodeResource(getResources(), R.drawable.bally4, options), startX, startY, 20, 20, direction, 4));
    }

    private void addRectangleArea (int startX, int startY, int endX, int endY, int colorCode){
        int r=255, g=255, b=255;

        if (colorCode == 1) {
            r = 190;
            g = 0;
            b = 0;
        }
        else if (colorCode == 2) {
            r = 210;
            g = 0;
            b = 210;
        }
        else if (colorCode == 3) {
            r = 0;
            g = 0;
            b = 203;
        }

        scoringAreas.add(new RectPlayer(new Rect(startX+marginLeft, startY+marginUp, endX+marginLeft, endY+marginUp), Color.rgb(r, g, b)));

        for( int x = 0; x < 1080*WIDTH_X/1080; x ++)
            for (int y = 0; y < 1920*HEIGHT_Y/1920; y ++) {
                if (x >= startX*WIDTH_X/1080+marginLeft && y >= startY*HEIGHT_Y/1920+marginUp & x <= endX*WIDTH_X/1080+marginLeft && y<= endY*HEIGHT_Y/1920+marginUp)
                        colors [x][y] = colorCode; // 1 = red color code
                }
    }

   @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int widht, int height) {

    }

   @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //thread = new MainThread(getHolder(), this);
       thread = new MainThread(getHolder(), this);
        thread.setRunning(true);

       if (thread.getState() == Thread.State.NEW)
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        int counter = 0;

        while (retry && counter<1000) {
            counter ++;
            try {
                thread.setRunning(false);
                thread.join();
                retry = false;
                thread = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        int xTouch = (int)event.getX();
        int yTouch = (int)event.getY();
        boolean dotAllowed = false;

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if(gameOver && System.currentTimeMillis() - gameOverTime >= 2000){
                    System.out.println(System.currentTimeMillis() - gameOverTime);
                    reset();
                    newLevel = -1;
                }
                else if (newLevel == 0 && System.currentTimeMillis() - newLevelTime >= 2000){           // if level is completed
                    reset();
                    newLevel = -1;
                    levelCompleted = false;
                    nextLevel = true;
                }else if (!gameOver && System.currentTimeMillis() - newLevelTime >= 1000){
                    if (startedGame == true && xTouch >= marginLeft && xTouch <= marginLeft+1000*WIDTH_X/1080 && yTouch >= marginUp && yTouch <= marginUp + 1000*HEIGHT_Y/1920 && borders[xTouch][yTouch] != 1) {
                        dotAllowed = true;

                        if (pause == 0)
                        pause = -1; // pause OFF
                    }

                    if (dotAllowed == true) {
                        if (selectedLine == GRAYLINE) {
                            drawnX = currentDotX = actualDotX;
                            drawnY = currentDotY = actualDotY;
                            targetDotX = xTouch;
                            targetDotY = yTouch;
                            drawingEnabled = true;

                            if (targetDotX > currentDotX)
                                directionDrawingX = 1;       // right
                            else
                                directionDrawingX = 2;

                            if (targetDotY > currentDotY)
                                directionDrawingY = 1;      // up
                            else
                                directionDrawingY = 2;
                        } else if (selectedLine == BLUELINE) {
                            drawnXBlue = currentDotXBlue = actualDotXBlue;
                            drawnYBlue = currentDotYBlue = actualDotYBlue;
                            targetDotXBlue = xTouch;
                            targetDotYBlue = yTouch;
                            drawingEnabledBlue = true;

                            if (targetDotXBlue > currentDotXBlue)
                                directionDrawingXBlue = 1;       // right
                            else
                                directionDrawingXBlue = 2;

                            if (targetDotYBlue > currentDotYBlue)
                                directionDrawingYBlue = 1;      // up
                            else
                                directionDrawingYBlue = 2;
                        } else if (selectedLine == PURPLELINE) {
                            drawnXPurple = currentDotXPurple = actualDotXPurple;
                            drawnYPurple = currentDotYPurple = actualDotYPurple;
                            targetDotXPurple = xTouch;
                            targetDotYPurple = yTouch;
                            drawingEnabledPurple = true;

                            if (targetDotXPurple > currentDotXPurple)
                                directionDrawingXPurple = 1;       // right
                            else
                                directionDrawingXPurple = 2;

                            if (targetDotYPurple > currentDotYPurple)
                                directionDrawingYPurple = 1;      // up
                            else
                                directionDrawingYPurple = 2;
                        }
                    } else if (xTouch >= 390*WIDTH_X/1080 && xTouch <= 690*WIDTH_X/1080 && yTouch >= 1400*HEIGHT_Y/1920 && yTouch <= 1600*HEIGHT_Y/1920)
                        selectedLine = BLUELINE;
                    else if (xTouch >= 50*WIDTH_X/1080 && xTouch <= 350*WIDTH_X/1080 && yTouch >= 1400*HEIGHT_Y/1920 && yTouch <= 1600*HEIGHT_Y/1920)
                        selectedLine = GRAYLINE;
                    else if (xTouch >= 730*WIDTH_X/1080 && xTouch <= 1030*WIDTH_X/1080 && yTouch >= 1400*HEIGHT_Y/1920 && yTouch <= 1600*HEIGHT_Y/1920)
                        selectedLine = PURPLELINE;
                    else if (xTouch <= 350*WIDTH_X/1080 && yTouch > 1600*HEIGHT_Y/1920 && event.getAction() == MotionEvent.ACTION_DOWN) {
                        if (pause == -1)
                            pause = 1;
                        else if (pause == 1)
                            pause = -1;
                    }
                    else if (startedGame == false && xTouch >= 300*WIDTH_X/1080 && xTouch <= 800*WIDTH_X/1080 && yTouch >= 600*HEIGHT_Y/1920 && yTouch <= 900*HEIGHT_Y/1920) {
                        startedGame = true;
                        newLevel = -2;
                        newLevelTime = System.currentTimeMillis();
                    }
                }
                break;
        }

        return true;
    }

    public void update(){
        if(!gameOver) {
            update2();

            if (levelIsScreen)
                screen.update();

            if (levelTime <= 0) {
                if (score - previousScore >= 0 && levelCompleted == false) {
                    newLevel = 0;
                    newLevelTime = System.currentTimeMillis();
                    //System.out.println(newLevelTime);
                    levelCompleted = true;
                    previousScore = score;
                } else if (levelCompleted == false){
                    gameOverTime = System.currentTimeMillis();
                    gameOver = true;
                    startedGame = false;
                }
            }

            if (startedGame) {
                int limit = obstacles.size();

                for (int i=0; i < limit; i++) {

                    int x = obstacles.get(i).x + 10;
                    int y = obstacles.get(i).y + 10;

                    if (newLevel != 0 && pause == -1){
                        int [][] pixels = new int [3][3];
                        int areaRedAmount = 0;
                        int areaBlueAmount = 0;
                        int areaGreenAmount = 0;

                        for (int j=0; j<3; j++)
                            for (int k=0; k<3; k++)
                            {
                                if (x-marginLeft >0 && x -marginLeft<1000 && y-marginUp>0 && y-marginUp<1000)
                                    pixels [j][k] = screen.getSpritesheet().getPixel(x-marginLeft-1+j, y-marginUp-1+k);
                                else
                                    pixels [j] [k] = -1;
                            }

                        for (int k=0; k<3; k++)
                            for (int l=0; l<3; l++)
                                if (pixels[k][l] != -1) {
                                    areaRedAmount += Color.red(pixels[k][l]);
                                    areaBlueAmount += Color.blue(pixels[k][l]);
                                    areaGreenAmount += Color.green(pixels[k][l]);
                                }

                        if (areaRedAmount > 2 * (areaGreenAmount + areaBlueAmount)) {
                            System.out.println("Area is mostly Red. " + areaRedAmount);
                            score += 0.2;
                        }
                        System.out.println(" x: " + x + " y: " + y + "R : " + areaRedAmount);

                    }

                    if (pause == -1 && levelCompleted == false)
                        levelTime -= 0.2;
                }
            }
        }
        else
            update2();
    }

    public void update2 (){
        int elapsedTime = (int)(System.currentTimeMillis() - startTime);
        startTime = System.currentTimeMillis();
        int speed = 2;
        int obstacleX, obstacleY;

        if (startedGame == true && pause == -1) {
            //levelTime -= 0.2;

            for (Screen ob : obstacles) {
                // * elapsedTime);
                ob.update();

                obstacleX = ob.getRectangle().centerX();
                obstacleY = ob.getRectangle().centerY();




                if (levelIsScreen) {
                    /*int pixel = screen.getSpritesheet().getPixel(obstacleX-marginLeft, obstacleY-marginUp);

                    int [][] pixels = new int [3][3];
                    int areaRedAmount = 0;
                    int areaBlueAmount = 0;
                    int areaGreenAmount = 0;
                    boolean isAreaRed;

                    for (int j=0; j<3; j++)
                        for (int k=0; k<3; k++)
                        {
                            if (obstacleX-marginLeft >0 && obstacleX -marginLeft<1000 && obstacleY-marginUp>0 && obstacleY-marginUp<1000)
                               pixels [j][k] = screen.getSpritesheet().getPixel(obstacleX-marginLeft-1+j, obstacleY-marginUp-1+k);
                            else
                                pixels [j] [k] = -1;
                        }

                    for (int k=0; k<3; k++)
                        for (int l=0; l<3; l++)
                            if (pixels[k][l] != -1) {
                                areaRedAmount += Color.red(pixels[k][l]);
                                areaBlueAmount += Color.blue(pixels[k][l]);
                                areaGreenAmount += Color.green(pixels[k][l]);
                            }*/

                    //if (areaRedAmount > 4 * (areaGreenAmount + areaBlueAmount))
                      //  System.out.println("Area is mostly Red. " + areaRedAmount);

                    //System.out.println(Color.red(pixel) + " " + Color.green(pixel) + " " + Color.blue(pixel));
                }

                int border = 0;

                if (ob.getDirection() == 1)
                    border = borders[obstacleX + (int) speed][obstacleY + (int) speed];
                else if (ob.getDirection() == 2)
                    border = borders[obstacleX + (int) speed][obstacleY - (int) speed];
                else if (ob.getDirection() == 3)
                    border = borders[obstacleX - (int) speed][obstacleY - (int) speed];
                else if (ob.getDirection() == 4)
                    border = borders[obstacleX - (int) speed][obstacleY + (int) speed];

                if (border == 0) {      // empty space
                    if (ob.getDirection() == 1) {
                        ob.incrementX(speed);
                        ob.incrementY(speed);
                    } else if (ob.getDirection() == 2) {
                        ob.incrementX(speed);
                        ob.decreaseY(speed);
                    } else if (ob.getDirection() == 3) {
                        ob.decreaseX(speed);
                        ob.decreaseY(speed);
                    } else if (ob.getDirection() == 4) {
                        ob.decreaseX(speed);
                        ob.incrementY(speed);
                    }
                } else {
                    if (ob.getDirection() == 1 && borders[obstacleX + (int) speed][obstacleY - (int) speed] >= 1 && borders[obstacleX + (int) speed][obstacleY + (int) speed] >= 1) {
                        ob.setDirection(4);
                    } else if (ob.getDirection() == 1 && borders[obstacleX - (int) speed][obstacleY + (int) speed] >= 1 && borders[obstacleX + (int) speed][obstacleY + (int) speed] >= 1) {
                        ob.setDirection(2);
                    } else if (ob.getDirection() == 1)
                        ob.setDirection(3);
                    else if (ob.getDirection() == 2 && borders[obstacleX + (int) speed][obstacleY - (int) speed] >= 1 && borders[obstacleX + (int) speed][obstacleY + (int) speed] >= 1) {
                        ob.setDirection(3);
                    } else if (ob.getDirection() == 2 && borders[obstacleX - (int) speed][obstacleY - (int) speed] >= 1 && borders[obstacleX + (int) speed][obstacleY - (int) speed] >= 1) {
                        ob.setDirection(1);
                    } else if (ob.getDirection() == 2)
                        ob.setDirection(4);
                    else if (ob.getDirection() == 3 && borders[obstacleX - (int) speed][obstacleY + (int) speed] >= 1 && borders[obstacleX - (int) speed][obstacleY - (int) speed] >= 1) {
                        ob.setDirection(2);
                    } else if (ob.getDirection() == 3 && borders[obstacleX - (int) speed][obstacleY - (int) speed] >= 1 && borders[obstacleX + (int) speed][obstacleY - (int) speed] >= 1) {
                        ob.setDirection(4);
                    } else if (ob.getDirection() == 3)
                        ob.setDirection(1);
                    else if (ob.getDirection() == 4 && borders[obstacleX - (int) speed][obstacleY + (int) speed] >= 1 && borders[obstacleX + (int) speed][obstacleY + (int) speed] >= 1) {
                        ob.setDirection(3);
                    } else if (ob.getDirection() == 4 && borders[obstacleX - (int) speed][obstacleY + (int) speed] >= 1 && borders[obstacleX - (int) speed][obstacleY - (int) speed] >= 1) {
                        ob.setDirection(1);
                    } else
                        ob.setDirection(2);
                }
            }
        }
    }

    @Override
    public void draw(Canvas canvas)
    {
        int colA = 0, colB = 0, colC = 0;

        if (WIDTH_X != 1080)
            canvas.setDensity(160*WIDTH_X/1080);

        if (canvas != null){
            super.draw(canvas);
            canvas.drawColor(Color.rgb(16,16,16));

            if (levelIsScreen == true) {

                screen.draw(canvas);
            }

            draw2(canvas);
        }

        if (startedGame == true || gameOver) {
            for (int i = 0; i < 10; i++) {
                if (drawingEnabled == true && pause == -1) {// && obstacleManager.borders[drawnX][drawnY] != 2) {
                    int equationType;

                    if (abs(targetDotX - currentDotX) > abs(targetDotY - currentDotY))
                        equationType = 2; // y counting
                    else
                        equationType = 1; // x counting

                    if (equationType == 1 && currentDotY != targetDotY)
                        drawnX = (drawnY - currentDotY) * (currentDotX - targetDotX) / (currentDotY - targetDotY) + currentDotX;
                    else if (equationType == 2 && targetDotX != currentDotX)
                        drawnY = drawnX * (currentDotY - targetDotY) / (currentDotX - targetDotX) + (currentDotY - currentDotX * (currentDotY - targetDotY) / (currentDotX - targetDotX));

                    dots.add(new Obstacle(50, Color.rgb(0, 255, 255), drawnX, drawnY, 4, 1));
                    //dots.add(new Screen(BitmapFactory.decodeResource(getResources(), R.drawable.dot2), drawnX, drawnY, 30, 30, 1));

                    for (int k = -3; k < 3; k++)
                        for (int l = -3; l < 3; l++)
                            if (borders[drawnX + k][drawnY + l] != 1)
                                borders[drawnX + k][drawnY + l] += 2;

                    int dotId = dots.size();
                    newDotId = dotId;
                    actualDotX = drawnX;
                    actualDotY = drawnY;

                    if (dotId > 500) {

                        for (int k = -3; k < 3; k++)
                            for (int l = -3; l < 3; l++)
                                if (borders[dots.get(0).getStartX() + k][dots.get(0).getStartY() + l] >= 2)
                                    borders[dots.get(0).getStartX() + k][dots.get(0).getStartY() + l] -= 2;

                        dots.remove(0);
                    }

                    if (drawnX == targetDotX) {
                        currentDotX = targetDotX;
                        currentDotY = targetDotY;
                        drawingEnabled = false;
                    } else {
                        if (equationType == 2) {
                            if (directionDrawingX == 1)
                                drawnX++;
                            else
                                drawnX--;
                        } else {
                            if (directionDrawingY == 1)
                                drawnY++;
                            else
                                drawnY--;
                        }
                    }
                }
            }

            for (int i = 0; i < 10; i++) {
                if (drawingEnabledBlue == true && pause == -1) {// && obstacleManager.borders[drawnX][drawnY] != 2) {
                    int equationType;

                    if (abs(targetDotXBlue - currentDotXBlue) > abs(targetDotYBlue - currentDotYBlue))
                        equationType = 2; // y counting
                    else
                        equationType = 1; // x counting

                    if (equationType == 1 && currentDotYBlue != targetDotYBlue)
                        drawnXBlue = (drawnYBlue - currentDotYBlue) * (currentDotXBlue - targetDotXBlue) / (currentDotYBlue - targetDotYBlue) + currentDotXBlue;
                    else if (equationType == 2 && targetDotXBlue != currentDotXBlue)
                        drawnYBlue = drawnXBlue * (currentDotYBlue - targetDotYBlue) / (currentDotXBlue - targetDotXBlue) + (currentDotYBlue - currentDotXBlue * (currentDotYBlue - targetDotYBlue) / (currentDotXBlue - targetDotXBlue));

                    dots2.add(new Obstacle(50, Color.rgb(255, 255, 0), drawnXBlue, drawnYBlue, 4, 1));

                    for (int k = -3; k < 3; k++)
                        for (int l = -3; l < 3; l++)
                            if (borders[drawnXBlue + k][drawnYBlue + l] != 1)
                                borders[drawnXBlue + k][drawnYBlue + l] += 2;

                    int dotId = dots2.size();
                    newDotIdBlue = dotId;
                    actualDotXBlue = drawnXBlue;
                    actualDotYBlue = drawnYBlue;

                    if (dotId > 500) {
                        for (int k = -3; k < 3; k++)
                            for (int l = -3; l < 3; l++)
                                if (borders[dots2.get(0).getStartX() + k][dots2.get(0).getStartY() + l] >= 2)
                                    borders[dots2.get(0).getStartX() + k][dots2.get(0).getStartY() + l] -= 2;

                        dots2.remove(0);
                    }

                    if (drawnXBlue == targetDotXBlue) {
                        currentDotXBlue = targetDotXBlue;
                        currentDotYBlue = targetDotYBlue;
                        drawingEnabledBlue = false;
                    } else {
                        if (equationType == 2) {
                            if (directionDrawingXBlue == 1)
                                drawnXBlue++;
                            else
                                drawnXBlue--;
                        } else {
                            if (directionDrawingYBlue == 1)
                                drawnYBlue++;
                            else
                                drawnYBlue--;
                        }
                    }
                }
            }

            for (int i = 0; i < 10; i++) {
                if (drawingEnabledPurple == true && pause == -1) {// && obstacleManager.borders[drawnX][drawnY] != 2) {
                    int equationType;

                    if (abs(targetDotXPurple - currentDotXPurple) > abs(targetDotYPurple - currentDotYPurple))
                        equationType = 2; // y counting
                    else
                        equationType = 1; // x counting

                    if (equationType == 1 && currentDotYPurple != targetDotYPurple)
                        drawnXPurple = (drawnYPurple - currentDotYPurple) * (currentDotXPurple - targetDotXPurple) / (currentDotYPurple - targetDotYPurple) + currentDotXPurple;
                    else if (equationType == 2 && targetDotXPurple != currentDotXPurple)
                        drawnYPurple = drawnXPurple * (currentDotYPurple - targetDotYPurple) / (currentDotXPurple - targetDotXPurple) + (currentDotYPurple - currentDotXPurple * (currentDotYPurple - targetDotYPurple) / (currentDotXPurple - targetDotXPurple));

                    dots3.add(new Obstacle(50, Color.rgb(255, 255, 255), drawnXPurple, drawnYPurple, 4, 1));

                    for (int k = -3; k < 3; k++)
                        for (int l = -3; l < 3; l++)
                            if (borders[drawnXPurple + k][drawnYPurple + l] != 1)
                                borders[drawnXPurple + k][drawnYPurple + l] += 2;

                    int dotId = dots3.size();
                    newDotIdPurple = dotId;
                    actualDotXPurple = drawnXPurple;
                    actualDotYPurple = drawnYPurple;

                    if (dotId > 500) {
                        for (int k = -3; k < 3; k++)
                            for (int l = -3; l < 3; l++)
                                if (borders[dots3.get(0).getStartX() + k][dots3.get(0).getStartY() + l] >= 2)
                                    borders[dots3.get(0).getStartX() + k][dots3.get(0).getStartY() + l] -= 2;

                        dots3.remove(0);
                    }

                    if (drawnXPurple == targetDotXPurple) {
                        currentDotXPurple = targetDotXPurple;
                        currentDotYPurple = targetDotYPurple;
                        drawingEnabledPurple = false;
                    } else {
                        if (equationType == 2) {
                            if (directionDrawingXPurple == 1)
                                drawnXPurple++;
                            else
                                drawnXPurple--;
                        } else {
                            if (directionDrawingYPurple == 1)
                                drawnYPurple++;
                            else
                                drawnYPurple--;
                        }
                    }
                }
            }

            for (int i = 0; i < newDotId - 1; i += 5) {
                if (canvas != null)
                dots.get(i).draw(canvas);
            }

            for (int i = 0; i < newDotIdBlue - 1; i += 5) {
                if (canvas != null)
                dots2.get(i).draw(canvas);
            }

            for (int i = 0; i < newDotIdPurple - 1; i += 5) {
                if (canvas != null)
                dots3.get(i).draw(canvas);
            }

            if (flickeringPhase <= 4)
                colA = colB = colC = 32;
            else if (flickeringPhase <= 8)
                colA = colB = colC = 64;
            else if (flickeringPhase <= 12)
                colA = colB = colC = 96;
            else if (flickeringPhase <= 16)
                colA = colB = colC = 128;
            else if (flickeringPhase <= 20)
                colA = colB = colC = 160;
            else if (flickeringPhase <= 24) {
                colA = colB = colC = 192;
                flickeringPhase = 1;
            }

            flickeringPhase++;

            lightBorder1.setColor(Color.rgb(colA, colB, colC));
            lightBorder2.setColor(Color.rgb(colA, colB, colC));
            lightBorder3.setColor(Color.rgb(colA, colB, colC));
            lightBorder4.setColor(Color.rgb(colA, colB, colC));

            if (selectedLine == 1) {
                lightBorder1.getRectangle().set(20*WIDTH_X/1080, 1440*HEIGHT_Y/1920, 380*WIDTH_X/1080, 1470*HEIGHT_Y/1920);
                lightBorder2.getRectangle().set(20*WIDTH_X/1080, 1530*HEIGHT_Y/1920, 380*WIDTH_X/1080, 1560*HEIGHT_Y/1920);
                lightBorder3.getRectangle().set(20*WIDTH_X/1080, 1470*HEIGHT_Y/1920, 50*WIDTH_X/1080, 1530*HEIGHT_Y/1920);
                lightBorder4.getRectangle().set(350*WIDTH_X/1080, 1470*HEIGHT_Y/1920, 380*WIDTH_X/1080, 1530*HEIGHT_Y/1920);
            } else if (selectedLine == 2) {
                lightBorder1.getRectangle().set(360*WIDTH_X/1080, 1440*HEIGHT_Y/1920, 720*WIDTH_X/1080, 1470*HEIGHT_Y/1920);
                lightBorder2.getRectangle().set(360*WIDTH_X/1080, 1530*HEIGHT_Y/1920, 720*WIDTH_X/1080, 1560*HEIGHT_Y/1920);
                lightBorder3.getRectangle().set(360*WIDTH_X/1080, 1470*HEIGHT_Y/1920, 390*WIDTH_X/1080, 1530*HEIGHT_Y/1920);
                lightBorder4.getRectangle().set(690*WIDTH_X/1080, 1470*HEIGHT_Y/1920, 720*WIDTH_X/1080, 1530*HEIGHT_Y/1920);
            } else if (selectedLine == 3) {
                lightBorder1.getRectangle().set(700*WIDTH_X/1080, 1440*HEIGHT_Y/1920, 1060*WIDTH_X/1080, 1470*HEIGHT_Y/1920);
                lightBorder2.getRectangle().set(700*WIDTH_X/1080, 1530*HEIGHT_Y/1920, 1060*WIDTH_X/1080, 1560*HEIGHT_Y/1920);
                lightBorder3.getRectangle().set(700*WIDTH_X/1080, 1470*HEIGHT_Y/1920, 730*WIDTH_X/1080, 1530*HEIGHT_Y/1920);
                lightBorder4.getRectangle().set(1030*WIDTH_X/1080, 1470*HEIGHT_Y/1920, 1060*WIDTH_X/1080, 1530*HEIGHT_Y/1920);
            }

            if (selectedLine >0) {
                lightBorder1.draw(canvas);
                lightBorder2.draw(canvas);
                lightBorder3.draw(canvas);
                lightBorder4.draw(canvas);
            }

            line1.draw(canvas);
            line2.draw(canvas);
            line3.draw(canvas);


        }

        if (newLevel == 0){
            Paint paint = new Paint();
            paint.setTextSize(100);
            paint.setColor(Color.GREEN);
            //drawCenterText(canvas, paint, "Level completed!");
            canvas.drawText("Level completed!", 200, 500+marginUp, paint);
        }

        if (pause == 1){
            Paint paint = new Paint();
            paint.setTextSize(100);
            paint.setColor(Color.rgb(250,150,0));
            //drawCenterText(canvas, paint, "Pause ON");
            canvas.drawText("Pause ON", 300, 300+marginUp, paint);
        }

        if (newLevel == -1) {
            Paint paint = new Paint();
            paint.setTextSize(100);

            if (elapsedPeriod < 5)
                paint.setColor(Color.rgb(250, 32, 32));
            else if (elapsedPeriod < 10)
                paint.setColor(Color.rgb(32, 32, 250));
            else
                paint.setColor(Color.rgb(32, 250, 32));

            elapsedPeriod ++;


            if (elapsedPeriod == 15)
                elapsedPeriod = 0;

            if (!nextLevel) {
                canvas.drawText("Start Game", 300, 700, paint);
                canvas.drawText("Hi Score: " + loadedText , 300, 1000, paint);
            }
            else
                canvas.drawText("Level " + gameLevel, 400, 700, paint);
        }

        if (gameOver) {
            Paint paint = new Paint();
            paint.setTextSize(100);
            paint.setColor(Color.MAGENTA);
            canvas.drawText("Game Over", 300, 800, paint);
        }
    }

    public void draw2 (Canvas canvas){

        border1.draw(canvas);
        border2.draw(canvas);
        border3.draw(canvas);
        border4.draw(canvas);

        if (startedGame == true || gameOver) {
            //scoringAreaRed1.draw(canvas);

            for (RectPlayer scoringArea : scoringAreas)
                scoringArea.draw(canvas);

            startLine1.draw(canvas);
            startLine2.draw(canvas);
            startLine3.draw(canvas);

            for (Screen ob: obstacles)
                ob.draw(canvas);

            Paint paint = new Paint();
            paint.setTextSize(70);

            if(score >= previousScore)
                paint.setColor(Color.rgb(255, 32, 32));
            else
                paint.setColor(Color.rgb(210, 0, 210));

            canvas.drawText("S:" + (int)score, 250, 150, paint);
            paint.setColor(Color.rgb(210, 0, 210));
            canvas.drawText("P:" + (int)previousScore, 500, 150, paint);
            paint.setColor(Color.rgb(128, 64, 64));
            canvas.drawText("L:" + gameLevel, 50, 150, paint);
            paint.setColor(Color.rgb(32, 128, 128));
            canvas.drawText("T:" + (int) levelTime, 850, 150, paint);

            paint.setColor(Color.rgb(250, 160, 0));
            canvas.drawText("PAUSE", 90, 1700, paint);

            paint.setColor(Color.rgb(250, 250, 250));
            canvas.drawText("BONUS", 420, 1700, paint);
        }
    }

  /*  private void drawCenterText(Canvas canvas, Paint paint, String text) {
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.getClipBounds(r);
        int cHeight = r.height();
        int cWidth = r.width();
        paint.getTextBounds(text, 0, text.length(), r);
        float x = cWidth / 2f - r.width() / 2f - r.left;
        float y = cHeight / 2f + r.height() / 2f - r.bottom;
        canvas.drawText(text, x, y, paint);
    }*/
}