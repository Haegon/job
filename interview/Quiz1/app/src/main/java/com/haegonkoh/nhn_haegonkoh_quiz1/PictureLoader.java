package com.haegonkoh.nhn_haegonkoh_quiz1;

/**
 * Created by Gohn on 2015. 10. 31..
 */

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// 사진 로드 하는 일을 하는 로더
public class PictureLoader {

    // 이미지의 높이는 100dp 로 고정
    final int PICTURE_HEIGHT = 100;
    // 이미지가 다운로드 되는 동안 보여줄 샘플 이미지
    final int DEFAULT_PICTURE = R.mipmap.ic_launcher;

    // 액티비티를 가진다
    Activity activity;

    // 사진 의주소를 키로 하고 리사이징한 이미지의 비트맵을 캐싱하기 위해 만든 해시맵
    Map<String, Bitmap> hashMap = Collections.synchronizedMap(new WeakHashMap<String, Bitmap>());

    // 이미지 뷰가 어떤 주소의 이미지를 보여줘야 하는지 가지는 해시맵
    Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());

    // 이미지 다운로드 풀링을 하기 위한 ExecutorService
    ExecutorService executorService;

    public PictureLoader(MainActivity activity) {
        this.activity = activity;
        this.executorService = Executors.newFixedThreadPool(5);
    }

    // 어댑터에서 이미지를 뿌릴떄 이 함수를 이용한다
    public void DisplayImage(ImageView imageView, String url) {

        // 이미지 주소의 비트맵이 해시맵에 있는지 검사하고 있으면 바로 이미지를 캐싱된 비트맵 이미지로 보여준다
        if (hashMap.containsKey(url)) {
            imageView.setImageBitmap(hashMap.get(url));
        } else {
            // 캐싱된 비트맵 이미지가 없다면 이미지 맵에 이미지 뷰를 추가하고 이미지를 받기위해 큐에 넣는다
            imageViews.put(imageView, url);
            queuePhoto(imageView, url);

            // 일단 기본 이미지를 출력한다
            imageView.setImageResource(DEFAULT_PICTURE);
        }
    }

    private void queuePhoto(ImageView imageView, String url) {
        PictureToDraw p = new PictureToDraw(imageView, url);

        // 이미지 뷰와 거기에 뿌릴 이미지 주소를 하나의 클래스로 생성하여
        // 비트맵을 로드하는 로더에 일을 시킨다
        executorService.submit(new BitmapLoader(p));
    }

    // 로더가 일하게될 컨텐츠
    private class PictureToDraw {
        public ImageView imageView;
        public String url;

        public PictureToDraw(ImageView imageView, String url) {
            this.imageView = imageView;
            this.url = url;
        }
    }

    // Runnable 인터페이스를 구현한 비트맵 로더 클래스다
    class BitmapLoader implements Runnable {
        PictureToDraw pictureToDraw;

        BitmapLoader(PictureToDraw pictureToDraw) {
            this.pictureToDraw = pictureToDraw;
        }

        @Override
        public void run() {
            // 재사용 된것이면 그냥 나간다
            if (imageViewReused(pictureToDraw))
                return;

            // 리사이징 처리된 웹에서 이미지를 가져온다
            Bitmap bmp = getBitmap(pictureToDraw.url);

            // 이 비트맵을 캐싱한다
            hashMap.put(pictureToDraw.url, bmp);

            // 이 비트맵으로 MainActivity에서 돌릴 스레드를 구현한다
            BitmapDisplayer bd = new BitmapDisplayer(bmp, pictureToDraw);

            // 메인 액티비티의 ui 쓰레드에서 다운받은 이미지를 바로 적용시켜 준다
            MainActivity a = (MainActivity) activity;
            a.runOnUiThread(bd);
        }
    }

     // 웹에서 이미지를 가져오는 부분
     Bitmap getBitmap(String url) {

        try {
            // 옵션을 설정했다
            // 이미지가 엄청 크므로 일단 1/10로 줄였다
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 10;

            // url에서 비트맵을 가져온다
            Bitmap src = BitmapFactory.decodeStream((InputStream) new URL(url).getContent(), null, options);

            // 원본의 비율을 잰다
            float ratio = ((float) src.getWidth()) / ((float) src.getHeight());

            // 높이 100dp가 디바이스에서 몇 픽셀인지 가져온다
            float px = Util.dpToPixel(activity, PICTURE_HEIGHT);

            // 이미지 사이즈를 줄이고 캐싱한다.
            return Bitmap.createScaledBitmap(src, (int) (px * ratio), (int) px, true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 이미지가 재사용된것인지 확인하는 함수
    boolean imageViewReused(PictureToDraw photoToLoad) {
        String url = imageViews.get(photoToLoad.imageView);
        if (url == null || !url.equals(photoToLoad.url))
            return true;
        return false;
    }

    // MainActivity runonuithread에서 돌아갈 부분
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PictureToDraw pictureToDraw;

        public BitmapDisplayer(Bitmap bitmap, PictureToDraw pictureToDraw) {
            this.bitmap = bitmap;
            this.pictureToDraw = pictureToDraw;
        }

        public void run() {
            if (imageViewReused(pictureToDraw))
                return;
            if (bitmap != null)
                // 이미지가 정상적으로 존재하면 그 비트맵을 출력하 고아니면 기본이미지를 출력한다
                pictureToDraw.imageView.setImageBitmap(bitmap);
            else
                pictureToDraw.imageView.setImageResource(DEFAULT_PICTURE);
        }
    }
}