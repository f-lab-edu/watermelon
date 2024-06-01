document.addEventListener("DOMContentLoaded", function() {
    fetchConcertList();
    document.getElementById('backButton').addEventListener('click', backToList);
});

function fetchConcertList() {
    fetch('http://localhost:8080/concerts/list')
        .then(response => response.json())
        .then(data => {
            const listContainer = document.getElementById('concertList');
            listContainer.innerHTML = ''; // 리스트를 새로 갱신하기 전에 초기화
            data.data.concertList.forEach(concert => {
                const div = document.createElement('div');
                div.textContent = `${concert.title} - ${concert.genre}`;
                div.style.cursor = "pointer";
                div.onclick = () => fetchConcertDetails(concert.concertId);
                listContainer.appendChild(div);
            });
        })
        .catch(error => console.error('Error loading the concert list:', error));
}

function fetchConcertDetails(concertId) {
    // API 호출하여 콘서트 정보를 가져오는 예시
    const concertDetails = {
        concertId: 1,
        location: 'LOCATION_A',
        date: '2024-09-01T00:00:00',
        startTime: '19:30',
        endTime: '21:30'
    };
    document.getElementById('concertId').textContent = concertDetails.concertId;
    document.getElementById('location').textContent = concertDetails.location;
    document.getElementById('date').textContent = concertDetails.date;
    document.getElementById('startTime').textContent = concertDetails.startTime;
    document.getElementById('endTime').textContent = concertDetails.endTime;

    document.getElementById('concertDetails').style.display = 'block';
    document.getElementById('concertList').style.display = 'none';
}

document.getElementById('backButton').addEventListener('click', function() {
    document.getElementById('concertDetails').style.display = 'none';
    document.getElementById('concertList').style.display = 'block';
});

function backToList() {
    document.getElementById('concertDetails').style.display = 'none';
    document.getElementById('concertList').style.display = 'block';
}
