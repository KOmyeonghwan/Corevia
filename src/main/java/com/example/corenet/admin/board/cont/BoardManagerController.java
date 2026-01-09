package com.example.corenet.admin.board.cont;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;

import com.example.corenet.admin.board.dto.BoardAdContentDTO;
import com.example.corenet.admin.board.dto.BoardUserContentDTO;
import com.example.corenet.admin.board.dto.CommentDTO;
import com.example.corenet.admin.board.repo.BoardManagerRepository;
import com.example.corenet.admin.board.serv.BoardManagerService;
import com.example.corenet.client.notification.serv.NotificationService;
import com.example.corenet.common.dto.LoginUserDTO;
import com.example.corenet.entity.NotificationType;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class BoardManagerController {

    private final NotificationService notificationService;
    private final BoardManagerService boardManagerService;
    private final BoardManagerRepository boardManagerRepository;
    private final JdbcTemplate jdbcTemplate;

    public BoardManagerController(NotificationService notificationService,
            BoardManagerService boardManagerService,
            BoardManagerRepository boardManagerRepository,
            JdbcTemplate jdbcTemplate) {
        this.notificationService = notificationService;
        this.boardManagerService = boardManagerService;
        this.boardManagerRepository = boardManagerRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    // [관리자] 게시글 삭제
    @DeleteMapping("/post/{id}")
    public ResponseEntity<String> deletePost(
            @PathVariable("id") Long postId,
            @RequestParam("boardCode") String boardCode) {
        boolean deleted = boardManagerService.deletePost(boardCode, postId);
        if (deleted) {
            return ResponseEntity.ok("삭제 성공");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제 실패");
        }
    }

    // [관리자] 게시글 리스트
    @GetMapping("/adboard/board/{boardCode}")
    @ResponseBody
    public String getAdBoardContent(
            @PathVariable("boardCode") String boardCode,
            @RequestParam(value = "keyWord", required = false) String keyWord,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @SessionAttribute("loginUser") LoginUserDTO loginUser) {

        Integer deptCode = loginUser.getDepartment_id();

        // 게시글 목록
        List<BoardAdContentDTO> contents = boardManagerService.showAdPostList(boardCode, deptCode, keyWord, searchType,
                page, size);

        // 전체 게시글 수
        int totalCount = boardManagerService.countAdPosts(boardCode, deptCode, keyWord, searchType);
        int totalPages = (int) Math.ceil((double) totalCount / size);

        StringBuilder sb = new StringBuilder();
        sb.append("<table class='board-table'>")
                .append("<thead>")
                .append("<tr>")
                .append("<th>번호</th>")
                .append("<th>제목</th>")
                .append("<th>게시판 코드</th>")
                .append("<th>작성자</th>")
                .append("<th>조회</th>")
                .append("<th>관리</th>")
                .append("</tr>")
                .append("</thead>")
                .append("<tbody>");

        int index = page * size - size + 1;
        for (BoardAdContentDTO c : contents) {
            String detailUrl = "/adboarddetail/" + c.getBoardCode() + "/" + c.getId();

            sb.append("<tr>")
                    .append("<td>").append(index++).append("</td>")
                    .append("<td><a href='").append(detailUrl).append("'>").append(c.getTitle()).append("</a></td>")
                    .append("<td>").append(c.getBoardCode()).append("</td>")
                    .append("<td>").append(c.getAuthor()).append("</td>")
                    .append("<td>").append(c.getViews()).append("</td>")
                    .append("<td><button class='btn-delete' data-id='").append(c.getId()).append("'>삭제</button></td>")
                    .append("</tr>");
        }
        sb.append("</tbody></table>");

        /* ================= 페이지네이션 ================= */
        sb.append("<div class='pagination'>");

        if (page > 1) {
            sb.append("<button class='page-btn' data-page='")
                    .append(page - 1).append("'>이전</button>");
        }

        for (int i = 1; i <= totalPages; i++) {
            sb.append("<button class='page-btn ")
                    .append(i == page ? "active" : "")
                    .append("' data-page='").append(i).append("'>")
                    .append(i).append("</button>");
        }

        if (page < totalPages) {
            sb.append("<button class='page-btn' data-page='")
                    .append(page + 1).append("'>다음</button>");
        }

        sb.append("</div>");

        return sb.toString();
    }

    // [사용자] 게시글 리스트
    @GetMapping("/userboard/board/{boardCode}")
    @ResponseBody
    public String getUserBoardContent(
            @PathVariable("boardCode") String boardCode,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @SessionAttribute("loginUser") LoginUserDTO loginUser) {

        Integer deptCode = loginUser.getDepartment_id();
        List<BoardUserContentDTO> contents = boardManagerService.showUserPostList(boardCode, deptCode, page, size);

        // 전체 게시글 수
        int totalCount = boardManagerService.countUserPosts(boardCode, deptCode);
        int totalPages = (int) Math.ceil((double) totalCount / size);

        StringBuilder sb = new StringBuilder();

        sb.append("<table class='board-table'>")
                .append("<thead>")
                .append("<tr>")
                .append("<th>번호</th>")
                .append("<th>제목</th>")
                .append("<th>작성자</th>")
                .append("<th>작성일</th>")
                .append("<th>조회</th>")
                .append("</tr>")
                .append("</thead>")
                .append("<tbody>");

        int index = 1;
        for (BoardUserContentDTO c : contents) {

            String detailUrl = "/userboarddetail/" + boardCode + "/" + c.getId();

            sb.append("<tr>")
                    .append("<td>").append(index++).append("</td>")
                    .append("<td><a href='").append(detailUrl).append("'>")
                    .append(c.getTitle()).append("</a></td>")
                    .append("<td>").append(c.getAuthor()).append("</td>")
                    .append("<td>").append(c.getCreateAtFormatted()).append("</td>")
                    .append("<td>").append(c.getViews()).append("</td>")
                    .append("</tr>");
        }

        sb.append("</tbody></table>");

        /* ================= 페이지네이션 ================= */
        sb.append("<div class='pagination'>");

        if (page > 1) {
            sb.append("<button class='page-btn' data-page='")
                    .append(page - 1).append("'>이전</button>");
        }

        for (int i = 1; i <= totalPages; i++) {
            sb.append("<button class='page-btn ")
                    .append(i == page ? "active" : "")
                    .append("' data-page='").append(i).append("'>")
                    .append(i).append("</button>");
        }

        if (page < totalPages) {
            sb.append("<button class='page-btn' data-page='")
                    .append(page + 1).append("'>다음</button>");
        }

        sb.append("</div>");

        return sb.toString();
    }

    // 게시판 + 댓글 table 추가
    @PostMapping("/adboard/createboardandcomment")
    public String createBoardAndComment(
            @RequestParam("boardCode") String boardCode,
            @RequestParam("boardName") String boardName,
            @SessionAttribute("loginUser") LoginUserDTO loginUser) {

        Integer deptCode = loginUser.getDepartment_id();

        boardManagerService.createBoardAndComment(boardCode, boardName, deptCode);
        return "redirect:/adboard";
    }

    // 게시글 작성
    @PostMapping("/userboard/post")
    public String postToBoard(
            @RequestParam("boardCode") String boardCode,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("id") Integer id,
            @RequestParam("userName") String userName,
            @RequestParam("deptCode") Integer deptCode,
            @RequestParam("positionLevel") Integer positionLevel,
            @RequestParam("boardFile") MultipartFile file) {

        String fileName = file.getOriginalFilename();
        String filePath = boardManagerService.fileSave(boardCode, file);
        Long postId = boardManagerService.savePost(boardCode, title, content, id, userName, deptCode, filePath,
                fileName);

        if ("notice".equals(boardCode)) {
            // 공지사항 알림 -> notification 추가
            List<Integer> allUserIds = boardManagerService.getAllUserIds();

            for (Integer userId : allUserIds) {
                notificationService.create(
                        userId, // 알림 받을 사용자
                        id, // 게시글 작성자
                        NotificationType.system, // 공지/시스템 타입
                        postId, // 참조 ID
                        title, // 알림 제목
                        userName + "님공지사항을 작성했습니다." // 알림 내용
                );
            }
        }

        if (positionLevel != null && (positionLevel <= 2 || positionLevel >= 10)) {
            // level ≤ 2 또는 level ≥ 10 → 관리자 페이지
            return "redirect:/adboard";
        } else {
            // 나머지 → 일반 사용자 페이지
            return "redirect:/userboard";
        }
    }

    // 댓글 작성
    @PostMapping("/userboard/comment/{boardCode}/{postId}")
    @ResponseBody
    public Map<String, Object> addComment(
            @PathVariable("boardCode") String boardCode,
            @PathVariable("postId") Long postId,
            @RequestParam("content") String content,
            @RequestParam(value = "parentId", required = false) Long parentId,
            @ModelAttribute("loginUser") LoginUserDTO loginUser) {

        Map<String, Object> result = new HashMap<>();

        // 로그인 검사
        if (loginUser == null) {
            result.put("status", "error");
            result.put("message", "로그인이 필요합니다.");
            return result;
        }

        // 댓글 내용 검사
        if (content == null || content.trim().isEmpty()) {
            result.put("status", "error");
            result.put("message", "댓글 내용을 입력해주세요.");
            return result;
        }

        // 댓글 저장
        boardManagerService.addComment(
                boardCode,
                postId,
                loginUser.getUserPk(),
                loginUser.getUserName(),
                content.trim(),
                loginUser.getDepartment_id(),
                parentId);

        result.put("status", "success");
        result.put("message", "댓글이 등록되었습니다.");

        return result;
    }

    // 댓글 가져오기
    @GetMapping("/userboard/comments/{boardCode}/{postId}")
    @ResponseBody
    public List<CommentDTO> getComments(
            @PathVariable("boardCode") String boardCode,
            @PathVariable("postId") Long postId) {

        return boardManagerService.getComments(boardCode, postId);
    }

    // [작업 중]파일 다운로드
    @GetMapping("/files/{boardCode}/{postId}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable("boardCode") String boardCode,
            @PathVariable("postId") Long postId,
            @RequestParam("fileName") String fileName) {

        try {
            Resource resource = boardManagerService.loadFile(boardCode, postId);

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + fileName + "\"")
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // [관리자/사용자] 게시글 업데이트
    @PostMapping("/boarddetailedit")
    public String updateBoardDetail(
            @RequestParam("boardCode") String boardCode,
            @RequestParam("postId") Long postId,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("positionLevel") Integer positionLevel,
            @RequestParam(value = "boardFile", required = false) MultipartFile file,
            @RequestParam(value = "existingFileUrl", required = false) String existingFileUrl,
            @RequestParam(value = "existingFileName", required = false) String existingFileName,
            @ModelAttribute("loginUser") LoginUserDTO loginUser) {
        // 로그인 체크
        if (loginUser == null) {
            return "redirect:/login";
        }

        // 서비스 호출
        String result = boardManagerService.updateBoardDetail(boardCode, postId, title, content, file, existingFileUrl,
                existingFileName);

        if (result != "ok") {
            return "<script>alert('파일 업로드 실패')</script>";
        }

        // 수정 완료 후 상세 페이지로 리다이렉트
        if (positionLevel != null && (positionLevel <= 2 || positionLevel >= 10)) {
            // level ≤ 2 또는 level ≥ 10 → 관리자 페이지
            return "redirect:/adboarddetail/" + boardCode + "/" + postId;
        } else {
            // 나머지 → 일반 사용자 페이지
            return "redirect:/userboarddetail/" + boardCode + "/" + postId;
        }
    }

    @PostMapping("/adboard/deleteboardandcomment")
    public String deleteBoard(
            @RequestParam("boardCode") String boardCode,
            HttpSession session) {

        LoginUserDTO loginUser = (LoginUserDTO) session.getAttribute("loginUser");
        if (loginUser == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        Integer deptCode = loginUser.getDepartment_id();

        boardManagerService.deleteBoardAndComment(boardCode, deptCode);
        return "redirect:/adboard";
    }
}