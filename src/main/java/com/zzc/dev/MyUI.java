package com.zzc.dev;

/**
 * Created by zuozc on 3/18/16.
 */
import com.zzc.model.*;
import com.zzc.model.Error;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class MyUI {

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

    static JPanel topPanel;
    static JPanel bottomPanel;
    static JPanel middlePanel;

    static void createTopPanel() {
        topPanel = new JPanel();
        String[] columnName = { "姓名", "性别", "单位", "参加项目", "备注" };
        String[][] rowData = { { "张三", "男", "计算机系", "100 米 ,200 米", "" },
                { "李四", "男", "化学系", "100 米，铅球", "" },
        };
        JTable table = new JTable(new DefaultTableModel(rowData, columnName));
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(scrollPane);
        topPanel.add(Box.createVerticalStrut(10));
    }

    static void createMiddlePanel() {
        middlePanel = new JPanel();
        middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.X_AXIS));

        JLabel sourceLabel = new JLabel("学生");
        sourceLabel.setAlignmentY(Component.TOP_ALIGNMENT);
        sourceLabel.setBorder(BorderFactory.createEmptyBorder(4, 5, 0, 5));
        DefaultListModel listModel = new DefaultListModel();
        listModel.addElement("100 米");
        listModel.addElement("200 米");
        listModel.addElement("400 米");
        listModel.addElement("跳远");
        listModel.addElement("跳高");
        listModel.addElement("铅球");
        JList sourceList = new JList(listModel);
        sourceList
                .setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        sourceList.setVisibleRowCount(5);
        JScrollPane sourceListScroller = new JScrollPane(sourceList);
        sourceListScroller.setPreferredSize(new Dimension(120, 80));
        sourceListScroller
                .setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        sourceListScroller.setAlignmentY(Component.TOP_ALIGNMENT);
        JPanel sourceListPanel = new JPanel();
        sourceListPanel.setLayout(new BoxLayout(sourceListPanel,
                BoxLayout.X_AXIS));
        sourceListPanel.add(sourceLabel);
        sourceListPanel.add(sourceListScroller);
        sourceListPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        sourceListPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 30));
        middlePanel.add(sourceListPanel);

        JButton toTargetButton = new JButton(">>");
        JButton toSourceButton = new JButton("<<");
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.add(toTargetButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(15, 15)));
        buttonPanel.add(toSourceButton);
        buttonPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 5, 15, 5));
        middlePanel.add(buttonPanel);

        JLabel targetLabel = new JLabel("查询项目：");
        targetLabel.setAlignmentY(Component.TOP_ALIGNMENT);
        targetLabel.setBorder(BorderFactory.createEmptyBorder(4, 5, 0, 5));
        DefaultListModel targetListModel = new DefaultListModel();
        targetListModel.addElement("100米");
        JList targetList = new JList(targetListModel);
        targetList
                .setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        targetList.setVisibleRowCount(5);
        JScrollPane targetListScroller = new JScrollPane(targetList);
        targetListScroller.setPreferredSize(new Dimension(120, 80));
        targetListScroller
                .setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        targetListScroller.setAlignmentY(Component.TOP_ALIGNMENT);
        JPanel targetListPanel = new JPanel();
        targetListPanel.setLayout(new BoxLayout(targetListPanel,
                BoxLayout.X_AXIS));
        targetListPanel.add(targetLabel);
        targetListPanel.add(targetListScroller);
        targetListPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        targetListPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));
        middlePanel.add(targetListPanel);
    }

    static void createBottomPanel() {
        JButton actionButton = new JButton("查询");
        JButton closeButton = new JButton("退出");
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(actionButton);
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(closeButton);
        bottomPanel.add(Box.createVerticalStrut(10));
        bottomPanel.add(buttonPanel);
        bottomPanel.add(Box.createVerticalStrut(10));
    }

    public static void main(String[] args) {
        createTopPanel();
        createMiddlePanel();
        createBottomPanel();

        JPanel panelContainer = new JPanel();
        panelContainer.setLayout(new GridBagLayout());

        GridBagConstraints c1 = new GridBagConstraints();
        c1.gridx = 0;
        c1.gridy = 0;
        c1.weightx = 1.0;
        c1.weighty = 1.0;

        c1.fill = GridBagConstraints.BOTH;
        panelContainer.add(topPanel, c1);

        GridBagConstraints c2 = new GridBagConstraints();
        c2.gridx = 0;
        c2.gridy = 1;
        c2.weightx = 1.0;
        c2.weighty = 0;
        c2.fill = GridBagConstraints.HORIZONTAL;
        panelContainer.add(middlePanel, c2);

        GridBagConstraints c3 = new GridBagConstraints();
        c3.gridx = 0;
        c3.gridy = 2;
        c3.weightx = 1.0;
        c3.weighty = 0;
        c3.fill = GridBagConstraints.HORIZONTAL;
        panelContainer.add(bottomPanel, c3);

        JFrame frame = new JFrame("Boxlayout 演示");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panelContainer.setOpaque(true);
        frame.setSize(new Dimension(480, 320));
        frame.setContentPane(panelContainer);
        frame.setVisible(true);
    }
}

