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

function selectSeat(seat) {
    const previouslySelectedSeat = document.querySelector('.seat.selected');
    if (previouslySelectedSeat) {
        previouslySelectedSeat.classList.remove('selected');
    }

    const seatElement = document.querySelector(`.seat[data-seat-id='${seat.seatId}']`);
    seatElement.classList.add('selected');

    const selectedSeatInfo = document.getElementById('selectedSeatInfo');
    selectedSeatInfo.innerHTML = `
        <p>선택된 좌석: ${seat.section}구역 ${seat.rowValue}열 ${seat.seatId}번</p>
    `;

    document.getElementById('paymentButton').style.display = 'block';
}

function processPayment() {
    const selectedSeat = document.querySelector('.seat.selected');
    if (!selectedSeat) {
        alert('좌석을 선택해 주세요.');
        return;
    }

    const seatId = selectedSeat.dataset.seatId;
    alert(`좌석 ${seatId}번 결제를 진행합니다.`);
    // 결제 로직을 추가할 수 있습니다.
}
