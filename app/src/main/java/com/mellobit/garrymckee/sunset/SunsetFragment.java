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

    private View mSceneView;
    private View mSunView;
    private View mReflectionView;
    private View mSkyView;
    private View mSeaView;

    private int mBlueSkyColor;
    private int mSunsetSkyColor;
    private int mNightSkyColor;

    float mSunTopPosition;
    float mReflectionBottomPosition;

    private boolean isSunSet;
    private static final int ANIMATION_TIME = 2500;

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
        mReflectionView = v.findViewById(R.id.reflection);

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
    @Override
    public void onGlobalLayout() {
        mReflectionView.setY(mSkyView.getHeight() - mSunView.getBottom());
        String string = "Distance from bottom of sun, to horizon is: " + mReflectionView.getY();
        String string2 = "Distance from bottom of sun, to horizon is: " + (mSkyView.getHeight() - mSunView.getBottom());
        Log.d("DISTCHECK", string);
        Log.d("DISTCHECK", string2);
        if(mSunTopPosition == 0) {
            Log.d("SUNCOORDS", "Setting SunTopPosition to: " + mSunView.getTop());
            mSunTopPosition = mSunView.getTop();
        } else {
            Log.d("SUNCOORDS", "SunTopPosition already set: " + mSunTopPosition);
        }


        startSunPulseAnimation();
    }


    private void startSunPulseAnimation() {
        ObjectAnimator sunPulseAnimator = ObjectAnimator
                .ofFloat(mSunView, "scaleX", 1f, 1.08f)
                .setDuration(500);

        ObjectAnimator reflectionPulseAnimator = ObjectAnimator
                .ofFloat(mReflectionView, "scaleX", 1f, 1.08f)
                .setDuration(500);

        sunPulseAnimator.setRepeatCount(ValueAnimator.INFINITE);
        sunPulseAnimator.setRepeatMode(ValueAnimator.REVERSE);

        reflectionPulseAnimator.setRepeatCount(ValueAnimator.INFINITE);
        reflectionPulseAnimator.setRepeatMode(ValueAnimator.REVERSE);

        sunPulseAnimator.start();
        reflectionPulseAnimator.start();
    }

    private void startSunSetAnimation() {

        checkDistance();

        float sunYStart = mSunView.getY();

        float sunYEnd = mSkyView.getHeight();

        ObjectAnimator heightAnimator = ObjectAnimator
                .ofFloat(mSunView, "y", sunYStart, sunYEnd)
                .setDuration(ANIMATION_TIME);

        ObjectAnimator reflectionHeightAnimator = ObjectAnimator
                .ofFloat(mReflectionView, "y", mReflectionView.getY(), 0 - mReflectionView.getHeight())
                .setDuration(ANIMATION_TIME);

        ObjectAnimator sunSkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor", mBlueSkyColor, mSunsetSkyColor)
                .setDuration(ANIMATION_TIME);

        ObjectAnimator nightSkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor", mSunsetSkyColor, mNightSkyColor)
                .setDuration(ANIMATION_TIME);

        sunSkyAnimator.setEvaluator(new ArgbEvaluator());
        nightSkyAnimator.setEvaluator(new ArgbEvaluator());
        heightAnimator.setInterpolator(new AccelerateInterpolator());
        reflectionHeightAnimator.setInterpolator(new AccelerateInterpolator());

        AnimatorSet sunSetAnimatorSet = new AnimatorSet();
        sunSetAnimatorSet
                .play(heightAnimator)
                .with(reflectionHeightAnimator)
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
                .setDuration(ANIMATION_TIME);

        ObjectAnimator sunSkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor", mSunsetSkyColor, mBlueSkyColor)
                .setDuration(ANIMATION_TIME);

        ObjectAnimator reflectionHeightAnimator = ObjectAnimator
                .ofFloat(mReflectionView, "y", 0 - mReflectionView.getHeight(), (mSkyView.getHeight() - (mSunTopPosition + mSunView.getHeight())))
                .setDuration(ANIMATION_TIME);

        ObjectAnimator nightSkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor", mNightSkyColor, mSunsetSkyColor)
                .setDuration(ANIMATION_TIME);


        sunSkyAnimator.setEvaluator(new ArgbEvaluator());
        nightSkyAnimator.setEvaluator(new ArgbEvaluator());
        heightAnimator.setInterpolator(new AccelerateInterpolator());
        reflectionHeightAnimator.setInterpolator(new AccelerateInterpolator());

        AnimatorSet sunRiseAnimatorSet = new AnimatorSet();
        sunRiseAnimatorSet
                .play(heightAnimator)
                .with(reflectionHeightAnimator)
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

    private void checkDistance() {
        float topDistance;
        float bottomDistance;

        topDistance = mSkyView.getHeight() - mSunView.getBottom();
        bottomDistance = mReflectionView.getY();

        Log.d("DISTCHECK", "Top Distance: " + topDistance);
        Log.d("DISTCHECK", "Bottom Distance: " + bottomDistance);
    }
}
