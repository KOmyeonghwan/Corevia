document.addEventListener("DOMContentLoaded", () => {
  const calendarEl = document.getElementById("calendar");
  const monthYearEl = document.getElementById("monthYear");
  const prevBtn = document.getElementById("prevMonth");
  const nextBtn = document.getElementById("nextMonth");
  const toggleViewBtn = document.getElementById("toggleView");
  const scheduleModal = document.getElementById("scheduleModal");
  const scheduleForm = document.getElementById("scheduleForm");
  const scheduleDateInput = document.getElementById("scheduleDate");
  const scheduleTimeInput = document.getElementById("scheduleTime");
  const scheduleTitleInput = document.getElementById("scheduleTitle");
  const scheduleDescInput = document.getElementById("scheduleDesc");
  const modalTitle = document.getElementById("modalTitle");
  const closeModalBtn = document.getElementById("closeModal");
  const deleteBtn = document.getElementById("deleteSchedule");
  let viewMode = "month"; // month or week
  let currentDate = new Date();
  // 일정 데이터 로드/저장
  const loadSchedules = () => {
      const data = localStorage.getItem("schedules");
      return data ? JSON.parse(data) : {};
  };
  const saveSchedules = (schedules) => {
      localStorage.setItem("schedules", JSON.stringify(schedules));
  };
  let schedules = loadSchedules();
  let editingDate = null;
  let editingIndex = null;
  // 달력 렌더링
  function renderCalendar() {
      calendarEl.innerHTML = "";
      const year = currentDate.getFullYear();
      const month = currentDate.getMonth();
      monthYearEl.textContent = `${year}년 ${month + 1}월`;
      const firstDay = new Date(year, month, 1);
      const startDay = firstDay.getDay();
      const daysInMonth = new Date(year, month + 1, 0).getDate();
      // 빈 칸
      for (let i = 0; i < startDay; i++) {
          const emptyDiv = document.createElement("div");
          emptyDiv.classList.add("empty");
          calendarEl.appendChild(emptyDiv);
      }
      for (let day = 1; day <= daysInMonth; day++) {
          const dayDiv = document.createElement("div");
          dayDiv.classList.add("day");
          const dateStr = `${year}-${String(month + 1).padStart(2, "0")}-${String(day).padStart(2, "0")}`;
          // 오늘 강조
          const today = new Date();
          if (year === today.getFullYear() && month === today.getMonth() && day === today.getDate()) {
              dayDiv.classList.add("today");
          }
          const dateNumber = document.createElement("div");
          dateNumber.classList.add("date-number");
          dateNumber.textContent = day;
          dayDiv.appendChild(dateNumber);
          // 일정 표시
          if (schedules[dateStr]) {
              schedules[dateStr].forEach((item, index) => {
                  const scheduleDiv = document.createElement("div");
                  scheduleDiv.classList.add("schedule");
                  scheduleDiv.textContent = item.time ? `${item.time} ${item.title}` : item.title; // 시간 표시
                  scheduleDiv.addEventListener("click", (e) => {
                      e.stopPropagation();
                      openScheduleModal(dateStr, index);
                  });
                  dayDiv.appendChild(scheduleDiv);
              });
          }
          dayDiv.addEventListener("click", () => openScheduleModal(dateStr));
          calendarEl.appendChild(dayDiv);
      }
  }
  // 모달 열기
  function openScheduleModal(dateStr, index = null) {
      editingDate = dateStr;
      editingIndex = index;
      if (index !== null) {
          // 기존 일정 편집
          const item = schedules[dateStr][index];
          scheduleTitleInput.value = item.title;
          scheduleDescInput.value = item.desc;
          scheduleTimeInput.value = item.time || "";
          modalTitle.textContent = "일정 수정";
          deleteBtn.style.display = "inline-block";
      } else {
          scheduleTitleInput.value = "";
          scheduleDescInput.value = "";
          scheduleTimeInput.value = "";
          modalTitle.textContent = "일정 추가";
          deleteBtn.style.display = "none";
      }
      scheduleDateInput.value = dateStr;
      scheduleModal.classList.remove("hidden");
      document.body.style.overflow = "hidden"; // 스크롤 잠금
  }
  // 모달 닫기
  function closeScheduleModal() {
      scheduleModal.classList.add("hidden");
      document.body.style.overflow = "auto";
      editingDate = null;
      editingIndex = null;
  }
  // 일정 저장
  scheduleForm.addEventListener("submit", (e) => {
      e.preventDefault();
      const date = scheduleDateInput.value;
      const title = scheduleTitleInput.value.trim();
      const desc = scheduleDescInput.value.trim();
      const time = scheduleTimeInput.value; // HH:MM 형식
      if (!title) {
          alert("일정 제목을 입력하세요.");
          return;
      }
      if (!schedules[date]) schedules[date] = [];
      if (editingIndex !== null) {
          // 기존 일정 수정
          schedules[date][editingIndex] = { title, desc, time };
      } else {
          // 새 일정 추가 (기존 일정에 push)
          schedules[date].push({ title, desc, time });
      }
      // 시간순 정렬
      schedules[date].sort((a, b) => {
          if (!a.time) return 1; // 시간 없는 일정 뒤로
          if (!b.time) return -1;
          return a.time.localeCompare(b.time);
      });
      saveSchedules(schedules);
      closeScheduleModal();
      renderCalendar();
  });
  // 일정 삭제
  deleteBtn.addEventListener("click", () => {
      if (editingDate !== null && editingIndex !== null) {
          schedules[editingDate].splice(editingIndex, 1);
          if (schedules[editingDate].length === 0) delete schedules[editingDate];
          saveSchedules(schedules);
          closeScheduleModal();
          renderCalendar();
      }
  });
  closeModalBtn.addEventListener("click", closeScheduleModal);
  // 이전/다음 달
  prevBtn.addEventListener("click", () => {
      currentDate.setMonth(currentDate.getMonth() - 1);
      renderCalendar();
  });
  nextBtn.addEventListener("click", () => {
      currentDate.setMonth(currentDate.getMonth() + 1);
      renderCalendar();
  });
  // 주간/월간 전환
  toggleViewBtn.addEventListener("click", () => {
      if (viewMode === "month") {
          viewMode = "week";
          toggleViewBtn.textContent = "월간 보기";
          renderWeekView();
      } else {
          viewMode = "month";
          toggleViewBtn.textContent = "주간 보기";
          renderCalendar();
      }
  });
  function renderWeekView() {
      calendarEl.innerHTML = "";
      const year = currentDate.getFullYear();
      const month = currentDate.getMonth();
      const today = new Date();
      const dayOfWeek = currentDate.getDay();
      const weekStart = new Date(currentDate);
      weekStart.setDate(currentDate.getDate() - dayOfWeek);
      for (let i = 0; i < 7; i++) {
          const dayDate = new Date(weekStart);
          dayDate.setDate(weekStart.getDate() + i);
          const dateStr = `${dayDate.getFullYear()}-${String(dayDate.getMonth() + 1).padStart(2, "0")}-${String(dayDate.getDate()).padStart(2, "0")}`;
          const dayDiv = document.createElement("div");
          dayDiv.classList.add("day");
          dayDiv.style.minHeight = "120px";
          if (dayDate.toDateString() === today.toDateString()) {
              dayDiv.classList.add("today");
          }
          const dateNumber = document.createElement("div");
          dateNumber.classList.add("date-number");
          dateNumber.textContent = `${dayDate.getMonth() + 1}/${dayDate.getDate()}`;
          dayDiv.appendChild(dateNumber);
          if (schedules[dateStr]) {
              schedules[dateStr].forEach((item, index) => {
                  const scheduleDiv = document.createElement("div");
                  scheduleDiv.classList.add("schedule");
                  scheduleDiv.textContent = item.title;
                  scheduleDiv.addEventListener("click", (e) => {
                      e.stopPropagation();
                      openScheduleModal(dateStr, index);
                  });
                  dayDiv.appendChild(scheduleDiv);
              });
          }
          dayDiv.addEventListener("click", () => openScheduleModal(dateStr));
          calendarEl.appendChild(dayDiv);
      }
      monthYearEl.textContent = `${year}년 ${month + 1}월 (주간 보기)`;
  }
  renderCalendar();
});