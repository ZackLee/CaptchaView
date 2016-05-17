package me.lino.captchainputdemo.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.lino.captchainputdemo.CustomPasswordTransformationMethod;
import me.lino.captchainputdemo.R;
import me.lino.captchainputdemo.view.imebugfixer.ImeDelBugFixedEditText;

/**
 * Captcha View
 * Created by lino on 16/5/17.
 */
public class CaptchaView extends LinearLayout {

    private ImeDelBugFixedEditText inputView;
    private TextView[] textViews;
    private Drawable lineDrawable;

    private static int CAPTCHA_COUNT = 4;
    private static final int DEFAULT_LINECOLOR = 0xaa888888;
    private String[] captchas;
    private PasswordTransformationMethod transformationMethod;
    private String transformation = "●";

    public CaptchaView(Context context) {
        this(context, null);
    }

    public CaptchaView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CaptchaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public void init(Context context, AttributeSet attrs, int defStyleAttr) {
        initArgs(context, attrs, defStyleAttr);
        initViews(context);
    }

    public void initArgs(Context context, AttributeSet attrs, int defStyleAttr) {
        if (lineDrawable == null)
            lineDrawable = new ColorDrawable(DEFAULT_LINECOLOR);

        textViews = new TextView[CAPTCHA_COUNT];
        captchas = new String[CAPTCHA_COUNT];

        transformationMethod = new CustomPasswordTransformationMethod(transformation);
    }

    public void initViews(Context context) {
        setBackgroundDrawable(initBackground());
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.edit_layout, this);

        //添加editText
        inputView = (ImeDelBugFixedEditText) view.findViewById(R.id.inputView);
        inputView.setTransformationMethod(transformationMethod);
        inputView.setMaxEms(CAPTCHA_COUNT);
//        inputView.setTextSize(5);
        // 添加删除键监听
        inputView.setDelKeyEventListener(onDelKeyEventListener);

        //添加输入监听
        inputView.addTextChangedListener(textWatcher);

        textViews[0] = inputView;
        for (int index = 1; index < CAPTCHA_COUNT; index++) {
            //diver
            View diviver = inflater.inflate(R.layout.divider, null);
            diviver.setBackgroundDrawable(lineDrawable);
            LayoutParams params = new LayoutParams(5, LayoutParams.MATCH_PARENT);
            addView(diviver, params);

            TextView textView = (TextView) inflater.inflate(R.layout.textview, null);
            LayoutParams params1 = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
            textView.setTransformationMethod(transformationMethod);

            addView(textView, params1);

            textViews[index] = textView;
        }

        //focus on
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                forceInputViewGetFocus();
            }
        });
    }

    private void forceInputViewGetFocus() {
        inputView.setFocusable(true);
        inputView.setFocusableInTouchMode(true);
        inputView.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(inputView, InputMethodManager.SHOW_IMPLICIT);
    }

    public Drawable initBackground() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.WHITE);
        drawable.setStroke(5, DEFAULT_LINECOLOR);

        return drawable;
    }


    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s == null) {
                return;
            }

            String newStr = s.toString();
            if (newStr.length() == 1) {
                captchas[0] = newStr;
            } else if (newStr.length() == 2) {
                String newNum = newStr.substring(1);
                for (int i = 0; i < captchas.length; i++) {
                    if (captchas[i] == null) {
                        captchas[i] = newNum;
                        textViews[i].setText(newNum);
                        break;
                    }
                }
                inputView.removeTextChangedListener(this);
                inputView.setText(captchas[0]);
                if (inputView.getText().length() >= 1) {
                    inputView.setSelection(1);
                }
                inputView.addTextChangedListener(this);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private ImeDelBugFixedEditText.OnDelKeyEventListener onDelKeyEventListener = new ImeDelBugFixedEditText.OnDelKeyEventListener() {

        @Override
        public void onDeleteClick() {
            for (int i = captchas.length - 1; i >= 0; i--) {
                if (captchas[i] != null) {
                    captchas[i] = null;
                    textViews[i].setText(null);
                    break;
                } else {
                    textViews[i].setText(null);
                }
            }
        }
    };


}
