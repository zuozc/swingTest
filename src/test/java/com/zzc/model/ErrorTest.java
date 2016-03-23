package com.zzc.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zuozc on 3/20/16.
 */
public class ErrorTest {
    public static void main(String args[]) {
        String[] errorMakers = {"刘芳", "叶茂", "肖焕", "杨连伟", "张子同"};
        List<ErrorMaker> errorMakerList = generateErrorMakerList(errorMakers);
        String[] errorTypeCodes = {"A-ABC", "AA-AB", "AB-ABCDE", "AC-ABC", "AAA-ABC", "AAB-ABCD", "ABA-ABCD", "ACC-ABCDE"};
        String[] errorTypeNames = {"语法;单句结构常见错误;复句运用错误",
                "实词;虚词",
                "搭配不当;成份残缺;成份多余;语序不当;句式杂糅",
                "分句之间缺乏密切的联系;结构层次混乱;关联词语使用的错误",
                "名次、动词形、容词的误用;数词、量词使用不当;代词使用不当",
                "副词误用;介词误用;连词误用;助词误用",
                "主谓搭配不当;动宾搭配不当;定语和中心语、状语和中心语、中心语和补语不搭配;主语和宾语不搭配",
                "关联词语搭配不当;缺少必要的关联词语;错用关联词语;滥用关联词语;关联词语位置不对"
        };
        String[] errors = {"AAA-2-1", "AAB-2-1", "ACB-2-3", "ABC-2-2", "ABC-2-1", "ABC-2-2",
                "ACB-2-1", "ACA-2-2", "AAB-2-3", "ABAD-2-4", "ABAC-2-5"};
    }

    public static List<ErrorMaker> generateErrorMakerList(String[] errorMakers) {
        if (errorMakers == null || errorMakers.length == 0) {
            return null;
        }

        List<ErrorMaker> errorMakerList = new ArrayList<ErrorMaker>();
        for (int i = 0; i < errorMakers.length; i++) {
            ErrorMaker errorMaker = new ErrorMaker(i + 1, errorMakers[i]);
            errorMakerList.add(errorMaker);
        }
        return errorMakerList;
    }

}
