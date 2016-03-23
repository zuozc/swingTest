package com.zzc.model;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by zuozc on 3/18/16.
 */
public class StorageStructTest {

    private static String configPath = "/gbk.properties";
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
    private static int semesterNum = 5;

    public static void main(String[] args) {
        loadConfig(configPath);

        List<ErrorMaker> errorMakerList = generateErrorMakerList(errorMakers);
        String[] errorStrs = {"ABACF-3-5", "AAA-2-1", "AABA-2-1", "ACB-2-3", "ABC-2-2", "ABC-2-1", "ABC-2-2",
                "ACB-2-1", "ACA-2-2", "AABC-2-3", "ABADF-2-4", "ABAE-2-5"};

        // make error list from error and error maker data
        List<Error> errorList = new ArrayList<Error>();
        for (int i = 0; i < errorStrs.length; i++) {
            String[] errorStr = errorStrs[i].split("-");
            String errorCode = errorStr[0];
            int errorMakerId = Integer.parseInt(errorStr[1]);
            int errorSemester = Integer.parseInt(errorStr[2]);

            Error error = new Error(errorCode, errorMakerList.get(errorMakerId - 1), errorSemester);
            errorList.add(error);
        }

        //processErrors(errorList);
        //processErrorsByPersonAndSemester(errorList);
    }

    private static void loadConfig(String configPath) {
        File f = new File(configPath);

        Properties props = new Properties();
        InputStream in = StorageStruct.class.getClass().getResourceAsStream(configPath);

        if (in == null) {
            System.out.println("failed to load property file. Use default configurations.");
        }
        else {
            InputStreamReader isr;
            try {
                String encode = "GBK";
                isr = new InputStreamReader(in, encode);
                props.load(isr);
                isr.close();
                in.close();

                Set keys = props.keySet();
                for (Iterator it = keys.iterator(); it.hasNext(); ) {
                    String k = it.next().toString();
                    System.out.println(k + ":" + props.getProperty(k));
                }
                semesterNum = Integer.parseInt(props.getProperty("semesterNumber"));
                errorMakers = props.getProperty("students").split(",");
                errorTypeCodes = props.getProperty("errorTypeCodes").split(",");
                errorTypeNames = props.getProperty("errorTypeNames").split(",");

            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException ne) {
                ne.printStackTrace();
            }
        }
    }

    public static double percentage(int part, int total) {
        if (total == 0) {
            return 0.00;
        }
        return ((double) (part) / (double) (total));
    }

    /**
     * 按人和学期分类统计错误
     *
     * */
    public static void processErrorsByPersonAndSemester(List<Error> errorList) {
        StorageStruct[][] structs = new StorageStruct[errorMakers.length][semesterNum];
        List<ArrayList<StorageStruct>> sLists = new ArrayList<ArrayList<StorageStruct>>();
        ArrayList<StorageStruct> sList;

        for (int i = 0; i < errorMakers.length; i++) {
            for (int j = 0; j < semesterNum; j++) {
                sList = new ArrayList<StorageStruct>();
                structs[i][j] = new StorageStruct('A', "所有的错误", null);
                sList.add(structs[i][j]);
                storeErrorTypes(errorTypeCodes, sList, errorTypeNames);
                sLists.add(sList);
            }
        }

        for (int i = 0 ; i < errorList.size(); i++) {
            Error curErrorElem = errorList.get(i);
            ErrorMaker curErrorMaker = curErrorElem.getErrorMaker();
            int x = curErrorMaker.getId() - 1;
            int y = curErrorElem.getSemester() - 1;
            structs[x][y].traverseAndStore(sLists.get(x * errorMakers.length + y), curErrorElem, null);
        }

        for (int i = 0; i < errorMakers.length; i++) {
            for (int j = 0; j < semesterNum; j ++) {
                System.out.println(errorMakers[i] + "，在第 " + (j + 1) + " 学期的错误统计：");
                sList = sLists.get(i * errorMakers.length + j);
                traverse(sList, "", sList.get(0).getErrorNum());
            }
        }
    }

    /**
     * 不分类，统计所有的错误
     *
     */
    public static void processErrors(List<Error> errorList) {
        ArrayList<StorageStruct> sList = new ArrayList<StorageStruct>();
        StorageStruct ancestor = new StorageStruct('A', "所有的错误", null);
        sList.add(ancestor);
        storeErrorTypes(errorTypeCodes, sList, errorTypeNames);

        //String[] errorCodes = {"AAA", "AAB", "ACB", "ABC", "ABC", "ABC", "ACB", "ACA", "AAB", "ABAD"};
        for (int i = 0; i < errorList.size(); i++) {
            ancestor.traverseAndStore(sList, errorList.get(i), new JTextArea());
        }
        traverse(sList, "", sList.get(0).getErrorNum());
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

    /**
     * 遍历所有错误信息，包括各个大类错误，及其子类的错误
     *
     * 输出：错误类名: 错误数量
     *
     * 输入：ArrayList<StorageStruct> sList 错误列表，String rootError 根错误编号，用于拼凑错误名
     *
     * */
    public static void traverse(ArrayList<StorageStruct> sList, String rootErrorCode, int preLevelTotalErrorsNum) {
        if (sList == null || sList.isEmpty()) {
            return ;
        }
        for (int i = 0; i < sList.size(); i++) {
            StorageStruct curNode = sList.get(i);
            rootErrorCode = rootErrorCode.concat(curNode.getCharacter().toString());
            //System.out.println(rootError + ", " + cur.errorName + " : " + cur.errorNum);
            int curLevelTotalErrorsNum = curNode.getErrorNum();
            System.out.printf("%s : %d, %.2f\n", rootErrorCode + ", " + curNode.getErrorName(), curNode.getErrorNum(), percentage(curLevelTotalErrorsNum, preLevelTotalErrorsNum));
            traverse(curNode.sList, rootErrorCode, curLevelTotalErrorsNum);
            rootErrorCode = rootErrorCode.substring(0, rootErrorCode.length() - 1);
        }
        System.out.println();
    }

    /**
     * 将错误种类信息，包括错误种类码，错误种类名，存入到存储结构中
     *
     * */
    public static void storeErrorTypes(String[] errorTypeCodes, ArrayList<StorageStruct> sList, String[] errorTypeNames) {
        if (errorTypeCodes == null || errorTypeCodes.length == 0) {
            System.out.println("Error Types Is Empty.");
            return ;
        }

        if (errorTypeNames == null || errorTypeNames.length == 0) {
            System.out.println("Error Type Names Is Empty.");
            return ;
        }

        if (sList == null || sList.isEmpty()) {
            System.out.println("StorageStruct List Is Empty.");
            return ;
        }

        if (errorTypeNames.length != errorTypeCodes.length) {
            System.out.println("Types' Num Is Not Equal With Type Names' Num");
        }

        for (int i = 0; i < errorTypeCodes.length; i++) {
            String[] names = errorTypeNames[i].split(";");
            String type = errorTypeCodes[i];
            int posOfSept = type.indexOf('-');
            String pos = type.substring(0, posOfSept);
            String errors = type.substring(posOfSept + 1);

            System.out.println(type + "; " + pos + "; " + errors);

            //addErrorTypeElements(errors, findPositionToAddErrorType(pos, sList), names);
            addErrorTypeElements(errors, findPosition(pos, sList), names);
        }
    }

    /**
     * 新建并将错误种类信息，存入存储结构中
     *
     * */
    public static void addErrorTypeElements(String errorTypeCodes, StorageStruct ss, String[] errorTypeNames) {
        if (errorTypeCodes == null || errorTypeCodes.isEmpty()) {
            System.out.println("Errors Is Empty.");
            return ;
        }

        // 将同属于一个父类的子错误种类（种类码和种类名称），分别加入到存储结构中
        for (int i = 0; i < errorTypeCodes.length(); i ++) {
            ss.addElement(errorTypeCodes.charAt(i), errorTypeNames[i]);
        }

    }


    /**
     * 找到当前处理的错误 type 应该存储的位置，哪个 StorageStruct List 的哪个 StorageStruct 节点
     *
     * 注意：使用该方法的前提是，要保证输入的种类码是按字母序的，否则可能出现对应错误
     *
     * 输入：String positionChars 位置码，指定了父类型的路径，ArrayList<StorageStruct> ssList 父类型的存储结构
     *
     * */
    public static StorageStruct findPositionToAddErrorType(String positionChars, ArrayList<StorageStruct> ssList) {
        if (positionChars == null || positionChars.isEmpty()) {
            return null;
        }

        StorageStruct target = null;
        for (int i = 0; i < positionChars.length(); i ++) {
            int curPos = positionChars.charAt(i) - StorageStruct.ROOT;
            target = ssList.get(curPos);
            ssList = target.getSList();
        }
        return target;
    }


    public static StorageStruct findPosition(String positionChars, ArrayList<StorageStruct> ssList) {
        if (positionChars == null || positionChars.isEmpty()) {
            return null;
        }
        if (ssList == null) {
            System.out.println("this Storage should not be null.");
            throw new NullPointerException("this Storage should not be null.");
        }

        ArrayList<StorageStruct> whereToStore = ssList;
        StorageStruct curNode = null;
        for (int i = 0; i < positionChars.length(); i++) {
            int j = 0;
            for (; j < whereToStore.size(); j ++) {
                curNode = whereToStore.get(j);
                if (curNode.getCharacter() == positionChars.charAt(i)) {
                    whereToStore = curNode.getSList();
                    break;
                }
            }
//            if (j == whereToStore.size()) {
//                System.out.println("当前的错误类型集中，不存在 " + positionChars + ", 请检查...");
//                throw new IllegalArgumentException("当前的错误类型集中，不存在 " + positionChars + ", 请检查...");
//            }
        }
        return curNode;
    }
}
