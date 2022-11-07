package ece.course.pedometer;



//EESM 5060 LAB ASSIGNMENT 1
//BY MIN TIANHAO && HUANG JIAXI





import android.content.Context;
        import android.hardware.Sensor;
        import android.hardware.SensorEvent;
        import android.hardware.SensorEventListener;
        import android.hardware.SensorManager;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Message;

// AccelerometerSensor -> PedometerSensor
public class PedometerSensor implements SensorEventListener {
    public final static String TAG_VALUE_DX = "tagValueDx";
    public final static String TAG_VALUE_DY = "tagValueDy";
    public final static String TAG_VALUE_DZ = "tagValueDz";

    private boolean isStarted = false;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Handler mHandler;

    public void startListening() {
        if (isStarted)
            return;
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        isStarted = true;
    }
    public void stopListening() {
        if (!isStarted)
            return;
        mSensorManager.unregisterListener(this);
        isStarted = false;
    }


    public PedometerSensor(Context context, Handler handler) {
        mHandler = handler;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() !=
                Sensor.TYPE_ACCELEROMETER)
            return;
        float dx = sensorEvent.values[0];
        float dy = sensorEvent.values[1];
        float dz = sensorEvent.values[2];
        if (mHandler != null) {
            Message message = mHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putFloat(TAG_VALUE_DX, dx);
            bundle.putFloat(TAG_VALUE_DY, dy);
            bundle.putFloat(TAG_VALUE_DZ, dz);
            message.setData(bundle);
            mHandler.sendMessage(message);
        }
    }


}
