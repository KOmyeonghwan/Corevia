package com.example.corenet.admin.doc.serv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.corenet.admin.doc.dto.ApproverDTO;
import com.example.corenet.admin.doc.dto.DocAdListDTO;
import com.example.corenet.admin.doc.dto.DocDetailDTO;
import com.example.corenet.admin.doc.dto.DocFileDTO;
import com.example.corenet.admin.doc.dto.DocManagerDTO;
import com.example.corenet.admin.doc.dto.DocUserListDTO;
import com.example.corenet.admin.doc.dto.TeamLeaderDataDTO;
import com.example.corenet.admin.doc.entity.DocManager;
import com.example.corenet.admin.doc.repo.DocApproverRepository;
import com.example.corenet.admin.doc.repo.DocFileRepository;
import com.example.corenet.admin.doc.repo.DocManagerRepository;

@Service
public class DocManagerService {

    private final JdbcTemplate jdbcTemplate;
    private final DocManagerRepository docManagerRepository;
    private final DocFileRepository docFileRepository;
    private final DocApproverRepository docApproverRepository;

    public DocManagerService(JdbcTemplate jdbcTemplate, DocManagerRepository docManagerRepository,
            DocFileRepository docFileRepository,
            DocApproverRepository docApproverRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.docManagerRepository = docManagerRepository;
        this.docApproverRepository = docApproverRepository;
        this.docFileRepository = docFileRepository;
    }

    // 문서 테이블 이름 생성
    public String getDocTableName(String docCode) {
        return "doc_" + docCode;
    }

    // 문서 결재자 및 파일 사용 여부 확인
    public Map<String, Object> getDocumentStatus(String docCode) {
        String sql = "SELECT use_approval, use_file FROM doc_manager WHERE doc_code = ?";

        try {
            return jdbcTemplate.queryForObject(sql, new Object[] { docCode }, (rs, rowNum) -> {
                Map<String, Object> map = new HashMap<>();
                map.put("use_approval", rs.getInt("use_approval"));
                map.put("use_file", rs.getInt("use_file"));

                // System.out.println("statusMap = " + map);
                return map;
            });
        } catch (EmptyResultDataAccessException e) {
            Map<String, Object> map = new HashMap<>();
            map.put("use_approval", null);
            map.put("use_file", null);
            return map;
        }
    }

    // 테이블 컬럼 조회
    public List<String> getTableColumns(String tableName) {
        String sql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ?";
        return jdbcTemplate.queryForList(sql, new Object[] { tableName }, String.class)
                .stream().map(String::toLowerCase).collect(Collectors.toList());
    }

    // 화면 데이터 -> DB 컬럼명 매핑
    public Map<String, Object> mapScreenToDbColumns(Map<String, Object> screenData) {
        Map<String, Object> dbData = new HashMap<>();

        // 화면 데이터 매핑
        screenData.forEach((key, value) -> {
            if (value == null)
                return;
            switch (key) {
                case "DOC_NO" -> dbData.put("doc_no", value);
                case "WRITER" -> dbData.put("writer", value);
                case "DEPT_CODE" -> dbData.put("dept_code", value);

                case "DETAIL_TITLE" -> dbData.put("detail_title", value);
                case "DETAIL_CONTENT" -> dbData.put("detail_content", value);
                case "DETAIL_NOTE" -> dbData.put("detail_note", value);

                case "DRAFTER_EMP_NO" -> dbData.put("drafter_emp_no", value);
                case "DRAFTER_DEPT" -> dbData.put("drafter_dept", value);
                case "DRAFTER_POSITION" -> dbData.put("drafter_position", value);
                case "DRAFTER_NAME" -> dbData.put("drafter_name", value);
                case "DRAFT_DATE" -> dbData.put("draft_date", value);

                case "PAGE_NO" -> dbData.put("page_no", value);
                case "PAGE_TOTAL" -> dbData.put("page_total", value);
                case "WRITE_DATE" -> dbData.put("write_date", value);
                case "DOC_STATUS" -> dbData.put("doc_status", value);
                default -> dbData.put(key.toLowerCase(), value);
            }
        });

        // DOC_STATUS가 없으면 기본값 'draft' 삽입
        dbData.putIfAbsent("doc_status", "draft"); // enum('draft','pending','approved','rejected', 'no_approval')

        return dbData;
    }

    // 테이블에 실제 존재하는 컬럼만 필터링
    public Map<String, Object> filterValidColumns(String tableName, Map<String, Object> clientData) {
        List<String> columns = getTableColumns(tableName);
        return clientData.entrySet().stream()
                .filter(e -> columns.contains(e.getKey().toLowerCase()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    // map + filter 통합
    public Map<String, Object> mapAndFilterScreenData(String tableName, Map<String, Object> screenData) {
        Map<String, Object> mapped = mapScreenToDbColumns(screenData);
        return filterValidColumns(tableName, mapped);
    }

    // doc 버튼 가져오기
    public List<DocManagerDTO> getAllDocsBtn(Integer deptCode) {

        String sql = "SELECT doc_code, doc_name, dept_code " +
                "FROM doc_manager " +
                "WHERE dept_code = 1 OR dept_code = ? " +
                "ORDER BY doc_name";

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new DocManagerDTO(
                        rs.getString("doc_code"),
                        rs.getString("doc_name"),
                        rs.getInt("dept_code")),
                deptCode);
    }

    // 부장 정보 가져오기
    public TeamLeaderDataDTO getTeamLeaderData(Integer deptCode) {

        String jobCode = deptCode + "001"; // 부장 -> 부서코드 + 001

        String sql = """
                    SELECT
                        u.user_name,
                        u.department_id,
                        u.jobcode,
                        p.position_title AS positionName
                    FROM users u
                    JOIN positions p
                      ON u.position_id = p.id
                    WHERE u.jobcode = ?
                """;

        return jdbcTemplate.queryForObject(
                sql,
                new Object[] { jobCode },
                (rs, rowNum) -> {
                    TeamLeaderDataDTO dto = new TeamLeaderDataDTO();
                    dto.setUserName(rs.getString("user_name"));
                    dto.setDepartmentId(rs.getInt("department_id"));
                    dto.setPositionName(rs.getString("positionName"));
                    dto.setEmployeeNo(rs.getString("jobcode"));
                    return dto;
                });
    }

    // 문서 서식 테이블 생성
    @Transactional
    public void createDocTable(
            String docCode,
            String docName,
            Integer deptCode,
            boolean useDocNo,
            boolean usePageNo,
            boolean useApproval,
            boolean useWriter,
            boolean useDrafter,
            boolean useDetailTitle,
            boolean useDetailContent,
            boolean useDetailNote,
            boolean useFile) {
        // ================= 중복 체크 =================
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) " +
                        "FROM doc_manager " +
                        "WHERE dept_code IN (1, ?) " +
                        "AND (doc_code = ? OR doc_name = ?)",
                Integer.class,
                deptCode,
                docCode,
                docName);

        if (count != null && count > 0) {
            throw new IllegalStateException("이미 존재하는 문서 코드 또는 이름입니다.");
        }

        // ================= 문서 메타 저장 =================
        DocManager docManager = new DocManager();
        docManager.setDocCode(docCode);
        docManager.setDocName(docName);
        docManager.setDeptCode(deptCode);

        // 결재자칸 활성화 여부(기본: 비활성)
        if (useApproval) {
            docManager.setUseApproval(1); // 화면에서 input 활성화 판단용
            // 실제 결재자 정보는 doc_approver 테이블에 doc_id로 삽입
        }

        if (useFile) {
            docManager.setUseFile(1);
        }

        docManagerRepository.save(docManager);

        // ================= 테이블 생성 =================
        String tableName = getDocTableName(docCode);
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS ")
                .append(tableName)
                .append(" ( ");
        sql.append("doc_id BIGINT AUTO_INCREMENT PRIMARY KEY, ");

        // 문서번호
        if (useDocNo) {
            sql.append("doc_no VARCHAR(100) COMMENT '문서 번호', ");
        }

        // 페이지
        if (usePageNo) {
            sql.append("page_no INT DEFAULT 1 COMMENT '현재 페이지', ");
            sql.append("page_total INT DEFAULT 1 COMMENT '전체 페이지', ");
        }

        // 작성자
        if (useWriter) {
            sql.append("writer VARCHAR(50) COMMENT '작성자', ");
            sql.append("dept_code int(30) COMMENT '작성자 부서코드', ");
            sql.append("write_date DATE COMMENT '작성일자', ");
        }

        // ====================

        // 기안자
        if (useDrafter) {
            sql.append("drafter_emp_no VARCHAR(30) COMMENT '기안자 사번', ");
            sql.append("drafter_dept VARCHAR(100) COMMENT '기안자 소속', ");
            sql.append("drafter_position VARCHAR(50) COMMENT '기안자 직급', ");
            sql.append("drafter_name VARCHAR(50) COMMENT '기안자 성명', ");
            sql.append("draft_date DATE COMMENT '기안일자', ");
        }

        // 본문
        if (useDetailTitle) {
            sql.append("detail_title VARCHAR(200) COMMENT '본문 제목', ");
        }
        if (useDetailContent) {
            sql.append("detail_content LONGTEXT COMMENT '상세 내용 (HTML)', ");
        }
        if (useDetailNote) {
            sql.append("detail_note TEXT COMMENT '특기사항', ");
        }

        // doc_status[ ENUM('draft초안', 'pending보류', 'approved승인', 'rejected반려'
        // 'discarded폐기''no_approval' -> 결재 항목이 없는 서류 ]
        if (useApproval) {
            sql.append(
                    "doc_status ENUM('draft', 'pending', 'approved', 'rejected', 'discarded', 'no_approval') DEFAULT 'draft' COMMENT '결재상태', ");
        } else {
            sql.append(
                    "doc_status ENUM('draft', 'pending', 'approved', 'rejected', 'discarded', 'no_approval') DEFAULT 'no_approval' COMMENT '결재상태', ");
        }

        sql.append("created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, ");
        sql.append("updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP ");

        sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");

        jdbcTemplate.execute(sql.toString());
    }

    // 문서 개별 삭제
    @Transactional
    public void deleteOneDoc(String docType, Long docId) {
        String docTable = getDocTableName(docType);

        System.out.println("파일 삭제 중: 테이블: " + docTable + ", 문서 ID: " + docId);
        String deleteSql = "DELETE FROM doc_file WHERE doc_id = ? AND doc_type = ?";
        jdbcTemplate.update(deleteSql, docId, docType);

        System.out.println("결재자 삭제 중: 테이블: " + docTable + ", 문서 ID: " + docId);
        deleteSql = "DELETE FROM doc_approver WHERE doc_id = ? AND doc_type = ?";
        jdbcTemplate.update(deleteSql, docId, docType);

        // 실제 문서 삭제
        deleteSql = "DELETE FROM " + docTable + " WHERE doc_id = ?";
        System.out.println("실제 문서 삭제 중: " + deleteSql);
        jdbcTemplate.update(deleteSql, docId);

    }

    // 문서 서식 삭제
    @Transactional
    public void deleteDoc(String docCode) {
        // 1. 실제 문서 테이블 삭제
        String tableName = getDocTableName(docCode);
        String dropTableSql = "DROP TABLE IF EXISTS " + tableName;
        jdbcTemplate.execute(dropTableSql);

        // 2. 메타 정보 삭제 (doc_manager 테이블)
        docManagerRepository.deleteByDocCode(docCode);

        // 3. 결재자 정보 삭제 (예: doc_approval 테이블)
        String deleteApprovalSql = "DELETE FROM doc_approver WHERE doc_type = ?";
        jdbcTemplate.update(deleteApprovalSql, docCode);

        // 4. 파일 정보 삭제 (doc_file 테이블 + 실제 파일 삭제)
        String selectFilesSql = "SELECT file_path FROM doc_file WHERE doc_type = ?";
        List<String> filePaths = jdbcTemplate.queryForList(selectFilesSql, String.class, docCode);

        // 실제 파일 삭제
        for (String pathStr : filePaths) {
            try {
                Path path = Paths.get(pathStr);
                Files.deleteIfExists(path);
            } catch (IOException e) {
                // 로그만 남기고 계속 진행
                System.err.println("파일 삭제 실패: " + pathStr);
                e.printStackTrace();
            }
        }

        // doc_file 테이블에서 레코드 삭제
        String deleteFilesSql = "DELETE FROM doc_file WHERE doc_type = ?";
        jdbcTemplate.update(deleteFilesSql, docCode);
    }

    // 전체 문서 개수 카운트
    public int countByDocCode(String docCode) {
        String docTableName = getDocTableName(docCode);

        String sql = "SELECT COUNT(*) FROM " + docTableName;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }

    // [사용자] 전자결재 작성 시 view 구현
    public Map<String, Boolean> getColumnStatus(String docCode, String[] columnNames) {
        String tableName = getDocTableName(docCode); // ex) doc_draft
        Map<String, Boolean> columnStatus = new HashMap<>();

        // 동적 문서 테이블 컬럼 조회
        List<String> columns = getTableColumns(tableName);
        Map<String, Boolean> dbColumns = columns.stream()
                .collect(Collectors.toMap(String::toUpperCase, c -> true)); // 대문자로 변환

        // 요청 컬럼 기준 true / false
        for (String col : columnNames) {
            switch (col.toUpperCase()) {
                case "DRAFTER":
                    String[] drafterCols = { "DRAFTER_EMP_NO", "DRAFTER_DEPT", "DRAFTER_POSITION", "DRAFTER_NAME" };
                    boolean drafterVisible = Arrays.stream(drafterCols)
                            .anyMatch(dbColumns::containsKey);
                    columnStatus.put("DRAFTER", drafterVisible);
                    break;

                case "FILE":
                    String[] fileCols = { "FILE_NAME", "FILE_PATH" }; // 실제 컬럼명으로 변경
                    boolean fileVisible = Arrays.stream(fileCols)
                            .anyMatch(dbColumns::containsKey);
                    columnStatus.put("FILE", fileVisible);
                    break;

                default:
                    columnStatus.put(col, dbColumns.getOrDefault(col.toUpperCase(), false));
                    break;
            }
        }

        // doc_manager.use_approval 조회
        String approvalSql = "SELECT use_approval FROM doc_manager WHERE doc_code = ?";
        Boolean useApproval = jdbcTemplate.queryForObject(approvalSql, Boolean.class, docCode);
        columnStatus.put("USE_APPROVAL", Boolean.TRUE.equals(useApproval));

        // doc_manager.use_file 조회
        String fileSql = "SELECT use_file FROM doc_manager WHERE doc_code = ?";
        Boolean useFile = jdbcTemplate.queryForObject(fileSql, Boolean.class, docCode);
        columnStatus.put("USE_FILE", Boolean.TRUE.equals(useFile));

        return columnStatus;
    }

    // [문서 저장]
    public Long saveDocumentAndGetId(String docCode, Map<String, Object> docData) {
        String tableName = getDocTableName(docCode); // ex) doc_draft

        // INSERT용 컬럼/값 준비 (null 제외)
        List<String> columns = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        docData.forEach((key, value) -> {
            if (value != null) {
                columns.add(key.toLowerCase());
                values.add(value);
            }
        });

        // created_at, updated_at 추가
        columns.add("created_at");
        columns.add("updated_at");
        LocalDateTime now = LocalDateTime.now();
        values.add(now);
        values.add(now);

        // 동적 INSERT SQL 생성
        String sql = String.format(
                "INSERT INTO %s (%s) VALUES (%s)",
                tableName,
                String.join(", ", columns),
                columns.stream().map(c -> "?").collect(Collectors.joining(", ")));

        // KeyHolder로 PK 반환
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < values.size(); i++) {
                ps.setObject(i + 1, values.get(i));
            }
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new RuntimeException("문서 저장 후 ID를 가져오지 못했습니다.");
        }

        Long docId = key.longValue();

        // ======== doc_approver 데이터 넣기 영역 ============

        String statusCheck = ((String) docData.get("doc_status")).toUpperCase();
        if (statusCheck == null) {
            System.out.println("Status not found.");
        }

        // use_approval 체크
        String approvalSql = "SELECT use_approval FROM doc_manager WHERE doc_code = ?";
        Boolean useApproval = jdbcTemplate.queryForObject(approvalSql, Boolean.class, docCode);

        // doc_approver에 추가(ceo, 부장)
        if (Boolean.TRUE.equals(useApproval)) {
            // 자기 부서 부장 정보 조회
            String managerSql = """
                        SELECT Jobcode, user_name, position_id
                        FROM users
                        WHERE department_id = ?
                          AND position_id = 2  -- 부장
                    """;

            Map<String, Object> manager = jdbcTemplate.queryForMap(
                    managerSql,
                    docData.get("dept_code") // 문서 작성자 부서 코드
            );

            // CEO 정보 조회
            String ceoSql = """
                        SELECT Jobcode, user_name, position_id
                        FROM users
                        WHERE position_id = 1  -- CEO
                        LIMIT 1
                    """;

            Map<String, Object> ceo = jdbcTemplate.queryForMap(ceoSql);

            // INSERT 쿼리
            String insertSql = """
                        INSERT INTO doc_approver
                        (doc_id, approver_emp_no, approver_name, approver_position, approval_status, doc_type)
                        VALUES (?, ?, ?, ?, ?, ?)
                    """;

            // 자기 부서 부장 → DRAFT
            jdbcTemplate.update(insertSql,
                    docId,
                    manager.get("Jobcode"),
                    manager.get("user_name"),
                    manager.get("position_id"),
                    statusCheck,
                    docCode);

            // CEO → DRAFT
            jdbcTemplate.update(insertSql,
                    docId,
                    ceo.get("Jobcode"),
                    ceo.get("user_name"),
                    ceo.get("position_id"),
                    statusCheck,
                    docCode);
        }

        return docId;
    }

    // 파일 정보 저장
    public void saveFileInfo(Map<String, Object> fileData) {
        String sql = """
                INSERT INTO doc_file
                (doc_id, doc_type, file_name, file_path, file_size, created_at)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(sql,
                fileData.get("doc_id"),
                fileData.get("doc_type"),
                fileData.get("file_name"),
                fileData.get("file_path"),
                fileData.get("file_size"),
                fileData.get("created_at"));
    }

    // [관리자] 부서별 + 상태별 문서 리스트 조회
    public List<DocAdListDTO> getDocList(
            String docCode,
            Integer departmentId,
            String status,
            int page,
            int pageSize) {

        if (docCode == null || docCode.trim().isEmpty()) {
            throw new IllegalArgumentException("docCode가 비어 있습니다.");
        }

        // page 보호
        page = Math.max(page, 1);
        int offset = (page - 1) * pageSize;

        String tableName = getDocTableName(docCode);

        StringBuilder sql = new StringBuilder();
        sql.append(
                "SELECT doc_id, doc_no, detail_title, writer, write_date, doc_status " +
                        "FROM " + tableName);

        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        // 상태 필터 (all 제외)
        if (status != null && !"all".equalsIgnoreCase(status)) {
            conditions.add("LOWER(doc_status) = LOWER(?)");
            params.add(status);
        }

        // 부서 필터 (1번 부서는 전체)
        if (departmentId != null && departmentId != 1) {
            conditions.add("dept_code = ?");
            params.add(departmentId);
        }

        // doc_status가 'draft'인 문서 제외
        conditions.add("LOWER(doc_status) != 'draft'");

        // WHERE 절 조합
        if (!conditions.isEmpty()) {
            sql.append(" WHERE ");
            sql.append(String.join(" AND ", conditions));
        }

        // 정렬 + 페이지네이션
        sql.append(" ORDER BY write_date DESC LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add(offset);

        return jdbcTemplate.query(
                sql.toString(),
                params.toArray(),
                (rs, rowNum) -> {
                    DocAdListDTO dto = new DocAdListDTO();

                    dto.setDocId(rs.getInt("doc_id"));
                    dto.setDocNo(rs.getString("doc_no"));
                    dto.setDetailTitle(rs.getString("detail_title"));
                    dto.setWriter(
                            rs.getString("writer") != null ? rs.getString("writer") : "알 수 없음");
                    dto.setWriteDate(
                            rs.getString("write_date") != null ? rs.getString("write_date") : "0000-00-00");

                    String statusValue = rs.getString("doc_status");
                    dto.setDocStatus(statusValue != null ? statusValue : "draft");

                    // 상태색 지정
                    dto.setStatusColor(getStatusColor(statusValue.toLowerCase()));

                    return dto;
                });
    }

    // [사용자] 부서별 + 상태별 + 임시(draft, 작성자 본인인 것만) 문서 리스트 조회
    public List<DocUserListDTO> getUserDocList(
            Integer jobCode,
            String docCode,
            Integer departmentId,
            String status,
            int page,
            int pageSize) {

        if (docCode == null || docCode.isBlank()) {
            throw new IllegalArgumentException("docCode가 비어 있습니다.");
        }

        page = Math.max(page, 1);
        int offset = (page - 1) * pageSize;

        String tableName = getDocTableName(docCode);

        StringBuilder sql = new StringBuilder(
                "SELECT doc_id, doc_no, detail_title, writer, write_date " +
                        "FROM " + tableName);

        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        // 부서 필터
        if (departmentId != null && departmentId != 1) {
            conditions.add("dept_code = ?");
            params.add(departmentId);
        }

        // 상태 필터 (정규화)
        if (status != null && !status.isBlank()) {
            conditions.add("doc_status = ?");
            params.add(status.toUpperCase());
        }

        // jobCode → doc_no 패턴
        if (jobCode != null) {
            conditions.add("doc_no LIKE ?");
            params.add("%-" + jobCode + "-%");
        }

        if (!conditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", conditions));
        }

        sql.append(" ORDER BY write_date DESC LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add(offset);

        return jdbcTemplate.query(
                sql.toString(),
                params.toArray(),
                (rs, rowNum) -> {
                    DocUserListDTO dto = new DocUserListDTO();
                    dto.setDocId(rs.getInt("doc_id"));
                    dto.setDocNo(rs.getString("doc_no"));
                    dto.setDetailTitle(rs.getString("detail_title"));
                    dto.setWriter(rs.getString("writer"));
                    dto.setWriteDate(rs.getString("write_date"));
                    return dto;
                });
    }

    // [관리자] 문서 개수
    public int getTotalDocCount(String docCode, Integer departmentId, String status) {
        String tableName = getDocTableName(docCode);
        String sql = String.format(
                "SELECT COUNT(*) FROM %s WHERE dept_code = ? AND LOWER(doc_status) = LOWER(?)",
                tableName);
        return jdbcTemplate.queryForObject(sql, Integer.class, departmentId, status);
    }

    // [사용자] 전자결재 문서 총 개수 조회
    public int getTotalUserDocCount(
            Integer jobCode,
            String docCode,
            Integer departmentId,
            String status) {

        // docCode 검증
        if (docCode == null || docCode.trim().isEmpty()) {
            throw new IllegalArgumentException("docCode가 비어 있습니다.");
        }

        // 문서 테이블 이름
        String tableName = getDocTableName(docCode);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) FROM ").append(tableName);

        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        // 부서 필터
        if (departmentId != null && departmentId != 1) {
            conditions.add("dept_code = ?");
            params.add(departmentId);
        }

        // 상태 필터
        if (status != null && !status.isBlank()) {
            conditions.add("doc_status = ?");
            params.add(status.toUpperCase());
        }

        // jobCode → doc_no LIKE (완전히 동일)
        if (jobCode != null) {
            conditions.add("doc_no LIKE ?");
            params.add("%-" + jobCode + "-%");
        }

        // WHERE 조합
        if (!conditions.isEmpty()) {
            sql.append(" WHERE ");
            sql.append(String.join(" AND ", conditions));
        }

        return jdbcTemplate.queryForObject(
                sql.toString(),
                params.toArray(),
                Integer.class);
    }

    // [문서 조회] 문서 상세 조회
    public DocDetailDTO getDocById(String docCode, Integer docId) {
        String tableName = getDocTableName(docCode);
        Map<String, Object> statusMap = getDocumentStatus(docCode); // 문서의 결재자와 파일 사용 여부 확인

        // int 값을 boolean으로 변환
        boolean useApproval = statusMap.get("use_approval") != null && ((Integer) statusMap.get("use_approval")) == 1;
        boolean useFile = statusMap.get("use_file") != null && ((Integer) statusMap.get("use_file")) == 1;

        String sql = String.format("SELECT * FROM %s WHERE doc_id = ?", tableName);

        // 문서 기본 정보 조회
        DocDetailDTO dto = jdbcTemplate.queryForObject(sql, new Object[] { docId }, (rs, rowNum) -> {
            DocDetailDTO docDto = new DocDetailDTO();

            // 문서명 가져오기
            String docName = docManagerRepository.findDocNameByDocCode(docCode);
            docDto.setDocName(docName);

            // 기본 문서 정보 셋팅
            docDto.setDocId(getColumnValue(rs, "doc_id", Integer.class, 0));
            docDto.setDocNo(getColumnValue(rs, "doc_no", String.class, "N/A"));
            docDto.setPageNo(getColumnValue(rs, "page_no", Integer.class, 1));
            docDto.setPageTotal(getColumnValue(rs, "page_total", Integer.class, 1));
            docDto.setWriter(getColumnValue(rs, "writer", String.class, "알 수 없음"));
            docDto.setDeptCode(getColumnValue(rs, "dept_code", Integer.class, 0));
            docDto.setWriteDate(getColumnValue(rs, "write_date", String.class, "2020-01-01"));

            // 기안자 정보
            docDto.setDrafterEmpNo(getColumnValue(rs, "drafter_emp_no", String.class, ""));
            docDto.setDrafterDept(getColumnValue(rs, "drafter_dept", String.class, ""));
            docDto.setDrafterPosition(getColumnValue(rs, "drafter_position", String.class, ""));
            docDto.setDrafterName(getColumnValue(rs, "drafter_name", String.class, ""));
            docDto.setDraftDate(getColumnValue(rs, "draft_date", String.class, "2020-01-01"));

            // 상세 내용
            docDto.setDetailTitle(getColumnValue(rs, "detail_title", String.class, ""));
            docDto.setDetailContent(getColumnValue(rs, "detail_content", String.class, ""));
            docDto.setDetailNote(getColumnValue(rs, "detail_note", String.class, ""));

            // 문서 상태 및 색상
            String docStatus = getColumnValue(rs, "doc_status", String.class, "draft");
            docDto.setDocStatus(docStatus);

            docDto.setStatusColor(getStatusColor(docStatus));

            return docDto;
        });

        // 결재자 정보 가져오기
        if (useApproval) {
            String approverSql = "SELECT * FROM doc_approver WHERE doc_id = ? AND doc_type =?";
            List<ApproverDTO> approvers = jdbcTemplate.query(approverSql, new Object[] { docId, docCode },
                    (rs, rowNum) -> {
                        ApproverDTO approver = new ApproverDTO();
                        approver.setApproverEmpNo(rs.getString("approver_emp_no"));
                        approver.setApproverName(rs.getString("approver_name"));
                        approver.setApproverPosition(rs.getInt("approver_position"));
                        approver.setApprovalComments(rs.getString("approval_comments"));
                        approver.setApprovalStatus(rs.getString("approval_status"));

                        Timestamp ts = rs.getTimestamp("approval_date");
                        if (ts != null) {
                            approver.setApprovalDate(ts.toLocalDateTime());
                        }

                        return approver;
                    });

            dto.setApprovers(approvers);
        }

        // 파일 정보 가져오기 (멀티 파일)
        if (useFile) {
            String fileSql = "SELECT * FROM doc_file WHERE doc_id = ?";
            jdbcTemplate.query(fileSql, new Object[] { docId }, (ResultSet rs) -> {
                while (rs.next()) {
                    // DocFileDTO로 파일 정보 저장
                    DocFileDTO fileDTO = new DocFileDTO();
                    fileDTO.setPath(rs.getString("file_path"));
                    fileDTO.setName(rs.getString("file_name"));
                    fileDTO.setSize(rs.getLong("file_size"));

                    Timestamp ts = rs.getTimestamp("created_at");
                    fileDTO.setCreatedDate(ts != null ? ts.toLocalDateTime().toLocalDate() : LocalDate.of(2020, 1, 1));

                    dto.getFiles().add(fileDTO);
                }
            });
        }

        return dto;
    }

    // 컬럼 값을 가져오는 함수 (타입 안전하게 처리)
    private <T> T getColumnValue(ResultSet rs, String columnName, Class<T> type, T defaultValue) {
        try {
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            boolean exists = false;
            for (int i = 1; i <= columnCount; i++) {
                if (meta.getColumnLabel(i).equalsIgnoreCase(columnName)) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                return defaultValue;
            }

            Object value = rs.getObject(columnName);
            if (value == null) {
                return defaultValue;
            }

            // === 타입별 안전 변환 ===

            // Date → String
            if (value instanceof java.sql.Date && type == String.class) {
                return type.cast(value.toString());
            }

            // Timestamp → LocalDateTime
            if (value instanceof Timestamp && type == LocalDateTime.class) {
                return type.cast(((Timestamp) value).toLocalDateTime());
            }

            // Long → Integer
            if (type == Integer.class && value instanceof Long) {
                return type.cast(((Long) value).intValue());
            }

            // 기본 캐스팅
            return type.cast(value);

        } catch (Exception e) {
            return defaultValue;
        }
    }

    // 문서 상태에 따른 색상 지정
    private String getStatusColor(String docStatus) {
        switch (docStatus.toLowerCase()) {
            case "draft":
                return "blue";
            case "pending":
                return "orange";
            case "approved":
                return "green";
            case "rejected":
                return "red";
            case "discarded":
                return "gray";
            default:
                return "black";
        }
    }

    /**
     * 문서 업데이트 [수정 중]
     * 
     * @param docTableName DB 테이블명 (docCode에 따라 동적)
     * @param docId        업데이트할 문서 ID
     * @param dbData       컬럼명-값 매핑
     */
    public void updateDocument(String docTableName, Long docId, Map<String, Object> dbData) {
        if (docTableName == null || docTableName.isBlank()) {
            throw new IllegalArgumentException("docTableName 누락");
        }
        if (docId == null) {
            throw new IllegalArgumentException("docId 누락");
        }
        if (dbData == null || dbData.isEmpty()) {
            throw new IllegalArgumentException("dbData가 비어있음");
        }

        // SET 절 만들기
        StringBuilder sql = new StringBuilder("UPDATE ").append(docTableName).append(" SET ");
        List<Object> params = new ArrayList<>();

        dbData.forEach((column, value) -> {
            sql.append(column).append(" = ?, ");
            params.add(value);
        });

        // 마지막 콤마 제거
        sql.setLength(sql.length() - 2);

        sql.append(" WHERE doc_id = ?");
        params.add(docId);

        // 업데이트 실행
        jdbcTemplate.update(sql.toString(), params.toArray());
    }

    public long countDocs() {
        return docManagerRepository.count();
    }

}