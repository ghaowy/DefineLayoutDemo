package com.imprexion.definelayoutdemo;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView mTvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvTitle = findViewById(R.id.tv_title);
        mTvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                handleAnimator();
                handleAnimator1();
            }
        });
    }

    private void handleAnimator1() {
        PropertyValuesHolder bgColorAnimator = PropertyValuesHolder.ofObject("backgroundColor",
                new ArgbEvaluator(),
                0xff009688, 0xff795548);
        PropertyValuesHolder rotationXAnimator = PropertyValuesHolder.ofFloat("rotationX",
                0f, 360f);
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(mTvTitle, bgColorAnimator, rotationXAnimator);
        objectAnimator.setDuration(3000);
        objectAnimator.setRepeatCount(1);
        objectAnimator.setRepeatMode(ValueAnimator.REVERSE);
        objectAnimator.start();
    }

    private void handleAnimator() {
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setObjectValues(new ValueBean(0, 0, Color.parseColor("#FF0000")), new ValueBean(360, 360, Color.parseColor("#FF00FF")));
        valueAnimator.setEvaluator(new MyTypeEvaluator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ValueBean animatedValue = (ValueBean) animation.getAnimatedValue();
                mTvTitle.setBackgroundColor(animatedValue.getBackGroundColor());
                mTvTitle.setRotationX(animatedValue.getRotateX());
                mTvTitle.setRotationY(animatedValue.getRotateY());
                mTvTitle.requestLayout();
            }
        });
        valueAnimator.setDuration(1000);
        valueAnimator.setRepeatCount(-1);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.start();
    }


    class MyTypeEvaluator implements TypeEvaluator<ValueBean> {
        ArgbEvaluator mArgbEvaluator;

        public MyTypeEvaluator() {
            mArgbEvaluator = new ArgbEvaluator();
        }

        @Override
        public ValueBean evaluate(float fraction, ValueBean startValue, ValueBean endValue) {
            int currentColor = (int) mArgbEvaluator.evaluate(fraction, startValue.getBackGroundColor(), endValue.getBackGroundColor());
            float currentRotationX = startValue.getRotateX() + (endValue.getRotateX() - startValue.getRotateX()) * fraction;
            float currentRotationY = startValue.getRotateY() + (endValue.getRotateY() - startValue.getRotateY()) * fraction;
//            float currentRotationZ = ValueBean.getRotationZ() + (endPropertyBean.getRotationZ() - startPropertyBean.getRotationZ()) * fraction;
//            float currentSize = ValueBean.getSize() + (endPropertyBean.getSize() - startPropertyBean.getSize()) * fraction;
            return new ValueBean(currentRotationX, currentRotationY, currentColor);
        }
    }


    class ValueBean {
        private float rotateX;
        private float rotateY;
        private int backGroundColor;

        public ValueBean(float rotateX, float rotateY, int backGroundColor) {
            this.rotateX = rotateX;
            this.rotateY = rotateY;
            this.backGroundColor = backGroundColor;
        }

        public float getRotateX() {
            return rotateX;
        }

        public void setRotateX(float rotateX) {
            this.rotateX = rotateX;
        }

        public float getRotateY() {
            return rotateY;
        }

        public void setRotateY(float rotateY) {
            this.rotateY = rotateY;
        }

        public int getBackGroundColor() {
            return backGroundColor;
        }

        public void setBackGroundColor(int backGroundColor) {
            this.backGroundColor = backGroundColor;
        }

        @Override
        public String toString() {
            return "ValueBean{" +
                    "rotateX=" + rotateX +
                    ", rotateY=" + rotateY +
                    ", backGroundColor=" + backGroundColor +
                    '}';
        }
    }


}
