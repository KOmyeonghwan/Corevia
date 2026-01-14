document.addEventListener("DOMContentLoaded", () => {
  let loginUserId = null; // 로그인 사용자 ID
  let currentRoomId = null;
  let stompClient = null;
  let senderName = null;

  const csrfToken = document.querySelector('meta[name="_csrf"]').content;
  const csrfHeader = document.querySelector(
    'meta[name="_csrf_header"]'
  ).content;

  // ===================== 함수 영역 =====================
  // 새로운 채팅방 생성 모달 열기
  function openNewChatModal() {
    if (!loginUserId) return console.error("로그인 정보 없음: 채팅 생성 불가");

    if (document.querySelector(".newChatModal")) {
      console.warn("이미 채팅 생성 모달이 열려 있습니다.");
      return;
    }

    fetch("/new_chat")
      .then((res) => res.text())
      .then((html) => {
        const wrapper = document.createElement("div");
        Object.assign(wrapper.style, {
          position: "absolute",
          top: "100px",
          left: "100px",
          width: "400px",
          height: "450px",
          borderRadius: "8px",
          cursor: "move",
          boxSizing: "border-box",
          boxShadow: "0 4px 12px rgba(0,0,0,0.15)",
          zIndex: "1000",
        });
        wrapper.innerHTML = html;
        document.body.appendChild(wrapper);

        const newChatModal = wrapper.querySelector(".newChatModal");
        const searchBtn = wrapper.querySelector("#searchBtn");
        const searchInput = wrapper.querySelector("#searchInput");
        const userSelect = wrapper.querySelector("#userSelect");
        const addBtn = wrapper.querySelector("#addBtn");
        const cancelBtn = wrapper.querySelector("#cancelBtn");

        // 드래그 기능
        let isDragging = false,
          startX,
          startY,
          origX,
          origY;
        newChatModal?.addEventListener("mousedown", (e) => {
          if (e.target.closest("input, textarea, select")) return;
          isDragging = true;
          startX = e.clientX;
          startY = e.clientY;
          origX = parseInt(wrapper.style.left, 10) || 0;
          origY = parseInt(wrapper.style.top, 10) || 0;
          e.preventDefault();
        });
        document.addEventListener("mousemove", (e) => {
          if (!isDragging) return;
          wrapper.style.left = origX + (e.clientX - startX) + "px";
          wrapper.style.top = origY + (e.clientY - startY) + "px";
        });
        document.addEventListener("mouseup", () => {
          isDragging = false;
        });

        // 유저 검색
        searchBtn.addEventListener("click", () => {
          const keyword = searchInput.value.trim();
          if (!keyword) return;
          fetch(`/chat/search-user?keyword=${encodeURIComponent(keyword)}`)
            .then((res) => res.json())
            .then((users) => {
              userSelect.innerHTML = "";
              users.forEach((u) => {
                const option = document.createElement("option");
                option.value = u.id;
                let text = `${u.userName} (사번: ${u.jobcode})`;
                if (u.position) text += `, ${u.position.positionTitle}`;
                if (u.department) text += `, ${u.department.departmentName}`;
                option.textContent = text;
                userSelect.appendChild(option);
              });
            });
        });

        // 채팅방 생성
        addBtn.addEventListener("click", () => {
          const selectedUsers = Array.from(userSelect.selectedOptions).map(
            (opt) => Number(opt.value)
          );
          if (!selectedUsers.length) return alert("참여자를 선택하세요");
          const csrfToken = document
            .querySelector('meta[name="_csrf"]')
            .getAttribute("content");
          const csrfHeader = document
            .querySelector('meta[name="_csrf_header"]')
            .getAttribute("content");
          fetch("/chat/create", {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              [csrfHeader]: csrfToken,
            },
            body: JSON.stringify(selectedUsers),
          })
            .then((res) => res.json())
            .then((data) => {
              console.log("채팅방 생성 완료:", data);
              wrapper.remove();
              loadChatRooms();
            });
        });

        cancelBtn.addEventListener("click", () => wrapper.remove());
      })
      .catch((err) => console.error("모달 로딩 실패:", err));
  }

  // 채팅방 목록 불러오기
  function loadChatRooms() {
    if (!loginUserId) return;

    fetch("/chat/my-rooms")
      .then((res) => res.json())
      .then((chatRooms) => {
        const container = document.querySelector(".chat-content");
        if (!container) return;
        container.innerHTML = ""; // 기존 채팅방 내용 초기화

        chatRooms.forEach((room) => {
          const card = document.createElement("div");
          card.className = "coworker-card";
          card.dataset.roomId = room.id;

          // 채팅방 이름
          const nameSpan = document.createElement("span");
          nameSpan.innerHTML = `<b>${room.roomName}</b>`;
          card.appendChild(nameSpan);

          // 삭제 버튼
          const delBtn = document.createElement("button");
          delBtn.textContent = "삭제";
          delBtn.className = "delete-btn";

          // 삭제 버튼 클릭 시 채팅방 삭제 처리
          delBtn.addEventListener("click", (e) => {
            e.stopPropagation(); // 카드 클릭 이벤트 막기
            const csrfToken = document
              .querySelector('meta[name="_csrf"]')
              .getAttribute("content");
            const csrfHeader = document
              .querySelector('meta[name="_csrf_header"]')
              .getAttribute("content");
            if (confirm("정말 채팅방을 삭제하시겠습니까?")) {
              fetch(`/chat/delete?roomId=${room.id}`, {
                method: "POST",
                headers: {
                  "Content-Type": "application/json",
                  [csrfHeader]: csrfToken,
                },
              })
                .then((res) => res.text())
                .then((responseText) => {
                  card.remove();
                })
                .catch((err) => {
                  console.error("채팅방 삭제 실패:", err);
                });
            }
          });

          // 삭제 버튼을 span 밖에 추가
          card.appendChild(delBtn);

          // 카드 클릭 이벤트 (채팅 열기)
          card.addEventListener("click", () => openChatModal(room.id));

          // 채팅방 카드 컨테이너에 추가
          container.appendChild(card);
        });
      })
      .catch((err) => {
        console.error("채팅방 목록 로딩 실패:", err);
      });
  }


  // ===================== WebSocket =====================
  const connectWS = (roomId, onMessageReceived) => {
    if (stompClient && stompClient.connected) stompClient.disconnect();

    const socket = new SockJS("/ws/chat");
    stompClient = Stomp.over(socket);

    stompClient.connect(
      {},
      (frame) => {
        console.log("Connected:", frame);
        stompClient.subscribe(`/room/${roomId}`, (msg) => {
          const message = JSON.parse(msg.body);
          if (onMessageReceived) onMessageReceived(message);
        });
      },
      (err) => console.error("STOMP error:", err)
    );
  };

  const renderMessage = (message, container) => {
    const div = document.createElement("div");
    div.className = `message ${
      message.senderId === loginUserId ? "sent" : "received"
    }`;
    if (message.senderId !== loginUserId) {
      const sender = document.createElement("div");
      sender.className = "sender-name";
      sender.textContent = message.senderName || "사용자";
      div.appendChild(sender);
    }
    const p = document.createElement("p");
    p.textContent = message.message;
    const span = document.createElement("span");
    span.className = "time";
    span.textContent = new Date(
      message.createdAt || Date.now()
    ).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
    div.appendChild(p);
    div.appendChild(span);
    container.appendChild(div);
    container.scrollTop = container.scrollHeight;
  };

  const sendWSMessage = (roomId, userName, text) => {
    if (!stompClient || !stompClient.connected) return;
    const message = {
      roomId,
      senderId: loginUserId,
      senderName: userName,
      message: text,
      type: "CHAT",
    };
    stompClient.send(`/send/chat/${roomId}`, {}, JSON.stringify(message));
  };

  // ===================== 채팅 모달 =====================

  // 채팅 모달 열기
  function openChatModal(roomId) {
    if (!loginUserId) return console.error("로그인 정보 없음: 채팅 불가");

    fetch(`/chat-modal?roomId=${roomId}`)
      .then((res) => res.text())
      .then((html) => {
        const wrapper = document.createElement("div");
        Object.assign(wrapper.style, {
          position: "absolute",
          top: "100px",
          left: "100px",
          width: "360px",
          height: "600px",
          borderRadius: "8px",
          backgroundColor: "white",
          cursor: "move",
          overflow: "hidden",
          display: "flex",
          flexDirection: "column",
          boxShadow: "0 4px 12px rgba(0,0,0,0.15)",
          zIndex: "1000",
        });
        wrapper.innerHTML = html;
        document.body.appendChild(wrapper);

        currentRoomId = roomId;
        const chatMessages = wrapper.querySelector(".chat-messages");
        const chatForm = wrapper.querySelector("#chat-form");
        const chatInput = wrapper.querySelector("#chat-input-text");

        senderName = chatInput.dataset.username;
        senderId = chatInput.dataset.id;

        // WebSocket 연결
        connectWS(roomId, (msg) => renderMessage(msg, chatMessages));

        // 기존 메시지 불러오기
        fetch(`/chat/messages/${roomId}`)
          .then((res) => res.json())
          .then((messages) => {
            chatMessages.innerHTML = "";
            messages.forEach((msg) => {
              const div = document.createElement("div");
              div.className = `message ${
                msg.senderId === loginUserId ? "sent" : "received"
              }`;
              if (msg.senderId !== loginUserId) {
                const sender = document.createElement("div");
                sender.className = "sender-name";
                sender.textContent = msg.senderName || "사용자";
                div.appendChild(sender);
              }
              const p = document.createElement("p");
              p.textContent = msg.message;
              const span = document.createElement("span");
              span.className = "time";
              span.textContent = new Date(msg.createdAt).toLocaleTimeString(
                [],
                { hour: "2-digit", minute: "2-digit" }
              );
              div.appendChild(p);
              div.appendChild(span);
              chatMessages.appendChild(div);
            });
            chatMessages.scrollTop = chatMessages.scrollHeight;
          });

        // 전송
        const send = () => {
          const text = chatInput.value.trim();
          if (!text) return;
          sendWSMessage(roomId, senderName, text);
          chatInput.value = "";
        };
        chatForm.addEventListener("submit", (e) => {
          e.preventDefault();
          send();
        });
        chatInput.addEventListener("keydown", (e) => {
          if (e.key === "Enter" && !e.shiftKey) {
            e.preventDefault();
            send();
          }
        });

        const closeBtn = wrapper.querySelector("#backChatMotal");
        closeBtn?.addEventListener("click", () => {
          // 소켓 연결 종료
          if (stompClient && stompClient.connected) {
            stompClient.disconnect(() => {
              console.log("WebSocket connection closed.");
            });
          }
          wrapper.remove();
        });

        // ESC 키로 모달 닫기
        const escListener = (e) => {
          if (e.key === "Escape") {
            // 소켓 연결 종료
            if (stompClient && stompClient.connected) {
              stompClient.disconnect(() => {
                console.log("WebSocket connection closed.");
              });
            }
            wrapper.remove();
            document.removeEventListener("keydown", escListener);
          }
        };
        document.addEventListener("keydown", escListener);

        // 드래그 기능
        let isDragging = false;
        let offsetX = 0,
          offsetY = 0;

        const header = wrapper.querySelector(".chat-header") || wrapper; // 헤더 없으면 전체 div
        header.addEventListener("mousedown", (e) => {
          isDragging = true;
          offsetX = e.clientX - wrapper.offsetLeft;
          offsetY = e.clientY - wrapper.offsetTop;
          wrapper.style.cursor = "grabbing";
        });

        document.addEventListener("mousemove", (e) => {
          if (isDragging) {
            wrapper.style.left = `${e.clientX - offsetX}px`;
            wrapper.style.top = `${e.clientY - offsetY}px`;
          }
        });

        document.addEventListener("mouseup", () => {
          if (isDragging) {
            isDragging = false;
            wrapper.style.cursor = "move";
          }
        });
      })
      .catch((err) => console.error("채팅 모달 로딩 실패:", err));
  }

  // ===================== 페이지 초기화 =====================
  function initPage() {
    const topLinks = document.querySelectorAll(".icon-top a");
    const chatUser = document.querySelector(".chat-user");
    const chatLink = document.getElementById("chat-view-status");

    if (chatUser && chatLink) {
      if (localStorage.getItem("chatUserVisible") === "true") {
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

    const savedHref = localStorage.getItem("activeLinkHref");
    if (savedHref)
      topLinks.forEach((l) => {
        if (l.href === savedHref) l.classList.add("focus");
      });
    topLinks.forEach((l) =>
      l.addEventListener("click", () => {
        topLinks.forEach((link) => link.classList.remove("focus"));
        l.classList.add("focus");
        localStorage.setItem("activeLinkHref", l.href);
      })
    );

    const newChatBtn = document.querySelector(".newChatroom");
    newChatBtn?.addEventListener("click", openNewChatModal);

    loadChatRooms();
  }

  // ===================== 로그인 정보 가져오기 =====================
  fetch("/chat/my-info")
    .then((res) => res.json())
    .then((user) => {
      loginUserId =
        user.id ?? user.ID ?? user.Objectid ?? user.objectid ?? null;
      if (!loginUserId) {
        alert("로그인 정보가 없어 채팅 기능을 사용할 수 없습니다.");
        return;
      }
      initPage(); // 로그인 정보가 로딩된 이후에만 초기화
    })
    .catch((err) => {
      console.error("로그인 정보 가져오기 실패", err);
      alert("로그인 정보가 없어 채팅 기능을 사용할 수 없습니다.");
    });

  // ===================== 관리자 페이지 =====================

  const positionLevel = document.querySelector("[data-position]")
    ? parseInt(
        document.querySelector("[data-position]").getAttribute("data-position"),
        10
      ) || null
    : null;
  const isAdminBtn = document.querySelector(".isAdmin");

  if (isAdminBtn && positionLevel >= 2) {
    isAdminBtn.closest("form").style.display = "none";
    isAdminBtn.addEventListener("submit", function (event) {
      event.preventDefault();
    });
  }
});
