document.addEventListener("DOMContentLoaded", () => {
  const openBtn = document.getElementById("btn-reply");
  const closeBtn = document.getElementById("close-compose");
  const modal = document.getElementById("composeModal");

  if (!openBtn || !closeBtn || !modal) {
    console.warn("필수 요소가 존재하지 않습니다.");
    return;
  }

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
});
