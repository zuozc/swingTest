package com.zzc.model;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by zuozc on 3/18/16.
 */
public class StorageStruct {

    public static final char ROOT = 'A';
    Character character;
    int errorNum;
    String errorName;
    ArrayList<StorageStruct> sList;
    StorageStruct parent;

    public StorageStruct() {}

    public StorageStruct(char c, String errorName) {
        this.character = c;
        this.errorNum = 0;
        this.errorName = errorName;
        this.sList = new ArrayList<StorageStruct>();
    }

    public StorageStruct(char c, String errorName, StorageStruct parent) {
        this.character = c;
        this.errorNum = 0;
        this.errorName = errorName;
        this.sList = new ArrayList<StorageStruct>();
        this.parent = parent;
    }

    public Character getCharacter() {
        return this.character;
    }

    public int getErrorNum() {
        return this.errorNum;
    }

    public String getErrorName() {
        return this.errorName;
    }

    public void setErrorNum(int errorNum) {
        this.errorNum = errorNum;
    }

    public ArrayList<StorageStruct> getSList() {
        return this.sList;
    }
    /**
     * 向存储结构中新增存储节点
     *
     * */
    public void addElement(char c, String errorName) {
        StorageStruct ss = new StorageStruct(c, errorName);
        ss.parent = this;
        if (this.sList == null) {
            this.sList = new ArrayList<StorageStruct>();
        }
        this.sList.add(ss);
    }

    /**
     * 遍历存储结构，并存储信息，包括当前存储节点的错误类型编码，错误数量，错误类型名称，下一层错误错处结构
     *
     * */
    public void traverseAndStore(ArrayList<StorageStruct> ssList, Error error, JTextArea output) {
        if (ssList == null || ssList.isEmpty()) {
            System.out.println("StorageStruct List Is Empty.");
            return ;
        }

        StorageStruct rootNode = ssList.get(0);

        String errorCode = error.getErrorCode();
        if (errorCode == null || errorCode.isEmpty()) {
            System.out.println("Error ID Is Empty.");
            return ;
        }

        ssList = matchAndCount(ssList, error, 0, output, rootNode);
        if (ssList != null) {
            int i = 1;
            for (; i <= errorCode.length(); i++) {
                ssList = matchAndCount(ssList, error, i, output, rootNode);
            }
        }
    }

    /**
     * 根据错误类型编码和存储结构，匹配错误编码，并计每类错误的数量，最后返回下一层的错误存储结构，用于下一层的错误匹配和计数
     *
     * 输入：ArrayList<StorageStruct> ssList 错误存储结构，char c 当前层的错误编码
     *
     * return: ArrayList<StorageStruct> 下一层的错误存储结构
     *
     * */
    public ArrayList<StorageStruct> matchAndCount(ArrayList<StorageStruct> ssList, Error error, int pos,
                                                  JTextArea output, StorageStruct rootNode) {
        String errorCode = error.getErrorCode();
        if (pos == errorCode.length()) {
            if (ssList != null && ! ssList.isEmpty()) {
                System.out.println("第 " + error.getLineInFile() + " 行中的错误码 " + errorCode + " 并没能匹配到最底层的子错误类型, 请检查...");
                output.append("第 " + error.getLineInFile() + " 行中的错误码 " + errorCode + " 并没能匹配到最底层的子错误类型, 请检查...\n");
                clearWrongCountFromEndToRoot(ssList.get(0).parent);
                return null;
            } else {
                return null;
            }
        }

        // 匹配结束
        if (ssList == null) {
            return null;
        }

        if (pos < errorCode.length() && ssList.isEmpty()) {
            System.out.println("第 " + error.getLineInFile() + " 行中的错误码 " + errorCode + " ，在当前的错误类型集中，不存在 " + ", 请检查...");
            output.append("第 " + error.getLineInFile() + " 行中的错误码 " + errorCode + " ，在当前的错误类型集中，不存在 " + ", 请检查...\n");
            clearWrongCountFromRootToEnd(rootNode, errorCode);
            return null;
        }

        // 匹配并计数
        int i = 0;
        StorageStruct ss = null;
        for (; i < ssList.size(); i++) {
            ss = ssList.get(i);
            if (ss.character.charValue() == errorCode.charAt(pos)) {
                ss.errorNum ++; // 错误数加一
                return ss.sList; // 返回下一层，用于下一层的匹配
            }
        }
        if (i == ssList.size()) {
            System.out.println("第 " + error.getLineInFile() + " 行中的错误码 " + errorCode + " ，在当前的错误类型集中，不存在 " + ", 请检查...");
            output.append("第 " + error.getLineInFile() + " 行中的错误码 " + errorCode + " ，在当前的错误类型集中，不存在 " + ", 请检查...\n");
            clearWrongCountFromEndToRoot(ss.parent);
        }

        return null;
    }


    public void clearWrongCountFromEndToRoot(StorageStruct node) {
        if (node == null) {
            return ;
        }
        node.errorNum --;
        clearWrongCountFromEndToRoot(node.parent);
    }

    public void clearWrongCountFromRootToEnd(StorageStruct root, String errorCode) {
        if (root == null) {
            System.out.println("Root Node Should NOT Be Null.");
            throw new NullPointerException("Root Node Should NOT Be Null.");
        }

        root.errorNum --;
        ArrayList<StorageStruct> sList = root.getSList();

        for (int i = 1; i < errorCode.length() && ! sList.isEmpty(); i++) {
            for (int j = 0; j < sList.size(); j ++) {
                StorageStruct curNode = sList.get(j);
                if (errorCode.charAt(i) == curNode.character) {
                    curNode.errorNum --;
                    sList = curNode.getSList();
                    break;
                }
            }
        }
    }

}
