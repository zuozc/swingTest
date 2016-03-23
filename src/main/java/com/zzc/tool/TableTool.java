package com.zzc.tool;

import java.util.ArrayList;
import java.util.List;

import com.zzc.UI.TryToUseTable;
import com.zzc.model.Error;
import com.zzc.model.ErrorMaker;
import com.zzc.model.StorageStruct;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Created by zuozc on 3/18/16.
 */
public class TableTool {

    /**
     * 计算数量百分比
     *
     * */
    public double percentage(int part, int total) {
        if (total == 0) {
            return 0.00;
        }
        return ((double) (part) / (double) (total));
    }

    public ArrayList<ArrayList<StorageStruct>> initiate(List<Error> errorList, List<ErrorMaker> errorMakerList, int semesterNum,
                                                        String[] errorTypeCodes, String[] errorTypeNames, DefaultListModel listModel, JTextArea output) {
        ArrayList<ArrayList<StorageStruct>> sLists = new ArrayList<ArrayList<StorageStruct>>();

        //processErrors(errorList, errorTypeCodes, errorTypeNames, sLists, listModel, output);
        processErrorsByPersonAndSemester(errorList, errorMakerList, semesterNum, errorTypeCodes, errorTypeNames, sLists, listModel, output);
        int length = sLists.size();

        processErrorsForAll(sLists, length, errorTypeCodes, errorTypeNames, listModel);
        processErrorsByPerson(sLists, length, errorMakerList, semesterNum, errorTypeCodes, errorTypeNames, listModel);
        processErrorsBySemester(sLists, length, errorMakerList, semesterNum, errorTypeCodes, errorTypeNames, listModel);
        //processErrors(errorList, errorTypeCodes, errorTypeNames, sLists, listModel, output);
        return sLists;
    }

    /**
     * 按人和学期分类统计错误
     *
     * */
    public void processErrorsByPersonAndSemester(List<Error> errorList, List<ErrorMaker> errorMakerList, int semesterNum,
                                                 String[] errorTypeCodes, String[] errorTypeNames,
                                                 ArrayList<ArrayList<StorageStruct>> sLists, DefaultListModel listModel, JTextArea output) {
        ArrayList<StorageStruct> sList;
        StorageStruct[][] structs = new StorageStruct[errorMakerList.size()][semesterNum];

        for (int i = 0; i < errorMakerList.size(); i++) {
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
            structs[x][y].traverseAndStore(sLists.get(x * errorMakerList.size() + y), curErrorElem, output);
        }

        for (int i = 0; i < errorMakerList.size(); i++) {
            for (int j = 0; j < semesterNum; j ++) {
                listModel.add(i * errorMakerList.size() + j, errorMakerList.get(i).getName() + "，在第 " + (j + 1) + " 学期的错误统计：");
                //TryToUseTable.listModel.addElement(errorMakerList.get(i).getName() + "，在第 " + (j + 1) + " 学期的错误统计：");
            }
        }
    }

    public void processErrorsByPerson(ArrayList<ArrayList<StorageStruct>> sLists, int length,  List<ErrorMaker> errorMakerList, int semester,
                                      String[] errorTypeCodes, String[] errorTypeNames,DefaultListModel listModel) {
        if (sLists == null) {
            System.out.println("Should NOT Be Null.");
            throw new NullPointerException("Should NOT Be Null.");
        }
        ArrayList<StorageStruct> sList;
        StorageStruct[] structs = new StorageStruct[errorMakerList.size()];
        int oldSize = sLists.size();

        for (int i = 0; i < errorMakerList.size(); i++) {
            sList = new ArrayList<StorageStruct>();
            structs[i] = new StorageStruct('A', "所有的错误", null);
            sList.add(structs[i]);
            storeErrorTypes(errorTypeCodes, sList, errorTypeNames);
            sLists.add(sList);
        }

        StorageStruct curRoot;
        StorageStruct personTotal;
        for (int i = 0; i < errorMakerList.size(); i++) {
            int index = oldSize + i;
            int start = i * errorMakerList.size();
            int end = start + semester;
            personTotal = sLists.get(index).get(0);
            for (int j = start; j < end; j++) {
                sList = sLists.get(j);
                curRoot = sList.get(0);

                recursiveCount(curRoot, personTotal);
            }
            listModel.add(index, errorMakerList.get(i).getName() + "的综合错误统计：");
        }
    }

    /**
     * 根据按人和学期统计（方法 processErrorsByPersonAndSemester ）的错误信息结果，按人统计错误信息
     *
     * @param sLists ArrayList<ArrayList<StorageStruct>>
     *               按人和学期统计的错误信息
     *
     * @param length int
     *               按人和学期统计的错误信息的条目的数量
     *
     * @param errorMakerList List<ErrorMaker>
     *                       错误制造者 List
     *
     * @param semester int
     *                 学期数量
     *
     * @param errorTypeCodes String[]
     *                       错误类型编码数组
     *
     * @param errorTypeNames String[]
     *                       错误类型名称数组
     *
     * @param listModel DefaultListModel
     *                  UI 中 JList 的条目列表
     *
     * */
    public void processErrorsBySemester(ArrayList<ArrayList<StorageStruct>> sLists, int length,  List<ErrorMaker> errorMakerList, int semester,
                                      String[] errorTypeCodes, String[] errorTypeNames,DefaultListModel listModel) {
        if (sLists == null) { // 按人和学期统计的错误信息，正常情况下是不会为空的
            System.out.println("Should NOT Be Null.");
            throw new NullPointerException("Should NOT Be Null.");
        }
        ArrayList<StorageStruct> sList; // 按某种方式统计错误信息时，存储其中的一个条目的 list
        StorageStruct[] structs = new StorageStruct[errorMakerList.size()]; // 有几个条目，就有几个 root 节点
        int oldSize = sLists.size(); // 记录新增节点之前的节点数量

        // 按学期统计的时候，初始化存储结构，包括结构，各节点的字符（每一条完整的路径都代表一类错误），数量（初始为零），节点的父节点
        for (int i = 0; i < semester; i++) {
            sList = new ArrayList<StorageStruct>();
            structs[i] = new StorageStruct('A', "所有的错误", null); // 初始化 root 节点
            sList.add(structs[i]); // root 节点加入到存储结构
            storeErrorTypes(errorTypeCodes, sList, errorTypeNames); // 按错误编码数组和错误名称数组，完成存储结构的初始化
            sLists.add(sList); // 将某一个存储结构加入存储结构列表
        }

        StorageStruct curRoot;
        StorageStruct semeTotal;
        for (int i = 0; i < semester; i++) { // 按学期来统计错误信息
            int index = oldSize + i; // 按学期统计的存储结构在存储结构列表中的 index
            int start = i; // 当前学期的第一个学生的错误存储结构，在存储结构列表的 index
            int end = length; // 遍历的上限位置
            semeTotal = sLists.get(index).get(0); // 当前学期的存储结构的 root 节点
            for (int j = start; j < end; j = j + errorMakerList.size()) { // 每次前进学生个数个位置（存储结构列表是线性存储的）
                sList = sLists.get(j); // 存储某个学期中的某个学生的错误信息的存储结构
                curRoot = sList.get(0); // root 节点

                recursiveCount(curRoot, semeTotal); // 统计每个错误类型的数量
            }
            listModel.add(index, "第 " + (i + 1) + " 学期的综合错误统计："); // 加入条目到 UI 的 List 里
        }
    }

    public void processErrorsForAll(ArrayList<ArrayList<StorageStruct>> sLists, int length,
                                    String[] errorTypeCodes, String[] errorTypeNames,DefaultListModel listModel) {
        if (sLists == null) {
            System.out.println("Should NOT Be Null.");
            throw new NullPointerException("Should NOT Be Null.");
        }
        ArrayList<StorageStruct> sList = new ArrayList<StorageStruct>();;
        StorageStruct curRoot;
        StorageStruct total = new StorageStruct('A', "所有的错误", null);
        sList.add(total);
        storeErrorTypes(errorTypeCodes, sList, errorTypeNames);
        sLists.add(sList);

        for (int i = 0; i < length; i ++) {
            sList = sLists.get(i);
            curRoot = sList.get(0);

            recursiveCount(curRoot, total);
        }

        listModel.add(sLists.size() - 1, "综合错误统计：");
    }

    public void recursiveCount(StorageStruct rootNode, StorageStruct target) {
        if (rootNode == null) {
            return ;
        }
        target.setErrorNum(target.getErrorNum() + rootNode.getErrorNum());

        ArrayList<StorageStruct> sList = rootNode.getSList();
        ArrayList<StorageStruct> tList = target.getSList();

        for (int i = 0; i < sList.size(); i++) {
            recursiveCount(sList.get(i), tList.get(i));
        }
    }

    /**
     * 不分类，统计所有的错误
     *
     */
    public void processErrors(List<Error> errorList, String[] errorTypeCodes, String[] errorTypeNames,
                              ArrayList<ArrayList<StorageStruct>> sLists, DefaultListModel listModel, JTextArea output) {
        ArrayList<StorageStruct> sList = new ArrayList<StorageStruct>();
        StorageStruct ancestor = new StorageStruct('A', "所有的错误", null);

        sList.add(ancestor);
        storeErrorTypes(errorTypeCodes, sList, errorTypeNames);
        sLists.add(sList);
        for (int i = 0; i < errorList.size(); i++) {
            ancestor.traverseAndStore(sList, errorList.get(i), output);
        }

        listModel.add(sLists.size() - 1, "综合错误统计：");
        //TryToUseTable.listModel.addElement("综合错误统计：");
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
    public void traverse(ArrayList<StorageStruct> sList, String rootErrorCode, int preLevelTotalErrorsNum, DefaultTableModel dtm) {
        if (sList == null || sList.isEmpty()) {
            return ;
        }
        for (int i = 0; i < sList.size(); i++) {
            StorageStruct curNode = sList.get(i);
            rootErrorCode = rootErrorCode.concat(curNode.getCharacter().toString());
            int curLevelTotalErrorsNum = curNode.getErrorNum();
            double perc = percentage(curLevelTotalErrorsNum, preLevelTotalErrorsNum);
            String[] tableVal = {rootErrorCode, curNode.getErrorName(), curNode.getErrorNum() + "", doubleToPercentage(perc)};

            dtm.addRow(tableVal);
            traverse(curNode.getSList(), rootErrorCode, curLevelTotalErrorsNum, dtm);
            rootErrorCode = rootErrorCode.substring(0, rootErrorCode.length() - 1);
        }
    }

    public String doubleToPercentage(double dNum) {
        dNum = Double.parseDouble(String.format("%.4f", dNum));
        String perc = (dNum * 100) + "";
        String[] twoP = perc.split("\\.");
        StringBuilder sb = new StringBuilder();

        sb.append(twoP[0]).append(".");

        if (twoP.length == 2) {
            if (twoP[1].length() > 2) {
                sb.append(twoP[1].substring(0, 2));
            } else {
                sb.append(twoP[1]);
            }
        } else {
            sb.append("0");
        }
        sb.append("%");
        return sb.toString();
    }

    /**
     * 将错误类型信息，包括错误类型码，错误类型名，存入到存储结构中
     *
     * @param errorTypeCodes String[]
     *                       错误类型编码数组
     *
     * @param sList ArrayList<StorageStruct>
     *              错误存储结构
     *
     * @param errorTypeNames String[]
     *                       错误类型名称数组
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
            String[] names = errorTypeNames[i].split(TryToUseTable.INPUT_ERROR_LINE_SEPARATOR);
            String type = errorTypeCodes[i];
            int posOfSept = type.indexOf(TryToUseTable.ERROR_SEPARATOR);
            String pos = type.substring(0, posOfSept);
            String errors = type.substring(posOfSept + 1);

            addErrorTypeElements(errors, findPosition(pos, sList), names);
        }
    }

    /**
     * 新建并将错误种类信息，存入存储结构中
     *
     * @param errorTypeCodes String
     *                       错误类型编码
     *
     * @param ss StorageStruct
     *           被存储的目的节点
     *
     * @param errorTypeNames String[]
     *                       错误类型名称
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

    /**
     * 找到当前处理的错误 type 应该存储的位置，哪个 StorageStruct List 的哪个 StorageStruct 节点
     *
     * @param positionChars String
     *                      位置码，指定了父类型的路径
     *
     * @param ssList ArrayList<StorageStruct>
     *               父类型的存储结构
     *
     * */
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
        }
        return curNode;
    }
}
