document.addEventListener("DOMContentLoaded", function() {
    // 로컬 스토리지에서 액세스 토큰 가져오기
    const accessToken = localStorage.getItem('accessToken');
    console.log('Access Token on DOMContentLoaded:', accessToken); // 디버깅용

    if (accessToken) {
        fetchMemberName(accessToken);
    } else {
        displayLoginButtons();
    }

    fetchConcertList(); // 콘서트 리스트는 인증 정보 필요 없음

    document.getElementById('backButton').addEventListener('click', backToList);
    document.getElementById('reserveButton').addEventListener('click', reserveTicket);
});

function fetchConcertList() {
    console.log('Fetching concert list'); // 디버깅용

    fetch('http://localhost:8080/concerts/list', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (response.status === 401) {
                displayLoginButtons();
                throw new Error('Unauthorized');
            }
            return response.json();
        })
        .then(data => {
            const listContainer = document.getElementById('concertList');
            listContainer.innerHTML = ''; // 리스트를 새로 갱신하기 전에 초기화
            data.data.concertList.forEach(concert => {
                const concertBox = document.createElement('div');
                concertBox.className = 'concert-box';
                concertBox.textContent = `${concert.title} - ${concert.genre}`;
                concertBox.onclick = () => fetchConcertDetails(concert.concertId);
                listContainer.appendChild(concertBox);
            });
        })
        .catch(error => console.error('Error loading the concert list:', error));
}

function fetchConcertDetails(concertId) {
    console.log('Fetching concert details for ID:', concertId); // 디버깅용

    fetch(`http://localhost:8080/concerts/mapping/${concertId}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (response.status === 401) {
                displayLoginButtons();
                throw new Error('Unauthorized');
            }
            return response.json();
        })
        .then(data => {
            const concertDetails = data.data.concertMappingList[0];
            document.getElementById('concertId').textContent = concertDetails.concertId;
            document.getElementById('title').textContent = concertDetails.title;
            document.getElementById('location').textContent = concertDetails.locationName || 'Unknown Location';
            document.getElementById('date').textContent = concertDetails.concertDate;
            document.getElementById('startTime').textContent = concertDetails.startTime;
            document.getElementById('endTime').textContent = concertDetails.endTime;

            document.getElementById('concertDetails').style.display = 'block';
            document.getElementById('concertList').style.display = 'none';
        })
        .catch(error => console.error('Error loading the concert details:', error));
}

function reserveTicket() {
    const concertMappingId = Number(document.getElementById('concertId').textContent); // 숫자로 변환
    const accessToken = localStorage.getItem('accessToken');
    console.log('Reserving ticket for concertMappingId:', concertMappingId); // 디버깅용

    fetch('http://localhost:8080/reservations/message', {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${accessToken}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ concertMappingId: concertMappingId })
    })
        .then(response => {
            if (response.status === 401) {
                displayLoginButtons();
                throw new Error('Unauthorized');
            }
            if (response.status === 400) {
                // 이미 예매 요청을 보낸 경우 대기열 화면으로 이동
                return getReservationId(concertMappingId, accessToken);
            }
            return response.json();
        })
        .then(data => {
            if (data.status === 'ok') {
                getReservationId(concertMappingId, accessToken);
            }
        })
        .catch(error => console.error('Error reserving ticket:', error));
}

function getReservationId(concertMappingId, accessToken) {
    fetch(`http://localhost:8080/reservations/id/${concertMappingId}`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${accessToken}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            const reservationId = data.data.reservationId;
            localStorage.setItem('concertMappingId', concertMappingId);
            localStorage.setItem('reservationId', reservationId);
            window.location.href = '/queue.html';
        })
        .catch(error => console.error('Error getting reservation ID:', error));
}

function fetchMemberName(token) {
    console.log('Access Token for fetchMemberName:', token); // 디버깅용

    fetch('http://localhost:8080/members/memberName', {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (response.status === 401) {
                displayLoginButtons();
                throw new Error('Unauthorized');
            }
            return response.text(); // 응답을 텍스트로 읽음
        })
        .then(data => {
            if (data) {
                displayMemberName(data);
            } else {
                displayLoginButtons();
                console.log('멤버 이름을 가져오는데 실패했습니다.');
            }
        })
        .catch(error => {
            displayLoginButtons();
            console.error('에러:', error);
        });
}

function displayMemberName(name) {
    const memberNameElement = document.getElementById('memberName');
    const signupElement = document.getElementById('signup');
    const loginElement = document.getElementById('login');

    if (memberNameElement && signupElement && loginElement) {
        memberNameElement.style.display = 'list-item';
        memberNameElement.textContent = `Welcome, ${name}`;
        signupElement.style.display = 'none';
        loginElement.style.display = 'none';
    }
}

function displayLoginButtons() {
    const signupElement = document.getElementById('signup');
    const loginElement = document.getElementById('login');
    const memberNameElement = document.getElementById('memberName');

    if (signupElement && loginElement && memberNameElement) {
        signupElement.style.display = 'list-item';
        loginElement.style.display = 'list-item';
        memberNameElement.style.display = 'none';
    }
}

function backToList() {
    document.getElementById('concertDetails').style.display = 'none';
    document.getElementById('concertList').style.display = 'block';
}
