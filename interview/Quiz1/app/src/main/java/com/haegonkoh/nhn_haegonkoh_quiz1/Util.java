package com.haegonkoh.nhn_haegonkoh_quiz1;

import android.app.Activity;
import android.util.TypedValue;

/**
 * Created by Gohn on 2015. 10. 31..
 */
public class Util {

    // dp단위의 크기를 디바이스별 해상도에 맞는 픽셀로 변환해주는 함수
    public static int dpToPixel(Activity activity, int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, activity.getApplicationContext().getResources().getDisplayMetrics());
    }
}
