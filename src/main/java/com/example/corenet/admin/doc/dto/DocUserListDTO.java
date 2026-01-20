package com.example.corenet.admin.doc.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocUserListDTO {
    private int docId;
    private String docNo;
    private String detailTitle;
    private String writer;
    private String writeDate;
    private String docType;

    // Getters and Setters
    public int getDocId() {
        return docId;
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }

    public String getDocNo() {
        return docNo;
    }

    public void setDocNo(String docNo) {
        this.docNo = docNo;
    }

    public String getDetailTitle() {
        return detailTitle;
    }

    public void setDetailTitle(String detailTitle) {
        this.detailTitle = detailTitle;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getWriteDate() {
        return writeDate;
    }

    public void setWriteDate(String writeDate) {
        this.writeDate = writeDate;
    }
}