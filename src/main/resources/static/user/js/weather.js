document.addEventListener("DOMContentLoaded", () => {
  const weatherBox = document.getElementById("weatherBox");
  const citySelect = document.getElementById("citySelect");

  const cities = [
    { name: "서울", lat: 37.5665, lon: 126.9780 },
    { name: "부산", lat: 35.1796, lon: 129.0756 },
    { name: "대구", lat: 35.8722, lon: 128.6014 },
    { name: "인천", lat: 37.4563, lon: 126.7052 },
    { name: "광주", lat: 35.1595, lon: 126.8526 },
    { name: "대전", lat: 36.3504, lon: 127.3845 },
    { name: "울산", lat: 35.5384, lon: 129.3114 },
    { name: "수원", lat: 37.2636, lon: 127.0286 }
  ];

  // 드롭다운 옵션 생성
  cities.forEach(city => {
    const option = document.createElement("option");
    option.value = JSON.stringify({ lat: city.lat, lon: city.lon });
    option.textContent = city.name;
    citySelect.appendChild(option);
  });

  // 선택한 도시 날씨 표시
  citySelect.addEventListener("change", e => {
    const value = e.target.value;
    if (!value) return;
    const coords = JSON.parse(value);
    loadWeather(coords.lat, coords.lon);
  });

  // 초기 로딩: 모든 도시 날씨 표시
  cities.forEach(city => loadWeather(city.lat, city.lon));

  // 위치 기반 날씨도 표시 (옵션)
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
      position => {
        loadWeather(position.coords.latitude, position.coords.longitude);
      },
      error => {
        console.warn("위치 권한 거부됨. 기본 부산 날씨 표시");
        loadWeather(35.1796, 129.0756); // 부산
      }
    );
  } else {
    console.warn("브라우저가 위치 정보를 지원하지 않음. 기본 부산 날씨 표시");
    loadWeather(35.1796, 129.0756); // 부산
  }
});

// 기존 loadWeather 함수 재사용
function loadWeather(lat, lon) {
  const weatherBox = document.getElementById("weatherBox");

  fetch(`/weather/current?lat=${lat}&lon=${lon}`)
    .then(res => res.json())
    .then(data => {
      if (!data.weather || data.weather.length === 0) {
        weatherBox.innerHTML += "<p>날씨 데이터가 없습니다.</p>";
        return;
      }

      const icon = data.weather[0].icon;
      const html = `
        <div class="weather-card">
            <h3>📍 ${data.name}</h3>
            <img src="https://openweathermap.org/img/wn/${icon}@2x.png" alt="날씨">
            <p class="temp">${Math.round(data.main.temp)}°C</p>
            <p>${data.weather[0].description}</p>
            <p>체감온도 ${Math.round(data.main.feels_like)}°C</p>
            <p>습도 ${data.main.humidity}%</p>
        </div>
      `;
      weatherBox.innerHTML += html;
    })
    .catch(err => {
      console.error(err);
      weatherBox.innerHTML += "<p>날씨 정보를 불러올 수 없습니다.</p>";
    });
}
