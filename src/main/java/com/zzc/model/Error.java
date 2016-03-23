package com.zzc.model;

/**
 * Created by zuozc on 3/20/16.
 */
public class Error {
    private String errorCode;
    private ErrorMaker errorMaker;
    private int semester;
    private int lineInFile;

    public Error() {}

    public Error(String errorCode, ErrorMaker errorMaker, int semester) {
        this.errorCode = errorCode;
        this.errorMaker = errorMaker;
        this.semester = semester;
    }

    public Error(String errorCode, ErrorMaker errorMaker, int semester, int lineInFile) {
        this.errorCode = errorCode;
        this.errorMaker = errorMaker;
        this.semester = semester;
        this.lineInFile = lineInFile;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public ErrorMaker getErrorMaker() {
        return this.errorMaker;
    }

    public int getSemester() {
        return this.semester;
    }

    public int getLineInFile() {
        return this.lineInFile;
    }
}
