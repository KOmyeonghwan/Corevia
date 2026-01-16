package com.example.corenet.showpagecont;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.example.corenet.admin.board.dto.BoardContentDetailDTO;
import com.example.corenet.admin.board.serv.BoardManagerService;
import com.example.corenet.admin.doc.dto.DocDetailDTO;
import com.example.corenet.admin.doc.dto.DocFileDTO;
import com.example.corenet.admin.doc.dto.DocUserListDTO;
import com.example.corenet.admin.doc.serv.DocManagerService;
import com.example.corenet.admin.user.service.UsersService;
import com.example.corenet.client.mypage.service.MypageService;
import com.example.corenet.client.notification.dto.NotificationDTO;
import com.example.corenet.client.notification.serv.NotificationService;
import com.example.corenet.common.dto.LoginUserDTO;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserShowPageController {

    private final UsersService usersService;

    private final BoardManagerService boardManagerService;
    private final DocManagerService docManagerService;
    private final NotificationService noticeService;

    private final MypageService mypageService;

    UserShowPageController(BoardManagerService boardManagerService, DocManagerService docManagerService,
            NotificationService noticeService, UsersService usersService, MypageService mypageService) {
        this.boardManagerService = boardManagerService;
        this.docManagerService = docManagerService;
        this.noticeService = noticeService;
        this.usersService = usersService;
        this.mypageService = mypageService;
    }

    @GetMapping("/usermain")
    public String showUserMainPage(@ModelAttribute("loginUser") LoginUserDTO loginUser, Model model) {
        if (loginUser == null) {
            return "redirect:/login";
        }

        // Integer posLevel = loginUser.getPositionLevel();

        // // posLevel 0~3, 10~11은 사용자 페이지 접근 허용
        // boolean isAdminRestricted = (posLevel != null)
        // && !((posLevel >= 0 && posLevel <= 3) || (posLevel == 10 || posLevel == 11));

        // if (isAdminRestricted) {
        // return "redirect:/admindashboard"; // 관리자 전용 페이지
        // }

        // 알림 가져오기
        Integer userPk = loginUser.getUserPk();
        List<NotificationDTO> noticeList = noticeService.getMainNotices(userPk);
        model.addAttribute("noticeList", noticeList);
        model.addAttribute("userPk", userPk);

        // 오늘의 사원 3명
        model.addAttribute("todayUsers", usersService.getTodayUsers());

        return "user/main";
    }

    // 사용자 날씨
    @GetMapping("/userweather")
    public String showUserMessage(@ModelAttribute("loginUser") LoginUserDTO loginUser) {
        if (loginUser == null)
            return "redirect:/login";

        return "user/weather";
    }

    // 사용자 스케쥴
    @GetMapping("/userschedule")
    public String showUserSchedule(@ModelAttribute("loginUser") LoginUserDTO loginUser, Model model) {
        if (loginUser == null)
            return "redirect:/login";

        // 알림 --> fragments에서 사용
        Integer userPk = (loginUser != null && loginUser.getUserPk() != null) ? loginUser.getUserPk() : null;
        model.addAttribute("userPk", userPk);

        return "user/schedule";
    }

    // 사용자 보드
    @GetMapping("/userboard")
    public String showUserBoard(@ModelAttribute("loginUser") LoginUserDTO loginUser, Model model) {
        if (loginUser == null)
            return "redirect:/login";

        model.addAttribute("boards", boardManagerService.getAllBoardsBtn(loginUser.getDepartment_id())); // 게시판 버튼과 코드
                                                                                                         // 전달

        // 알림 --> fragments에서 사용
        Integer userPk = (loginUser != null && loginUser.getUserPk() != null) ? loginUser.getUserPk() : null;
        model.addAttribute("userPk", userPk);

        return "user/board";
    }

    // 사용자 보드 디테일(게시글 상세)
    @GetMapping("/userboarddetail/{boardCode}/{id}")
    public String getUserBoardDetail(
            @PathVariable("boardCode") String boardCode,
            @PathVariable("id") Long id,
            @ModelAttribute("loginUser") LoginUserDTO loginUser,
            HttpSession session,
            Model model) {

        // 로그인 체크
        if (loginUser == null) {
            return "redirect:/login";
        }

        // 게시글 상세 조회
        BoardContentDetailDTO post = boardManagerService.getUserBoardDetail(boardCode, id, session, loginUser);
        model.addAttribute("post", post);

        // 작성자 여부 판단
        boolean isAuthor = loginUser.getUserPk().equals(post.getUserId());
        model.addAttribute("isAuthor", isAuthor);

        // 알림 --> fragments에서 사용
        Integer userPk = (loginUser != null && loginUser.getUserPk() != null) ? loginUser.getUserPk() : null;
        model.addAttribute("userPk", userPk);
        
        model.addAttribute("boardCode", boardCode);
        model.addAttribute("boardId", id);

        return "user/board-detail";
    }

    // 사용자 보드 디테일
    @GetMapping("/userboarddetailedit/{boardCode}/{id}")
    public String showUserBoardDetailEdit(@PathVariable("boardCode") String boardCode,
            @PathVariable("id") Long id,
            @ModelAttribute("loginUser") LoginUserDTO loginUser,
            HttpSession session,
            Model model) {
        if (loginUser == null)
            return "redirect:/login";

        // 게시글 상세 조회
        BoardContentDetailDTO post = boardManagerService.getUserBoardDetail(boardCode, id, session, loginUser);
        model.addAttribute("post", post);

        // 알림 --> fragments에서 사용
        Integer userPk = (loginUser != null && loginUser.getUserPk() != null) ? loginUser.getUserPk() : null;
        model.addAttribute("userPk", userPk);

        return "user/board-detail-edit";
    }

    // 사용자 전자결재
    @GetMapping("/userdoc")
    public String showUserDoc(
            @ModelAttribute("loginUser") LoginUserDTO loginUser,
            Model model) {

        if (loginUser == null) {
            return "redirect:/login";
        }

        Integer userPk = loginUser.getUserPk();
        model.addAttribute("userPk", userPk);

        // 문서 버튼 목록
        model.addAttribute(
                "docs",
                docManagerService.getAllDocsBtn(loginUser.getDepartment_id()));

        return "user/doc";
    }

    @GetMapping("/userdoc/ajax")
    @ResponseBody
    public Map<String, Object> showUserDocAjax(
            @ModelAttribute("loginUser") LoginUserDTO loginUser,
            @RequestParam(name = "docType", required = false) String docType,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "status", required = false) String status) {

        if (loginUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }

        // docType 방어
        if (docType == null || docType.isBlank()) {
            return Map.of(
                    "doclist", List.of(),
                    "currentPage", page,
                    "totalPages", 0);
        }

        // 문서 목록
        List<DocUserListDTO> doclist = docManagerService.getUserDocList(
                loginUser.getJobcode(),
                docType,
                loginUser.getDepartment_id(),
                status.toLowerCase(),
                page,
                pageSize);

        // 전체 개수
        int totalDocs = docManagerService.getTotalUserDocCount(
                loginUser.getJobcode(),
                docType,
                loginUser.getDepartment_id(),
                status.toLowerCase());

        int totalPages = (int) Math.ceil((double) totalDocs / pageSize);

        // System.out.println("totalDocs > : " + totalDocs);
        // System.out.println("totalPages > : " + totalPages);

        Map<String, Object> response = new HashMap<>();
        response.put("doclist", doclist);
        response.put("currentPage", page);
        response.put("totalPages", totalPages);

        return response;
    }

    // 사용자 전자결재 디테일
    @GetMapping("/userdocdetail")
    public String showUserDocDetail(@ModelAttribute("loginUser") LoginUserDTO loginUser, Model model) {
        if (loginUser == null)
            return "redirect:/login";

        // 알림 --> fragments에서 사용
        Integer userPk = (loginUser != null && loginUser.getUserPk() != null) ? loginUser.getUserPk() : null;
        model.addAttribute("userPk", userPk);

        return "user/doc-detail";
    }

    @GetMapping("/userdocdetail/{docId}")
    public String showUserDocDetail(
            @ModelAttribute("loginUser") LoginUserDTO loginUser,
            @RequestParam(value = "docType", defaultValue = "Draft") String docType,
            @PathVariable("docId") Integer docId,
            @RequestParam(value = "status", required = false, defaultValue = "DRAFT") String status,
            Model model) {

        if (loginUser == null) {
            return "redirect:/login";
        }

        // 알림용
        Integer userPk = loginUser.getUserPk();
        model.addAttribute("userPk", userPk);

        model.addAttribute("docType", docType);
        model.addAttribute("docId", docId);

        // 문서 정보 조회
        DocDetailDTO docDetail = docManagerService.getDocById(docType, docId);
        model.addAttribute("docdetail", docDetail);
        model.addAttribute("approvers", docDetail.getApprovers());
        model.addAttribute("docStatus", docDetail.getDocStatus());

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

        return "user/doc-detail";
    }

    // 사용자 전자결재 edit
    @GetMapping("/userdocedit")
    public String showUserDocEdit(@ModelAttribute("loginUser") LoginUserDTO loginUser, Model model) {
        if (loginUser == null)
            return "redirect:/login";
        model.addAttribute("docs", docManagerService.getAllDocsBtn(loginUser.getDepartment_id()));
        model.addAttribute("teamLeader", docManagerService.getTeamLeaderData(loginUser.getDepartment_id()));

        // 알림 --> fragments에서 사용
        Integer userPk = (loginUser != null && loginUser.getUserPk() != null) ? loginUser.getUserPk() : null;
        model.addAttribute("userPk", userPk);

        return "user/doc-edit";
    }

    @GetMapping("/userdocdetailedit/{docType}/{docId}")
    public String showUserDocDetailEdit(
            @ModelAttribute("loginUser") LoginUserDTO loginUser,
            @PathVariable("docType") String docType,
            @PathVariable("docId") Integer docId,
            Model model) {
        if (loginUser == null) {
            return "redirect:/login";
        }

        /* ================= 공통 ================= */
        model.addAttribute("docs",
                docManagerService.getAllDocsBtn(loginUser.getDepartment_id()));

        model.addAttribute("teamLeader",
                docManagerService.getTeamLeaderData(loginUser.getDepartment_id()));

        Integer userPk = loginUser.getUserPk();
        model.addAttribute("userPk", userPk);

        model.addAttribute("docType", docType);
        model.addAttribute("docId", docId);

        /* ================= 문서 상세 ================= */
        DocDetailDTO docDetail = docManagerService.getDocById(docType, docId);
        model.addAttribute("docdetail", docDetail);
        model.addAttribute("approvers", docDetail.getApprovers());
        model.addAttribute("docStatus", docDetail.getDocStatus());

        /* ================= 첨부파일 ================= */
        List<Map<String, Object>> files = new ArrayList<>();

        if (docDetail.getFiles() != null) {
            for (DocFileDTO fileDetail : docDetail.getFiles()) {
                Map<String, Object> file = new HashMap<>();
                file.put("path", fileDetail.getPath());
                file.put("name", fileDetail.getName());
                file.put("size", fileDetail.getSize());
                file.put("date", fileDetail.getCreatedDate());
                files.add(file);
            }
        }

        model.addAttribute("files", files);

        return "user/doc-detail-edit";
    }

    // ==================

    // 채탕 모달
    @GetMapping("/chat-modal")
    public String showChatModal(
        @ModelAttribute("loginUser") LoginUserDTO loginUser, 
        @RequestParam("roomId") Integer roomId,
        HttpSession session,
        Model model
    ) {
        
        // 모델에 roomId 전달 (뷰에서 모달을 렌더링할 때 사용)
        model.addAttribute("roomId", roomId);
        model.addAttribute("loginUserName", loginUser.getUserName());
        model.addAttribute("loginUserId", loginUser.getUserPk());
        
        return "user/includes/chat_modal";
    }

    // 뉴 채팅
    @GetMapping("/new_chat")
    public String showNewChat() {
        // 같은 부서 사람들 이름 가져와야 함
        return "user/includes/new_chat";
    }

    // ===================

    @GetMapping("/user-mypage")
    public String showmyPage(@ModelAttribute("loginUser") LoginUserDTO loginUser, Model model,
            @RequestParam(value = "forcePwChange", required = false) Boolean forcePwChange) {
        if (loginUser == null) {
            return "redirect:/login";
        }

        Integer boardCount = mypageService.getBoardTotal(loginUser.getDepartment_id(), loginUser.getUserPk());
        Integer commentCount = mypageService.getCommentTotal(loginUser.getDepartment_id(), loginUser.getUserPk());
        Integer docCount = mypageService.getDocTotal(loginUser.getJobcode());

        model.addAttribute("postCount", boardCount);
        model.addAttribute("commentCount", commentCount);
        model.addAttribute("docCount", docCount);
        model.addAttribute("forcePwChange", forcePwChange != null && forcePwChange);

        return "user/user-mypage";
    }

}
