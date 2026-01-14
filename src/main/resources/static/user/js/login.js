document.addEventListener("DOMContentLoaded", () => {
  // 모달 요소들
  const signupModal = document.getElementById("signup-modal");
  const findIdModal = document.getElementById("find-id-modal");
  const changePwModal = document.getElementById("change-pw-modal");
  const resultModal = document.getElementById("resultModal");

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

  // 모달 열기
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

  // 모달 닫기 (× 버튼)
  closeBtns.forEach((btn) => {
    btn.addEventListener("click", () => {
      const modal = btn.closest(".modal");
      if (modal) modal.style.display = "none";
    });
  });



  //[[------실시간 유효성 검사------]]
  const dangerPattern = /[<>'";()_+=-]/;

  const fields = {
    userId: {
      input: document.getElementById("register-userId"),
      errMsg: document.querySelector(".idErrMsg"),
      validate: function () {
        const value = this.input.value.trim();
        if (value.length < 6 || value.length > 20) {
          this.errMsg.textContent = "아이디는 6~20자 사이여야 합니다.";
          return false;
        }
        if (!/^[a-zA-Z0-9]+$/.test(value)) {
          this.errMsg.textContent = "아이디는 영문과 숫자만 허용됩니다.";
          return false;
        }
        if (dangerPattern.test(value)) {
          this.errMsg.textContent =
            "아이디에 사용할 수 없는 문자가 포함되어 있습니다.";
          return false;
        }
        this.errMsg.textContent = "";
        return true;
      },
    },
    name: {
      input: document.getElementById("register-name"),
      errMsg: document.querySelector(".nameErrMsg"),
      validate: function () {
        const value = this.input.value.trim();
        if (value.length < 2) {
          this.errMsg.textContent = "이름은 최소 2자 이상입니다.";
          return false;
        }
        if (dangerPattern.test(value)) {
          this.errMsg.textContent =
            "이름에 사용할 수 없는 문자가 포함되어 있습니다.";
          return false;
        }
        this.errMsg.textContent = "";
        return true;
      },
    },
    email: {
      input: document.getElementById("register-email"),
      domain: document.getElementById("register-email-domain"),
      errMsg: document.querySelector(".emailErrMsg"),
      validate: function () {
        const fullEmail =
          this.input.value.trim() + "@" + this.domain.value.trim();
        const emailRegex = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$/;
        if (!emailRegex.test(fullEmail)) {
          this.errMsg.textContent = "올바른 이메일 형식이 아닙니다.";
          return false;
        }
        if (dangerPattern.test(fullEmail)) {
          this.errMsg.textContent =
            "이메일에 사용할 수 없는 문자가 포함되어 있습니다.";
          return false;
        }
        this.errMsg.textContent = "";
        return true;
      },
    },
    phone: {
      input: document.getElementById("register-phone"),
      errMsg: document.querySelector(".phoneErrMsg"),
      validate: function () {
        const value = this.input.value.trim();
        if (!/^[0-9]{10,11}$/.test(value)) {
          this.errMsg.textContent = "전화번호는 숫자 10~11자리여야 합니다.";
          return false;
        }
        this.errMsg.textContent = "";
        return true;
      },
    },
    password: {
      input: document.getElementById("register-password"),
      errMsg: document.querySelector(".pwErrMsg"),
      validate: function () {
        const value = this.input.value.trim();

        if (value.length < 6 || value.length > 50) {
          this.errMsg.textContent = "비밀번호는 6~50자 사이여야 합니다.";

          return false;
        }

        // 영문 + 숫자 + 특수문자 포함
        if (!/(?=.*[A-Za-z])(?=.*[0-9])(?=.*[!@#$%^&*])/.test(value)) {
          this.errMsg.textContent =
            "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다.";
          return false;
        }

        // 위험 문자 체크
        if (dangerPattern.test(value)) {
          this.errMsg.textContent =
            "비밀번호에 사용할 수 없는 문자가 포함되어 있습니다.";
          return false;
        }

        this.errMsg.textContent = "";
        return true;
      },
    }
  };

  // --- 실시간 이벤트 등록 ---
  for (const key in fields) {
    if (fields[key].domain) {
      // email은 input + domain 체크
      fields[key].input.addEventListener(
        "input",
        fields[key].validate.bind(fields[key])
      );
      fields[key].domain.addEventListener(
        "input",
        fields[key].validate.bind(fields[key])
      );
    } else {
      fields[key].input.addEventListener(
        "input",
        fields[key].validate.bind(fields[key])
      );
    }
  }

  // --- 폼 제출 시 최종 체크 ---
  signupForm.addEventListener("submit", (e) => {
    e.preventDefault();

    // console.log("Submit 직전 password:", fields.password.input.value);

    let isValid = true;
    for (const key in fields) {
      const valid = fields[key].validate();
      if (!valid) isValid = false;
    }


    if (isValid) {

      const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
      const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
      
      // 유효성 검사 통과 후 AJAX 요청 보내기
      const formData = new FormData(signupForm); 
      
      fetch('/register', {
        method: 'POST',
        body: formData,
        headers: {
          [csrfHeader]: csrfToken  
        }
      })
      .then(response => response.json())  
      .then(data => {
        if (data.success) {
          window.location.href = '/login';  // 성공 시 로그인 페이지로 리디렉션
        } else {
          alert('회원가입 실패: ' + data.message);
        }
      })
      .catch(error => {
        console.error('폼 제출 오류:', error);
        alert('서버 오류가 발생했습니다.');
      });
    } else {
      alert("입력값을 다시 확인해주세요.");
    }
  });


});