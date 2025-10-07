function confirmDelete() {
    const confirmed = confirm("삭제하시겠습니까?");
    if (confirmed) {
        // 삭제 동작 실행 (예: 페이지 이동, 함수 호출 등)
        alert("삭제되었습니다."); // 예시 동작
        location.href = 'doc.html';
    } 
}