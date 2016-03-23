package com.zzc.tool;

import java.util.ArrayList;
import java.util.List;

import com.zzc.dev.LogAnalyzer;
import com.zzc.model.Error;
import com.zzc.model.ErrorMaker;
import com.zzc.model.StorageStruct;

import javax.swing.*;

/**
 * Created by zuozc on 3/18/16.
 */
public class UIBackend {

    public double percentage(int part, int total) {
        if (total == 0) {
            return 0.00;
        }
        return ((double) (part) / (double) (total));
    }

    /**
     * 按人和学期分类统计错误
     *
     * */
    public void processErrorsByPersonAndSemester(List<Error> errorList, List<ErrorMaker> errorMakerList,
                                                 int semesterNum, String[] errorTypeCodes, String[] errorTypeNames, JTextArea output) {
        StorageStruct[][] structs = new StorageStruct[errorMakerList.size()][semesterNum];
        List<ArrayList<StorageStruct>> sLists = new ArrayList<ArrayList<StorageStruct>>();
        ArrayList<StorageStruct> sList;

        for (int i = 0; i < errorMakerList.size(); i++) {
            for (int j = 0; j < semesterNum; j++) {
                sList = new ArrayList<StorageStruct>();
                structs[i][j] = new StorageStruct('A', "所有的错误");
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
            structs[x][y].traverseAndStore(sLists.get(x * errorMakerList.size() + y), curErrorElem, output);
        }

        //output.append("#########################################################################\n");
        //output.append("分类错误统计(按人和学期)：\n");
        output.append("-----------------------------------------------------\n");
        for (int i = 0; i < errorMakerList.size(); i++) {
            for (int j = 0; j < semesterNum; j ++) {
                System.out.println(errorMakerList.get(i).getName() + "，在第 " + (j + 1) + " 学期的错误统计：");
                output.append(errorMakerList.get(i).getName() + "，在第 " + (j + 1) + " 学期的错误统计：" + "\n");
                sList = sLists.get(i * errorMakerList.size() + j);
                traverse(sList, "", sList.get(0).getErrorNum(), output);
                output.append("-----------------------------------------------------\n");
            }
        }
    }

    /**
     * 不分类，统计所有的错误
     *
     */
    public void processErrors(List<Error> errorList, String[] errorTypeCodes, String[] errorTypeNames, JTextArea output) {
        ArrayList<StorageStruct> sList = new ArrayList<StorageStruct>();
        StorageStruct ancestor = new StorageStruct('A', "所有的错误");
        sList.add(ancestor);
        storeErrorTypes(errorTypeCodes, sList, errorTypeNames);

        System.out.println("不分类错误统计：");
        //output.append("不分类错误统计：\n");
        output.append("-----------------------------------------------------\n");
        //String[] errorCodes = {"AAA", "AAB", "ACB", "ABC", "ABC", "ABC", "ACB", "ACA", "AAB", "ABAD"};
        for (int i = 0; i < errorList.size(); i++) {
            ancestor.traverseAndStore(sList, errorList.get(i), output);
        }
        traverse(sList, "", sList.get(0).getErrorNum(), output);
        output.append("-----------------------------------------------------\n");
    }

    public List<ErrorMaker> generateErrorMakerList(String[] errorMakers) {
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
    public void traverse(ArrayList<StorageStruct> sList, String rootErrorCode, int preLevelTotalErrorsNum, JTextArea output) {
        if (sList == null || sList.isEmpty()) {
            return ;
        }
        for (int i = 0; i < sList.size(); i++) {
            StorageStruct curNode = sList.get(i);
            rootErrorCode = rootErrorCode.concat(curNode.getCharacter().toString());
            //System.out.println(rootError + ", " + cur.errorName + " : " + cur.errorNum);
            int curLevelTotalErrorsNum = curNode.getErrorNum();
            double perc = percentage(curLevelTotalErrorsNum, preLevelTotalErrorsNum);

            //String outputLine = String.format("%5s\t%4d\t%6s", rootErrorCode, curNode.getErrorNum(), doubleToPercentage(perc));
            String outputLine = String.format("%s\t%s\t\t%4d\t%6s", rootErrorCode, curNode.getErrorName(), curNode.getErrorNum(), doubleToPercentage(perc));
            //System.out.printf("%s : %d, %s\n", rootErrorCode + ", " + curNode.getErrorName(), curNode.getErrorNum(), doubleToPercentage(perc));
            //StringBuilder sb = new StringBuilder(rootErrorCode + ", " + curNode.getErrorName() + " : " + curNode.getErrorNum() + ", ");
            //sb.append(doubleToPercentage(perc));
            //output.append(sb.toString() + "\n");

            //String[] tableVal = {rootErrorCode, curNode.getErrorName(), curNode.getErrorNum() + "", doubleToPercentage(perc)};
            //LogAnalyzer.dtm.addRow(tableVal);

            System.out.println(outputLine);
            output.append(outputLine + "\n");

            traverse(curNode.getSList(), rootErrorCode, curLevelTotalErrorsNum, output);
            rootErrorCode = rootErrorCode.substring(0, rootErrorCode.length() - 1);
        }
        //output.append("\n");
        System.out.println();
    }

    public String doubleToPercentage(double dNum) {
        dNum = Double.parseDouble(String.format("%.4f", dNum));
        return (dNum * 100.0) + "%";
    }
    /**
     * 将错误种类信息，包括错误种类码，错误种类名，存入到存储结构中
     *
     * */
    public void storeErrorTypes(String[] errorTypeCodes, ArrayList<StorageStruct> sList, String[] errorTypeNames) {
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
            String[] names = errorTypeNames[i].split(LogAnalyzer.INPUT_ERROR_LINE_SEPARATOR);
            String type = errorTypeCodes[i];
            int posOfSept = type.indexOf(LogAnalyzer.ERROR_SEPARATOR);
            String pos = type.substring(0, posOfSept);
            String errors = type.substring(posOfSept + 1);

            //System.out.println(type + "; " + pos + "; " + errors);

            addErrorTypeElements(errors, findPositionToAddErrorType(pos, sList), names);
        }
    }

    /**
     * 新建并将错误种类信息，存入存储结构中
     *
     * */
    public void addErrorTypeElements(String errorTypeCodes, StorageStruct ss, String[] errorTypeNames) {
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
    public StorageStruct findPositionToAddErrorType(String positionChars, ArrayList<StorageStruct> ssList) {
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
}
