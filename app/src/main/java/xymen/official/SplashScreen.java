package xymen.official;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity implements View.OnClickListener {

    TextView x;
    TextView ymen;
    TextView i;
    TextView nc;
    TextView powered_by;
    ImageView ic_launcher;
    LinearLayout xymen_inc_container;

    Handler handler;
    Runnable launchMainActivity;

    boolean isFirstTime = true;


    Animation rotateAnimation;
    TranslateAnimation translateAnimation;


    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        if (BuildConfig.APPLICATION_ID.equals("xymen.official")) {
            RelativeLayout root = (RelativeLayout) findViewById(R.id.root_layout);
            root.setOnClickListener(this);
        }
        x = (TextView) findViewById(R.id.x);
        ymen = (TextView) findViewById(R.id.ymen);
//        TextView space = (TextView) findViewById(R.id.space);
        i = (TextView) findViewById(R.id.i);
        nc = (TextView) findViewById(R.id.nc);
        ic_launcher = (ImageView) findViewById(R.id.ic_launcher);
        powered_by = (TextView) findViewById(R.id.powered_by);
        xymen_inc_container = (LinearLayout) findViewById(R.id.xiptide_inc);
        handler = new Handler();
        launchMainActivity = new Runnable() {
            @Override
            public void run() {
                launchMainActivity();
            }
        };
    }

    private void launchMainActivity() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (isFirstTime)
            StartAnimations();
        isFirstTime = !isFirstTime;

        super.onWindowFocusChanged(hasFocus);
    }


    public void StartAnimations() {
        handler.removeCallbacks(launchMainActivity);
//        Animation anim_x;
        Animation anim_ymen;
//        Animation anim_space;
//        Animation anim_i;
        Animation anim_nc;
        Animation anim_launcher;
        Animation anim_xymen_inc;
        Animation anim_powered_by;
        long delay;


        anim_xymen_inc = AnimationUtils.loadAnimation(this, R.anim.xymen_inc);
        anim_powered_by = AnimationUtils.loadAnimation(this, R.anim.powered_by);
//        anim_x = AnimationUtils.loadAnimation(this, R.anim.x);
        anim_ymen = AnimationUtils.loadAnimation(this, R.anim.remaining_letter);
//        anim_space = AnimationUtils.loadAnimation(this, R.anim.space);
//        anim_i = AnimationUtils.loadAnimation(this, R.anim.i);
        anim_nc = AnimationUtils.loadAnimation(this, R.anim.remaining_letter);
        anim_launcher = AnimationUtils.loadAnimation(this, R.anim.ic_launcher);

        powered_by.setAnimation(anim_powered_by);
        setAnimationFromCenterScreen(x);    //x.setAnimation(anim_x);
        ymen.setAnimation(anim_ymen);
//        space.setAnimation(anim_space);
        setAnimationFromCenterScreen(i);     // i.setAnimation(anim_i);
        nc.setAnimation(anim_nc);
        ic_launcher.setAnimation(anim_launcher);
        xymen_inc_container.setAnimation(anim_xymen_inc);

        x.getAnimation().setStartTime(0);
        i.getAnimation().setStartTime(0);
        delay = rotateAnimation.getDuration() + translateAnimation.getDuration();

        anim_ymen.setStartOffset(delay);
        anim_nc.setStartOffset(delay);
        delay += anim_ymen.getDuration();

        anim_xymen_inc.setStartOffset(delay);
        delay += anim_xymen_inc.getDuration() / 2;


        anim_launcher.setStartOffset(delay);
        delay += (anim_xymen_inc.getDuration() / 2) + (anim_launcher.getDuration() / 3);


        anim_powered_by.setStartOffset(delay);

        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(x.getAnimation());
        animationSet.addAnimation(anim_ymen);
//        animationSet.addAnimation(anim_space);
        animationSet.addAnimation(i.getAnimation());
        animationSet.addAnimation(anim_nc);
        animationSet.addAnimation(anim_launcher);
        animationSet.addAnimation(anim_powered_by);
        animationSet.addAnimation(anim_xymen_inc);


        animationSet.startNow();
        handler.postDelayed(launchMainActivity, 7000);
    }

    public void setAnimationFromCenterScreen(View view) {
        int originalPos[] = new int[2];
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        view.getLocationOnScreen(originalPos);
        int xStart;
        xStart = ((dm.widthPixels - view.getWidth()) / 2) - originalPos[0];


        rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        translateAnimation = new TranslateAnimation(xStart, 0, 0, 0);
        translateAnimation.setDuration(1000);
        translateAnimation.setStartOffset(rotateAnimation.getDuration());
//        anim.setFillAfter( true );
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(rotateAnimation);
        animationSet.addAnimation(translateAnimation);
        view.setAnimation(animationSet);
    }

    @Override
    public void onClick(View v) {
        handler.removeCallbacks(launchMainActivity);
        launchMainActivity();
    }
}
