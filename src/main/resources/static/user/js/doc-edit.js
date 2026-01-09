// 삭제 확인 및 처리
function handleDelete() {
    if (window.confirm("정말 삭제하시겠습니까?")) {
        // 실제 삭제 로직이 들어갈 수 있음 (예: API 호출 등)
        showToast("삭제되었습니다.");
        window.location.href = "/userdoc";
    }
}

// 임시 저장 처리
function handleDraftSave() {
    showToast("임시저장되었습니다.");
    window.location.href = "/userdoc";
    // 저장 처리 로직 (예: localStorage, 서버 전송 등)
    // window.location.reload(); // 필요하다면 새로고침 또는 이동
}

// 버튼 이벤트 바인딩
document.getElementById("deleteBtn")?.addEventListener("click", handleDelete);
document.getElementById("draftBtn")?.addEventListener("click", handleDraftSave);

// 간단한 toast 메시지 함수 예시
function showToast(message) {
    alert(message); // 나중에 커스텀 토스트로 교체 가능
}
 
