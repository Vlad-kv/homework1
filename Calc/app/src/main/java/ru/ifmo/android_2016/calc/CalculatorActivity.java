package ru.ifmo.android_2016.calc;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class CalculatorActivity extends AppCompatActivity {

    private static final String KEY_INPUT_STR = "INPUT_STR";
    private static final String KEY_SELECTION_POS = "SELECTION_POS";
    private static final String KEY_RESULT_STR = "RESULT_STR";

    private TextView result;
    private EditText input;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(KEY_INPUT_STR, input.getText().toString());
        outState.putInt(KEY_SELECTION_POS, input.getSelectionEnd());
        outState.putString(KEY_RESULT_STR, result.getText().toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        result = (TextView)findViewById(R.id.result);
        input = (EditText)findViewById(R.id.input);

        if (savedInstanceState != null) {
            input.setText(savedInstanceState.getString(KEY_INPUT_STR));
            input.setSelection(savedInstanceState.getInt(KEY_SELECTION_POS));
            result.setText(savedInstanceState.getString(KEY_RESULT_STR));
        }
    }

    private String inStr;
    int pos;

    char nextChar() {
        while ((pos < inStr.length()) && (inStr.charAt(pos) == ' ')) {
            pos++;
        }
        if (pos == inStr.length()) {
            return 0;
        }
        return inStr.charAt(pos);
    }

    boolean isDigit(char c) {
        return (('0' <= c) && (c <= '9'));
    }

    double getNumber() throws MyException{
        if (! isDigit(nextChar())) {
            throw new IncorrectExpressionException();
        }

        double res = 0;

        while (isDigit(nextChar())) {
            res = res * 10 + nextChar() - '0';
            pos++;
        }

        if (nextChar() != ',') {
            return res;
        }

        pos++;

        if (! isDigit(nextChar())) {
            throw new IncorrectExpressionException();
        }

        double exp = 1;

        while (isDigit(nextChar())) {
            exp *= 0.1;
            res += (nextChar() - '0') * exp;
            pos++;
        }
        return res;
    }

    double getExpr() throws MyException {
        if (nextChar() == '(') {
            pos++;
            double res = getSum();
            if (nextChar() != ')') {
                throw new IncorrectExpressionException();
            }
            pos++;
            return res;
        }
        if (nextChar() == '+') {
            pos++;
            return getExpr();
        }
        if (nextChar() == '-') {
            pos++;
            return -getExpr();
        }
        return getNumber();
    }

    double getMultiplication() throws MyException{
        double res = getExpr();
        while ((nextChar() == '*') || (nextChar() == '/')) {
            if (nextChar() == '*') {
                pos++;
                res = res * getExpr();
            } else {
                pos++;
                res = res / getExpr();
            }
        }
        return res;
    }

    double getSum() throws MyException{
        double res = getMultiplication();
        while ((nextChar() == '+') || (nextChar() == '-')) {
            if (nextChar() == '+') {
                pos++;
                res = res + getMultiplication();
            } else {
                pos++;
                res = res - getMultiplication();
            }
        }
        return res;
    }

    double parse() throws MyException {
        pos = 0;
        double res = getSum();
        if (nextChar() != 0) {
            throw new IncorrectExpressionException();
        }
        return res;
    }

    public void onClickEqv(View view) {
        inStr = input.getText().toString();
        double res;

        try{
            res = parse();
        } catch (IncorrectExpressionException ex) {
            result.setText(R.string.incorrect_expression_exception_text);
            return;
        } catch (MyException ex) {
            result.setText(R.string.error_text);
            return;
        }
        result.setText(String.valueOf(res));
    }

    public void onClickClear(View view) {
        input.setText("");
        result.setText("");
    }

    public void onClickAdded(View view) {
        String s = "";
        switch (view.getId()) {
            case R.id.d0:
                s = "0";
                break;
            case R.id.d1:
                s = "1";
                break;
            case R.id.d2:
                s = "2";
                break;
            case R.id.d3:
                s = "3";
                break;
            case R.id.d4:
                s = "4";
                break;
            case R.id.d5:
                s = "5";
                break;
            case R.id.d6:
                s = "6";
                break;
            case R.id.d7:
                s = "7";
                break;
            case R.id.d8:
                s = "8";
                break;
            case R.id.d9:
                s = "9";
                break;
            case R.id.add:
                s = "+";
                break;
            case R.id.sub:
                s = "-";
                break;
            case R.id.mul:
                s = "*";
                break;
            case R.id.div:
                s = "/";
                break;
            case R.id.left_bracket:
                s = "(";
                break;
            case R.id.right_bracket:
                s = ")";
                break;
            case R.id.comma:
                s = ",";
                break;
            case R.id.space_button:
                s = " ";
                break;
        }

        StringBuilder str = new StringBuilder(input.getText());
        int pos = input.getSelectionEnd();

        str.insert(pos, s);

        input.setText(str);
        input.setSelection(pos + 1);
    }

    public void onClickDel(View view) {
        int pos = input.getSelectionEnd();
        StringBuilder str = new StringBuilder(input.getText());

        if (pos > 0) {
            str.deleteCharAt(pos - 1);
            pos--;
        }
        input.setText(str);
        input.setSelection(pos);
    }
}
