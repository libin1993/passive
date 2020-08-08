package com.synway.passive.location.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Author：Li Bin on 2019/8/1 16:23
 * Description：
 */
public class LimitInputTextWatcher implements TextWatcher {
    /**
     * et
     */
    private EditText et = null;
    /**
     * 筛选条件
     */
    private String regex;

    // 默认的筛选条件
    public static String DEFAULT_REGEX = "[^a-zA-Z0-9\\u4E00-\\u9FA5]";

    /**
     * (正则:只能输入简体中文)
     */
    public static String CHINESE_REGEX = "[^\\u4E00-\\u9FA5]";

    public static String CHINESE_NUMBER_REGEX = "[^0-9\\u4E00-\\u9FA5]";

    /**
     * 构造方法
     *
     * @param et
     */
    public LimitInputTextWatcher(EditText et) {
        this.et = et;
        this.regex = DEFAULT_REGEX;
    }

    /**
     * 构造方法
     *
     * @param et    et
     * @param regex 筛选条件
     */
    public LimitInputTextWatcher(EditText et, String regex) {
        this.et = et;
        this.regex = regex;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String str = editable.toString();
        String inputStr = clearLimitStr(regex, str);
        et.removeTextChangedListener(this);
        // et.setText方法可能会引起键盘变化,所以用editable.replace来显示内容
        editable.replace(0, editable.length(), inputStr.trim());
        et.addTextChangedListener(this);

    }

    /**
     * 清除不符合条件的内容
     *
     * @param regex
     * @return
     */
    private String clearLimitStr(String regex, String str) {
        return str.replaceAll(regex, "");
    }
}
