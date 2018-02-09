package com.mellobit.garrymckee.sunset;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
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

    private boolean isSunSet;
    private boolean isAnimating;

    private int elapsedTime;

    private AnimatorSet mAnimatorSet;

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
                if(isAnimating) {
                    Log.d("AnimCheck", "Attempting to cancel...");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        elapsedTime = (int)mAnimatorSet.getCurrentPlayTime();
                    } else {
                        elapsedTime = ANIMATION_TIME;
                    }
                    mAnimatorSet.cancel();
                    reverseAnimation();
                    isAnimating = false;
                } else {
                    if(!isSunSet){
                        startSunSetAnimation();
                    } else {
                        startSunRiseAnimation();
                    }
                }


            }

            private void reverseAnimation() {
                if(!isSunSet) {

                } else {
                    float sunYStart = mSunView.getY();
                    float reflectionYStart = mReflectionView.getY();
                    int skyColorStart = ((ColorDrawable) mSkyView.getBackground()).getColor();

                    ObjectAnimator heightAnimator = ObjectAnimator
                            .ofFloat(mSunView, "y", sunYStart, mSunTopPosition)
                            .setDuration(elapsedTime);

                    ObjectAnimator reflectionHeightAnimator = ObjectAnimator
                            .ofFloat(mReflectionView, "y", reflectionYStart, mSkyView.getHeight() - (mSunTopPosition + mSunView.getHeight()) )
                            .setDuration(elapsedTime);

                    ObjectAnimator sunSkyAnimator = ObjectAnimator
                            .ofInt(mSkyView, "backgroundColor", skyColorStart, mBlueSkyColor)
                            .setDuration(elapsedTime);

                    mAnimatorSet = new AnimatorSet();

                    sunSkyAnimator.setEvaluator(new ArgbEvaluator());
                    heightAnimator.setInterpolator(new AccelerateInterpolator());
                    reflectionHeightAnimator.setInterpolator(new AccelerateInterpolator());

                    mAnimatorSet
                            .play(heightAnimator)
                            .with(reflectionHeightAnimator)
                            .with(sunSkyAnimator);

                    mAnimatorSet.start();

                    isAnimating = true;
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
        float reflectionYStart = mReflectionView.getY();
        float reflectionYEnd = 0 - mReflectionView.getHeight();

        AnimationTarget target = new AnimationTarget();

        target.setSunStart(sunYStart);
        target.setSunEnd(sunYEnd);
        target.setReflectionStart(reflectionYStart);
        target.setReflectionEnd(reflectionYEnd);
        target.setColorStart(mBlueSkyColor);
        target.setColorEnd(mNightSkyColor);

        ObjectAnimator heightAnimator = ObjectAnimator
                .ofFloat(mSunView, "y", sunYStart, sunYEnd)
                .setDuration(ANIMATION_TIME);

        ObjectAnimator reflectionHeightAnimator = ObjectAnimator
                .ofFloat(mReflectionView, "y", reflectionYStart, reflectionYEnd)
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

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet
                .play(heightAnimator)
                .with(reflectionHeightAnimator)
                .with(sunSkyAnimator)
                .before(nightSkyAnimator);
        mAnimatorSet.start();
        isAnimating = true;

        mAnimatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                Log.i("TEST", "Animation Starting");

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                Log.i("TEST", "Animation Finished");
                isSunSet = true;
                isAnimating = false;
                mAnimatorSet = null;
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

        float reflectionYStart = 0 - mReflectionView.getHeight();
        float reflectionYEnd = mSkyView.getHeight() - (mSunTopPosition + mSunView.getHeight());
        Log.d("SUNCOORDS", "Sun to: " + sunYEnd);

        AnimationTarget target = new AnimationTarget();

        target.setSunStart(sunYStart);
        target.setSunEnd(sunYEnd);
        target.setReflectionStart(reflectionYStart);
        target.setReflectionEnd(reflectionYEnd);
        target.setColorStart(mNightSkyColor);
        target.setColorEnd(mBlueSkyColor);

        ObjectAnimator heightAnimator = ObjectAnimator
                .ofFloat(mSunView, "y", sunYStart, sunYEnd)
                .setDuration(ANIMATION_TIME);

        ObjectAnimator sunSkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor", mSunsetSkyColor, mBlueSkyColor)
                .setDuration(ANIMATION_TIME);

        ObjectAnimator reflectionHeightAnimator = ObjectAnimator
                .ofFloat(mReflectionView, "y", reflectionYStart, reflectionYEnd)
                .setDuration(ANIMATION_TIME);

        ObjectAnimator nightSkyAnimator = ObjectAnimator
                .ofInt(mSkyView, "backgroundColor", mNightSkyColor, mSunsetSkyColor)
                .setDuration(ANIMATION_TIME);


        sunSkyAnimator.setEvaluator(new ArgbEvaluator());
        nightSkyAnimator.setEvaluator(new ArgbEvaluator());
        heightAnimator.setInterpolator(new AccelerateInterpolator());
        reflectionHeightAnimator.setInterpolator(new AccelerateInterpolator());

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet
                .play(heightAnimator)
                .with(reflectionHeightAnimator)
                .with(sunSkyAnimator)
                .after(nightSkyAnimator);
        mAnimatorSet.start();

        mAnimatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                isAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                Log.i("TEST", "Animation Finished");
                isSunSet = false;
                isAnimating = false;
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

    class AnimationTarget {
        private float sunStart;
        private float sunEnd;
        private float reflectionStart;
        private float reflectionEnd;

        private int colorStart;
        private int colorEnd;

        public void setSunStart(float sunStart) {
            this.sunStart = sunStart;
        }

        public void setSunEnd(float sunEnd) {
            this.sunEnd = sunEnd;
        }

        public void setReflectionStart(float reflectionStart) {
            this.reflectionStart = reflectionStart;
        }

        public void setReflectionEnd(float reflectionEnd) {
            this.reflectionEnd = reflectionEnd;
        }

        public void setColorStart(int colorStart) {
            this.colorStart = colorStart;
        }

        public void setColorEnd(int colorEnd) {
            this.colorEnd = colorEnd;
        }

        public void reverseAnimation() {
            float sunYStart = sunEnd;
            float sunYEnd = sunStart;

            float reflectionYStart = reflectionEnd;
            float reflectionYEnd = reflectionStart;
            Log.d("SUNCOORDS", "Sun to: " + sunYEnd);

            AnimationTarget target = new AnimationTarget();

            target.setSunStart(sunYStart);
            target.setSunEnd(sunYEnd);
            target.setReflectionStart(reflectionYStart);
            target.setReflectionEnd(reflectionYEnd);
            target.setColorStart(mNightSkyColor);
            target.setColorEnd(mBlueSkyColor);

            ObjectAnimator heightAnimator = ObjectAnimator
                    .ofFloat(mSunView, "y", sunYStart, sunYEnd)
                    .setDuration(ANIMATION_TIME);

            ObjectAnimator sunSkyAnimator = ObjectAnimator
                    .ofInt(mSkyView, "backgroundColor", mSunsetSkyColor, mBlueSkyColor)
                    .setDuration(ANIMATION_TIME);

            ObjectAnimator reflectionHeightAnimator = ObjectAnimator
                    .ofFloat(mReflectionView, "y", reflectionYStart, reflectionYEnd)
                    .setDuration(ANIMATION_TIME);

            ObjectAnimator nightSkyAnimator = ObjectAnimator
                    .ofInt(mSkyView, "backgroundColor", mNightSkyColor, mSunsetSkyColor)
                    .setDuration(ANIMATION_TIME);


            sunSkyAnimator.setEvaluator(new ArgbEvaluator());
            nightSkyAnimator.setEvaluator(new ArgbEvaluator());
            heightAnimator.setInterpolator(new AccelerateInterpolator());
            reflectionHeightAnimator.setInterpolator(new AccelerateInterpolator());

            mAnimatorSet = new AnimatorSet();
            mAnimatorSet
                    .play(heightAnimator)
                    .with(reflectionHeightAnimator)
                    .with(sunSkyAnimator)
                    .after(nightSkyAnimator);
            mAnimatorSet.start();

            mAnimatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    isAnimating = true;
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    Log.i("TEST", "Animation Finished");
                    isSunSet = false;
                    isAnimating = false;
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                    mAnimatorSet = null;
                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }

    }
}
