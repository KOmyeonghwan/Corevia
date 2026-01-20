document.addEventListener("DOMContentLoaded", () => {

  /* =========================
     답장 모달
  ========================= */

  const openBtn = document.querySelector(".btn-reply");
  const closeBtn = document.getElementById("close-compose");
  const modal = document.getElementById("composeModal");

  if (openBtn && closeBtn && modal) {

    openBtn.addEventListener("click", () => {
      modal.classList.remove("hidden");
    });

    closeBtn.addEventListener("click", () => {
      modal.classList.add("hidden");
    });

    modal.addEventListener("click", (e) => {
      if (e.target === modal) {
        modal.classList.add("hidden");
      }
    });
  }

  /* =========================
     메일 삭제
  ========================= */

  const deleteBtn = document.querySelector(".btn-delete");

  if (deleteBtn) {
    deleteBtn.addEventListener("click", () => {

      if (!confirm("메일을 삭제하시겠습니까?")) return;

      const mailId = deleteBtn.dataset.mailId;

      const csrfToken = document.querySelector('meta[name="_csrf"]').content;
      const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

      fetch(`/user/mail/delete/${mailId}`, {
        method: "POST",
        headers: {
          [csrfHeader]: csrfToken
        },
        credentials: "same-origin"
      })
        .then(res => {
          if (!res.ok) throw new Error("삭제 실패");
          return res.text();
        })
        .then(() => {
          alert("메일이 삭제되었습니다.");
          location.href = "/usermail";
        })
        .catch(err => alert(err.message));
    });
  }

});
