package com.haegonkoh.nhn_haegonkoh_quiz1;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Gohn on 2015. 10. 31..
 */
public class PictureListAdapter extends BaseAdapter {

    // 사진 외곽선의 두께를 dp로 사용
    final int STROKE_WIDTH = 20;

    // MainActivity를 가진다
    Activity activity;

    // 서버에서 받은 데이터를 넘겨 받는다
    ArrayList<Picture> data;
    LayoutInflater layout;

    // 사진을 로드해줄 녀석
    PictureLoader pictureLoader;

    public PictureListAdapter(MainActivity activity, ArrayList<Picture> data) {

        this.activity = activity;
        this.data = data;
        this.layout = LayoutInflater.from(activity.getApplicationContext());
        this.pictureLoader = new PictureLoader(activity);
    }

    public void add(int index, Picture addData) {
        data.add(index, addData);
        notifyDataSetChanged();
    }

    // 10개씩 넣을때 사용한다
    public void addArrayList(ArrayList<Picture> addData) {
        for ( int i = 0 ; i < addData.size() ; i++ ) {
            data.add(addData.get(i));
        }
        // 추가 되면 어탭터 갱신
        notifyDataSetChanged();
    }

    public void delete(int index) {
        data.remove(index);
        notifyDataSetChanged();
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View itemLayout = convertView;

        // 리스트뷰에서 리스트 레이아웃 하나에 들어갈 컴포넌트를 초기화 한다.
        if (itemLayout == null) {
            itemLayout = layout.inflate(R.layout.picture_list, null);
        }

        // list에 넣을 이미지뷰를 가져온다
        ImageView imageView = (ImageView) itemLayout.findViewById(R.id.imageJSON);

        // PictureLoader를 통해 이미지를 imageView에 출력한다.
        pictureLoader.DisplayImage(imageView, data.get(position).url);

        // 외곽선은 서버데이터를 따라 칠한다
        GradientDrawable backgroundGradient = (GradientDrawable) imageView.getBackground();
        backgroundGradient.setStroke(Util.dpToPixel(activity, STROKE_WIDTH), data.get(position).color);

        return itemLayout;
    }
}
