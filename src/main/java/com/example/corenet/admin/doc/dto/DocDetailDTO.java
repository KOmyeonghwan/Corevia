package com.example.corenet.admin.doc.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocDetailDTO {

    private String docName;

    // 기본 문서 정보
    private Integer docId = 0;
    private String docNo = "N/A";
    private Integer pageNo = 1;
    private Integer pageTotal = 1;
    private String writer = "알 수 없음";
    private Integer deptCode = 0;
    private LocalDate writeDate = LocalDate.of(2020, 1, 1); // LocalDate로 변경 (시간 없음)

    // 기안자 정보
    private String drafterEmpNo = "";
    private String drafterDept = "";
    private String drafterPosition = "";
    private String drafterName = "";
    private LocalDate draftDate = LocalDate.of(0000, 1, 1);

    // 상세 내용
    private String detailTitle = "";
    private String detailContent = "";
    private String detailNote = "";

    // 문서 상태 및 색상
    private String docStatus = "draft";
    private String statusColor = "black";

    // 추가된 파일 정보
    private List<DocFileDTO> files = new ArrayList<>();

    // 결재자 정보
    private List<ApproverDTO> approvers;

    // 기본 생성자
    public DocDetailDTO() {
    }

    // 모든 필드를 포함한 생성자
    // 모든 필드를 포함한 생성자
    public DocDetailDTO(String docName, Integer docId, String docNo, Integer pageNo, Integer pageTotal,
            String writer, Integer deptCode, LocalDate writeDate, String drafterEmpNo, String drafterDept,
            String drafterPosition, String drafterName, LocalDate draftDate, String detailTitle,
            String detailContent, String detailNote, String docStatus, String statusColor,
            List<DocFileDTO> files, List<ApproverDTO> approvers) {

        this.docName = docName;
        this.docId = docId;
        this.docNo = docNo;
        this.pageNo = pageNo;
        this.pageTotal = pageTotal;
        this.writer = writer;
        this.deptCode = deptCode;
        this.writeDate = writeDate;
        this.drafterEmpNo = drafterEmpNo;
        this.drafterDept = drafterDept;
        this.drafterPosition = drafterPosition;
        this.drafterName = drafterName;
        this.draftDate = draftDate;
        this.detailTitle = detailTitle;
        this.detailContent = detailContent;
        this.detailNote = detailNote;
        this.docStatus = docStatus;
        this.statusColor = statusColor;

        // 멀티파일 필드 초기화
        this.files = (files != null) ? files : new ArrayList<>();

        // 결재자 리스트 초기화
        this.approvers = (approvers != null) ? approvers : new ArrayList<>();
    }

    // Getter / Setter methods
    public List<ApproverDTO> getApprovers() {
        return approvers;
    }

    public void setApprovers(List<ApproverDTO> approvers) {
        this.approvers = approvers;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public Integer getDocId() {
        return docId;
    }

    public void setDocId(Integer docId) {
        this.docId = docId;
    }

    public String getDocNo() {
        return docNo;
    }

    public void setDocNo(String docNo) {
        this.docNo = docNo;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageTotal() {
        return pageTotal;
    }

    public void setPageTotal(Integer pageTotal) {
        this.pageTotal = pageTotal;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public Integer getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(Integer deptCode) {
        this.deptCode = deptCode;
    }

    public LocalDate getWriteDate() {
        return writeDate;
    }

    public void setWriteDate(String writeDateStr) {
        // 'T'를 공백으로 변경하여 날짜 문자열을 변환할 수 있게 만듬
        writeDateStr = writeDateStr.replace("T", " ");

        // 문자열을 LocalDate로 변환 (시간 정보는 제외하고 날짜만 처리)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            this.writeDate = LocalDate.parse(writeDateStr, formatter);
        } catch (DateTimeParseException e) {
            // 변환 실패 시 기본값 설정 (예: 2020-01-01)
            this.writeDate = LocalDate.of(2020, 1, 1);
        }
    }

    public String getDrafterEmpNo() {
        return drafterEmpNo;
    }

    public void setDrafterEmpNo(String drafterEmpNo) {
        this.drafterEmpNo = drafterEmpNo;
    }

    public String getDrafterDept() {
        return drafterDept;
    }

    public void setDrafterDept(String drafterDept) {
        this.drafterDept = drafterDept;
    }

    public String getDrafterPosition() {
        return drafterPosition;
    }

    public void setDrafterPosition(String drafterPosition) {
        this.drafterPosition = drafterPosition;
    }

    public String getDrafterName() {
        return drafterName;
    }

    public void setDrafterName(String drafterName) {
        this.drafterName = drafterName;
    }

    public LocalDate getDraftDate() {
        return draftDate;
    }

    public void setDraftDate(String draftDateStr) {
        // 'T'를 공백으로 변경하여 날짜 문자열을 변환할 수 있게 만듬
        draftDateStr = draftDateStr.replace("T", " ");

        // 문자열을 LocalDate로 변환 (시간 정보는 제외하고 날짜만 처리)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            this.draftDate = LocalDate.parse(draftDateStr, formatter);
        } catch (DateTimeParseException e) {
            // 변환 실패 시 기본값 설정 (예: 2020-01-01)
            this.draftDate = LocalDate.of(2020, 1, 1);
            System.out.println("Invalid date format, using default: " + e.getMessage());
        }
    }

    public String getDetailTitle() {
        return detailTitle;
    }

    public void setDetailTitle(String detailTitle) {
        this.detailTitle = detailTitle;
    }

    public String getDetailContent() {
        return detailContent;
    }

    public void setDetailContent(String detailContent) {
        this.detailContent = detailContent;
    }

    public String getDetailNote() {
        return detailNote;
    }

    public void setDetailNote(String detailNote) {
        this.detailNote = detailNote;
    }

    public String getDocStatus() {
        return docStatus;
    }

    public void setDocStatus(String docStatus) {
        this.docStatus = docStatus;
    }

    public String getStatusColor() {
        return statusColor;
    }

    public void setStatusColor(String statusColor) {
        this.statusColor = statusColor;
    }

    // File information getters and setters
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        StringBuilder sb = new StringBuilder();
        sb.append("DocDetailDTO{")
                .append("docId=").append(docId)
                .append(", docNo='").append(docNo).append('\'')
                .append(", pageNo=").append(pageNo)
                .append(", pageTotal=").append(pageTotal)
                .append(", writer='").append(writer).append('\'')
                .append(", deptCode=").append(deptCode)
                .append(", writeDate='").append(writeDate != null ? writeDate.format(formatter) : "N/A").append('\'')
                .append(", drafterEmpNo='").append(drafterEmpNo).append('\'')
                .append(", drafterDept='").append(drafterDept).append('\'')
                .append(", drafterPosition='").append(drafterPosition).append('\'')
                .append(", drafterName='").append(drafterName).append('\'')
                .append(", draftDate='").append(draftDate != null ? draftDate.format(formatter) : "N/A").append('\'')
                .append(", detailTitle='").append(detailTitle).append('\'')
                .append(", detailContent='").append(detailContent).append('\'')
                .append(", detailNote='").append(detailNote).append('\'')
                .append(", docStatus='").append(docStatus).append('\'')
                .append(", statusColor='").append(statusColor).append('\'');

        // 멀티파일 정보
        sb.append(", files=").append(files);

        // 결재자 정보
        sb.append(", approvers=");
        if (approvers != null && !approvers.isEmpty()) {
            sb.append('[');
            for (int i = 0; i < approvers.size(); i++) {
                sb.append(approvers.get(i).toString());
                if (i < approvers.size() - 1)
                    sb.append(", ");
            }
            sb.append(']');
        } else {
            sb.append("[]");
        }

        sb.append('}');
        return sb.toString();
    }

}