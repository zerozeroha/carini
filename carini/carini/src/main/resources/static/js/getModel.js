var carRadarLabels = ["주행", "가격", "거주성", "품질", "디자인", "연비"];
var charts = {};
var backgroundColors = [
    'rgba(255, 99, 132, 0.2)', // Performance
    'rgba(54, 162, 235, 0.2)', // Price
    'rgba(75, 192, 192, 0.2)', // Geoju
    'rgba(153, 102, 255, 0.2)', // Quality
    'rgba(255, 159, 64, 0.2)', // Design
    'rgba(255, 206, 86, 0.2)'  // Efficiency
];

var borderColors = [
    'rgba(255, 99, 132, 1)', // Performance
    'rgba(54, 162, 235, 1)', // Price
    'rgba(75, 192, 192, 1)', // Geoju
    'rgba(153, 102, 255, 1)', // Quality
    'rgba(255, 159, 64, 1)', // Design
    'rgba(255, 206, 86, 1)'  // Efficiency
];

function createRadarChart(car) {
    var carYValues = [
        car.carScPer,
        car.carScPrice,
        car.carScGeoju,
        car.carScQuality,
        car.carScDesign,
        car.carScEff
    ];

    var carDatasets = [{
        label: car.carName,
        data: carYValues,
        fill: true,
        backgroundColor: 'rgba(65, 82, 98, 0.2)',
        borderColor: 'rgb(65, 82, 98)',
        pointBackgroundColor: backgroundColors,
        pointBorderColor: '#fff',
        pointHoverBackgroundColor: '#fff',
        pointHoverBorderColor: borderColors
    }];

    var radarChartElement = document.getElementById('carRadarChart');

    charts['carRadarChart'] = new Chart(radarChartElement, {
        type: 'radar',
        data: {
            labels: carRadarLabels,
            datasets: carDatasets
        },
        options: {
            responsive: false,
            plugins: {
                legend: { display: false }
            },
            scales: {
                r: {
                    suggestedMin: 0,
                    suggestedMax: 10,
                    ticks: {
                        stepSize: 2
                    }
                }
            }
        }
    });
} //createRadarChart end

function createBarChart(car) {
    var carYValues = [
        car.carScPer,
        car.carScPrice,
        car.carScGeoju,
        car.carScQuality,
        car.carScDesign,
        car.carScEff
    ];

    var labels = ['주행', '가격', '거주성', '품질', '디자인', '연비'];

    

    var barChartElement = document.getElementById('carBarChart');

    charts['carBarChart'] = new Chart(barChartElement, {
        type: "bar",
        data: {
            labels: labels,
            datasets: [{
                label: 'Car Ratings',
                data: carYValues,
                backgroundColor: backgroundColors,
                borderColor: borderColors,
                borderWidth: 1,
                barThickness: 30
            }]
        },
        options: {
            responsive: true,
            aspectRatio: 2,
            plugins: {
                legend: {
                    display: false // 범례를 숨김
                }
            },
            scales: {
                x: {
                    display: true // x축 틱 표시
                },
                y: {
                    beginAtZero: true,
                    ticks: {
                        display: true // y축 틱 숨기기
                    },
                    max: 10
                }
            }
        }
    });
} // createBarChart end

function toggleBookmark(carId, isBookmarked) {
    let form = document.createElement('form');
    form.method = 'POST';
    form.action = isBookmarked ? '/model/bookmark/delete/' + carId : '/model/bookmark/' + carId;
    document.body.appendChild(form);
    form.submit();
}


