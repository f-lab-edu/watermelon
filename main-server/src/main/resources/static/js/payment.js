document.addEventListener("DOMContentLoaded", function() {
    const concertMappingId = new URLSearchParams(window.location.search).get('concertMappingId');
    const accessToken = localStorage.getItem('accessToken');

    if (!concertMappingId || !accessToken) {
        alert('필수 정보가 없습니다.');
        window.location.href = '/index.html';
        return;
    }

    fetchSeats(concertMappingId, accessToken);

    document.getElementById('paymentButton').addEventListener('click', processPayment);
});

function fetchSeats(concertMappingId, accessToken) {
    fetch(`http://localhost:8080/seats/available/${concertMappingId}`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${accessToken}`,
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            if (data.status === 'ok') {
                displaySeats(data.data.seatList);
            } else {
                alert('좌석 정보를 가져오는 데 실패했습니다.');
            }
        })
        .catch(error => console.error('Error fetching seats:', error));
}

function displaySeats(seatList) {
    const seatsContainer = document.getElementById('seatsContainer');
    seatsContainer.innerHTML = ''; // 기존 좌석 초기화

    seatList.forEach(seat => {
        const seatElement = document.createElement('div');
        seatElement.className = `seat ${seat.isAvailable ? '' : 'unavailable'}`;
        seatElement.textContent = seat.seatId;
        seatElement.dataset.seatId = seat.seatId;
        seatElement.dataset.section = seat.section;
        seatElement.dataset.row = seat.rowValue;

        if (seat.isAvailable) {
            seatElement.addEventListener('click', () => selectSeat(seat));
        }

        seatsContainer.appendChild(seatElement);
    });
}

let selectedSeat = null;

function selectSeat(seat) {
    const previouslySelectedSeat = document.querySelector('.seat.selected');
    if (previouslySelectedSeat) {
        previouslySelectedSeat.classList.remove('selected');
    }

    const seatElement = document.querySelector(`.seat[data-seat-id='${seat.seatId}']`);
    seatElement.classList.add('selected');

    selectedSeat = seat;

    const selectedSeatInfo = document.getElementById('selectedSeatInfo');
    selectedSeatInfo.innerHTML = `
        <p>선택된 좌석: ${seat.section}구역 ${seat.rowValue}열 ${seat.seatId}번</p>
    `;

    document.getElementById('paymentButton').style.display = 'block';
}

function processPayment() {
    if (!selectedSeat) {
        alert('좌석을 선택해 주세요.');
        return;
    }

    const reservationId = localStorage.getItem('reservationId');
    const accessToken = localStorage.getItem('accessToken');
    const seatId = selectedSeat.seatId;

    fetch('http://localhost:8080/payments/process', {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${accessToken}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ reservationId: Number(reservationId), seatId: seatId })
    })
        .then(response => response.json())
        .then(data => {
            if (data.status === 'ok') {
                alert(`결제 성공! 좌석 번호: ${data.data.seatId}`);
                window.location.href = '/index.html';
            } else {
                alert('결제에 실패했습니다.');
            }
        })
        .catch(error => console.error('Error processing payment:', error));
}
