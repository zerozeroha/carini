// 즐겨찾기 =====================================================================
function toggleBookmark(carId, isBookmarked) {
    let form = document.createElement('form');
    form.method = 'POST';
    form.action = isBookmarked ? '/mypage/bookmark/delete/' + carId : '/mypage/bookmark/' + carId;
    document.body.appendChild(form);
    form.submit();
}
 
 // 가격 범위 설정 ===============================================================
document.addEventListener('DOMContentLoaded', function () {
	var priceSlider = document.getElementById('priceSlider');
	var filterMinPrice = document.getElementById('filterMinPrice');
	var filterMaxPrice = document.getElementById('filterMaxPrice');
	
	noUiSlider.create(priceSlider, {
	    start: [0, 220000],
	    connect: true,
	    range: {
	        'min': 0,
	        'max': 220000
	    },
	    step: 100,
		 tooltips: true,
	    format: {
	        to: function (value) {
	            return Math.round(value);
	        },
	        from: function (value) {
	            return Number(value);
	        }
	    }
	});

    // 슬라이더 값이 변경될 때 input 요소 업데이트
    priceSlider.noUiSlider.on('update', function (values, handle) {
        var value = values[handle];

        if (handle === 0) {
            filterMinPrice.value = value;
        } else {
            filterMaxPrice.value = value;
        }
    });

});

// 차 비교 =====================================================================
var charts = {}; // 차트 인스턴스를 저장할 객체

// 페이지 넘어가도 comparisonSection이 초기화되지 않도록 localStorage에 저장
let selectedCarIds = JSON.parse(localStorage.getItem('selectedCarIds')) || [];

/**
 * carData 선택
 */
function selectCarForComparison(carId) {
    if (selectedCarIds.includes(carId)) {
        alert('이 차는 이미 선택되었습니다.');
        return;
    }

    if (selectedCarIds.length >= 2) {
        alert('비교할 차는 두 대만 선택할 수 있습니다.');
        return;
    }

    selectedCarIds.push(carId);
    localStorage.setItem('selectedCarIds', JSON.stringify(selectedCarIds));
    
	 	
    if (selectedCarIds.length === 2) {
        fetchComparisonData(selectedCarIds[0], selectedCarIds[1]);
    } else {
        fetchSingleCarData(carId);
    }
}

/**
 * carData 한개일 때
 */
function fetchSingleCarData(carId) {
    fetch(`/model/getModel?carId=${carId}`)
        .then(response => response.json())
        .then(data => {
            if (selectedCarIds.length === 1) {
                document.getElementById('comparisonSection').style.display = 'block';
                populateCarData(data, 1);
            } else if (selectedCarIds.length === 2) {
                populateCarData(data, 2);
            }
        });
}

/**
 * carData 두개일 때
 */
function fetchComparisonData(carId1, carId2) {
    fetch(`/model/compare?carId1=${carId1}&carId2=${carId2}`)
        .then(response => response.json())
        .then(data => {
            document.getElementById('comparisonSection').style.display = 'block';
            populateCarData(data.car1, 1);
            populateCarData(data.car2, 2);
        });
}

/**
 * carData 추가
 */

var carRadarLabels = ["주행", "가격", "거주성", "품질", "디자인", "연비"]

function populateCarData(car, position) {
	// 표 내용
    document.getElementById(`car${position}Img`).src = car.carImg;
    document.getElementById(`car${position}Name`).innerText = car.carName;
    document.getElementById(`car${position}Price`).innerText = new Intl.NumberFormat('ko-KR').format(Math.round(car.carAvgPrice));
    document.getElementById(`car${position}Size`).innerText = car.carSize;
    document.getElementById(`car${position}Fuel`).innerText = car.carFuel;
    document.getElementById(`car${position}Eff`).innerText = car.carEff;
	
    var chartIds = [
        `car${position}ChartPrice`,
        `car${position}ChartAvg`
    ];

    var carYValues = [
        car.carAvgPrice,
        car.carScAvg
    ];

    for (let i = 0; i < chartIds.length; i++) {
        var chartId = chartIds[i];

        // 기존 차트가 있으면 제거
        if (charts[chartId]) {
            charts[chartId].destroy();
        }
        
		let carBarDatasets = [];
		
		if(position === 1) {
			barColor = 'rgba(255, 99, 132, 0.5)';
		} else if(position === 2) {
			barColor = 'rgba(54, 162, 235, 0.5)';
		}
		
		carBarDatasets.push({
			backgroundColor: barColor,
            data: [carYValues[i]],
            barThickness: 10
		})
		
        // 새로운 차트 생성 및 저장
        charts[chartId] = new Chart(chartId, {
            type: "bar",
            data: {
                labels: [""], // X축 레이블 숨김
                datasets: carBarDatasets
            }, // data end
            options: {
				responsive: true,
				aspectRatio: 10,
                plugins: {
                    legend: {
                        display: false // 범례를 숨김
                    }
                },
                scales: {
                    x: {
                        display: false // x축 틱 숨기기
                    },
                    y: {
						beginAtZero : true,
                        ticks: {
                           display: true, // y축 틱 숨기기
                        }
                    }
                },
                indexAxis: 'y'
            } // options END
        }); // 바 차트 생성 END
    } // for END
	 
	 // 육각형 차트 만들기
	 
	 // label, y값, datasets 설정
	 var carYValues = [
	 		         car.carScPer,
	 		         car.carScPrice,
	 		         car.carScGeoju,
	 		         car.carScQuality,
	 		         car.carScDesign,
	 		         car.carScEff
	 		     ];
	    
	 var carDatasets = []
	 
	 if(position === 1){
		radarBackgroundColor = 'rgba(255, 99, 132, 0.2)';
		radarColor = 'rgb(255, 99, 132)';
	 } else if(position === 2) {
		radarBackgroundColor = 'rgba(54, 162, 235, 0.2)';
		radarColor = 'rgb(54, 162, 235)';
	 }
	 
	 carDatasets.push(
		{
			label: car.carName,
			data: carYValues,
			fill: true,
			backgroundColor: radarBackgroundColor,
		    borderColor: radarColor,
		    pointBackgroundColor: radarColor,
			pointBorderColor: '#fff',
		    pointHoverBackgroundColor: '#fff',
		    pointHoverBorderColor: radarColor
		})
	
	 
	 let radarChartElement  = document.getElementById(`car${position}RadarChart`);
	 
	 // 기존 차트 있으면 제거
	 if (charts[`car${position}RadarChart`]) {
	         charts[`car${position}RadarChart`].destroy();
	     }
	 
	 // Radar 차트 생성
	 charts[`car${position}RadarChart`] = new Chart(radarChartElement , {
		type: 'radar',
		data: {
			labels: carRadarLabels,
			datasets: carDatasets
		},
		options: {
			responsive: false,
			plugins: {
				legend: {display:false}
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
	 })
	 saveCompareCar(car, position);
} // function populateCarData END

/**
 * radarChart 합치기
 */
var combineCarDic = {};

function saveCompareCar(car, position){
	combineCarDic[position] = car;
}

function combineRadarChart() {
	
	// Show the combined radar chart and hide the individual ones
    var carRadarChart = document.getElementById('carRadarChart');
    var carCombineRadarChartTR = document.getElementById('carCombineRadarChartTR');
    
    if (carRadarChart && carCombineRadarChartTR) {
        if (carRadarChart.style.display === 'none') {
            carRadarChart.style.display = 'table-row';
            carCombineRadarChartTR.style.display = 'none';
        } else {
            carRadarChart.style.display = 'none';
            carCombineRadarChartTR.style.display = 'table-row';
        }
    } else {
        console.error('One or both elements not found: carRadarChart or carCombineRadarChartTR');
    }


    // position = 1, 2 둘 다 데이터가 있을 때 실행
    if (charts['car1RadarChart'] && charts['car2RadarChart']) {
        var carDatasets = [];

        for (i = 1; i <= 2; i++) {
			if (i === 1) {
	            radarBackgroundColor = 'rgba(255, 99, 132, 0.2)';
	            radarColor = 'rgb(255, 99, 132)';
	        } else if (i === 2) {
	            radarBackgroundColor = 'rgba(54, 162, 235, 0.2)';
	            radarColor = 'rgb(54, 162, 235)';
	        }
            car = combineCarDic[i];
            var carYValues = [
                car.carScPer,
                car.carScPrice,
                car.carScGeoju,
                car.carScQuality,
                car.carScDesign,
                car.carScEff
            ];
            carDatasets.push({
                label: car.carName,
                data: carYValues,
                fill: true,
                backgroundColor: radarBackgroundColor,
                borderColor: radarColor,
                pointBackgroundColor: radarColor,
                pointBorderColor: '#fff',
                pointHoverBackgroundColor: '#fff',
                pointHoverBorderColor: radarColor
            });
        } // for end

        var radarChartElement = document.getElementById(`carCombineRadarChart`);

        // 기존 차트 있으면 제거
        if(charts[`carCombineRadarChart`]){
        	charts[`carCombineRadarChart`].destroy();
		}
		
		document.getElementById("carRadarChart").remove();
		
        // Radar 차트 생성
        charts[`carCombineRadarChart`] = new Chart(radarChartElement, {
            type: 'radar',
            data: {
                labels: carRadarLabels,
                datasets: carDatasets
            },
            options: {
                responsive: false,
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
        
	} // if end
	

} // function end


function returnChart() {
    var carRadarChart = document.getElementById('carRadarChart');
    var carCombineRadarChartTR = document.getElementById('carCombineRadarChartTR');
    console.log(carRadarChart); // null
    console.log(carCombineRadarChartTR);
    if (carRadarChart && carCombineRadarChartTR) {
        carRadarChart.style.display = 'table-row';
        carCombineRadarChartTR.style.display = 'none';
    } else {
        console.error('One or both elements not found: carRadarChart or carCombineRadarChartTR');
    }
}



/**
 * carData 삭제
 */
function clearCarData(position) {
    document.getElementById(`car${position}Img`).src = '';
    document.getElementById(`car${position}Name`).innerText = '';
    document.getElementById(`car${position}Price`).innerText = '';
    document.getElementById(`car${position}Size`).innerText = '';
    document.getElementById(`car${position}Fuel`).innerText = '';
    document.getElementById(`car${position}Eff`).innerText = '';

    var chartIds = [
        `car${position}ChartPrice`,
        `car${position}ChartAvg`
    ];

    for (let chartId of chartIds) {
        if (charts[chartId]) {
            charts[chartId].destroy(); // 차트 제거
            charts[chartId] = null; // 차트 인스턴스 삭제
        }   
    document.getElementById(chartId).innerText = ''; // 차트 캔버스 초기화
    }
    
    // RadarChart 제거
    if (charts[`car${position}RadarChart`]) {
    	charts[`car${position}RadarChart`].destroy();
    }

    console.log(selectedCarIds);
}


/**
 * 비교 블록 삭제
 */
function removeCarFromComparison(position) {
    if (position === 1) {
        selectedCarIds = selectedCarIds.filter((_, index) => index !== 0);
    } else if (position === 2) {
        selectedCarIds = selectedCarIds.filter((_, index) => index !== 1);
    }
    
    localStorage.setItem('selectedCarIds', JSON.stringify(selectedCarIds));

    if (selectedCarIds.length === 0) {
        document.getElementById('comparisonSection').style.display = 'none';
    } else {
        const remainingCarId = selectedCarIds[0];
        fetchSingleCarData(remainingCarId);
    }

    clearCarData(position);
}



// 페이지 로드 시 로컬 스토리지에 저장된 데이터 가져오기
window.onload = function() {
    if (selectedCarIds.length > 0) {
        document.getElementById('comparisonSection').style.display = 'block';
        fetchComparisonData(selectedCarIds[0], selectedCarIds[1]);
    }
};

// Scroll to comparison section
document.querySelectorAll('#goToComparison').forEach(item => {
    item.addEventListener('click', event => {
        event.preventDefault(); // Prevent default anchor click behavior
        document.getElementById('comparisonSection').scrollIntoView({ behavior: 'smooth' });
    });
});
