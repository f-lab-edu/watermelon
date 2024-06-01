document.getElementById('loginForm').addEventListener('submit', function(event) {
    event.preventDefault(); // 폼의 기본 제출을 방지
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    fetch('http://localhost:8080/members/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
    })
        .then(response => response.json())
        .then(data => {
            if (data.status === "true") {
                // 성공 시 accessToken을 로컬 스토리지에 저장
                localStorage.setItem('accessToken', data.accessToken);
                localStorage.setItem('refreshToken', data.refreshToken);
                alert('Login successful!'); // 로그인 성공 메시지 표시
                window.location.href = '/index.html'; // 메인 페이지로 리다이렉트
            } else {
                alert(data.message); // 실패 메시지 표시
            }
        })
        .catch(error => {
            console.error('Error during login:', error);
            alert('Login error. Please try again.'); // 로그인 중 오류 처리
        });
});
