document.addEventListener("DOMContentLoaded", () => {
  const topLinks = document.querySelectorAll(".icon-top a");

  const chatUser = document.querySelector(".chat-user");
  const chatLink = document.getElementById("chat-view-status");

  if (chatUser && chatLink) {
    // 페이지 로드 시 상태 복원
    const savedState = localStorage.getItem("chatUserVisible") === "true";

    if (savedState) {
      chatUser.classList.add("active");
      chatLink.classList.add("active");
    }

    chatLink.addEventListener("click", (e) => {
      e.preventDefault();

      const isActive = chatUser.classList.toggle("active");
      chatLink.classList.toggle("active");

      localStorage.setItem("chatUserVisible", isActive);
    });
  }

  // 페이지 로드 시 localStorage에서 활성화된 링크 복원
  const savedHref = localStorage.getItem("activeLinkHref");
  if (savedHref) {
    topLinks.forEach((link) => {
      if (link.href === savedHref) {
        link.classList.add("focus");
      }
    });
  }

  topLinks.forEach((link) => {
    link.addEventListener("click", (e) => {
      // 모든 링크에서 focus 클래스 제거
      topLinks.forEach((l) => l.classList.remove("focus"));

      // 클릭한 링크에 focus 클래스 추가
      link.classList.add("focus");

      // 선택한 링크 href를 저장
      localStorage.setItem("activeLinkHref", link.href);
    });
  });

  // [chat-user card click 시 chat_modal.html 화면에 출력]
  const coWorkers = document.querySelectorAll(".coworker-card");
  coWorkers.forEach((card) => {
    card.addEventListener("click", () => {
      fetch("includes/chat_modal.html") // 문자열로 존재
        .then((response) => response.text())
        .then((html) => {
          const container = document.querySelector(".main-view");
          if (!container) {
            console.error("출력할 위치를 찾을 수 없습니다.");
            return;
          }
          const wrapper = document.createElement("div");

          Object.assign(wrapper.style, {
            position: "absolute",
            top: "100px",
            left: "100px",
            width: "360px", // ✅ CSS와 동일하게 고정
            height: "600px", // ✅ CSS와 동일하게 고정
            borderRadius: "8px",
            backgroundColor: "white",
            cursor: "move",
            boxSizing: "border-box",
            overflow: "hidden", // ✅ 전체 스크롤 제거 (스크롤은 chat-messages에서만)
            display: "flex", // ✅ 내부 flex layout 지원
            flexDirection: "column",
            fontFamily: "'Segoe UI', sans-serif",
            boxShadow: "0 4px 12px rgba(0, 0, 0, 0.15)",
            zIndex: "1000",
          });

          // HTML 삽입
          wrapper.innerHTML = html;
          document.body.appendChild(wrapper); // DOM에 붙이기

          // 모달 드래그 기능
          let isDragging = false;
          let startX, startY, origX, origY;

          const chatHeader = wrapper.querySelector(".chatroom-header");
          const chatMessages = wrapper.querySelector(".chat-messages");

          [chatHeader, chatMessages].forEach((el) => {
            el.addEventListener("mousedown", (e) => {
              isDragging = true;
              startX = e.clientX;
              startY = e.clientY;
              origX = parseInt(wrapper.style.left, 10);
              origY = parseInt(wrapper.style.top, 10);
              e.preventDefault();
            });
          });

          [chatHeader, chatMessages].forEach((el) => {
            el.addEventListener("mousemove", (e) => {
              if (!isDragging) return;
              const deltaX = e.clientX - startX;
              const deltaY = e.clientY - startY;
              wrapper.style.left = origX + deltaX + "px";
              wrapper.style.top = origY + deltaY + "px";
            });
          });

          [chatHeader, chatMessages].forEach((el) => {
            el.addEventListener("mouseup", (e) => {
              isDragging = false;
            });
          });

          // 모달 내부 요소는 wrapper가 DOM에 붙은 후에 선택해야 동작함
          const chatForm = wrapper.querySelector(".chat-input");
          const input = wrapper.querySelector("#chat-input-text");
          const closeBtn = wrapper.querySelector("#backChatMotal");

          if (!chatForm || !input || !chatMessages) {
            console.error("모달 내부 요소를 찾을 수 없습니다.");
            return;
          }

          // form submit 이벤트 연결
          chatForm.addEventListener("submit", (e) => {
            e.preventDefault();
            sendMessage();
          });

          // 모달 닫기
          if (closeBtn) {
            closeBtn.addEventListener("click", () => {
              wrapper.remove();
            });
          }
        })
        .catch((err) => {
          console.error("모달 로딩 실패:", err);
        });
    });
  });

  // 채팅방 생성
  const newChatBtn = document.querySelector(".newChatroom");

  newChatBtn.addEventListener("click", () => {
    if (document.querySelector(".newChatModal")) {
      console.warn("이미 채팅 생성 모달이 열려 있습니다.");
      return;
    }

    fetch("includes/new_chat.html")
      .then((response) => response.text())
      .then((html) => {
        const container = document.querySelector(".main-view, .main-chat-view");
        if (!container) {
          console.error("출력할 위치를 찾을 수 없습니다.");
          return;
        }
        const wrapper = document.createElement("div");
        Object.assign(wrapper.style, {
          position: "absolute",
          top: "100px",
          left: "100px",
          width: "340px",
          height: "330px",
          borderRadius: "8px",
          cursor: "move",
          boxSizing: "border-box",
          boxShadow: "0 4px 12px rgba(0, 0, 0, 0.15)",
          zIndex: "1000",
        });

        wrapper.innerHTML = html;
        document.body.appendChild(wrapper); 

        let isDragging = false;
        let startX, startY, origX, origY;

        const newChatModal = wrapper.querySelector(".newChatModal");
        if (newChatModal) {
          newChatModal.addEventListener("mousedown", (e) => {
            isDragging = true;
            startX = e.clientX;
            startY = e.clientY;
            origX = parseInt(wrapper.style.left, 10) || 0;
            origY = parseInt(wrapper.style.top, 10) || 0;
            e.preventDefault();
          });

          document.addEventListener("mousemove", (e) => {
            if (!isDragging) return;
            const deltaX = e.clientX - startX;
            const deltaY = e.clientY - startY;
            wrapper.style.left = origX + deltaX + "px";
            wrapper.style.top = origY + deltaY + "px";
          });

          document.addEventListener("mouseup", () => {
            isDragging = false;
          });
        } else {
          console.warn("드래그할 .modal 요소를 찾을 수 없습니다.");
        }

        const newChatRoomForm = document.getElementById("addBtn");
        // form submit 이벤트 연결
        newChatRoomForm.addEventListener("submit", (e) => {
          e.preventDefault();
          newChatroom();
        });

        const closeBtn = document.getElementById("cancelBtn");
        // 모달 닫기
        if (closeBtn) {
          closeBtn.addEventListener("click", () => {
            wrapper.remove();
          });
        }
      });
  });
});

// ***[함수 영역]***
function sendMessage() {
  // 채팅 입력창
  const input = document.getElementById("chat-input-text");
  const text = input.value.trim();
  if (!text) return;

  console.log("text: " + text);

  // 채팅창 전체
  const chatMessages = document.querySelector(".chat-messages");

  // 새 메시지 요소 생성
  const messageDiv = document.createElement("div");
  // <div class="message sent">내용</div>
  messageDiv.classList.add("message", "sent"); // messageDiv라는 요소에 "message"와 "sent"라는 두 개의 클래스를 추가하는 역할

  // 메시지 내용
  const messageText = document.createElement("p");
  messageText.textContent = text;

  // 시간 표시 내용
  const timeSpan = document.createElement("span");
  timeSpan.className = "time";

  // 현재 시간 표시 (HH:MM AM/PM)
  const now = new Date();
  let hours = now.getHours();
  const minutes = now.getMinutes().toString().padStart(2, "0");
  const ampm = hours >= 12 ? "PM" : "AM";
  hours = hours % 12 || 12;
  timeSpan.textContent = `${hours}:${minutes} ${ampm}`;

  // 데이터 입히기
  messageDiv.appendChild(messageText);
  messageDiv.appendChild(timeSpan);

  // 채팅창에 메시지 붙이기
  chatMessages.appendChild(messageDiv);

  // 스크롤을 제일 아래로 내림
  chatMessages.scrollTop = chatMessages.scrollHeight;

  // 입력창 초기화 및 포커스 유지
  input.value = "";
  input.focus();
}

// 새로운 채팅방 생성 함수
function newChatroom() {}
