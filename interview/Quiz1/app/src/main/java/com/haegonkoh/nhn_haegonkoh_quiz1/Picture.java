package com.haegonkoh.nhn_haegonkoh_quiz1;

/**
 * Created by Gohn on 2015. 10. 29..
 */

// JSON에서 color, url을 담기위한 클래스
public class Picture {
    public int color;
    public String url;

    public Picture(int color, String url) {
        this.color = color;
        this.url = url;
    }
}
