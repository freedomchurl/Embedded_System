package com.example.caucse.Embedded_Final_Project1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("7segment");
    }
    public native String SSegmentWrite(int data);

    public native int SSegmentIOCtlHseg(byte [] arr);

    private EditText mEditText;
    private Button mButtonStart;
    private Button mButtonClock;
    private Button mButtonNormal;

    private TimerThread mTimerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        this.mEditText = (EditText) findViewById(R.id.editText);
        this.mButtonClock = (Button) findViewById(R.id.button_clock_form);
        this.mButtonStart = (Button)findViewById(R.id.button_count_start);
        this.mButtonNormal = (Button)findViewById(R.id.button_normal_form);

        mButtonStart.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v)
            {
                if(mTimerThread != null)
                {
                    if(mTimerThread.isAlive())
                    {
                        mTimerThread.setThreadShouldStop(true);
                        try
                        {
                            mTimerThread.join();
                        }catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    mTimerThread = null;
                }

                int msec = Integer.parseInt(mEditText.getText().toString());
                mTimerThread = new TimerThread();
                mTimerThread.setmTimeMillisec(msec);
                mTimerThread.start();
            }
        });
        mButtonClock.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
                byte hseg[] = {0,0,1,0,1,0};
                SSegmentIOCtlHseg(hseg);
            }
        });
        mButtonNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte hseg[] = {0,0,0,0,0,0};
                SSegmentIOCtlHseg(hseg);
            }
        });

        SSegmentWrite(0);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */

    class TimerThread extends Thread{
        private int mTimeMillisec = 0;
        private boolean mShouldStop = false;

        public void setmTimeMillisec(int msec)
        {
            mTimeMillisec = msec;
        }

        public void setThreadShouldStop(boolean shouldStop)
        {
            mShouldStop = shouldStop;
        }

        public void run()
        {
            long timeEnd = System.currentTimeMillis() + mTimeMillisec;
            setThreadShouldStop(false);
            do {
                long remain = timeEnd - System.currentTimeMillis();
                SSegmentWrite((int) remain /10);

            }while(!mShouldStop && timeEnd > System.currentTimeMillis());
        }
    }

}
