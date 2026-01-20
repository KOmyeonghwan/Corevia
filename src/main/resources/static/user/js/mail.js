document.addEventListener("DOMContentLoaded", () => {

  const openBtn = document.getElementById("open-compose");
  const closeBtn = document.getElementById("close-compose");
  const modal = document.getElementById("composeModal");
  const sendBtn = document.getElementById("send-mail");
  const mailForm = document.getElementById("mailForm");
  const sentBtn = document.querySelector(".send-mail-room");
  const inboxBtn = document.querySelector(".get-mail-room");


  if (!openBtn || !closeBtn || !modal || !sendBtn || !mailForm) {
    console.warn("메일 관련 필수 요소가 존재하지 않습니다.");
    return;
  }

  if (sentBtn) {
    sentBtn.addEventListener("click", () => {
      location.href = "/usermail/sent";
    });
  }

  if (inboxBtn) {
    inboxBtn.addEventListener("click", () => {
      location.href = "/usermail";
    });
  }

  // 모달 열기
  openBtn.addEventListener("click", () => {
    modal.classList.remove("hidden");
  });

  // 모달 닫기
  closeBtn.addEventListener("click", () => {
    modal.classList.add("hidden");
  });

  modal.addEventListener("click", (e) => {
    if (e.target === modal) modal.classList.add("hidden");
  });

  // 메일 보내기
  sendBtn.addEventListener("click", () => {

    const fileInput = mailForm.querySelector('input[name="attachment"]');

    console.log("fileInput.files =", fileInput.files);
    console.log("file =", fileInput.files[0]);


    const formData = new FormData(mailForm);

    const recipientEmail = formData.get("recipientEmail");
    const title = formData.get("title");
    const description = formData.get("description");

    if (!recipientEmail || !title || !description) {
      alert("받는 사람, 제목, 내용을 모두 입력해주세요.");
      return;
    }

    if (!recipientEmail.includes("@")) {
      alert("회사 이메일을 입력하세요.");
      return;
    }

    // ✅ CSRF 토큰
    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

    fetch("/user/mail/send", {
      method: "POST",
      headers: {
        [csrfHeader]: csrfToken
      },
      credentials: 'same-origin',
      body: formData
    })
      .then(res => {
        if (!res.ok) throw new Error("메일 전송 실패");
        return res.text();
      })
      .then(msg => {
        alert(msg);
        modal.classList.add("hidden");
        mailForm.reset();
        location.reload();
      })
      .catch(err => {
        alert(err.message);
      });
  });


});
