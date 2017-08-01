package com.haizhi.np.dispatch.util;

import com.haizhi.np.dispatch.constants.EnterpriseStatus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhangjunfei on 2017/6/19.
 */
public class Utils {

    public static String unicodeToString(String str) {
        if(str.startsWith("\"")){
            str = str.substring(1,str.length()-1);
        }
        if(!str.startsWith("\\")){
            return str;
        }
        Pattern pattern = null;
        if (str.startsWith("&#")) {
            pattern = Pattern.compile("(?:&#(\\d{4,5});?)");
        } else if (str.startsWith("\\u")) {
            pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        } else {
            throw new RuntimeException("Unicode Not Support");
        }

        char ch;
        String ret = "";
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            if (matcher.groupCount() > 1) {
                ch = (char) Integer.parseInt(matcher.group(2), 16);
            } else {
                ch = (char) Integer.parseInt(matcher.group(1), 10);
            }
            ret += ch;
        }
        return ret;
    }

    public static boolean matchNegativeStatus(String status){
        return Pattern.matches(EnterpriseStatus.ENTTERPRISESTATUSREGEX,status);
    }
}
