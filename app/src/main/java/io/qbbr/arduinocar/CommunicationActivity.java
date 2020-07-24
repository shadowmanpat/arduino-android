package io.qbbr.arduinocar;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class CommunicationActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    public static Handler handler;
    private StringBuilder sb = new StringBuilder();
    private ConnectedThread connectedThread;

    public static final char CMD_LED_ON = 'Y';
    public static final char CMD_LED_OFF = 'N';

    Button onBtn;
    Button offBtn;
//    ImageButton btnForward;
//    ImageButton btnForwardRight;
//    ImageButton btnRotateLeft;
//    ImageButton btnStop;
//    ImageButton btnRotateRight;
//    ImageButton btnBackwardLeft;
//    ImageButton btnBackward;
//    ImageButton btnBackwardRight;
//
//    TextView tvDistance;
//    Button btnDistance;
//
//    RadioButton radioBtnServoLeft;
//    RadioButton radioBtnServoMid;
//    RadioButton radioBtnServoRight;
//
//    SeekBar seekBarSpeed;
//    TextView tvSpeed;
//
    TextView tvArduino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communicatoin);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        offBtn = findViewById(R.id.off);
        offBtn.setOnClickListener(this);
        onBtn = findViewById(R.id.on);
        onBtn.setOnClickListener(this);

        tvArduino = findViewById(R.id.temperature);

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                byte[] readBuf = (byte[]) msg.obj;
                String strIncom = new String(readBuf, 0, msg.arg1);
//                sb = new StringBuilder();
//                sb.append(strIncom);

//                Log.d(MainActivity.LOG_TAG, "received: " + sb.toString() + ", b:" + msg.arg1);

                tvArduino.setText( strIncom);

//                Log.d(MainActivity.LOG_TAG, "str: " + sb.toString() + ", b:" + msg.arg1);
//                switch (msg.what) {
//                    case ConnectedThread.RECIEVE_MESSAGE:
//                        byte[] readBuf = (byte[]) msg.obj;
//                        String strIncom = new String(readBuf, 0, msg.arg1);
//                        sb.append(strIncom);
//                        int endOfLineIndex = sb.indexOf("\r\n");
//                        if (endOfLineIndex > 0) {
//                            String sbprint = sb.substring(0, endOfLineIndex);
//                            sb.delete(0, sb.length());
//                            tvArduino.setText("Arduino answer: " + sbprint);
//                        }
//                        Log.d(MainActivity.LOG_TAG, "str: " + sb.toString() + ", b:" + msg.arg1);
//                        break;
//                }
            }
        };

        connectedThread = new ConnectedThread(MainActivity.bluetoothSocket, handler);
        connectedThread.start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.on:
                connectedThread.write(CMD_LED_ON);
                break;
            case R.id.off:
                connectedThread.write(CMD_LED_OFF);
                break;
//
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
//        tvSpeed.setText("Speed: " + String.valueOf(i));
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
