package com.example.corenet.admin.board.serv;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.corenet.admin.board.dto.BoardAdContentDTO;
import com.example.corenet.admin.board.dto.BoardContentDetailDTO;
import com.example.corenet.admin.board.dto.BoardMangerDTO;
import com.example.corenet.admin.board.dto.BoardUserContentDTO;
import com.example.corenet.admin.board.dto.CommentDTO;
import com.example.corenet.admin.board.entity.BoardManager;
import com.example.corenet.admin.board.repo.BoardManagerRepository;
import com.example.corenet.common.dto.LoginUserDTO;
import com.example.corenet.admin.board.repo.BoardContentRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

@Service
public class BoardManagerService {
        private final JdbcTemplate jdbcTemplate;
        private final BoardManagerRepository boardManagerRepository;
        private final BoardContentRepository boardContentRepository;

        @Value("${board.upload.path}")
        private String boardDir;

        public BoardManagerService(
                        JdbcTemplate jdbcTemplate,
                        BoardManagerRepository boardManagerRepository,
                        BoardContentRepository boardContentRepository) {
                this.jdbcTemplate = jdbcTemplate;
                this.boardManagerRepository = boardManagerRepository;
                this.boardContentRepository = boardContentRepository;
        }

        private String getBoardTableName(String boardCode) {
                return "board_" + boardCode; // ex. board_notice
        }

        private String getCommentTableName(String boardCode) {
                return "comment_" + boardCode; // ex. comment_notice
        }

        // board 버튼 가져오기
        public List<BoardMangerDTO> getAllBoardsBtn(Integer dept_code) {
                String sql = "SELECT board_code, board_name, dept_code " +
                                "FROM board_manager " +
                                "WHERE dept_code = 1 OR dept_code = ?"; // 1은 공통으로 보여지는 게시판

                return jdbcTemplate.query(
                                sql,
                                (rs, rowNum) -> new BoardMangerDTO(
                                                rs.getString("board_code"),
                                                rs.getString("board_name"),
                                                rs.getInt("dept_code")),
                                dept_code);
        }

        // [관리자] 게시글 리스트 가져오기
        public List<BoardAdContentDTO> showAdPostList(
                        String boardCode,
                        Integer deptCode,
                        String keyWord,
                        String searchType,
                        int page,
                        int size) {

                String tableName = "board_" + boardCode;

                // 다른 부서 같은 게시판 확인
                Integer deptCount = boardManagerRepository.countDistinctDeptByBoardCode(boardCode);

                if (deptCount == null) {
                        throw new IllegalArgumentException("존재하지 않는 게시판입니다.");
                }

                StringBuilder sql = new StringBuilder();
                List<Object> params = new ArrayList<>();

                sql.append("SELECT id, board_code, title, content, user_name AS author, views ");
                sql.append("FROM ").append(tableName).append(" WHERE 1=1 ");

                // 부서 전용 게시판
                if (deptCount != null && deptCount > 1) {
                        sql.append(" AND dept_code = ? ");
                        params.add(deptCode);
                }

                // 검색 조건
                if (keyWord != null && !keyWord.trim().isEmpty() && searchType != null) {
                        if ("name".equals(searchType)) {
                                sql.append(" AND user_name LIKE ? ");
                        } else if ("title".equals(searchType)) {
                                sql.append(" AND title LIKE ? ");
                        }
                        params.add("%" + keyWord.trim() + "%");
                }

                // page: 현재 몇 번째 페이지인지 / size: 한 페이지당 게시글 개수
                sql.append(" ORDER BY create_at DESC LIMIT ? OFFSET ? ");
                params.add(size);
                params.add((page - 1) * size);

                // System.out.println();
                // System.out.println(sql);
                // System.out.println("PARAMS = " + params);

                return jdbcTemplate.query(
                                sql.toString(),
                                ps -> {
                                        for (int i = 0; i < params.size(); i++) {
                                                ps.setObject(i + 1, params.get(i));
                                        }
                                },
                                (rs, rowNum) -> new BoardAdContentDTO(
                                                rs.getLong("id"),
                                                rs.getString("title"),
                                                rs.getString("board_code"),
                                                rs.getString("author"),
                                                rs.getInt("views")));

        }

        // [관리자] 게시판 개수
        public int countAdPosts(String boardCode,
                        Integer deptCode,
                        String keyWord,
                        String searchType) {

                String tableName = getBoardTableName(boardCode);

                // 부서 공유 여부 확인
                Integer deptCount = boardManagerRepository.countDistinctDeptByBoardCode(boardCode);

                // COUNT SQL 생성
                StringBuilder sql = new StringBuilder();
                List<Object> params = new ArrayList<>();

                sql.append(" SELECT COUNT(*) ")
                                .append(" FROM ").append(tableName)
                                .append(" WHERE 1=1 ");

                // 여러 부서가 같은 테이블 사용 → 부서 제한
                if (deptCount != null && deptCount > 1) {
                        sql.append(" AND dept_code = ? ");
                        params.add(deptCode);
                }

                // 검색 조건
                if (keyWord != null && !keyWord.trim().isEmpty()
                                && searchType != null && !searchType.trim().isEmpty()) {

                        if ("title".equals(searchType)) {
                                sql.append(" AND title LIKE ? ");
                        } else if ("name".equals(searchType)) {
                                sql.append(" AND user_name LIKE ? ");
                        }

                        params.add("%" + keyWord.trim() + "%");
                }

                return jdbcTemplate.queryForObject(
                                sql.toString(),
                                params.toArray(),
                                Integer.class);
        }

        // [사용자] 게시글 리스트 가져오기
        public List<BoardUserContentDTO> showUserPostList(
                        String boardCode,
                        Integer deptCode,
                        int page,
                        int size) {

                String tableName = "board_" + boardCode;

                Integer deptCount = boardManagerRepository.countDistinctDeptByBoardCode(boardCode);

                if (deptCount == null) {
                        throw new IllegalArgumentException("존재하지 않는 게시판입니다.");
                }

                StringBuilder sql = new StringBuilder();
                List<Object> params = new ArrayList<>();

                sql.append("SELECT id, title, user_name AS author, views, create_at ");
                sql.append("FROM ").append(tableName).append(" WHERE 1=1 ");

                // 부서 전용 게시판
                if (deptCount > 1) {
                        sql.append(" AND dept_code = ? ");
                        params.add(deptCode);
                }

                // 페이징
                sql.append(" ORDER BY create_at DESC LIMIT ? OFFSET ? ");
                params.add(size);
                params.add((page - 1) * size);

                return jdbcTemplate.query(
                                sql.toString(),
                                params.toArray(),
                                (rs, rowNum) -> new BoardUserContentDTO(
                                                rs.getLong("id"),
                                                rs.getString("title"),
                                                boardCode,
                                                rs.getString("author"),
                                                rs.getInt("views"),
                                                rs.getTimestamp("create_at").toLocalDateTime()));
        }

        // [사용자] 게시판 개수
        public int countUserPosts(String boardCode, Integer deptCode) {
                String tableName = getBoardTableName(boardCode);

                // 부서 공유 여부 확인
                Integer deptCount = boardManagerRepository.countDistinctDeptByBoardCode(boardCode);

                // COUNT SQL 생성
                StringBuilder sql = new StringBuilder();
                List<Object> params = new ArrayList<>();

                sql.append(" SELECT COUNT(*) ")
                                .append(" FROM ").append(tableName)
                                .append(" WHERE 1=1 ");

                // 여러 부서가 같은 테이블 사용 → 부서 제한
                if (deptCount != null && deptCount > 1) {
                        sql.append(" AND dept_code = ? ");
                        params.add(deptCode);
                }

                return jdbcTemplate.queryForObject(
                                sql.toString(),
                                params.toArray(),
                                Integer.class);
        }

        // 게시글과 댓글 테이블 생성
        @Transactional
        public void createBoardAndComment(String boardCode, String boardName, Integer deptCode) {

                Integer count = jdbcTemplate.queryForObject(
                                "SELECT COUNT(*) " +
                                                "FROM board_manager " +
                                                "WHERE dept_code IN (1, ?) " +
                                                "AND (board_code = ? OR board_name = ?)",
                                Integer.class,
                                deptCode,
                                boardCode,
                                boardName);
                boolean duplicated = count != null && count > 0;
                if (duplicated) {
                        throw new IllegalStateException("이미 존재하는 게시판 코드 또는 이름입니다.");
                }

                BoardManager boardManager = new BoardManager();
                boardManager.setBoardCode(boardCode);
                boardManager.setBoardName(boardName);
                boardManager.setDeptCode(deptCode);
                boardManagerRepository.save(boardManager);

                String boardTableName = getBoardTableName(boardCode);
                String boardSql = String.format(
                                "CREATE TABLE IF NOT EXISTS %s (" +
                                                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                                                "title VARCHAR(255) NOT NULL, " +
                                                "content TEXT , " +
                                                "file_url VARCHAR(2000), " +
                                                "file_name VARCHAR(255), " +
                                                "board_code VARCHAR(255), " +
                                                "user_id INT NOT NULL, " +
                                                "user_name VARCHAR(30) NOT NULL, " +
                                                "dept_code INT NOT NULL, " + // 부서 코드
                                                "create_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                                                "views INT DEFAULT 0, " +
                                                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +

                                                // FK: users
                                                // "CONSTRAINT fk_%s_user FOREIGN KEY (user_id) " +
                                                // "REFERENCES users(id) ON DELETE CASCADE, " +
                                                // FK: board_manager
                                                // "CONSTRAINT fk_%s_board FOREIGN KEY (board_code) " +
                                                // "REFERENCES board_manager(board_code) " +
                                                // "ON UPDATE CASCADE ON DELETE RESTRICT" +

                                                ")",
                                boardTableName);

                String commentTableName = getCommentTableName(boardCode);
                String commentSql = String.format(
                                "CREATE TABLE IF NOT EXISTS %s (" +
                                                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                                                "post_id BIGINT NOT NULL, " +
                                                "user_id INT NOT NULL, " +
                                                "user_name VARCHAR(30) NOT NULL, " +
                                                "content TEXT NOT NULL, " +
                                                "parent_id BIGINT, " +
                                                "depth INT DEFAULT 0, " + // 깊이: 0=댓글, 1=대댓글, 2=대대댓글
                                                "dept_code INT NOT NULL, " + // 부서 코드
                                                "status VARCHAR(10) DEFAULT 'normal', " +
                                                "create_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                                                "FOREIGN KEY (post_id) REFERENCES %s(id) ON DELETE CASCADE, " +
                                                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, " +
                                                "FOREIGN KEY (parent_id) REFERENCES %s(id) ON DELETE CASCADE" +
                                                ")",
                                commentTableName, boardTableName, commentTableName);

                try {
                        jdbcTemplate.execute(boardSql);
                        jdbcTemplate.execute(commentSql);
                } catch (Exception e) {
                        throw new RuntimeException("게시판 생성 중 오류 발생:" + e.getMessage(), e);
                }
        }

        // 게시글 추가
        public Long savePost(String boardCode, String title, String content, Integer userId, String userName,
                        Integer deptCode, String fileUrl, String fileName) {

                String tableName = "board_" + boardCode;
                String finalFileUrl = (fileUrl != null && !fileUrl.isEmpty()) ? fileUrl : null;

                String sql = String.format(
                                "INSERT INTO %s (board_code, title, content, user_id, user_name, dept_code, file_url, file_name) "
                                                +
                                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                                tableName);

                KeyHolder keyHolder = new GeneratedKeyHolder();

                jdbcTemplate.update(connection -> {
                        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                        ps.setString(1, boardCode);
                        ps.setString(2, title);
                        ps.setString(3, content);
                        ps.setInt(4, userId);
                        ps.setString(5, userName);
                        ps.setInt(6, deptCode);
                        ps.setString(7, finalFileUrl);
                        ps.setString(8, fileName);
                        return ps;
                }, keyHolder);

                // 새로 생성된 ID 반환
                return keyHolder.getKey().longValue();
        }

        // 파일저장
        public String fileSave(String boardCode, MultipartFile file) {

                if (file == null || file.isEmpty()) {
                        return null;
                }

                try {
                        // uploads/board/notice
                        Path uploadPath = Paths.get(boardDir, boardCode);
                        Files.createDirectories(uploadPath);

                        String originalFile = file.getOriginalFilename();
                        String ext = "";

                        if (originalFile != null && originalFile.contains(".")) {
                                ext = originalFile.substring(originalFile.lastIndexOf(".")).toLowerCase();
                        }

                        String savedName = UUID.randomUUID() + ext;
                        Path savePath = uploadPath.resolve(savedName);

                        // 이 방식이 깨짐 문제 가장 적음
                        Files.copy(
                                        file.getInputStream(),
                                        savePath,
                                        StandardCopyOption.REPLACE_EXISTING);

                        // DB 저장용 경로 (컨트롤러 URL 기준)
                        return boardCode + "/" + savedName;

                } catch (Exception e) {
                        throw new RuntimeException("파일 저장 실패", e);
                }
        }

        // [관리자/사용자] 게시글 삭제
        @Transactional
        public boolean deletePost(String boardCode, Long postId) {

                if (!boardCode.matches("^[a-zA-Z0-9_]+$")) {
                        throw new IllegalArgumentException("Invalid board code");
                }

                String tableName = "board_" + boardCode;

                int rows = jdbcTemplate.update(
                                "DELETE FROM " + tableName + " WHERE id = ?",
                                postId);

                jdbcTemplate.update(
                                "DELETE FROM notifications WHERE reference_id = ?",
                                postId);

                return rows > 0;
        }

        // [관리자/사용자] 게시글 상세 조회
        public BoardContentDetailDTO getUserBoardDetail(
                        String boardCode,
                        Long id,
                        HttpSession session,
                        LoginUserDTO loginUser) {
                // 사용자 기준으로 view count
                String viewKey = "VIEW_" + boardCode + "_" + id + "_" + loginUser.getUserId();

                if (session.getAttribute(viewKey) == null) {
                        boardContentRepository.increseView(boardCode, id);
                        session.setAttribute(viewKey, true);
                }

                return boardContentRepository.findUserBoardDetail(boardCode, id);
        }

        // 파일 업데이트
        private String fileUpdate(String boardCode, MultipartFile file) {
                try {
                        // board.upload.path로 설정된 값을 가져와서 업로드 경로로 사용
                        String uploadDir = boardDir + "/" + boardCode; // boardCode에 따른 폴더 생성

                        File dir = new File(uploadDir);
                        if (!dir.exists()) {
                                dir.mkdirs(); // 폴더가 없으면 생성
                        }

                        // 파일 이름 지정 (UUID로 이름 변경)
                        String savedFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                        String filePath = uploadDir + "/" + savedFileName;

                        // 파일 저장
                        file.transferTo(new File(filePath));

                        return boardCode + "/" + savedFileName; // 파일 URL 반환
                } catch (IOException e) {
                        e.printStackTrace();
                        throw new RuntimeException("파일 저장에 실패했습니다.");
                }
        }

        // [관리자/사용자] 게시글 수정
        public String updateBoardDetail(
                        String boardCode,
                        Long postId,
                        String title,
                        String content,
                        MultipartFile file,
                        String existingFileUrl,
                        String existingFileName) {

                String tableName = getBoardTableName(boardCode);

                // 초기값: 기존 파일 정보 유지
                String fileUrl = existingFileUrl;
                String fileName = existingFileName;

                System.out.println("fileUrl> " + fileUrl);
                System.out.println("fileName> " + fileName);

                // 새 파일 업로드가 있을 경우 처리
                if (file != null && !file.isEmpty()) {
                        // 기존 파일 삭제
                        if (existingFileUrl != null && !existingFileUrl.isEmpty()) {
                                String filePath = boardDir + existingFileUrl; // boardDir + 기존 파일 경로
                                File oldFile = new File(filePath);
                                if (oldFile.exists()) {
                                        boolean deleted = oldFile.delete(); // 기존 파일 삭제
                                        if (!deleted) {
                                                System.out.println("기존 파일 삭제 실패");
                                        } else {
                                                System.out.println("기존 파일 삭제 성공");
                                        }
                                }
                        }

                        // 새 파일 저장
                        fileUrl = fileUpdate(boardCode, file); // 새 파일 경로
                        fileName = file.getOriginalFilename(); // 새 파일명
                }

                // SQL 작성
                String sql;
                if ((fileUrl == null || fileUrl.isEmpty()) && (fileName == null || fileName.isEmpty())) {
                        // 파일 없는 경우
                        sql = String.format("""
                                            UPDATE %s
                                            SET title = ?, content = ?
                                            WHERE id = ?
                                        """, tableName);

                        jdbcTemplate.update(sql, title, content, postId);
                } else {
                        // 파일 있는 경우
                        sql = String.format("""
                                            UPDATE %s
                                            SET title = ?, content = ?, file_name = ?, file_url = ?
                                            WHERE id = ?
                                        """, tableName);

                        jdbcTemplate.update(sql, title, content, fileName, fileUrl, postId);
                }

                System.out.println("after fileUrl> " + fileUrl);
                System.out.println("after fileName> " + fileName);

                return "ok";
        }

        // 댓글 쓰기
        @Transactional
        public void addComment(
                        String boardCode,
                        Long postId,
                        Integer userId,
                        String userName,
                        String content,
                        Integer deptCode,
                        Long parentId) {

                String commentTable = getCommentTableName(boardCode);

                int depth = 0;
                if (parentId != null) {
                        // parent 댓글의 depth + 1 계산
                        String depthSql = "SELECT depth FROM " + commentTable + " WHERE id = ?";
                        depth = jdbcTemplate.queryForObject(depthSql, Integer.class, parentId) + 1;
                }

                String sql = "INSERT INTO " + commentTable + " " +
                                "(post_id, user_id, user_name, content, dept_code, parent_id, depth) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?)";

                jdbcTemplate.update(sql, postId, userId, userName, content, deptCode, parentId, depth);
        }

        // [사용자]댓글 가져오기
        public List<CommentDTO> getComments(String boardCode, Long postId) {
                String tableName = "comment_" + boardCode;

                String sql = "SELECT id, post_id, user_id, user_name, content, parent_id, depth, create_at, status " +
                                "FROM " + tableName + " " +
                                "WHERE post_id = ? " +
                                "ORDER BY parent_id ASC, depth ASC, id ASC";

                return jdbcTemplate.query(sql, (rs, rowNum) -> {
                        CommentDTO dto = new CommentDTO();
                        dto.setId(rs.getLong("id"));
                        dto.setPostId(rs.getLong("post_id"));
                        dto.setUserId(rs.getInt("user_id"));
                        dto.setUserName(rs.getString("user_name"));

                        String content = rs.getString("content");
                        String status = rs.getString("status"); // hidden, normal 등
                        dto.setStatus(status);

                        // 숨김 처리된 댓글이면 내용 변경
                        if ("hidden".equalsIgnoreCase(status)) {
                                dto.setContent("비공개 처리된 댓글입니다.");
                        } else {
                                dto.setContent(content);
                        }

                        dto.setParentId(rs.getLong("parent_id") == 0 ? null : rs.getLong("parent_id"));
                        dto.setDepth(rs.getInt("depth"));
                        dto.setCreateAt(rs.getTimestamp("create_at").toLocalDateTime());
                        return dto;
                }, postId);
        }

        // 게시판 삭제
        @Transactional
        public void deleteBoardAndComment(String boardCode, Integer deptCode) {

                if (boardCode == null || boardCode.isBlank()) {
                        throw new IllegalArgumentException("게시판 코드가 없습니다.");
                }

                if (deptCode == null) {
                        throw new IllegalArgumentException("부서 코드가 없습니다.");
                }

                String boardTable = "board_" + boardCode;
                String commentTable = "comment_" + boardCode;

                /* 해당 게시판을 사용하는 부서 수 조회 */
                Integer deptCount = boardManagerRepository.countDistinctDeptByBoardCode(boardCode);

                if (deptCount == null || deptCount == 0) {
                        throw new IllegalStateException("존재하지 않는 게시판입니다.");
                }

                /*
                 * 여러 부서가 사용하는 게시판
                 */
                if (deptCount > 1) {

                        //  해당 부서의 게시글 ID 조회
                        List<Long> postIds = jdbcTemplate.queryForList(
                                        "SELECT id FROM " + boardTable + " WHERE dept_code = ?",
                                        Long.class,
                                        deptCode);

                        //  댓글 삭제
                        if (!postIds.isEmpty()) {
                                String inSql = postIds.stream()
                                                .map(id -> "?")
                                                .collect(Collectors.joining(","));

                                jdbcTemplate.update(
                                                "DELETE FROM " + commentTable + " WHERE post_id IN (" + inSql + ")",
                                                postIds.toArray());
                        }

                        //  게시글 삭제
                        jdbcTemplate.update(
                                        "DELETE FROM " + boardTable + " WHERE dept_code = ?",
                                        deptCode);

                        //  board_manager에서 해당 부서만 제거
                        jdbcTemplate.update(
                                        "DELETE FROM board_manager WHERE board_code = ? AND dept_code = ?",
                                        boardCode,
                                        deptCode);

                        return;
                }

                /*                
                  단일 부서만 사용하는 게시판                 
                 */
                if (deptCount == 1) {

                        // 댓글 테이블 삭제
                        if (existsTable(commentTable)) {
                                jdbcTemplate.execute("DROP TABLE " + commentTable);
                        }

                        // 게시글 테이블 삭제
                        if (existsTable(boardTable)) {
                                jdbcTemplate.execute("DROP TABLE " + boardTable);
                        }

                        // board_manager 삭제
                        jdbcTemplate.update(
                                        "DELETE FROM board_manager WHERE board_code = ?",
                                        boardCode);

                }
        }

        private boolean existsTable(String tableName) {
                Integer count = jdbcTemplate.queryForObject(
                                "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = ?",
                                Integer.class,
                                tableName);
                return count != null && count > 0;
        }

        // 파일 다운로드
        public Resource loadFile(String boardCode, Long postId) throws MalformedURLException {

                String tableName = getBoardTableName(boardCode);

                String sql = "SELECT file_url FROM " + tableName + " WHERE id = ?";
                String fileUrl = jdbcTemplate.queryForObject(sql, String.class, postId);

                if (fileUrl == null || fileUrl.isBlank()) {
                        throw new IllegalArgumentException("File not found");
                }

                // 앞의 / 제거
                if (fileUrl.startsWith("/")) {
                        fileUrl = fileUrl.substring(1);
                }

                Path uploadRoot = Paths.get(System.getProperty("user.dir"), "upload")
                                .toAbsolutePath()
                                .normalize();

                Path filePath = uploadRoot.resolve(fileUrl).normalize();

                if (!filePath.startsWith(uploadRoot)) {
                        throw new SecurityException("Invalid file path");
                }

                Resource resource = new UrlResource(filePath.toUri());

                if (!resource.exists()) {
                        throw new RuntimeException("File does not exist");
                }

                return resource;
        }

        // 이동 [이전/다음]
        public List<Long> findPrevPostIds(String boardCode, Long postId, Integer deptCode) {
                String tableName = getBoardTableName(boardCode);

                String sql = """
                                    SELECT id
                                    FROM %s
                                    WHERE id < ?
                                    ORDER BY id DESC
                                """.formatted(tableName);

                return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("id"), postId);
        }

        public List<Long> findNextPostIds(String boardCode, Long postId, Integer deptCode) {
                String tableName = getBoardTableName(boardCode);

                String sql = """
                                    SELECT id
                                    FROM %s
                                    WHERE id > ?
                                    ORDER BY id ASC
                                """.formatted(tableName);

                return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("id"), postId);
        }

        // 공지사항: 전체 사용자 리스트 가져오기
        public List<Integer> getAllUserIds() {
                String sql = "SELECT id FROM users WHERE id NOT IN (1, 2, 3)";
                return jdbcTemplate.queryForList(sql, Integer.class);
        }

        public long countBoards() {
                return boardManagerRepository.count();
        }
        

}
