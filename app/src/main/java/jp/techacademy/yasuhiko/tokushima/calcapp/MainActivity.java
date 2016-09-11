package jp.techacademy.yasuhiko.tokushima.calcapp;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.apache.commons.lang3.math.NumberUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Log用タグ
    String logTag = "calc-log";
    // Log出力コントロール用フラグ
    static boolean log_f = false;        // true: 出力する、false: 出力しない
    EditText editText1;
    EditText editText2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText1 = (EditText) findViewById(R.id.editText1);
        editText1.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // key入力された時の共通処理に飛ばして戻り値をそのまま返す
                // （感覚的にはこの中に書いたものと同じように書けるが、他のEditTextでも使えるようにするため）
                return procKeyInput(editText1, v, keyCode, event);
            }
        });

        editText2 = (EditText) findViewById(R.id.editText2);
        editText2.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // key入力された時の共通処理に飛ばして戻り値をそのまま返す
                return procKeyInput(editText2, v, keyCode, event);
            }
        });

        Button button_plus = (Button) findViewById(R.id.button_plus);
        button_plus.setOnClickListener(this);

        Button button_minus = (Button) findViewById(R.id.button_minus);
        button_minus.setOnClickListener(this);

        Button button_multiply = (Button) findViewById(R.id.button_multiply);
        button_multiply.setOnClickListener(this);

        Button button_division = (Button) findViewById(R.id.button_division);
        button_division.setOnClickListener(this);

        Button button_clear = (Button) findViewById(R.id.button_clear);
        button_clear.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, SecondActivity.class);
        String ans = "";

        if (v.getId() == R.id.button_plus) {
            ans = getCalc("+");
        } else if (v.getId() == R.id.button_minus) {
            ans = getCalc("-");
        } else if (v.getId() == R.id.button_multiply) {
            ans = getCalc("*");
        } else if (v.getId() == R.id.button_division) {
            ans = getCalc("/");
        } else if (v.getId() == R.id.button_clear) {
            editText1.setText("");
            editText2.setText("");
            return;
        }

        if (log_f) Log.d(logTag, "onClick(1) ans = " + ans);

        // error以外の時に次の画面へ遷移する
        if (!ans.equals("error")) {
            String formatAns = formatNum(ans.replace(",", ""));
            formatAns = formatNum2(formatAns);
            if (log_f) Log.d(logTag, "intentデータ formatAns = " + formatAns);
            intent.putExtra("ANSWER", formatAns);
            startActivity(intent);
        }
    }

    // key入力された時の共通処理
    private boolean procKeyInput(EditText editText, View v, int keyCode, KeyEvent event) {
        // 入力された値毎の処理（より数値入力が自然になるようにするため）
    if (event.getAction() == KeyEvent.ACTION_DOWN
            && keyCode == KeyEvent.KEYCODE_0) {
        // 数値が0しか入力されていない時、0が２個以上並ばないように入力を抑制する
        if (editText.getText().toString().equals("0")) {
            return true;
        }
    } else if (event.getAction() == KeyEvent.ACTION_UP) {
            // 上記以外のKey入力処理

            // 数値判定
            // 空白だったら何もしない
            if (editText.getText().toString().equals("")) {
                if (log_f) Log.d(logTag, "空白判定。なにもしない");
                return true;
            } else {
                // カンマ編集、小数点以下の編集を行う
                String str1 = formatNum(editText.getText().toString().replace(",", ""));
                if (log_f) Log.d(logTag, "カンマと小数点以下整形後 入力値 = " + str1);
                editText.setText(str1);

                // カーソル位置を最後に移動させる
                editText.setSelection(editText.getText().length());
            }
        }
        // 上記以外はfalseを返す
        return false;
    }

    // 数値チェック（Apache Commons LangのNumberUtils使用）
    public boolean isNumeric(String s) {
        if (log_f) Log.d(logTag,"(isNumeric) 入力 = [" + s + "]");
        return NumberUtils.isNumber(s);
    }

    // 四則演算処理
    // 念のため、画面の数値１、数値２に対して、数値チェックも行う
    // 数値チェックエラーの場合の戻り値 = "error"
    //
    // 引数=sの値により、四則演算のどれかを判断する
    // "+" = 足し算、"-"=引き算、"*"=掛け算、"/"=割り算
    private String getCalc(String s) {
        Double d1;
        Double d2;
        Double d_ans;

        // 画面の数値１、数値２に対して、数値チェックとDouble変数へのセット
        // なにやら、BigDecimalの方が良いみたいだけど、minSdkVersionが19だと使えないみたいなので）
        if (isNumeric(editText1.getText().toString().replace(",", ""))) {
            d1 = Double.parseDouble(editText1.getText().toString().replace(",", ""));
        } else {
            showErrorDialog("数値1に数字を入力して下さい。");
            return "error";
        }
        if (isNumeric(editText2.getText().toString().replace(",", ""))) {
            d2 = Double.parseDouble(editText2.getText().toString().replace(",", ""));
        } else {
            showErrorDialog("数値2に数字を入力して下さい。");
            return "error";
        }

        if (log_f) Log.d(logTag, "四則演算(1) d1 = " + String.valueOf(d1) + ", d2 = " + String.valueOf(d2));
        // 四則演算処理
        switch (s) {
            case "+":
                d_ans = d1 + d2;
                break;
            case "-":
                d_ans = d1 - d2;
                break;
            case "*":
                d_ans = d1 * d2;
                break;
            case "/":
                if (d2 == 0d) {
                    showErrorDialog("0で割ることは出来ません（数値2 = 0）");
                    return "error";
                }
                d_ans = d1 / d2;
                break;
            default:
                d_ans = 0d;
        }

        if (log_f) Log.d(logTag, "四則演算(2) d_ans = " + String.valueOf(d_ans));

        return String.valueOf(d_ans);
    }

    // 数値フォーマット用の関数
    // （minSdkVersionが19だとDecimalFormatやNumberFormatが使えないみたいなので、
    //   自前で作成。この条件で使える同系統の関数も調べにくかったので・・・^ ^;;;)
    // なお、今回はターゲットをDouble型のみに限定
    // 主な機能
    // ・整数部分をカンマ(,)区切りにする
    // ・小数点以下が0だった場合、整数部分のみを返す
    public String formatNum(String s) {
        if (log_f) Log.d(logTag, "formatNum関数スタート");
        // Stringに返還
        String str1;        // 整数部用
        String str1z = "";  // 整数部の先頭の0をカットする時に使用する
        String str1c = "";  // カンマ区切り後の整数部を格納するための変数
        String str2;        // 少数部用
        String str_fugo = "";

        // 符号の有無を調べる（マイナス値かどうか調べる）
        if (s.substring(0, 1).equals("-")) {
            str_fugo = "-";
        }
        if (log_f) Log.d(logTag, "符号の値 = " + str_fugo);

        // 小数点の位置を調べる
        int iPoint = s.indexOf(".");
        if (log_f) Log.d(logTag, "小数点の位置 = " + String.valueOf(iPoint));

        // 整数部と少数部に分けてそれぞれ格納する（マイナス値だった場合、先頭のマイナスは外す）
        if (iPoint >= 0) {
            if (str_fugo.equals("-")) {
                str1 = s.substring(1, iPoint);
            } else {
                str1 = s.substring(0, iPoint);
            }
            str2 = s.substring(iPoint + 1);
        } else {
            if (str_fugo.equals("-")) {
                str1 = s.substring(1);
            } else {
                str1 = s;
            }
            str2 = "";
        }
        if (log_f) Log.d(logTag, "str1 = " + str1 + ", str2 = " + str2);

        // str1は、頭の0をカットする
        for (int i = 0; i < str1.length(); i++) {
            if (log_f) Log.d(logTag, "先頭0カット  str1z = " + str1z + ", str1.substring(i, i + 1) = " +
                    str1.substring(i, i + 1) + ", i = " + String.valueOf(i));
            if (!str1.substring(i, i + 1).equals("0")) {
                str1z = str1z + str1.substring(i);
                break;
            }
        }
        if (str1z.equals("")) {
            str1z = "0";
        }
        if (log_f) Log.d(logTag, "str1 = " + str1 + ", str1z = " + str1z);
        if (isNumeric(str1z)) {
            int i_str1 = Integer.parseInt(str1z);
            str1z = String.valueOf(i_str1);
            if (log_f) Log.d(logTag, "str1z = " + str1z + ", i_str1 = " + String.valueOf(i_str1));
        }

        // 整数部をカンマ区切りにする
        int j = str1z.length();
        for (int i = 0; i <= str1z.length() - 1; i++) {
            if (log_f) Log.d(logTag, "str1c(1) = " + str1c + ", str1 = " + str1z + ", str1(" + String.valueOf(i) +
                    "," + String.valueOf(i+1) + ") = " + str1z.substring(i, i + 1) + ", i = " +
                    String.valueOf(i) + ", j = " + String.valueOf(j));
            // jが3の倍数+1だった時にカンマを付ける
            if (j % 3 == 1 && j > 2) {
                str1c = str1c + str1z.substring(i, i + 1) + ",";
            } else {
                str1c = str1c + str1z.substring(i, i + 1);
            }
            j--;
            if (log_f) Log.d(logTag, "str1c(2) = " + str1c + ", i = " + String.valueOf(i) + ", j = " + String.valueOf(j));
        }
        if (log_f) Log.d(logTag, "str1c(3) = " + str1c);

        if (str2.equals("")) {
            if (log_f) Log.d(logTag, "formatNum 戻り値 = " + str_fugo + str1c);
            return str_fugo + str1c;
        } else {
            if (log_f) Log.d(logTag, "formatNum 戻り値 = " + str_fugo + str1c + "." + str2);
            return str_fugo + str1c + "." + str2;
        }
    }

    // 小数点以下が"0"の時に".0"を消すための関数
    public String formatNum2(String s) {
        if (log_f) Log.d(logTag, "formatNum2関数スタート");
        // Stringに返還
        String str1;        // 整数部用
        String str2;        // 少数部用

        // 小数点の位置を調べる
        int iPoint = s.indexOf(".");
        if (log_f) Log.d(logTag, "小数点の位置 = " + String.valueOf(iPoint));

        // 整数部と少数部に分けてそれぞれ格納する（マイナス値だった場合、先頭のマイナスは外す）
        if (iPoint >= 0) {
            str1 = s.substring(0, iPoint);
            str2 = s.substring(iPoint + 1);
        } else {
            str1 = s;
            str2 = "0";
        }
        if (log_f) Log.d(logTag, "str1 = " + str1 + ", str2 = " + str2);

        if (str2.equals("0")) {
            if (log_f) Log.d(logTag, "formatNum 戻り値 = " + str1);
            return str1;
        } else {
            if (log_f) Log.d(logTag, "formatNum 戻り値 = " + str1 + "." + str2);
            return str1 + "." + str2;
        }
    }

    // エラー時のメッセージを表示する
    private void showErrorDialog(String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("エラー");
        alertDialogBuilder.setMessage(msg);
        alertDialogBuilder.setPositiveButton("OK", null);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
