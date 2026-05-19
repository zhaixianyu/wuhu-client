package com.zxy.wuhuclient.Utils;

import net.minecraft.world.food.FoodProperties;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PinYinSearch {
    public static void main(String[] args) {
        getPinYin("曾0长0");
    }

    public static ArrayList<String> getPinYin(String str) {

        char[] ch = str.toCharArray();
        HanyuPinyinOutputFormat gs = new HanyuPinyinOutputFormat();
        gs.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        gs.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        gs.setVCharType(HanyuPinyinVCharType.WITH_V);

        ArrayList<String[]> pinyin = new ArrayList<>();
        try {
            for (char c : ch) {
                if (c <= 128) pinyin.add(new String[]{"" + c});
                else pinyin.add(PinyinHelper.toHanyuPinyinStringArray(c, gs));
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            throw new RuntimeException(e);
        }
        return getStrings(pinyin);
    }

    public static boolean hasPinYin(String zh, String py) {
        return getPinYin(zh).stream().anyMatch(s -> s.contains(py));
    }

    @NotNull
    private static ArrayList<String> getStrings(ArrayList<String[]> pinyin) {
        if(pinyin == null || pinyin.isEmpty()) return new ArrayList<>();
        //全拼
        ArrayList<String> quanPin = new ArrayList<>();
        //首字母简拼
        ArrayList<String> jianPin = new ArrayList<>();
        //如果输入的是 曾长 那么pinyin中的内容为 ["zeng","ceng"],["chang","zhang"]
        //处理第一个字 不要信idea的用getFirst方法 旧版jdk不支持
        for (String py : pinyin.get(0)) {
            quanPin.add(py);
            jianPin.add("" + py.charAt(0));
        }

        //从第二个字开始遍历
        for (int i = 1; i < pinyin.size(); i++) {
            ArrayList<String> tempQuanPin = new ArrayList<>();
            ArrayList<String> tempJianPin = new ArrayList<>();
            //遍历当前字的全部读音
            for (String currPinYin : pinyin.get(i)) {
                for (int i1 = 0; i1 < quanPin.size(); i1++) {
                    tempQuanPin.add(quanPin.get(i1) + currPinYin);
                    tempJianPin.add(jianPin.get(i1) + currPinYin.charAt(0));
                }
            }
            quanPin = tempQuanPin;
            jianPin = tempJianPin;
        }
        quanPin.addAll(jianPin);
        return quanPin;
    }

}
