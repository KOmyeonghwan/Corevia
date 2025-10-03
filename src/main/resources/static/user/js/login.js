document.addEventListener("DOMContentLoaded", () => {
    // 모달 요소들
    const signupModal = document.getElementById("signup-modal");
    const findIdModal = document.getElementById("find-id-modal");
    const changePwModal = document.getElementById("change-pw-modal");
  
    // 열기 버튼
    const openSignupBtn = document.getElementById("openSignupModal");
    const openFindIdBtn = document.getElementById("openFindIdModal");
    const openChangePwBtn = document.getElementById("openChangePwModal");
  
    // 닫기 버튼 (모달 내 .close 전부 수집)
    const closeBtns = document.querySelectorAll(".modal .close");
  
    // 회원가입 폼
    const signupForm = document.getElementById("signupForm");
  
    if (!signupModal || !signupForm || !openSignupBtn) {
      console.error("회원가입 관련 요소가 없습니다.");
      return;
    }
  
    // -------------------------------
    // 모달 열기
    // -------------------------------
  
    openSignupBtn.addEventListener("click", (e) => {
      e.preventDefault();
      signupModal.style.display = "flex";
      signupForm.email?.focus();
    });
  
    openFindIdBtn?.addEventListener("click", (e) => {
      e.preventDefault();
      findIdModal.style.display = "flex";
    });
  
    openChangePwBtn?.addEventListener("click", (e) => {
      e.preventDefault();
      changePwModal.style.display = "flex";
    });
  
    // -------------------------------
    // 모달 닫기 (× 버튼)
    // -------------------------------
  
    closeBtns.forEach((btn) => {
      btn.addEventListener("click", () => {
        const modal = btn.closest(".modal");
        if (modal) modal.style.display = "none";
      });
    });
  
    // -------------------------------
    // 바깥 영역 클릭 시 모달 닫기
    // -------------------------------
  
    window.addEventListener("click", (e) => {
      const modals = [signupModal, findIdModal, changePwModal];
      modals.forEach((modal) => {
        if (e.target === modal) {
          modal.style.display = "none";
        }
      });
    });
  
    // -------------------------------
    // 회원가입 제출 처리
    // -------------------------------
  
  //   signupForm.addEventListener("submit", async (e) => {
  //     e.preventDefault();
  
  //     const data = {
  //       email: signupForm.email?.value.trim(),
  //       name: signupForm.name?.value.trim(),
  //       phone: signupForm.phone?.value.trim(),
  //       password: signupForm.password?.value,
  //       role: document.getElementById("role")?.value
  //         ? parseInt(document.getElementById("role").value)
  //         : null,
  //       position_id: document.getElementById("position")?.value
  //         ? parseInt(document.getElementById("position").value)
  //         : null,
  //       department_id: document.getElementById("department")?.value
  //         ? parseInt(document.getElementById("department").value)
  //         : null,
  //     };
  
  //     try {
  //       const res = await fetch("/api/users", {
  //         method: "POST",
  //         headers: { "Content-Type": "application/json" },
  //         body: JSON.stringify(data),
  //       });
  
  //       if (res.ok) {
  //         alert("회원가입이 완료되었습니다.");
  //         signupForm.reset();
  //         signupModal.style.display = "none";
  //       } else {
  //         const error = await res.json();
  //         alert("회원가입 실패: " + (error?.message || "서버 오류"));
  //       }
  //     } catch (err) {
  //       console.error("에러 발생:", err);
  //       alert("서버와의 통신 중 오류가 발생했습니다.");
  //     }
  //   });
 });
  