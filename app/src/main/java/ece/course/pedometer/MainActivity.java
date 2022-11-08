package ece.course.pedometer;



//EESM 5060 LAB ASSIGNMENT 1
//BY MIN TIANHAO && HUANG JIAXI





import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private final static float MAX_GRAVITY = 9.82f;
    private final static float MAX_FORCE = (float) (Math.sqrt(MAX_GRAVITY*MAX_GRAVITY*2));
    private final static float DETECT_THRESHOLD = 0.1f;
    private float mX = -100.0f;
    private float mY = -100.0f;
    private float mZ = -100.0f;
    private float mF = 0.0f;
    private int mWalkCnt = 0;
    private boolean mWalkCnted = false;
    private int mJmpCnt = 0;
    private int mJmpTimeCnt = 0;
    private boolean mJmpCnted = false;
    private final static int JMP_TIME_THRESHOLD = 5;

    private DisplayView mDisplayView;
    private PedometerSensor mPedometerSensor;

    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;



    @SuppressLint("HandlerLeak")
    @Override
    public void onCreate(Bundle savedInstanceState) {

        mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDisplayView = (DisplayView)findViewById(R.id.mDisplayView);
        mPedometerSensor = new PedometerSensor(this, new Handler(){
            @Override
            public void handleMessage(Message msg) {
                float tmpX = msg.getData().getFloat(PedometerSensor.TAG_VALUE_DX);
                float tmpY = -msg.getData().getFloat(PedometerSensor.TAG_VALUE_DY);
                float tmpZ = msg.getData().getFloat(PedometerSensor.TAG_VALUE_DZ);
                if (tmpX - mX > DETECT_THRESHOLD || tmpX - mX < -DETECT_THRESHOLD ||
                        tmpY - mY > DETECT_THRESHOLD || tmpY - mY < -DETECT_THRESHOLD ||
                        tmpZ - mZ > DETECT_THRESHOLD || tmpZ - mZ < -DETECT_THRESHOLD) {
                    mX = tmpX;
                    mY = tmpY;
                    mZ = tmpZ;
                    mF = (float) Math.sqrt(mX*mX+mY*mY+mZ*mZ);
                    TextView tvValueX = (TextView) findViewById(R.id.tvValueX);
                    TextView tvValueY = (TextView) findViewById(R.id.tvValueY);
                    TextView tvValueZ = (TextView) findViewById(R.id.tvValueZ);
                    TextView tvValueF = (TextView) findViewById(R.id.tvValueF);
                    TextView tvValueWC = (TextView) findViewById(R.id.tvValueWC);
                    TextView tvValueJC = (TextView) findViewById(R.id.tvValueJC);
                    tvValueX.setText("" + mX);
                    tvValueY.setText("" + mY);
                    tvValueZ.setText("" + mZ);
                    tvValueF.setText("" + mF);
                    tvValueWC.setText("" + mWalkCnt);
                    tvValueJC.setText("" + mJmpCnt);
                    mDisplayView.setPtr(0, (float) ((mF - 9.81)/ MAX_FORCE));

                    // Walk Count, Verified to be accurate
                    if ((mF - 9.81) > 6 && mWalkCnted == false) {
                        mWalkCnted = true;
                        mWalkCnt = mWalkCnt + 1;
                    }
                    if ((mF - 9.81) <= 6 && mWalkCnted == true) {
                        mWalkCnted = false;
                    }

                    // Jump Count, Verified to be accurate
                    if (mF < 2 && mJmpCnted == false) {
                        mJmpTimeCnt = 0;
                        mJmpCnted = true;
                        mJmpCnt = mJmpCnt + 1;
                        mWalkCnt = mWalkCnt - 2;
                        if (mWalkCnt < 0) {
                            mWalkCnt = 0;
                        }
                    }
                    if (mF >= 2 && mJmpCnted == true) {
                        mJmpTimeCnt = mJmpTimeCnt + 1;
                        if (mJmpTimeCnt > JMP_TIME_THRESHOLD) {
                            mJmpCnted = false;
                        }
                    }

                }
                Button btnClear =(Button) findViewById(R.id.btnClear);
                btnClear.setOnClickListener(new View.OnClickListener() {//onclicklistener
                    public void onClick(View view) {
                        mWalkCnt = 0;
                        TextView tvValueWC = (TextView) findViewById(R.id.tvValueWC);
                        tvValueWC.setText("" + mWalkCnt);
                        mJmpCnt = 0;
                        TextView tvValueJC = (TextView) findViewById(R.id.tvValueJC);
                        tvValueJC.setText("" + mJmpCnt);
                    }


                });




            }



        });



    }

    public synchronized void onResume() {
        super.onResume();
        mWakeLock.acquire();
        if (mPedometerSensor != null) {
            mPedometerSensor.startListening();
        }
    }
    public synchronized void onPause() {
        mWakeLock.release();
        if (mPedometerSensor != null) {
            mPedometerSensor.stopListening();
        }
        super.onPause();
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuPtrBall :
                mDisplayView.setPtrType(DisplayView.TYPE_BALL);
                return true;
            case R.id.menuPtrSquare :
                mDisplayView.setPtrType(DisplayView.TYPE_SQUARE);
                return true;
            case R.id.menuPtrDiamond :
                mDisplayView.setPtrType(DisplayView.TYPE_DIAMOND);
                return true;
            case R.id.menuPtrArc :
                mDisplayView.setPtrType(DisplayView.TYPE_ARC);
                return true;
            case R.id.menuPtrRed :
                mDisplayView.setPtrColor(Color.RED);
                return true;
            case R.id.menuPtrBlue :
                mDisplayView.setPtrColor(Color.BLUE);
                return true;
            case R.id.menuPtrGreen :
                mDisplayView.setPtrColor(Color.GREEN);
                return true;
            case R.id.menuPtrWhite :
                mDisplayView.setPtrColor(Color.WHITE);
                return true;
        }
        return false;
    }

}