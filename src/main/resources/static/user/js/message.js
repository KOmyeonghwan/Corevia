function sendMessage() {
  // 채팅 입력창
  const input = document.getElementById("chat-input-text");
  const text = input.value.trim();
  if (!text) return;

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
