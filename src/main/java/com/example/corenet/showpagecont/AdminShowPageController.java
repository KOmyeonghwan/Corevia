package com.example.corenet.showpagecont;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.corenet.admin.board.dto.BoardContentDetailDTO;
import com.example.corenet.admin.board.dto.BoardMangerDTO;
import com.example.corenet.admin.board.dto.commentDTO.CommentGroupDTO;
import com.example.corenet.admin.board.dto.commentDTO.CommentListDTO;
import com.example.corenet.admin.board.serv.BoardManagerService;
import com.example.corenet.admin.board.serv.CommentService;
import com.example.corenet.admin.department.serv.DepartmentService;
import com.example.corenet.admin.doc.dto.DocAdListDTO;
import com.example.corenet.admin.doc.dto.DocDetailDTO;
import com.example.corenet.admin.doc.dto.DocFileDTO;
import com.example.corenet.admin.doc.serv.DocManagerService;
import com.example.corenet.admin.user.repository.UsersRepository;
import com.example.corenet.admin.user.service.UsersService;
import com.example.corenet.common.dto.LoginUserDTO;
import com.example.corenet.entity.Department;
import com.example.corenet.entity.User;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminShowPageController {

    private final CommentService commentService;

    private final BoardManagerService boardManagerService;

    private final DocManagerService docManagerService;

    @Autowired
    private UsersService usersService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private DepartmentService departmentService;

    AdminShowPageController(BoardManagerService boardManagerService, CommentService commentService,
            DocManagerService docManagerService) {
        this.boardManagerService = boardManagerService;
        this.commentService = commentService;
        this.docManagerService = docManagerService;
    }

    @GetMapping("/adminuser")
    public String showAdminUserPage(
            @ModelAttribute("loginUser") LoginUserDTO loginUser,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model) {

        if (loginUser == null) // 로그인 유저가 없다면 로그인 페이지로 리다이렉트
            return "redirect:/login";

        List<User> userList = usersService.getUsersForViewer(loginUser);

        // 검색 필터
        if ("userName".equals(searchType) && keyword != null && !keyword.isEmpty()) {
            userList = userList.stream()
                    .filter(u -> u.getUserName().contains(keyword))
                    .toList();
        } else if ("department".equals(searchType) && keyword != null && !keyword.isEmpty()) {
            userList = userList.stream()
                    .filter(u -> u.getDepartment() != null &&
                            u.getDepartment().getDepartmentName().contains(keyword))
                    .toList();
        }

        int pageSize = 10;

        // ✅ totalPages 먼저 계산
        int totalPages = (userList.size() + pageSize - 1) / pageSize;

        // ✅ pages 생성
        List<Map<String, Object>> pages = new ArrayList<>();
        for (int i = 0; i < totalPages; i++) {
            Map<String, Object> pageMap = new HashMap<>();
            pageMap.put("number", i);
            pageMap.put("isCurrent", i == page);
            pageMap.put("searchType", searchType == null ? "" : searchType);
            pageMap.put("keyword", keyword == null ? "" : keyword);
            pages.add(pageMap);
        }
        model.addAttribute("pages", pages);

        // ✅ 실제 데이터 페이징
        int start = page * pageSize;
        int end = Math.min(start + pageSize, userList.size());
        List<User> pagedUsers = userList.subList(start, end);
        model.addAttribute("users", pagedUsers);

        // ✅ 페이징 정보
        model.addAttribute("currentPage", page);
        model.addAttribute("hasPrevious", page > 0);
        model.addAttribute("hasNext", page < totalPages - 1);
        model.addAttribute("prevPage", page - 1);
        model.addAttribute("nextPage", page + 1);
        model.addAttribute("searchType", searchType == null ? "" : searchType);
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        model.addAttribute("departments", departmentService.findAll());

        return "admin/adminuser";
    }

    @GetMapping("/admindashboard")
    public String showAdminUserPage(@ModelAttribute("loginUser") LoginUserDTO loginUser, Model model) {
        if (loginUser == null)
            return "redirect:/login";

    Integer posLevel = loginUser.getPositionLevel();

    // 관리자 조건
    boolean isAdmin = (posLevel != null) && ((posLevel >= 0 && posLevel <= 3) || posLevel == 10 || posLevel == 11);
    if (!isAdmin) {
        return "redirect:/usermain"; // 일반 사용자는 접근 불가
    }

        model.addAttribute("departmentCount", departmentService.countDepartments());
        model.addAttribute("approvalCount", docManagerService.countDocs());
        model.addAttribute("boardCount", boardManagerService.countBoards());
        model.addAttribute("newUserCount", usersService.countToday());

        return "admin/admindashboard";
    }

    @GetMapping("/adboard")
    public String showAdminBoard(@ModelAttribute("loginUser") LoginUserDTO loginUser, Model model) {
        model.addAttribute("boards", boardManagerService.getAllBoardsBtn(loginUser.getDepartment_id())); // 게시판 버튼과 코드
        return "admin/adboard";
    }

    @GetMapping("/adboarddetail/{boardCode}/{postId}")
    public String showAdminAdBoardDetail(
            @PathVariable("boardCode") String boardCode,
            @PathVariable("postId") Long postId,
            @ModelAttribute("loginUser") LoginUserDTO loginUser,
            HttpSession session,
            Model model) {

        BoardContentDetailDTO post = boardManagerService.getUserBoardDetail(boardCode, postId, session, loginUser);
        model.addAttribute("post", post);

        Integer deptCode = loginUser.getDepartment_id();

        // 🔹 이전 / 다음 게시글 ID (단건)
        Long prevPostId = boardManagerService
                .findPrevPostIds(boardCode, postId, deptCode)
                .stream().findFirst().orElse(null);

        Long nextPostId = boardManagerService
                .findNextPostIds(boardCode, postId, deptCode)
                .stream().findFirst().orElse(null);

        model.addAttribute("prevPostId", prevPostId);
        model.addAttribute("nextPostId", nextPostId);

        return "admin/adboarddetail";
    }

    @GetMapping("/adboardedit/{boardCode}/{postId}")
    public String showAdminBoardEdit(
            @PathVariable("boardCode") String boardCode,
            @PathVariable("postId") Long postId,
            @ModelAttribute("loginUser") LoginUserDTO loginUser,
            HttpSession session,
            Model model) {

        model.addAttribute("boards", boardManagerService.getAllBoardsBtn(loginUser.getDepartment_id()));

        BoardContentDetailDTO post = boardManagerService.getUserBoardDetail(boardCode, postId, session, loginUser);
        model.addAttribute("post", post);

        return "admin/adboardedit";
    }

    @GetMapping("/adboardwrite")
    public String showAdminBoardWrite(@ModelAttribute("loginUser") LoginUserDTO loginUser, Model model) {
        model.addAttribute("boards", boardManagerService.getAllBoardsBtn(loginUser.getDepartment_id()));
        return "admin/adboardwrite";
    }

    // 관리자 코멘트
    @GetMapping("/adcomment")
    public String showAdminComment(
            @RequestParam(value = "boardCode", required = false) String boardCode,
            @RequestParam(value = "keyWordString", required = false) String keyWordString,
            @RequestParam(value = "keyWord", required = false) String keyWord,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @ModelAttribute("loginUser") LoginUserDTO loginUser,
            Model model) {

        try {
            if (loginUser == null) {
                throw new IllegalStateException("로그인이 필요합니다.");
            }

            Integer deptId = loginUser.getDepartment_id();

            List<BoardMangerDTO> boards = boardManagerService.getAllBoardsBtn(deptId);
            model.addAttribute("boards", boards);

            if (boards == null || boards.isEmpty()) {
                throw new IllegalArgumentException("현재 생성된 게시판이 없습니다.");
            }

            if (boardCode == null || boardCode.isBlank()) {
                throw new IllegalArgumentException("선택된 게시판이 없습니다.");
            }

            boolean boardExists = boards.stream()
                    .anyMatch(b -> b.getBoardCode().equals(boardCode));

            if (!boardExists) {
                throw new IllegalArgumentException("존재하지 않는 게시판입니다.");
            }

            if (page < 1)
                page = 1;
            if (size < 1 || size > 50)
                size = 10;

            // 댓글 조회
            List<CommentListDTO> comments = commentService.getCommentList(
                    boardCode, deptId, keyWordString, keyWord, page, size);
            model.addAttribute("comments", comments);

            // 전체 댓글 수 & 페이지 번호 생성
            int totalCount = commentService.getCommentTotalCount(
                    boardCode, deptId, keyWordString, keyWord);
            int totalPages = (int) Math.ceil((double) totalCount / size);

            List<Map<String, Object>> pages = new ArrayList<>();
            for (int i = 1; i <= totalPages; i++) {
                Map<String, Object> pageMap = new HashMap<>();
                pageMap.put("page", i);
                pageMap.put("active", i == page);
                pages.add(pageMap);
            }
            model.addAttribute("pages", pages);

            // 공통 정보
            model.addAttribute("currentBoardCode", boardCode);
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);

        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("comments", Collections.emptyList());
            model.addAttribute("currentBoardCode", null);
            model.addAttribute("pages", Collections.emptyList());

        } catch (Exception e) {
            model.addAttribute("errorMessage", "데이터를 불러오는 중 오류가 발생했습니다.");
            model.addAttribute("comments", Collections.emptyList());
            model.addAttribute("pages", Collections.emptyList());
        }

        return "admin/adcomment";
    }

    // @GetMapping("/adcommentdetail/{boardCode}/{postId}")
    // public String showAdminCommentDetail(
    // @PathVariable("boardCode") String boardCode,
    // @PathVariable("postId") Long postId,
    // @ModelAttribute("loginUser") LoginUserDTO loginUser,
    // Model model) {

    // // 댓글 상세를 원댓글+대댓글 구조로 가져오기
    // List<CommentGroupDTO> commentGroups = commentService.getCommentGroups(
    // boardCode,
    // postId,
    // loginUser.getDepartment_id());

    // if (!commentGroups.isEmpty()) {
    // model.addAttribute("commentGroups",
    // Collections.singletonList(commentGroups.get(0)));
    // } else {
    // model.addAttribute("commentGroups", Collections.emptyList());
    // }

    // //model.addAttribute("commentGroups", commentGroups);

    // return "admin/adcommentdetail";
    // }

    @GetMapping("/adcommentdetail/{boardCode}/{page}")
    public String showAdminCommentDetail(
            @PathVariable("boardCode") String boardCode,
            @PathVariable("page") int page,
            @ModelAttribute("loginUser") LoginUserDTO loginUser,
            Model model) {

        int deptId = loginUser.getDepartment_id();

        // 전체 댓글 그룹 조회
        List<CommentGroupDTO> allGroups = commentService.getCommentGroupsByBoard(boardCode, deptId);
        int totalGroups = allGroups.size();

        if (totalGroups == 0) {
            model.addAttribute("commentGroups", Collections.emptyList());
            model.addAttribute("boardCode", boardCode);
            model.addAttribute("postId", null);
            model.addAttribute("hasPrev", false);
            model.addAttribute("hasNext", false);
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("prevPostId", null);
            model.addAttribute("nextPostId", null);
            return "admin/adcommentdetail";
        }

        // 페이지 보정 (1-based)
        if (page < 1)
            page = 1;
        if (page > totalGroups)
            page = totalGroups;

        // 현재 페이지 댓글 그룹
        CommentGroupDTO currentGroup = allGroups.get(page - 1);
        model.addAttribute("commentGroups", Collections.singletonList(currentGroup));
        model.addAttribute("boardCode", boardCode);
        model.addAttribute("postId", currentGroup.getRoot().getPostId());

        // 이전/다음 댓글 그룹 계산
        Integer prevPage = (page > 1) ? page - 1 : null;
        Integer nextPage = (page < totalGroups) ? page + 1 : null;

        model.addAttribute("prevPage", prevPage);
        model.addAttribute("nextPage", nextPage);

        // 현재 페이지/총 페이지
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalGroups);

        return "admin/adcommentdetail";
    }

    @GetMapping("/adcommentedit/{boardCode}/{postId}")
    public String showAdminCommentDetailEdit(
            @PathVariable("boardCode") String boardCode,
            @PathVariable("postId") Long postId,
            @ModelAttribute("loginUser") LoginUserDTO loginUser,
            Model model) {

        // 댓글 상세를 원댓글+대댓글 구조로 가져오기
        List<CommentGroupDTO> commentGroups = commentService.getCommentGroups(
                boardCode,
                postId,
                loginUser.getDepartment_id());

        model.addAttribute("commentGroups", commentGroups);

        return "admin/adcommentedit";
    }

    @GetMapping("/addepartment")
    public String departmentPage(Model model) {
        List<Department> departments = departmentService.getAllDepartments();

        // '예외' 부서를 제외
        departments = departments.stream()
                .filter(dept -> !dept.getDepartmentName().equals("예외"))
                .collect(Collectors.toList());

        List<Map<String, Object>> deptList = new ArrayList<>();

        for (Department dept : departments) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", dept.getId());
            map.put("departmentName", dept.getDepartmentName());

            // 부서장 (position_id = 2 → 부장)
            User manager = usersRepository.findFirstByDepartmentIdAndPositionId(dept.getId(), 2);
            map.put("managerName", manager != null ? manager.getUserName() : "-");

            // 인원 수
            int memberCount = usersRepository.countByDepartmentId(dept.getId());
            map.put("memberCount", memberCount);

            deptList.add(map);
        }

        model.addAttribute("departments", deptList);
        return "admin/addepartment";
    }

    // 전자결재
    @GetMapping("/adapprovallist")
    public String showAdminApprovalList(@ModelAttribute("loginUser") LoginUserDTO loginUser, Model model) {
        // 부서별 문서 버튼
        model.addAttribute("docs", docManagerService.getAllDocsBtn(loginUser.getDepartment_id()));

        return "admin/adapprovallist";
    }

    @GetMapping("/adapprovallist/ajax")
    @ResponseBody
    public Map<String, Object> getDocListAjax(
            @RequestParam(name = "docCode") String docCode,
            @RequestParam(name = "status") String status,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @ModelAttribute("loginUser") LoginUserDTO loginUser) {

        if (docCode == null || docCode.trim().isEmpty()) {
            System.out.println("⚠ docCode가 없어 기본값으로 설정됨");
            docCode = "Draft";
        }

        int pageSize = 10;

        List<DocAdListDTO> docs = docManagerService.getDocList(docCode, loginUser.getDepartment_id(), status, page,
                pageSize);

        int totalDocs = docManagerService.getTotalDocCount(docCode, loginUser.getDepartment_id(), status);
        int totalPages = (int) Math.ceil((double) totalDocs / pageSize);

        Map<String, Object> response = new HashMap<>();
        response.put("docs", docs);
        response.put("currentPage", page);
        response.put("totalPages", totalPages);

        return response;
    }

    @GetMapping("/adpprovaldetail")
    public String showAdminApprovalDetail(
            @RequestParam("docCode") String docCode,
            @RequestParam("docId") Integer docId,
            @ModelAttribute("loginUser") LoginUserDTO loginUser,
            Model model) {

        // 문서 정보 조회
        DocDetailDTO docDetail = docManagerService.getDocById(docCode, docId);
        model.addAttribute("docdetail", docDetail);
        model.addAttribute("approvers", docDetail.getApprovers());

        List<Map<String, Object>> files = new ArrayList<>();

        List<DocFileDTO> fileDetails = docDetail.getFiles(); // 파일 목록 가져오기

        if (fileDetails != null) {
            for (DocFileDTO fileDetail : fileDetails) {
                Map<String, Object> file = new HashMap<>();
                file.put("path", fileDetail.getPath());
                file.put("name", fileDetail.getName());
                file.put("size", fileDetail.getSize());
                file.put("date", fileDetail.getCreatedDate());
                files.add(file);
            }
        }

        model.addAttribute("files", files);

        // 문서 코드
        model.addAttribute("docCode", docCode);

        return "admin/adpprovaldetail";
    }

    // 문서 서식 추가
    @GetMapping("/addocedit")
    public String showAdDocEdit(
            @ModelAttribute("loginUser") LoginUserDTO loginUser,
            Model model) {
        model.addAttribute("loginUser", loginUser);
        return "admin/addocedit";
    }

    @GetMapping("/adminsystem")
    public String ShowAdminSystem() {
        return "admin/adminsystem";
    }

    @GetMapping("/adminlog")
    public String showAdminLogPage(
            @ModelAttribute("loginUser") LoginUserDTO loginUser) {

        // 1️⃣ 로그인 체크
        if (loginUser == null) {
            return "redirect:/login";
        }

        // 2️⃣ 시스템 관리자 체크 (position_id = 6)
        Integer positionId = loginUser.getPosition_id();
        if (positionId == null || positionId != 6) {
            // 권한 없으면 관리자 대시보드 또는 메인으로
            return "redirect:/admindashboard";
            // 또는 return "redirect:/usermain";
        }

        return "admin/adminlog";
    }

}
