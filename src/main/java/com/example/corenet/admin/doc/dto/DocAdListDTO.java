package com.example.corenet.admin.doc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DocAdListDTO {

    @JsonProperty("docId")
    private Integer docId;

    @JsonProperty("docNo")
    private String docNo;

    @JsonProperty("detailTitle")
    private String detailTitle;

    @JsonProperty("writer")
    private String writer;

    @JsonProperty("writeDate")
    private String writeDate;

    @JsonProperty("docStatus")
    private String docStatus;

    @JsonProperty("statusColor")
    private String statusColor;

    // 기본 생성자
    public DocAdListDTO() {}

    // Getter / Setter
    public Integer getDocId() { return docId; }
    public void setDocId(Integer docId) { this.docId = docId; }

    public String getDocNo() { return docNo; }
    public void setDocNo(String docNo) { this.docNo = docNo; }

    public String getDetailTitle() { return detailTitle; }
    public void setDetailTitle(String detailTitle) { this.detailTitle = detailTitle; }

    public String getWriter() { return writer; }
    public void setWriter(String writer) { this.writer = writer; }

    public String getWriteDate() { return writeDate; }
    public void setWriteDate(String writeDate) { this.writeDate = writeDate; }

    public String getDocStatus() { return docStatus; }
    public void setDocStatus(String docStatus) { this.docStatus = docStatus; }

    public String getStatusColor() { return statusColor; }
    public void setStatusColor(String statusColor) { this.statusColor = statusColor; }
}