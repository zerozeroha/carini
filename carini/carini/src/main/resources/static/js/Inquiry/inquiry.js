var temp = 0; // 세션에서 temp 값 가져오기
	console.log(temp);
	function pop() {
		$.ajax({
			type: "GET",
			url: "/inquiry/inquiryList",
			success: function (response) {
				if (!response.success) {
					alert(response.message);
					window.location.href = response.redirectUrl;
				} else {
					var inquiries = response.inquirys;
					var qnaUl = $('#qna_ul');
					qnaUl.empty(); // 기존 내용 삭제

					for (var i = 0; i < inquiries.length; i++) {
						var inquiry = inquiries[i];

						// 클로저를 이용한 클릭 이벤트 핸들러 설정
						(function (inquiry) {
							var qnaDiv = $('<div>').addClass('qna_div').click(function () {
								viewdetail(inquiry.reId);
							});

							var qnaHeader = $('<div>').addClass('qna_header').css("height", "30px");
							var qnaTitle = $('<div>').addClass('qna_title').append($('<span>').text(inquiry.reTitle)).css("width", "150px").css("font-weight", "bold").css("text-align", "left");
							var qnaDate = $('<div>').addClass('qna_title').attr('id', 'q_date').css("margin-left", "35%").append($('<span>').text(new Date(inquiry.reDate).toLocaleDateString()));
							var deleteButton = $('<button>').addClass('delete_qna').text('x').click(function (event) {
								event.stopPropagation(); // 클릭 이벤트 전파 방지
								deleteInquiry(inquiry.reId);
							});

							qnaHeader.append(qnaTitle, qnaDate, deleteButton).css("display", "flex").css("justify-content", "space-between");

							var qnaWrapper = $('<div>').attr('id', 'qna_wrapper');
							var qnaContent = $('<div>').addClass('qna_content').append(
								$('<div>').attr('id', 'click_qna').text(inquiry.reContent),
								$('<span>').addClass('qna_result').text(inquiry.reCheckRq ? '답변 완료' : '답변 대기 중').css('color', inquiry.reCheckRq ? 'black' : 'black').css('border', inquiry.reCheckRq ? '2px solid green' : '2px solid red').css("font-weight", "bold").css("margin-top", "10%").css("width", "120px")
							).css("display", "flex").css("justify-content", "space-between");


							qnaWrapper.append(qnaContent);
							qnaDiv.append(qnaHeader, qnaWrapper);
							qnaDiv.css('height', '130px');
							qnaUl.append(qnaDiv);
						})(inquiry); // IIFE로 inquiry를 전달
					}
				}
			}
		});

		// 팝업 띄우는 로직
		if (temp == 0) {
			temp = 1;
			var popup = document.getElementById('popup');
			var overlay = document.getElementById('popup-overlay');
			popup.classList.add('popup');
			popup.classList.remove('popup_hidden');
			overlay.classList.add('active');
		}
	}

	// 삭제 기능 함수
	function deleteInquiry(reId) {
		$.ajax({
			type: "POST",
			url: "/inquiry/inquirydelete",
			data: {reId: reId},
			success: function (response) {
				console.log("111111111111");
				alert(response.message);
				 window.location.reload();

			}
		});
	}

	//팝업창 닫기 기능
	function closePopup() {
		temp = 0;
		var popup = document.getElementById('popup');
		var insertpop = document.getElementById('insertpopup');
		var getpopup = document.getElementById('getpopup');
		var editpopup = document.getElementById('editpopup');
		var overlay = document.getElementById('popup-overlay');

		popup.classList.add('popup_hidden');
		popup.classList.remove('popup');
		overlay.classList.remove('active');

		insertpop.classList.add('popup_hidden');
		insertpop.classList.remove('popup');
		overlay.classList.remove('active');

		getpopup.classList.add('popup_hidden');
		getpopup.classList.remove('popup');
		overlay.classList.remove('active');
	}


	function to_insert_popup() {
		if (temp == 1) {
			var popup = document.getElementById('popup');
			var insertpop = document.getElementById('insertpopup');
			var overlay = document.getElementById('popup-overlay');
			popup.classList.add('popup_hidden');
			popup.classList.remove('popup');
			popup.classList.remove('overlay');
			insertpop.classList.add('popup');
			insertpop.classList.remove('popup_hidden');
			insertpop.classList.remove('overlay');
		}
	}

	function to_popup() {
		if (temp == 1)
			var getpopup = document.getElementById('getpopup')
		var popup = document.getElementById('popup')
		getpopup.classList.add('popup_hidden');
		getpopup.classList.remove('popup');
		popup.classList.add('popup');
		popup.classList.remove('popup_hidden');

	}

	function viewdetail(reId) {
		console.log(reId)
		$.ajax({
			type: "GET",
			data: {reId: reId},
			url: "/inquiry/getinquiry",
			success: function (response) {
				if (!response.success) {
					alert(response.message);
					pop();
				} else {

					var inquiry = response.inquiry;

					var popup = document.getElementById('popup');
					var getpopup = document.getElementById('getpopup');
					var overlay = document.getElementById('popup-overlay');
					popup.classList.add('popup_hidden');
					popup.classList.remove('popup');
					getpopup.classList.add('popup');
					getpopup.classList.remove('popup_hidden');
					console.log(inquiry.reDateRq);
					console.log(inquiry.reCheckRq);
					// 사용자 문의 정보 출력
					$('#detailsreTitle').text(inquiry.reTitle); // 사용자 문의 제목
					$('#q_content').text(inquiry.reContent);// 사용자 문의 내용
					$('#detailsreDate').text(new Date(inquiry.reDate).toLocaleDateString()); // 사용자 문의 날짜   
					// 관리자 답변 정보 출력
					$('#detailsreTitleRq').text(inquiry.reTitleRq) // 관리자 문의 제목
					$('#a_content').text(inquiry.reContentRq).css('border', inquiry.reCheckRq ? '1px solid green' : '1px solid red');// 관리자 문의 내용

					var reDateRqText = inquiry.reDateRq ? new Date(inquiry.reDateRq).toLocaleDateString() : '';
					$('#detailsreDateRq').text(reDateRqText).css("height", "35px"); // 관리자 문의 날짜
					$('.a_section').css('border', inquiry.reCheckRq ? '1px solid green' : '1px solid red');
				}
			},
			error: function (xhr, status, error) {
				var response = JSON.parse(xhr.responseText);
				alert(response.message);
			}
		});
	}

	function verifyPassword(event) {
		event.preventDefault();
		var reTitle = $("input[name='reTitle']").val();
		var reContent = $("#write_qna").val();
		$.ajax({
			type: "Post",
			url: "/inquiry/inquirywrite",
			data: {
				reTitle: reTitle,
				reContent: reContent
			},

			success: function (response) {
				// 오류 메시지 초기화
				$("#reTitleError").text("");
				$("#reContentError").text("");
				if (response.success) {
					alert(response.message);
					 window.location.reload();
				}
			},
			error: function (xhr, status, error) {
				var response = JSON.parse(xhr.responseText);
				alert(response.message);
				$.each(response.errors, function (field, errorMessage) {
					if (field === "reTitle") {
						$("#reTitleError").text(errorMessage);
						$("#reContentError").text("");
					} else if (field === "reContent") {
						$("#reContentError").text(errorMessage);
						$("#reTitleError").text("");
					}

				});
				if (response.errors && response.errors.reContent && response.errors.reTitle) {
					var reContent = response.errors.reContent; // reContent에 대한 오류 메시지를 가져옴
					var reTitle = response.errors.reTitle; // reContent에 대한 오류 메시지를 가져옴
					// reContentError와 reTitleError에 오류 메시지 설정
					$("#reContentError").text(reContent);
					$("#reTitleError").text(reTitle);
				}
			}
		});
	}