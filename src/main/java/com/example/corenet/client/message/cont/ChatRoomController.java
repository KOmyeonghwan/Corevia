package com.example.corenet.client.message.cont;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.corenet.client.message.Serv.ChatRoomService;
import com.example.corenet.common.dto.ChatRoomDTO;
import com.example.corenet.common.dto.LoginUserDTO;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/chat")
public class ChatRoomController {

    @Autowired
    private ChatRoomService chatRoomService;

    @GetMapping("/my-rooms")
    public List<ChatRoomDTO> getRooms(HttpSession session) {
        LoginUserDTO loginUser = (LoginUserDTO) session.getAttribute("loginUser");
        if (loginUser == null) {
            return List.of(); // 로그인 안 됨
        }

        Long currentUserId = loginUser.getUserPk().longValue();
        return chatRoomService.getChatRooms(currentUserId);
    }

    // 채팅방 삭제
    @PostMapping("/delete")
    public ResponseEntity<?> deleteChatRoom(@RequestParam("roomId") Long roomId, HttpSession session) {
        // 로그인 사용자 확인
        LoginUserDTO loginUser = (LoginUserDTO) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다");
        }

        boolean isDeleted = chatRoomService.deleteChatRoom(roomId, loginUser.getUserPk().longValue());
        if (isDeleted) {
            return ResponseEntity.ok("채팅방 삭제 완료");
        } else {
            return ResponseEntity.status(400).body("채팅방 삭제 실패");
        }
    }

}
