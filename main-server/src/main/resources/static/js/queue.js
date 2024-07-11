document.addEventListener("DOMContentLoaded", function() {
    const concertMappingId = localStorage.getItem('concertMappingId');
    const reservationId = localStorage.getItem('reservationId');
    const accessToken = localStorage.getItem('accessToken');

    if (concertMappingId && reservationId && accessToken) {
        // 페이지 로드 시 먼저 랭크 조회
        getReservationRank(concertMappingId, reservationId, accessToken);
        // 이후 10초 간격으로 폴링 시작
        startPolling(concertMappingId, reservationId, accessToken);
    } else {
        alert('대기열 정보를 불러오지 못했습니다.');
        window.location.href = '/index.html';
    }

    document.getElementById('backToConcertDetails').addEventListener('click', () => {
        stopPolling();
        window.location.href = `/index.html`; // concertDetails는 index.html에 포함되어 있습니다.
    });
});

let pollingInterval = null;

function startPolling(concertMappingId, reservationId, accessToken) {
    pollingInterval = setInterval(() => {
        getReservationRank(concertMappingId, reservationId, accessToken);
    }, 10000);
}

function stopPolling() {
    if (pollingInterval) {
        clearInterval(pollingInterval);
        pollingInterval = null;
    }
}

function getReservationRank(concertMappingId, reservationId, accessToken) {
    fetch(`http://localhost:8080/reservations/rank/${concertMappingId}/${reservationId}`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${accessToken}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            const rank = data.data.reservationRank;
            const status = data.data.reservationStatus;
            document.getElementById('queueMessage').textContent = `Your rank is ${rank}`;

            if (status === 'AVAILABLE') {
                window.location.href = `/payment.html?concertMappingId=${concertMappingId}`;
            }
        })
        .catch(error => console.error('Error getting reservation rank:', error));
}
