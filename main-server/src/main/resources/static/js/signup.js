document.getElementById('signupForm').addEventListener('submit', function(event) {
    event.preventDefault();  // 폼의 기본 제출 동작을 방지
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const memberName = document.getElementById('memberName').value;

    fetch('http://localhost:8080/members/signup', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password, memberName })
    })
        .then(response => response.json())
        .then(data => {
            // 성공 메시지 표시 후, 사용자가 확인 버튼을 누르면 홈페이지로 리다이렉트
            alert('Signup successful!');  // 성공 알림
            window.location.href = '/index.html';  // index.html로 리다이렉트
        })
        .catch(error => {
            console.error('Error during signup:', error);
            alert('Error during signup');  // 에러 알림
        });
});
