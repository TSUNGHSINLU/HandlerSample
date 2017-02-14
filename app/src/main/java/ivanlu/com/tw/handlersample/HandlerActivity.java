package ivanlu.com.tw.handlersample;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.badoo.mobile.util.WeakHandler;

public class HandlerActivity extends AppCompatActivity {

    private final static String TAG = HandlerActivity.class.getSimpleName();

    private WeakHandler mHandler; // We still need at least one hard reference to WeakHandler
    private HandlerThread mWorker = null;
    private Handler mWorkerHandler = null;

    private class ApplyDataHandler extends Handler {

        @SuppressWarnings("unused")
        public ApplyDataHandler() {

        }

        public ApplyDataHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            try {
                Log.d(TAG, "non UI handleMessage msg.what = " + msg.what);
            } catch (Exception e) {
                Log.e(TAG,"" + e);
            }
        }
    }

    private Handler handler = new Handler(){
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "UI handleMessage msg.what = " + msg.what);
        }
    };

    static {
        Log.d(TAG, "static");
    }

    public HandlerActivity(){
        Log.d(TAG, "HandlerActivity construct");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handler);
        Log.d(TAG, "onCreate");
        mHandler = new WeakHandler();
        mWorker = new HandlerThread("load_data_worker");
        mWorker.start();
        mWorkerHandler = new ApplyDataHandler(mWorker.getLooper());
        handler.sendEmptyMessage(0);
        mWorkerHandler.sendEmptyMessage(0);

        mHandler.postDelayed(new Runnable() {
            public void run() {
                Log.d(TAG, "library UI postDelayed1");
            }
        }, 2000);

        handler.postDelayed(new Runnable() {
            public void run() {
                Log.d(TAG, "UI postDelayed1");
            }
        }, 4000);

        mWorkerHandler.postDelayed(new Runnable() {
            public void run() {
                Log.d(TAG, "non UI postDelayed1");
            }
        }, 1000);

        this.runOnUiThread(new Runnable() {
            public void run() {
                Log.d(TAG, "runOnUiThread1");
            }
        });

        new Handler(HandlerActivity.this.getMainLooper()).post(new Runnable() {
            public void run() {
                Log.d(TAG, "getMainLooper1");
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart1");
        Message msg = Message.obtain(handler, 1);
        handler.sendMessageDelayed(msg, 3000);
        Message msg2 = Message.obtain(mWorkerHandler, 1);
        mWorkerHandler.sendMessageDelayed(msg2,1000);
        SystemClock.sleep(2000);
        Log.d(TAG, "onStart2");
        test();
    }

    private void test(){
        Log.d(TAG, "test");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        this.runOnUiThread(new Runnable() {
            public void run() {
                Log.d(TAG, "runOnUiThread2");
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        new Handler(HandlerActivity.this.getMainLooper()).post(new Runnable() {
            public void run() {
                Log.d(TAG, "getMainLooper2");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWorkerHandler != null) {
            mWorkerHandler.removeCallbacksAndMessages(null);
        }
        if (mWorker != null) {
            mWorker.quit();
        }
        if(handler !=null){
            handler.removeCallbacksAndMessages(null);
        }
    }
}
