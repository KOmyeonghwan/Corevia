package com.example.corenet.admin.board.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BoardUserContentDTO {

    private Long id;
    private String title;
    private String boardCode;
    private String author;
    private int views;
    private LocalDateTime createAt;

    public BoardUserContentDTO() {}

    public BoardUserContentDTO(Long id, String title, String boardCode, String author, int views, LocalDateTime createAt) {
        this.id = id;
        this.title = title;
        this.boardCode = boardCode;
        this.author = author;
        this.views = views;
        this.createAt = createAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public String getCreateAtFormatted() {
        if (createAt == null) return "";
        return createAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
