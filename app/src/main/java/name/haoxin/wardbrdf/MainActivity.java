package name.haoxin.wardbrdf;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import name.haoxin.wardbrdf.action.CaptureCallBack;
import name.haoxin.wardbrdf.action.Save;

public class MainActivity extends Activity {
    private boolean showQuickAction;
    private Timer fpsUpdateTimer;
    private TimerTask fpsUpdateTask;
    private TextView fpsTextView;
    private Handler fpsUpdateHandler;
    private CustomGLSurfaceView glView;
    private boolean pcfON = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        glView = (CustomGLSurfaceView) findViewById(R.id.glview);
        initQuickButton();
        startFpsUpdateTimer();
    }

    private void startFpsUpdateTimer() {
        fpsTextView = (TextView) findViewById(R.id.fpsText);
        fpsUpdateHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.arg1 == 1) {
                    fpsTextView.setText(glView.getFps());
                }
            }
        };
        fpsUpdateTimer = new Timer();
        fpsUpdateTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = fpsUpdateHandler.obtainMessage();
                msg.arg1 = 1;
                msg.sendToTarget();
            }
        };
        fpsUpdateTimer.schedule(fpsUpdateTask, 1000, 1000);
    }

    private void initQuickButton() {
        final Toast imageSaved = Toast.makeText(MainActivity.this, "Screenshot Saved", Toast.LENGTH_LONG);
        final Toast imageNotSaved = Toast.makeText(MainActivity.this, "Failed To Save The Screenshot", Toast.LENGTH_LONG);
        final Toast renderWhenDirtyON = Toast.makeText(MainActivity.this, "Render When Dirty(Capture Disabled)", Toast.LENGTH_SHORT);
        final Toast renderWhenDirtyOFF = Toast.makeText(MainActivity.this, "Render Continually", Toast.LENGTH_SHORT);
        final Toast pcfON = Toast.makeText(MainActivity.this, "PCF ON", Toast.LENGTH_SHORT);
        final Toast pcfOFF = Toast.makeText(MainActivity.this, "PCF OFF", Toast.LENGTH_SHORT);
        final RelativeLayout quickButtonGroup = ((RelativeLayout) findViewById(R.id.quickGroup));

        final Button screenshot = (Button) findViewById(R.id.screenshot);
        screenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (glView.isRenderOnRequest()) {
                    renderWhenDirtyON.show();
                } else {
                    glView.capture(saveCapture);
                }
            }

            final CaptureCallBack saveCapture = new CaptureCallBack() {
                @Override
                public void saveCapture(Bitmap bitmap) {
                    String prefix;
                    if (MainActivity.this.pcfON) {
                        prefix = "PCFON";
                    } else {
                        prefix = "PCFOFF";
                    }
                    if (Save.saveImage(MainActivity.this, bitmap, prefix)) {
                        imageSaved.show();
                    } else {
                        imageNotSaved.show();
                    }
                }
            };
        });

        final Button renderOnRequest = (Button) findViewById(R.id.renderOnRequest);
        renderOnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                glView.switchRenderMode();
                if (glView.isRenderOnRequest()) {
                    renderWhenDirtyON.show();
                } else {
                    renderWhenDirtyOFF.show();
                }
            }
        });

        final Button quickButton = (Button) findViewById(R.id.quickButton);
        quickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (showQuickAction) {
                    quickButtonGroup.setVisibility(View.INVISIBLE);
                    quickButton.setAlpha(0.3f);
                } else {
                    quickButtonGroup.setVisibility(View.VISIBLE);
                    quickButton.setAlpha(1.0f);
                }
                showQuickAction = !showQuickAction;
            }
        });

        final Button pcfButton = (Button) findViewById(R.id.pcf);
        pcfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.pcfON = !MainActivity.this.pcfON;
                MainActivity.this.glView.setPCFON(MainActivity.this.pcfON);
                if (MainActivity.this.pcfON){
                    pcfON.show();
                }else{
                    pcfOFF.show();
                }
            }
        });
    }

}
