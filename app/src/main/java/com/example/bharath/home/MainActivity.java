package com.example.bharath.home;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.w3c.dom.Text;

import java.lang.annotation.Target;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    Animation move1,move2;
    CardView v0,v1,v2,v3;
    float h,w,x,y;
    int bFLAG=0,boundFlag=0;
    BtReceiver btrec;
    RelativeLayout rel;
    final int[] i = {10};
    final HashMap<String,String> action=new HashMap<String,String>();
    int index=0,flag=0;
    Handler mHandler=null;
    String buffer;
    LinearLayout ll1;
    LST t1=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        t1=new LST(MainActivity.this);
        v0=(CardView)findViewById(R.id.view0);
        v1=(CardView)findViewById(R.id.view1);
        v2=(CardView)findViewById(R.id.view2);
        v3=(CardView)findViewById(R.id.view3);
        rel=(RelativeLayout)findViewById(R.id.rel);
        final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
        action.put("00","Call");
        action.put("01","Message");
        action.put("10","Camera");
        action.put("11","Music");
        btrec=new BtReceiver();
        IntentFilter iff=new IntentFilter();
        iff.addAction(Bluetooth.BLUETOOTH_SERVICE);
        registerReceiver(btrec,iff);
        mHandler = new Handler();
        t1.setLetterSpacing(i[0]);
        t1.setTextSize(25);
        t1.setText("Recognising...");
        final LinearLayout ll;
        ll=(LinearLayout)findViewById(R.id.ll);
        ll1=(LinearLayout)findViewById(R.id.ll1);

        timer();
        ll.addView(t1);
        move1=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.move);
        move2=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.move1);
        move1.setDuration(1000);
        t1.startAnimation(move1);


        move1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                move2.setDuration(700);
                t1.startAnimation(move2);
                t1.setLetterSpacing(i[0]);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {


            }
        });


        move2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                move1.setDuration(1000);
                i[0]=10;
                t1.setLetterSpacing(i[0]);
                t1.startAnimation(move1);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    public void bound(float tx, float ty, float tw, float th){
        Paint paint = new Paint();
        int myColour = Color.argb(255, 0, 255, 127);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        paint.setColor(myColour);
        Bitmap bg = Bitmap.createBitmap(rel.getWidth(), rel.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bg);
        canvas.drawRect(tx, ty, tx + tw, ty + th, paint);

        ImageView iV = new ImageView(this);
        iV.setImageBitmap(bg);

        rel.addView(iV);
        index=rel.indexOfChild(iV);
        Log.d("Index:",""+index);


        if(bFLAG==0){

            bFLAG++;
            Intent ii=new Intent(this,Bluetooth.class);
            ii.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startService(ii);

        }

    }

    public void timer()
    {

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (true) {
                    try {
                        Thread.sleep(1000);
                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {

                                i[0]+=5;
                                h= v3.getHeight();
                                w = v3.getWidth();
                                x=v3.getX();
                                y=v3.getY();
                                if(boundFlag==0) {
                                    bound(x, y, w, h);
                                    boundFlag++;
                                }
                            }
                        });
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }).start();


    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(btrec);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class BtReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            String s=intent.getStringExtra("bt");
            Toast.makeText(getApplicationContext(),"Display:"+s,Toast.LENGTH_SHORT).show();
            if(buffer==null){
                buffer="";
                buffer+=s;
                buffer=buffer.trim();
                t1.setText("Recognised..."+buffer);
                Log.d("Index_delete:", "" + index);
                rel.removeViewAt(index);
                checkDraw(buffer);
                return;
            }
            else if(buffer.length()==2){
                buffer=null;
            }
            else{
                buffer+=s;
                buffer=buffer.trim();
                rel.removeViewAt(index);
                checkDraw(buffer);
                String t=action.get(buffer);
                Toast.makeText(getApplicationContext(),"Action baby:"+t, Toast.LENGTH_LONG).show();
                t1.setText("Recognising...");
                buffer=null;
            }

        }
    }

    public void checkDraw(String xx){

        Log.d("Status:",""+xx);
        int length=xx.length();
        if(length==1){
            int t=Integer.parseInt(xx);
            if(t==0){

                h= v0.getHeight();
                w = v0.getWidth();
                x=v0.getX();
                y=v0.getY();
                bound(x,y,w,(2*h)+70);

            }
            else{

                h= v2.getHeight();
                w = v2.getWidth();
                x=v2.getX();
                y=v2.getY();
                bound(x,y,w,(2*h)+70);


            }
        }
        else{

            int yy=Integer.parseInt(xx);
            switch (yy){
                case 00:
                    h= v0.getHeight();
                    w = v0.getWidth();
                    x=v0.getX();
                    y=v0.getY();
                    bound(x,y,w,h);
                    break;
                case 01:
                    h= v1.getHeight();
                    w = v1.getWidth();
                    x=v1.getX();
                    y=v1.getY();
                    bound(x,y,w,h);
                    break;
                case 10:
                    h= v2.getHeight();
                    w = v2.getWidth();
                    x=v2.getX();
                    y=v2.getY();
                    bound(x,y,w,h);
                    break;
                case 11:
                    h= v3.getHeight();
                    w = v3.getWidth();
                    x=v3.getX();
                    y=v3.getY();
                    bound(x,y,w,h);
                    break;
            }

        }

    }
}
