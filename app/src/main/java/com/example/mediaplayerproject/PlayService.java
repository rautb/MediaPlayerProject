package com.example.mediaplayerproject;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PlayService extends AppCompatActivity {

    Button pause,prev,next;
    TextView textView,tv1,tv2;
    SeekBar seekBar;

    private double startTime = 0;
    private double finalTime = 0;
    private Handler myHandler = new Handler();
    private int forwardTime = 5000;
    private int backwardTime = 5000;

    static MediaPlayer myMediaPlayer;
    int pos;
    String sname;

    ArrayList<File> mySongs;
    Thread updateSeekBar;

    public static int oneTimeOnly = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_service);

        next=(Button)findViewById(R.id.next);
        prev=(Button)findViewById(R.id.prev);
        pause=(Button)findViewById(R.id.pause);
        textView=(TextView) findViewById(R.id.songLabel);
        tv2=(TextView) findViewById(R.id.tv2);
        tv1=(TextView) findViewById(R.id.tv1);
        seekBar=(SeekBar) findViewById(R.id.seekbar);

        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        updateSeekBar=new Thread(){

            @Override
            public  void run(){

                int totalDuration=myMediaPlayer.getDuration();
                int currentPosition=0;

                while(currentPosition<totalDuration){
                    try{
                        sleep(500);
                        currentPosition=myMediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                    }
                    catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }

            }
        };

        if(myMediaPlayer!=null){
            myMediaPlayer.stop();
            myMediaPlayer.release();
        }

        Intent i=getIntent();
        Bundle bundle=i.getExtras();

        mySongs=(ArrayList) bundle.getParcelableArrayList("songs");

        sname=mySongs.get(pos).getName().toString();

        String songName= i.getStringExtra("songname");

        textView.setText(songName);
        textView.setSelected(true);

        pos= bundle.getInt("posn",0);

        Uri u=Uri.parse(mySongs.get(pos).toString());

        myMediaPlayer= MediaPlayer.create(getApplicationContext(),u);

        myMediaPlayer.start();
        seekBar.setMax(myMediaPlayer.getDuration());

        updateSeekBar.start();





        finalTime =myMediaPlayer.getDuration();
        startTime = myMediaPlayer.getCurrentPosition();

        if (oneTimeOnly == 0) {
            seekBar.setMax((int) finalTime);
            oneTimeOnly = 1;
        }

        tv2.setText(String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime)))
        );

        tv1.setText(String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime)))
        );

        seekBar.setProgress((int)startTime);
        myHandler.postDelayed(UpdateSongTime,100);




        seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        seekBar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary),PorterDuff.Mode.SRC_IN);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                myMediaPlayer.seekTo(seekBar.getProgress());

            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                seekBar.setMax(myMediaPlayer.getDuration());

                if(myMediaPlayer.isPlaying()){

                    pause.setBackgroundResource(R.drawable.icon_play);
                    myMediaPlayer.pause();
                }

                else {
                    pause.setBackgroundResource(R.drawable.icon_paues);
                    myMediaPlayer.start();
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myMediaPlayer.stop();
                myMediaPlayer.release();
                pos=((pos+1)%mySongs.size());

                Uri u= Uri.parse(mySongs.get(pos).toString());

                myMediaPlayer=MediaPlayer.create(getApplicationContext(),u);

                sname=mySongs.get(pos).getName().toString();
                textView.setText(sname);

                myMediaPlayer.start();
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myMediaPlayer.stop();
                myMediaPlayer.release();

                pos=((pos-1)<0)?(mySongs.size()-1):(pos-1);
                Uri u=Uri.parse(mySongs.get(pos).toString());
                myMediaPlayer=MediaPlayer.create(getApplicationContext(),u);

                sname=mySongs.get(pos).getName().toString();
                textView.setText(sname);

                myMediaPlayer.start();


            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()== android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    public double getStartTime() {
        return startTime;
    }

    public double getFinalTime() {
        return finalTime;
    }

    public int getForwardTime() {
        return forwardTime;
    }

    public int getBackwardTime() {
        return backwardTime;
    }


    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = myMediaPlayer.getCurrentPosition();
            tv1.setText(String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) startTime)))
            );
            seekBar.setProgress((int)startTime);
            myHandler.postDelayed(this, 100);
        }
    };
}


