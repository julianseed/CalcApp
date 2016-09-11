package jp.techacademy.yasuhiko.tokushima.calcapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {

    // Log用タグ
    String logTag = "calc-log";
    // Log出力コントロール用フラグ
    static boolean log_f = false;        // true: 出力する、false: 出力しない

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Intent intent = getIntent();
        String str = intent.getStringExtra("ANSWER");

        if (log_f) Log.d(logTag, "Second onCreate(1) str = " + str);

        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(str);
    }
}
