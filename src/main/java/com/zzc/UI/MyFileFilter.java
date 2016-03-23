package com.zzc.UI;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class MyFileFilter extends FileFilter {
    String ext;

    public MyFileFilter(String ext) {
        this.ext = ext;
    }

    /* 在accept()方法中,当程序所抓到的是一个目录而不是文件时,我们返回true值,表示将此目录显示出来. */
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        }
        String fileName = file.getName();
        int index = fileName.lastIndexOf('.');
        if (index > 0 && index < fileName.length() - 1) {
            // 表示文件名称不为".xxx"现"xxx."之类型
            String extension = fileName.substring(index + 1).toLowerCase();
            // 若所抓到的文件扩展名等于我们所设置要显示的扩展名(即变量ext值),则返回true,表示将此文件显示出来,否则返回
            // true.
            if (extension.equals(ext))
                return true;
        }
        return false;
    }

    // 实现getDescription()方法,返回描述文件的说明字符串!!!
    public String getDescription() {
        if (ext.toLowerCase().equals("txt"))
            return "文本文件(*.txt)";
        return "";
    }
}
