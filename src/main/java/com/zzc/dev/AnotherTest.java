package com.zzc.dev;

/**
 * Created by zuozc on 3/18/16.
 */

import com.zzc.tool.UIBackend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AnotherTest {

    private static String[] errorTypeCodes = {"A-ABC", "AA-AB", "AB-ABCDE", "AC-ABC", "AAA-ABC", "AAB-ABCD", "ABA-ABCD", "ACC-ABCDE"};
    private static String[] errorTypeNames = {"语法;单句结构常见错误;复句运用错误",
            "实词;虚词",
            "搭配不当;成份残缺;成份多余;语序不当;句式杂糅",
            "分句之间缺乏密切的联系;结构层次混乱;关联词语使用的错误",
            "名词、动词形、容词的误用;数词、量词使用不当;代词使用不当",
            "副词误用;介词误用;连词误用;助词误用",
            "主谓搭配不当;动宾搭配不当;定语和中心语、状语和中心语、中心语和补语不搭配;主语和宾语不搭配",
            "关联词语搭配不当;缺少必要的关联词语;错用关联词语;滥用关联词语;关联词语位置不对"
    };
    private static String[] errorMakers = {"刘芳", "叶茂", "肖焕", "杨连伟", "张子同"};
    private static String[] students = {"Liu", "Ye", "Xiao", "Yang", "Zhang"};
    private static int semesterNum = 5;
    private static String pattern = "([A-Z]{1,5}-[1-5]-[1-5];)*[A-Z]{1,5}-[1-5]-[1-5]";

    public static void main(String[] args) {

        UIBackend ui = new UIBackend();
        double[] ds = {0.00, 0.01, 0.0001, (2.0/3.0), 0.757575, 0.0000000000000000000000000000000000000001};

        for (int i = 0; i < ds.length; i++) {
            System.out.println(ui.doubleToPercentage(ds[i]));
            //System.out.println(ui.doubleToPercentage(0.00));
        }

        String s = "定语和中心语、状语和中心语、中心语和补语不搭配";
        for (int i = 0; i < errorTypeNames.length; i++) {
            String[] names = errorTypeNames[i].split(";");
            for (int j = 0; j < names.length; j++) {
                System.out.printf("%25s\n", names[j]);
            }
        }
        //System.out.printf("%50s\n", s);

        System.out.println(s.length());
        //test();

    }

    public static void test() {
        String s = "AAAA---2-1  ;A ABA-2-1;ACB-2-3||;ABC-2-2?;ABC-2-1;ABC- 2-2;  ;;ACB-2-1; ACA-2-2; AABC-2-3;ABAD-2-4; ABAC-2-5 ;";

        s = s.replaceAll("[^A-Z1-5-;/-]", "").replaceAll(";{2,}", ";").replaceAll("-{2,}", "-").replaceAll(";$", "");
        System.out.println(s);

        System.out.println(s.replaceAll("[^A-Z1-5-;/-]", "").replaceAll(";{2,}", ";").replaceAll("-{2,}", "-").replaceAll(";$", ""));

        if (s.matches(pattern)) {
            System.out.println("matched!!!!!");
        }

        InputStreamReader isr;
        BufferedReader br;

        try {
            String errorFilePath = "/Users/zuozc/error_test.txt";
            File errorFile = new File(errorFilePath);
            isr = new InputStreamReader(new FileInputStream(errorFile));
            br = new BufferedReader(isr);

            String line;
            List<String> errors = new ArrayList<String>();
            while ((line = br.readLine()) != null) {
                line = line.replaceAll("[^A-Z1-5-;/-]", "").replaceAll(";{2,}", ";").replaceAll("-{2,}", "-").replaceAll(";$", "");

                if (line.matches(pattern)) {
                    String[] errorStrs = line.split(LogAnalyzer.INPUT_ERROR_LINE_SEPARATOR);
                    for (int i = 0; i < errorStrs.length; i++) {
                        errors.add(errorStrs[i]);
                    }
                } else {
                    System.out.println("not matched: " + line);
                }
            }
            System.out.println("totally " + errors.size() + " errors.");
            br.close();
            isr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

