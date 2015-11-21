package com.toast.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    final String TAG = "haegonkoh";

    EditText et1;
    EditText et2;

    Button btn;

    TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et1 = (EditText)findViewById(R.id.editText_1);
        et2 = (EditText)findViewById(R.id.editText_2);
        result = (TextView)findViewById(R.id.text_index);
        btn = (Button)findViewById(R.id.btn_find);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            // 버튼을 누르면 입력받은 두 문자열을 가지고 첫번째 문자열에 두번째 문자열이 있으면 그 시작 인덱스를 결과에 출력한다
            case R.id.btn_find:
                NativeString n = new NativeString();
                result.setText( String.format("%d",n.find(et1.getText().toString(), et2.getText().toString())) );
                break;
            default:
                Log.e(TAG,"unknown view : " + v.toString());
                break;
        }
    }
}
