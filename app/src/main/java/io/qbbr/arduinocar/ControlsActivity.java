package io.qbbr.arduinocar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class ControlsActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    public static Handler handler;
    private StringBuilder sb = new StringBuilder();
    private ConnectedThread connectedThread;

    public static final char CMD_FORWARD_LEFT = 'l';
    public static final char CMD_FORWARD = 'f';
    public static final char CMD_FORWARD_RIGHT = 'r';
    public static final char CMD_BACKWARD = 'b';
    public static final char CMD_BACKWARD_LEFT = 'h';
    public static final char CMD_BACKWARD_RIGHT = 'j';
    public static final char CMD_ROTATE_LEFT = 'n';
    public static final char CMD_ROTATE_RIGHT = 'm';
    public static final char CMD_STOP = 's';
    // CMD_SPEED  0 - 9
    public static final char CMD_GET_SPEED = 'e';
    public static final char CMD_SERVO_MID = 'w';
    public static final char CMD_SERVO_LEFT = 'a';
    public static final char CMD_SERVO_RIGHT = 'd';
    public static final char CMD_GET_DISTANCE = 'g';

    ImageButton btnForwardLeft;
    ImageButton btnForward;
    ImageButton btnForwardRight;
    ImageButton btnRotateLeft;
    ImageButton btnStop;
    ImageButton btnRotateRight;
    ImageButton btnBackwardLeft;
    ImageButton btnBackward;
    ImageButton btnBackwardRight;

    TextView tvDistance;
    Button btnDistance;

    RadioButton radioBtnServoLeft;
    RadioButton radioBtnServoMid;
    RadioButton radioBtnServoRight;

    SeekBar seekBarSpeed;
    TextView tvSpeed;

    TextView tvArduino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controls);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btnForward = findViewById(R.id.btnForward);
        btnForward.setOnClickListener(this);
        btnForwardLeft = findViewById(R.id.btnForwardLeft);
        btnForwardLeft.setOnClickListener(this);
        btnForwardRight = findViewById(R.id.btnForwardRight);
        btnForwardRight.setOnClickListener(this);
        btnRotateLeft = findViewById(R.id.btnRotateLeft);
        btnRotateLeft.setOnClickListener(this);
        btnStop = findViewById(R.id.btnStop);
        btnStop.setOnClickListener(this);
        btnRotateRight = findViewById(R.id.btnRotateRight);
        btnRotateRight.setOnClickListener(this);
        btnBackwardLeft = findViewById(R.id.btnBackwardLeft);
        btnBackwardLeft.setOnClickListener(this);
        btnBackward = findViewById(R.id.btnBackward);
        btnBackward.setOnClickListener(this);
        btnBackwardRight = findViewById(R.id.btnBackwardRight);
        btnBackwardRight.setOnClickListener(this);

        tvDistance = findViewById(R.id.tvDistance);
        btnDistance = findViewById(R.id.btnDistance);
        btnDistance.setOnClickListener(this);

        radioBtnServoLeft = findViewById(R.id.radioBtnServoLeft);
        radioBtnServoLeft.setOnClickListener(this);
        radioBtnServoMid = findViewById(R.id.radioBtnServoMid);
        radioBtnServoMid.setOnClickListener(this);
        radioBtnServoMid.setChecked(true);
        radioBtnServoRight = findViewById(R.id.radioBtnServoRight);
        radioBtnServoRight.setOnClickListener(this);

        tvSpeed = findViewById(R.id.tvSpeed);
        seekBarSpeed = findViewById(R.id.seekBarSpeed);
        seekBarSpeed.setOnSeekBarChangeListener(this);

        tvArduino = findViewById(R.id.tvArduino);

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case ConnectedThread.RECIEVE_MESSAGE:
                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);
                        sb.append(strIncom);
                        int endOfLineIndex = sb.indexOf("\r\n");
                        if (endOfLineIndex > 0) {
                            String sbprint = sb.substring(0, endOfLineIndex);
                            sb.delete(0, sb.length());
                            tvArduino.setText("Arduino answer: " + sbprint);
                        }
                        Log.d(MainActivity.LOG_TAG, "str: " + sb.toString() + ", b:" + msg.arg1);
                        break;
                }
            }
        };

        connectedThread = new ConnectedThread(MainActivity.bluetoothSocket, handler);
        connectedThread.start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnForwardLeft:
                connectedThread.write(CMD_FORWARD_LEFT);
                break;
            case R.id.btnForward:
                connectedThread.write(CMD_FORWARD);
                break;
            case R.id.btnForwardRight:
                connectedThread.write(CMD_FORWARD_RIGHT);
                break;
            case R.id.btnRotateLeft:
                connectedThread.write(CMD_ROTATE_LEFT);
                break;
            case R.id.btnStop:
                connectedThread.write(CMD_STOP);
                break;
            case R.id.btnRotateRight:
                connectedThread.write(CMD_ROTATE_RIGHT);
                break;
            case R.id.btnBackwardLeft:
                connectedThread.write(CMD_BACKWARD_LEFT);
                break;
            case R.id.btnBackward:
                connectedThread.write(CMD_BACKWARD);
                break;
            case R.id.btnBackwardRight:
                connectedThread.write(CMD_BACKWARD_RIGHT);
                break;
            case R.id.btnDistance:
//                setDistance(12);
                connectedThread.write(CMD_GET_DISTANCE);
                break;
            case R.id.radioBtnServoLeft:
                connectedThread.write(CMD_SERVO_LEFT);
                break;
            case R.id.radioBtnServoMid:
                connectedThread.write(CMD_SERVO_MID);
                break;
            case R.id.radioBtnServoRight:
                connectedThread.write(CMD_SERVO_RIGHT);
                break;
        }
    }

//    private void setDistance(int d) {
//        tvDistance.setText("Distance: " + String.valueOf(d) + " cm");
//    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch (seekBar.getId()) {
            case R.id.seekBarSpeed:
                setSpeed(i);
                break;
        }
    }

    private void setSpeed(int i) {
        // speed values: 0-9
        connectedThread.write(Character.forDigit(i - 1, 10));
        tvSpeed.setText("Speed: " + String.valueOf(i));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
