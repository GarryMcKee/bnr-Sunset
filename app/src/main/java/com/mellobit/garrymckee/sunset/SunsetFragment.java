package com.mellobit.garrymckee.sunset;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by Garry on 27/01/2018.
 */

public class SunsetFragment extends Fragment implements ViewTreeObserver.OnGlobalLayoutListener{

    private static final String KEY_SUN_TOP = "keySunTop";

    private View mSceneView;
    private View mSunView;
    private View mSkyView;
    private View mSeaView;

    private int mBlueSkyColor;
    private int mSunsetSkyColor;
    private int mNightSkyColor;

    float mSunTopPosition;

    private boolean isSunSet;

    public static SunsetFragment newInstance() {
        return new SunsetFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sunset, container, false);

        mSceneView = v;
        mSunView = v.findViewById(R.id.sun);
        mSkyView = v.findViewById(R.id.sky);
        mSeaView = v.findViewById(R.id.sea);

        Resources resources = getResources();
        mBlueSkyColor = resources.getColor(R.color.blue_sky);
        mSunsetSkyColor = resources.getColor(R.color.sunset_sky);
        mNightSkyColor = resources.getColor(R.color.night_sky);

        mSceneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isSunSet){
                    startSunSetAnimation();
                } else {
                    startSunRiseAnimation();
                }
            }
        });

        mSceneView.getViewTreeObserver().addOnGlobalLayoutListener(this);

        return v;
    }

    private void startSunPulseAnimation() {
        ObjectAnimator sunPulseAnimator = ObjectAnimator
                .ofFloat(mSunView, "scaleX", 1f, 1.08f)
                .setDuration(500);

        sunPulseAnimator.setRepeatCount(ValueAnimator.INFINITE);
        sunPulseAnimator.setRepeatMode(ValueAnimator.REVERSE);

        sunPulseAnimator.start();
    }

    private void startSunSetAnimation() {
        float sunYStart = mSunView.getTop();
        Log.d("SUNCOORDS", "Sun at: " + sunYStart);
        float sunYEnd = mSkyView.getHeight();

        ObjectAnimator heightAnimator = ObjectAnimator
                .ofFloat(mSunView, "y", sunYStart, sunYEnd)
                .setDuration(1000);

        ObjectAnimator sunSkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor", mBlueSkyColor, mSunsetSkyColor)
                .setDuration(1000);

        ObjectAnimator nightSkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor", mSunsetSkyColor, mNightSkyColor)
                .setDuration(1000);

        sunSkyAnimator.setEvaluator(new ArgbEvaluator());
        nightSkyAnimator.setEvaluator(new ArgbEvaluator());
        heightAnimator.setInterpolator(new AccelerateInterpolator());

        AnimatorSet sunSetAnimatorSet = new AnimatorSet();
        sunSetAnimatorSet
                .play(heightAnimator)
                .with(sunSkyAnimator)
                .before(nightSkyAnimator);
        sunSetAnimatorSet.start();

        sunSetAnimatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                Log.i("TEST", "Animation Finished");
                isSunSet = true;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private void startSunRiseAnimation() {
        float sunYStart = mSeaView.getTop();
        float sunYEnd = mSunTopPosition;
        Log.d("SUNCOORDS", "Sun to: " + sunYEnd);

        ObjectAnimator heightAnimator = ObjectAnimator
                .ofFloat(mSunView, "y", sunYStart, sunYEnd)
                .setDuration(1000);

        ObjectAnimator sunSkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor", mSunsetSkyColor, mBlueSkyColor)
                .setDuration(1000);

        ObjectAnimator nightSkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor", mNightSkyColor, mSunsetSkyColor)
                .setDuration(1000);

        sunSkyAnimator.setEvaluator(new ArgbEvaluator());
        nightSkyAnimator.setEvaluator(new ArgbEvaluator());
        heightAnimator.setInterpolator(new AccelerateInterpolator());

        AnimatorSet sunRiseAnimatorSet = new AnimatorSet();
        sunRiseAnimatorSet
                .play(heightAnimator)
                .with(sunSkyAnimator)
                .after(nightSkyAnimator);
        sunRiseAnimatorSet.start();

        sunRiseAnimatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                Log.i("TEST", "Animation Finished");
                isSunSet = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    @Override
    public void onGlobalLayout() {
        if(mSunTopPosition == 0) {
            Log.d("SUNCOORDS", "Setting SunTopPosition to: " + mSunView.getTop());
            mSunTopPosition = mSunView.getTop();
        } else {
            Log.d("SUNCOORDS", "SunTopPosition already set: " + mSunTopPosition);
        }


        startSunPulseAnimation();
    }
}
