package com.zzc.dev;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.List;
import java.util.ArrayList;

import com.zzc.UI.MyFileFilter;
import com.zzc.model.ErrorMaker;
import com.zzc.model.Error;
import com.zzc.tool.UIBackend;
import com.zzc.log.LogWindow;
import com.zzc.log.LogAppenderFactory;

/**
 * Created by zuozc on 3/18/16.
 */
public class LogAnalyzer extends Component {

    public static final String INPUT_ERROR_LINE_SEPARATOR = ";";
    public static final String ERROR_SEPARATOR = "-";
    private static String ERROR_LINE_PATTERN = "([A-Z]{1,5}" + ERROR_SEPARATOR + "[1-5]" + ERROR_SEPARATOR
            + "[1-5];)*[A-Z]{1,5}" + ERROR_SEPARATOR + "[1-5]" + ERROR_SEPARATOR + "[1-5]";
    public static String INVALID_CHAR_PATTERN = "[^A-Z1-5-;/"+ ERROR_SEPARATOR +"]";
    public static String REPEAT_ERROR_SEPARATOR = ERROR_SEPARATOR + "{2,}";
    public static String REPEAT_INPUT_ERROR_LINE_SEPARATOR = INPUT_ERROR_LINE_SEPARATOR + "{2,}";
    public static String POSTFIX_INPUT_ERROR_LINE_SEPARATOR = INPUT_ERROR_LINE_SEPARATOR + "$";

    private JPanel mainPane = new JPanel();
    private JFileChooser chooser=new JFileChooser();
    //private final LogWindow logWindow = LogAppenderFactory.getLogWindow();
    private JButton open = new JButton("选择");
    private JLabel label = new JLabel("选择错误信息文件哈：");
    private JTextArea total = new JTextArea();
    private JTextArea clascified = new JTextArea();
    private JTable totalT;

    private File chosenDir;
    private File errorFile;

    private static String[] errorTypeCodes = {"A-ABC", "AA-AB", "AB-ABCDE", "AC-ABC", "AAA-ABC", "AAB-ABCD", "ABA-ABCD", "ACC-ABCDE"};
    private static String[] errorTypeNames = {"语法;单句结构常见错误;复句运用错误",
            "实词;虚词",
            "搭配不当;成份残缺;成份多余;语序不当;句式杂糅",
            "分句间联系不密切;结构层次混乱;关联词语使用错误",
            "名、动、形容词的误用;数、量词使用不当;代词使用不当",
            "副词误用;介词误用;连词误用;助词误用",
            "主谓搭配不当;动宾搭配不当;定状补和中心语不搭配;主宾不搭配",
            "关联词语搭配不当;缺少关联词语;错用关联词语;滥用关联词语;关联词语位置不对"
    };
    private static String[] errorMakers = {"刘芳", "叶茂", "肖焕", "杨连伟", "张子同"};
    private static int semesterNum = 5;
    private static String[] errorTitle = {"错误编码", "错误名", "数量", "百分比"};

    public static DefaultTableModel dtm = new DefaultTableModel(null, errorTitle);


    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowUI();
            }
        });
    }

    private static void createAndShowUI() {
        JFrame frame = new JFrame("错误信息分析器");
        LogAnalyzer logAnalyzer = new LogAnalyzer();
        logAnalyzer.mainPane.setLayout(null);

        Container contentPane = frame.getContentPane();
        frame.setLayout(null);
        contentPane.add(logAnalyzer.mainPane);

        logAnalyzer.label.setBounds(10, 10, 150, 20);
        logAnalyzer.open.setBounds(180, 10, 100, 20);
        //logAnalyzer.total.setBounds(10, 50, 740, 400);

        JLabel totalLabel = new JLabel("综合统计：");
        totalLabel.setBounds(10, 40, 100, 20);
        JScrollPane tScrollPane = new JScrollPane(logAnalyzer.total);
        //logAnalyzer.totalT = new JTable(dtm);
        //JScrollPane tScrollPane = new JScrollPane(logAnalyzer.totalT);
        tScrollPane.setBounds(5, 60, 450, 550);

        JLabel classifiedLabel = new JLabel("分类错误统计（按人和学期）：");
        classifiedLabel.setBounds(510, 40, 250, 20);
        JScrollPane cScrollPane = new JScrollPane(logAnalyzer.clascified);
        cScrollPane.setBounds(505, 60, 450, 550);

        logAnalyzer.mainPane.add(logAnalyzer.label);
        logAnalyzer.mainPane.add(logAnalyzer.open);
        //logAnalyzer.mainPane.add(Box.createVerticalStrut(50));
        logAnalyzer.mainPane.add(totalLabel);
        logAnalyzer.mainPane.add(tScrollPane);
        logAnalyzer.mainPane.add(classifiedLabel);
        logAnalyzer.mainPane.add(cScrollPane);

        //frame.setContentPane(contentPane);
        frame.setBounds(0,0,1000,700);
        logAnalyzer.mainPane.setBounds(20,0,1000,680);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setAlwaysOnTop(false);
    }

    public LogAnalyzer() {

        open.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    choseDir();
                } catch (Exception ex) {
                    System.out.print(ex.getStackTrace());
                    showErrorPane(ex.getMessage(), ex.getMessage());
                }
            }
        });
    }

    void choseDir() {
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        //chooser.addChoosableFileFilter(new MyFileFilter("txt"));
        chooser.resetChoosableFileFilters();
        chooser.setFileFilter(new MyFileFilter("txt"));
        if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
            chosenDir = chooser.getSelectedFile();
            System.out.println("You have chosen " + chosenDir);
            errorFile = chooser.getSelectedFile();
            processFile(chosenDir);
        }
    }

    void processFile(File dir) {
        if (dir.isFile()) {
            UIBackend uiBackend = new UIBackend();
            List<ErrorMaker> errorMakerList = uiBackend.generateErrorMakerList(errorMakers);
            //String[] errorStrs = {"AAAA-2-1", "AABA-2-1", "ACB-2-3", "ABC-2-2", "ABC-2-1", "ABC-2-2",
            //        "ACB-2-1", "ACA-2-2", "AABC-2-3", "ABAD-2-4", "ABAC-2-5"};

            List<String> errors = new ArrayList<String>();;
            InputStreamReader isr;
            BufferedReader br;
            try {
                isr = new InputStreamReader(new FileInputStream(errorFile));
                br = new BufferedReader(isr);

                String line;

                while ((line = br.readLine()) != null) {
                    String[] errorStrs = line.replaceAll(INVALID_CHAR_PATTERN, "")
                            .replaceAll(REPEAT_INPUT_ERROR_LINE_SEPARATOR, INPUT_ERROR_LINE_SEPARATOR)
                            .replaceAll(REPEAT_ERROR_SEPARATOR, ERROR_SEPARATOR)
                            .replaceAll(POSTFIX_INPUT_ERROR_LINE_SEPARATOR, "")
                            .split(INPUT_ERROR_LINE_SEPARATOR);

                    for (int i = 0; i < errorStrs.length; i ++) {
                        errors.add(errorStrs[i]);
                    }
                }
                br.close();
                isr.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // make error list from error and error maker data
            List<Error> errorList = new ArrayList<Error>();
            for (int i = 0; i < errors.size(); i++) {
                String[] errorStr = errors.get(i).split(ERROR_SEPARATOR);
                String errorCode = errorStr[0];
                int errorMakerId = Integer.parseInt(errorStr[1]);
                int errorSemester = Integer.parseInt(errorStr[2]);

                Error error = new Error(errorCode, errorMakerList.get(errorMakerId - 1), errorSemester);
                errorList.add(error);
            }

            try {
                total.setText("");
                clascified.setText("");
                uiBackend.processErrors(errorList, errorTypeCodes, errorTypeNames, total);
                uiBackend.processErrorsByPersonAndSemester(errorList, errorMakerList, semesterNum, errorTypeCodes, errorTypeNames, clascified);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("not a file");
        }
    }

    void showErrorPane(String title, String msg) {
        LogWindow logWindow = LogAppenderFactory.getLogWindow();
        logWindow.log(msg);
        JOptionPane pane = new JOptionPane(msg, JOptionPane.ERROR_MESSAGE);
        JDialog dialog = pane.createDialog("Application says: " + title);
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
    }
}
