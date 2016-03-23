package com.zzc.UI;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;
import java.util.Iterator;

import com.zzc.model.ErrorMaker;
import com.zzc.model.Error;
import com.zzc.model.StorageStruct;
import com.zzc.tool.TableTool;

/**
 * Created by zuozc on 3/18/16.
 */
public class TryToUseTable extends Component {

    private static final String encode = "UTF-8";
    private static String errorMsgHint = "下面的输出可能需要你处理下哦~";
    private static String errorMsgSummary = "注意哦~ 本次统计将不包含上述行的错误~";
    private static final String configPath = "/config.properties";

    private JPanel mainPane = new JPanel();
    private JFileChooser chooser=new JFileChooser();
    private JButton open = new JButton("选择");
    private JLabel label = new JLabel("选择错误信息文件哈：");
    private static JTextArea output = new JTextArea();

    private static File chosenDir;
    private static File errorFile;

    private static String[] errorTypeCodes = {"A-ABC", "AB-ABCDE", "AA-AB", "AC-ABC", "AAA-ABC", "AAB-ABCD", "ABA-ABCD", "ACC-ABCDE"};
    private static String[] errorTypeNames = {"语法;单句结构常见错误;复句运用错误",
            "搭配不当;成份残缺;成份多余;语序不当;句式杂糅",
            "实词;虚词",
            "分句间联系不密切;结构层次混乱;关联词语使用错误",
            "名、动、形容词的误用;数、量词使用不当;代词使用不当",
            "副词误用;介词误用;连词误用;助词误用",
            "主谓搭配不当;动宾搭配不当;定状补和中心语不搭配;主宾不搭配",
            "关联词语搭配不当;缺少关联词语;错用关联词语;滥用关联词语;关联词语位置不对"
    };
    private static String[] errorMakers = {"刘芳", "叶茂", "肖焕", "杨连伟", "张子同"};
    private static int semesterNum = 5;
    private static String[] errorTitle = {"错误编码", "错误名", "数量", "百分比"};

    public static final String COMMA = ",";
    public static final String INPUT_ERROR_LINE_SEPARATOR = ";";
    public static final String ERROR_SEPARATOR = "-";
    public static String ERROR_LINE_PATTERN = "([A-Z]{1,}" + ERROR_SEPARATOR + "[1-" + errorMakers.length + "]" + ERROR_SEPARATOR
            + "[1-" + semesterNum + "];)*[A-Z]{1,}" + ERROR_SEPARATOR + "[1-" + errorMakers.length + "]"
            + ERROR_SEPARATOR + "[1-" + semesterNum + "]";
    public static String INVALID_CHAR_PATTERN = "[^A-Z1-" + ((errorMakers.length > semesterNum) ? errorMakers.length : semesterNum) + "/-;/"+ ERROR_SEPARATOR +"]";
    public static String REPEAT_ERROR_SEPARATOR = ERROR_SEPARATOR + "{2,}";
    public static String REPEAT_INPUT_ERROR_LINE_SEPARATOR = INPUT_ERROR_LINE_SEPARATOR + "{2,}";
    public static String POSTFIX_INPUT_ERROR_LINE_SEPARATOR = INPUT_ERROR_LINE_SEPARATOR + "$";

    private static DefaultTableModel dtm = new DefaultTableModel(null, errorTitle);
    private static DefaultListModel listModel = new DefaultListModel();
    private static JList sourceList = new JList(listModel);
    private static JTable totalT = new JTable(dtm);
    private static JScrollPane tScrollPane;

    private static List<ArrayList<StorageStruct>> sLists;
    private static TableTool uiBackend = new TableTool();
    private static TryToUseTable tryToUseTable = new TryToUseTable();
    private static List<ErrorMaker> errorMakerList;

    public static void main(String[] args) {
        System.out.println(ERROR_LINE_PATTERN);
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowUI();
            }
        });
    }

    private void loadConfig(String configPath) {
        Properties props = new Properties();
        InputStream in = this.getClass().getResourceAsStream(configPath);
        if (in == null) {
            System.out.println("failed to load property file. Use default configurations.");
        }
        else {
            InputStreamReader isr;
            try {
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
                errorMakers = props.getProperty("students").split(COMMA);
                errorTypeCodes = props.getProperty("errorTypeCodes").split(COMMA);
                errorTypeNames = props.getProperty("errorTypeNames").split(COMMA);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException ne) {
                ne.printStackTrace();
            }
        }
    }

    private static void createAndShowUI() {
        JFrame frame = new JFrame("错误信息分析器");

        tryToUseTable.mainPane.setLayout(null);

        Container contentPane = frame.getContentPane();
        frame.setLayout(null);
        contentPane.add(tryToUseTable.mainPane);

        tryToUseTable.label.setBounds(10, 10, 150, 20);
        tryToUseTable.open.setBounds(180, 10, 100, 20);

        JLabel totalLabel = new JLabel("统计结果选项：");
        totalLabel.setBounds(10, 40, 100, 20);
        sourceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tScrollPane = new JScrollPane(sourceList);
        tScrollPane.setBounds(5, 60, 300, 450);

        JLabel classifiedLabel = new JLabel("统计结果展示：");
        classifiedLabel.setBounds(360, 40, 250, 20);
        JScrollPane cScrollPane = new JScrollPane(totalT);
        cScrollPane.setBounds(355, 60, 600, 450);

        JLabel errorMsgLabel = new JLabel("提示信息：");
        errorMsgLabel.setBounds(10, 520, 100, 20);
        JScrollPane errorMsgScrolPane = new JScrollPane(output);
        errorMsgScrolPane.setBounds(5, 540, 950, 110);

        tryToUseTable.mainPane.add(tryToUseTable.label);
        tryToUseTable.mainPane.add(tryToUseTable.open);
        tryToUseTable.mainPane.add(totalLabel);
        tryToUseTable.mainPane.add(tScrollPane); // 在程序加载 frame 的时候，添加 统计结果选择 list 框
        tryToUseTable.mainPane.add(classifiedLabel);
        tryToUseTable.mainPane.add(cScrollPane);
        tryToUseTable.mainPane.add(errorMsgLabel);
        tryToUseTable.mainPane.add(errorMsgScrolPane);

        frame.setBounds(0,0,1000,700);
        tryToUseTable.mainPane.setBounds(20, 0, 1000, 680);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setAlwaysOnTop(false);

    }

    public TryToUseTable() {
        loadConfig(configPath);
        errorMakerList = uiBackend.generateErrorMakerList(errorMakers);

        open.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    clearPreResult(); // 删掉或者清空上次遗留下来的 component
                    listModel = new DefaultListModel(); // 新建一个 list model，来存放最新的条目
                    sourceList = new JList(listModel);
                    sourceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    tScrollPane = new JScrollPane(sourceList);
                    tScrollPane.setBounds(5, 60, 300, 450);
                    tryToUseTable.mainPane.add(tScrollPane);

                    sourceList.addListSelectionListener(new ListSelectionListener() {
                        public void valueChanged(ListSelectionEvent e) {
                            dtm.getDataVector().clear(); // 清空上次的展示信息
                            int index = sourceList.getSelectedIndex(); // 获取本次选中的条目编号，条目编号是跟存储的错误统计信息一一对应的
                            //System.out.println("index: " + index);
                            // 根据选择的条目，加载错误统计信息
                            uiBackend.traverse(sLists.get(index), "", sLists.get(index).get(0).getErrorNum(), dtm);
                        }
                    });
                    choseDir(listModel); // 处理选中的包含错误信息的文件
                } catch (Exception ex) {
                    ex.printStackTrace();
                    //showErrorPane(ex.getMessage(), ex.getMessage());
                }
            }
        });
    }

    void clearPreResult() {
        dtm.getDataVector().clear(); // 清空上次展示的内容
        tryToUseTable.mainPane.remove(tScrollPane); // 不删的话，会影响后面新的加载
        output.setText("");
    }

    void choseDir(DefaultListModel listModel) {
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.resetChoosableFileFilters();
        chooser.setFileFilter(new MyFileFilter("txt"));
        if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
            chosenDir = chooser.getSelectedFile();
            //System.out.println("You have chosen " + chosenDir);
            errorFile = chooser.getSelectedFile();

            processFile(chosenDir, listModel);
        }
    }

    void processFile(File dir, DefaultListModel listModel) {
        if (dir.isFile()) {
            if (output.getText().length() == 0) {
                output.append(errorMsgHint + "\n");
            }
            output.append("\n在本次所选择的文件( " + chosenDir + " )里，\n");

            InputStreamReader isr;
            BufferedReader br;
            // make error list from error and error maker data
            List<Error> errorList = new ArrayList<Error>();

            try {
                isr = new InputStreamReader(new FileInputStream(errorFile));
                br = new BufferedReader(isr);

                String line;
                int lineNO = 1;
                while ((line = br.readLine()) != null) {
                    line = line.toUpperCase().replaceAll(INVALID_CHAR_PATTERN, "")
                            .replaceAll(REPEAT_INPUT_ERROR_LINE_SEPARATOR, INPUT_ERROR_LINE_SEPARATOR)
                            .replaceAll(REPEAT_ERROR_SEPARATOR, ERROR_SEPARATOR)
                            .replaceAll(POSTFIX_INPUT_ERROR_LINE_SEPARATOR, "");

                    if (line.matches(ERROR_LINE_PATTERN)) {
                        String[] errorStrs = line.split(INPUT_ERROR_LINE_SEPARATOR);

                        for (int i = 0; i < errorStrs.length; i++) {
                            String[] errorStr = errorStrs[i].split(ERROR_SEPARATOR);
                            String errorCode = errorStr[0];
                            int errorMakerId = Integer.parseInt(errorStr[1]);
                            int errorSemester = Integer.parseInt(errorStr[2]);

                            Error error = new Error(errorCode, errorMakerList.get(errorMakerId - 1), errorSemester, lineNO);
                            errorList.add(error);
                        }

                    } else {
                        String errorMsgLine = "第 " + lineNO + " 行（" + line + "）包含不符合规范的错误编码.. ";
                        System.out.println(errorMsgLine);
                        output.append(errorMsgLine + "\n");
                    }
                    lineNO ++;
                }

                br.close();
                isr.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                sLists = uiBackend.initiate(errorList, errorMakerList, semesterNum, errorTypeCodes, errorTypeNames, listModel, output);

                if (output.getLineCount() != 0) {
                    output.append("\n" + errorMsgSummary + "\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("not a file");
        }
    }
}
