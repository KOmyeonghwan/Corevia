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

    let viewMode = "month";
    let currentDate = new Date();
    let schedules = {}; // { 'yyyy-MM-dd': [{id, title, desc, time}] }
    let editingDate = null;
    let editingScheduleId = null;

    // 서버에서 렌더링된 로그인 유저 ID
    // const loginUserId = "{{loginUser.userPk}}";
    const loginUserId = calendarEl.dataset.userId;

    /** ---------------- 서버 연동 함수 ---------------- */

    // 특정 기간 일정 불러오기
    async function loadSchedulesFromServer(startDate, endDate) {
        try {
            const res = await fetch(`/api/schedules?userId=${loginUserId}&start=${startDate}&end=${endDate}`);
            const data = await res.json();
            schedules = {};
            data.forEach(item => {
                const dateStr = item.startDatetime.split('T')[0];
                const time = item.startDatetime.split('T')[1]?.substring(0, 5) || '';
                if (!schedules[dateStr]) schedules[dateStr] = [];
                schedules[dateStr].push({
                    id: item.id,
                    title: item.title,
                    desc: item.description,
                    time
                });
            });
        } catch (err) {
            console.error(err);
            alert("일정 불러오기 실패");
        }
    }

    // 일정 저장 (추가/수정)
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;
    async function saveScheduleToServer(scheduleData) {
        try {
            if (editingScheduleId) {
                await fetch(`/api/schedules/${editingScheduleId}`, {
                    method: "PUT",
                    headers: { "Content-Type": "application/json", [csrfHeader]: csrfToken },
                    body: JSON.stringify(scheduleData)
                });
            } else {
                const res = await fetch(`/api/schedules`, {
                    method: "POST",
                    headers: { "Content-Type": "application/json", [csrfHeader]: csrfToken },
                    body: JSON.stringify(scheduleData)
                });
                const newSchedule = await res.json();
                return newSchedule.id;
            }
        } catch (err) {
            console.error(err);
            alert("서버 저장 실패");
        }
    }

    // 일정 삭제
    async function deleteScheduleFromServer(scheduleId) {
        try {
            await fetch(`/api/schedules/${scheduleId}`, { method: "DELETE", headers: { [csrfHeader]: csrfToken } });
        } catch (err) {
            console.error(err);
            alert("삭제 실패");
        }
    }

    /** ---------------- 달력 렌더링 ---------------- */

    async function renderCalendar() {
        calendarEl.innerHTML = "";

        const year = currentDate.getFullYear();
        const month = currentDate.getMonth();
        const firstDay = new Date(year, month, 1);
        const lastDay = new Date(year, month + 1, 0);
        const startDate = `${year}-${String(month + 1).padStart(2, '0')}-01`;
        const endDate = `${year}-${String(month + 1).padStart(2, '0')}-${String(lastDay.getDate()).padStart(2, '0')}`;

        monthYearEl.textContent = `${year}년 ${month + 1}월`;

        // 서버에서 일정 로드
        await loadSchedulesFromServer(startDate, endDate);

        const startDay = firstDay.getDay();
        const daysInMonth = lastDay.getDate();

        // 빈 칸
        for (let i = 0; i < startDay; i++) {
            const emptyDiv = document.createElement("div");
            emptyDiv.classList.add("empty");
            calendarEl.appendChild(emptyDiv);
        }

        // 날짜 렌더링
        for (let day = 1; day <= daysInMonth; day++) {
            const dayDiv = document.createElement("div");
            dayDiv.classList.add("day");
            const dateStr = `${year}-${String(month + 1).padStart(2, "0")}-${String(day).padStart(2, "0")}`;

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
                schedules[dateStr].forEach(item => {
                    const scheduleDiv = document.createElement("div");
                    scheduleDiv.classList.add("schedule");
                    scheduleDiv.textContent = item.time ? `${item.time} ${item.title}` : item.title;
                    scheduleDiv.addEventListener("click", e => {
                        e.stopPropagation();
                        openScheduleModal(dateStr, item.id);
                    });
                    dayDiv.appendChild(scheduleDiv);
                });
            }

            dayDiv.addEventListener("click", () => openScheduleModal(dateStr));
            calendarEl.appendChild(dayDiv);
        }
    }

    /** ---------------- 모달 ---------------- */

    function openScheduleModal(dateStr, scheduleId = null) {
        editingDate = dateStr;
        editingScheduleId = scheduleId;

        if (scheduleId) {
            const item = Object.values(schedules).flat().find(s => s.id === scheduleId);
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
        document.body.style.overflow = "hidden";
    }

    function closeScheduleModal() {
        scheduleModal.classList.add("hidden");
        document.body.style.overflow = "auto";
        editingDate = null;
        editingScheduleId = null;
    }

    /** ---------------- 이벤트 ---------------- */

    // 일정 저장
    scheduleForm.addEventListener("submit", async e => {
        e.preventDefault();
        const date = scheduleDateInput.value;
        const title = scheduleTitleInput.value.trim();
        const desc = scheduleDescInput.value.trim();
        const time = scheduleTimeInput.value;
        if (!title) return alert("일정 제목을 입력하세요.");

        const startDatetime = time ? `${date}T${time}:00` : `${date}T00:00:00`;
        const scheduleData = {
            userId: loginUserId,
            title,
            description: desc,
            startDatetime,
            endDatetime: startDatetime,
            isAdminView: false
        };
        const newId = await saveScheduleToServer(scheduleData);

        if (editingScheduleId) {
            // 수정
            const itemIndex = schedules[date].findIndex(s => s.id === editingScheduleId);
            schedules[date][itemIndex] = { id: editingScheduleId, title, desc, time };
        } else {
            // 새 일정
            if (!schedules[date]) schedules[date] = [];
            schedules[date].push({ id: newId, title, desc, time });
        }

        closeScheduleModal();
        renderCalendar();
    });

    // 일정 삭제
    deleteBtn.addEventListener("click", async () => {
        if (editingScheduleId) {
            await deleteScheduleFromServer(editingScheduleId);
            // 삭제 후 schedules 업데이트
            for (const key in schedules) {
                schedules[key] = schedules[key].filter(s => s.id !== editingScheduleId);
                if (schedules[key].length === 0) delete schedules[key];
            }
            closeScheduleModal();
            renderCalendar();
        }
    });

    closeModalBtn.addEventListener("click", closeScheduleModal);

    prevBtn.addEventListener("click", () => {
        currentDate.setMonth(currentDate.getMonth() - 1);
        renderCalendar();
    });
    nextBtn.addEventListener("click", () => {
        currentDate.setMonth(currentDate.getMonth() + 1);
        renderCalendar();
    });

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

    /** ---------------- 주간 보기 ---------------- */
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
            if (dayDate.toDateString() === today.toDateString()) dayDiv.classList.add("today");

            const dateNumber = document.createElement("div");
            dateNumber.classList.add("date-number");
            dateNumber.textContent = `${dayDate.getMonth() + 1}/${dayDate.getDate()}`;
            dayDiv.appendChild(dateNumber);

            if (schedules[dateStr]) {
                schedules[dateStr].forEach(item => {
                    const scheduleDiv = document.createElement("div");
                    scheduleDiv.classList.add("schedule");
                    scheduleDiv.textContent = item.title;
                    scheduleDiv.addEventListener("click", e => {
                        e.stopPropagation();
                        openScheduleModal(dateStr, item.id);
                    });
                    dayDiv.appendChild(scheduleDiv);
                });
            }

            dayDiv.addEventListener("click", () => openScheduleModal(dateStr));
            calendarEl.appendChild(dayDiv);
        }

        monthYearEl.textContent = `${year}년 ${month + 1}월 (주간 보기)`;
    }

    /** 초기 렌더링 */
    renderCalendar();
});
