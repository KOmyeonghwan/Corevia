document.addEventListener("DOMContentLoaded", () => {
  
  const openBtn = document.getElementById("open-compose");
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


  //페이지 네이션 및 tab 구현
  const itemsPerPage = 10;
  const contents = document.querySelectorAll('.tab-content');
  const tabs = document.querySelectorAll('.tab');

  // 페이지네이션 초기화
  contents.forEach(content => {
    paginate(content);
  });

  // 탭 클릭 이벤트
  tabs.forEach(tab => {
    tab.addEventListener('click', () => {
      const target = tab.getAttribute('data-tab');

      // 탭 active 처리
      tabs.forEach(t => t.classList.remove('active'));
      tab.classList.add('active');

      // 콘텐츠 표시 처리
      contents.forEach(content => {
        if (content.id === 'content-' + target) {
          content.classList.remove('hidden');
        } else {
          content.classList.add('hidden');
        }
      });

      // 탭 변경 시 첫 페이지로 리셋
      const currentContent = document.querySelector(`#content-${target}`);
      showPage(currentContent, 1);
    });
  });

  // [페이지네이션 처리 함수]
  function paginate(content) {
    const tbody = content.querySelector('tbody');
    const rows = Array.from(tbody.querySelectorAll('tr'));
    const totalPages = Math.ceil(rows.length / itemsPerPage);
    const visiblePageCount = 10;
  
    // 기존 pagination 영역 제거
    const existingPagination = content.querySelector('.pagination');
    if (existingPagination) existingPagination.remove();
    
    // 새 pagination 영역 생성
    const pagination = document.createElement('div');
    pagination.classList.add('pagination');
    content.appendChild(pagination);

    // Object.assign(el.style, {
    //   color: 'red',
    //   backgroundColor: 'blue',
    //   padding: '6px 12px',
    //   borderRadius: '4px',
    //   border: '1px solid #ccc',
    //   cursor: 'pointer',
    // });
  
    // 페이지 버튼 렌더 함수
    function renderPageButtons(currentPage) {
      pagination.innerHTML = ''; // 기존 버튼 제거
  
      const currentBlock = Math.floor((currentPage - 1) / visiblePageCount);
      const startPage = currentBlock * visiblePageCount + 1;
      const endPage = Math.min(startPage + visiblePageCount - 1, totalPages);
  
      // 이전 블록 버튼
      if (startPage > 1) {
        const prevBtn = document.createElement('button');
        prevBtn.textContent = '<';
        prevBtn.addEventListener('click', () => {
          renderPageButtons(startPage - 1);
          showPage(content, startPage - 1);
        });
        pagination.appendChild(prevBtn);
      }
  
      // 숫자 페이지 버튼
      for (let i = startPage; i <= endPage; i++) {
        const btn = document.createElement('button');
        btn.textContent = i;
        if (i === currentPage) btn.classList.add('active');
        btn.addEventListener('click', () => {
          showPage(content, i);
          renderPageButtons(i);
        });
        pagination.appendChild(btn);
      }
  
      // 다음 블록 버튼
      if (endPage < totalPages) {
        const nextBtn = document.createElement('button');
        nextBtn.textContent = '>';
        nextBtn.addEventListener('click', () => {
          renderPageButtons(endPage + 1);
          showPage(content, endPage + 1);
        });
        pagination.appendChild(nextBtn);
      }
    }
  
    // 첫 렌더링
    renderPageButtons(1);
    showPage(content, 1);
  }
  

  // 특정 페이지 보여주기
  function showPage(content, page) {
    const rows = Array.from(content.querySelectorAll('tbody tr'));
    const paginationButtons = content.querySelectorAll('.pagination button');

    const start = (page - 1) * itemsPerPage;
    const end = start + itemsPerPage;

    rows.forEach((row, index) => {
      row.style.display = (index >= start && index < end) ? '' : 'none';
    });

    // 버튼 active 처리
    paginationButtons.forEach((btn, idx) => {
      if (idx === page - 1) {
        btn.classList.add('active');
      } else {
        btn.classList.remove('active');
      }
    });
  }



});
