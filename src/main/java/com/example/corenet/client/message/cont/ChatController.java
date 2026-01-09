package com.example.corenet.client.message.cont;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.corenet.admin.user.service.UsersService;
import com.example.corenet.client.message.Serv.ChatService;
import com.example.corenet.common.dto.ChatMessageDTO;
import com.example.corenet.common.dto.ChatMessageRequest;
import com.example.corenet.common.dto.LoginUserDTO;
import com.example.corenet.entity.ChatRoom;
import com.example.corenet.entity.ChatMessage;
import com.example.corenet.entity.User;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;
    private final UsersService usersService;

    /** 채팅방 생성 (1:1 or 그룹) */
    @PostMapping("/create")
    public ResponseEntity<?> createChatRoom(
            @RequestBody List<Integer> userIds,
            HttpSession session) {

        // 로그인 사용자 확인
        LoginUserDTO loginUser = (LoginUserDTO) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다");
        }

        // 채팅방 생성 (로그인 사용자 자동 포함)
        ChatRoom room = chatService.createChatRoom(userIds, loginUser.getUserPk().longValue());

        return ResponseEntity.ok(room);
    }

    /** 메시지 조회 */
    @GetMapping("/messages/{roomId}")
    public ResponseEntity<?> getMessages(@PathVariable("roomId") Long roomId) {
        List<ChatMessage> messages = chatService.getMessages(roomId);

        // senderName 포함 DTO 변환
        List<ChatMessageDTO> dtoList = messages.stream().map(msg -> {
            String senderName = usersService.findById(msg.getSenderId())
                    .map(User::getUserName)
                    .orElse("사용자");
            return new ChatMessageDTO(
                    msg.getId(),
                    msg.getSenderId(),
                    senderName,
                    msg.getMessage(),
                    msg.getCreatedAt());
        }).toList();

        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/search-user")
    public ResponseEntity<?> searchUser(@RequestParam("keyword") String keyword) {
        List<User> users = usersService.findByUserName(keyword);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/rooms")
    public ResponseEntity<?> getChatRooms() {
        // 예: 로그인 사용자 기준으로 모든 채팅방 가져오기
        List<ChatRoom> rooms = chatService.getAllChatRooms(); // chatService에 구현
        return ResponseEntity.ok(rooms);
    }

    @PostMapping("/message")
    public ResponseEntity<?> sendMessage(
            @RequestBody ChatMessageRequest req,
            HttpSession session) {

        // 세션에서 LoginUserDTO 가져오기
        LoginUserDTO loginUser = (LoginUserDTO) session.getAttribute("loginUser");

        if (loginUser == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다");
        }

        // ChatService 호출
        ChatMessage saved = chatService.sendMessage(
                req.getRoomId(),
                loginUser.getUserPk().longValue(), // Integer → Long 변환
                req.getMessage());

        return ResponseEntity.ok(saved);
    }

    @GetMapping("/my-info")
    public Map<String, Object> getLoginUser(HttpSession session) {
        LoginUserDTO loginUser = (LoginUserDTO) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Map.of("id", null, "userName", "게스트");
        }
        return Map.of("id", loginUser.getUserPk(), "userName", loginUser.getUserName());
    }

}
