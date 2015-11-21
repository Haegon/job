package com.haegonkoh.nhn_haegonkoh_quiz1;

import android.app.ListActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.JsonReader;
import android.widget.AbsListView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;


public class MainActivity extends ListActivity implements AbsListView.OnScrollListener{

    // 서버 주소
    final String SERVER_URL = "http://ticketlink.dn.toastoven.net/mobile/etc/frameImages8.json";

    // 이미지 클래스의 리스트. 컬러와 주소 정보가 있다.
    ArrayList<Picture> pictureList;

    // 페이지 카운트를 증가시키기위한 변수
    int page;

    // 최소 10개의 json을 읽으면 isBasicData가 true가 되고 그때부터 이미지를 로드하기 시작한다
    boolean isBasicData = false;

    // 리스트뷰에 뿌려줄 어댑터터
    public PictureListAdapter adaptor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pictureList = new ArrayList<Picture>();
        page = 0;

        // 통신하여 데이터를 바다온다
        httpRequestGet();

        // 최소 10개의 json이 들어올때 까지 기다린다
        while (!isBasicData) {
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (pictureList.size() > 10) {
                isBasicData = true;
            }
        }

        // 어댑터를 셋팅한다
        // 처음엔 10개의 데이터만 가져와 리스트뷰로 보여주게 한다
        adaptor = new PictureListAdapter(MainActivity.this, new ArrayList<Picture>(pictureList.subList(page*10,(page+1)*10)));

        setListAdapter(adaptor);

        // 리스트 뷰의 스크롤 리스너를 MainActivity 가져간다
        getListView().setOnScrollListener(this);
    }

    // HTTP GET을 통해서 json 데이터를 가져온다
    private void httpRequestGet() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // HTTP Client하나를 만들어서 서버로 get을 요청한다
                    HttpClient client = new DefaultHttpClient();
                    HttpGet req = new HttpGet();
                    req.setURI(new URI(SERVER_URL));
                    HttpResponse response = client.execute(req);
                    HttpEntity entity = response.getEntity();

                    // 데이터의 양이 약 28Mb 정도되고 full data가 올떄 까지 기다릴 수 없으므로
                    // Stream하게 entity를 읽어보자
                    readJsonStream(entity.getContent());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // 데이터가 json리스트임을 알고 있으므로
    // JsonReader를 통해 데이터를 분석하자
    public void readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            readMessagesArray(reader);
        } catch (Exception e) {
            throw e;
        } finally {
            // 아 끝나면 JsonReader를 close한다
            reader.close();
        }
    }

    public void readMessagesArray(JsonReader reader) throws IOException {
        reader.beginArray();
        // 시작부터 JsonObject 하나씩 계속 읽는다
        while (reader.hasNext()) {
            readMessage(reader);
        }
        reader.endArray();
    }

    public void readMessage(JsonReader reader) throws IOException {

        int color = 0;
        String url = "";

        // Array에 있는 JsonObject 하나를 싹 검사한다
        reader.beginObject();
        while (reader.hasNext()) {
            // json 키 값을 찾는다
            String name = reader.nextName();

            // 컬러인건 컬러 값으로 주소는 주소 값으로 받는다
            if (name.equals("color")) {
                color = Color.parseColor(reader.nextString());
            } else if (name.equals("imageUrl")) {
                url = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();


        // 컬러, 주소값을 리스트에 담는다
        Picture i = new Picture(color, url);
        pictureList.add(i);
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // 맨아래에 스크롤이 닿으면 10개 이미지로된 한페이지를 어맵처에 추가한다
        if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0) {
            page++;
            adaptor.addArrayList(new ArrayList<Picture>(pictureList.subList(page * 10, (page + 1) * 10)));
        }
    }
}
