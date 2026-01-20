document.addEventListener("DOMContentLoaded", () => {
  
    const backBtn = document.querySelector(".btn-back");
    const listBtn = document.querySelector(".btn-list");
    const editBtn = document.querySelector(".btn-edit");
    const deleteBtn = document.querySelector(".btn-delete");
  
    // 뒤로 가기 (히스토리)
    backBtn?.addEventListener("click", () => {
      // history.back();
      window.location.href = "/board.html"; // 목록 페이지 경로 수정
    });
  
    // 목록 페이지로 이동
    listBtn?.addEventListener("click", () => {
      window.location.href = "/board.html"; // 목록 페이지 경로 수정
    });
  
    // 수정 버튼 클릭 
    editBtn?.addEventListener("click", () => {
      alert("수정 페이지로 이동합니다.");
      // window.location.href = `/edit.html?id=1`; // 필요 시 수정
    });
  
    // 삭제 버튼 클릭
    deleteBtn?.addEventListener("click", () => {
      const confirmed = confirm("정말 삭제하시겠습니까?");
      if (confirmed) {
        alert("삭제되었습니다.");
        window.location.href = "/board.html";
      }
    });
    
  });
  