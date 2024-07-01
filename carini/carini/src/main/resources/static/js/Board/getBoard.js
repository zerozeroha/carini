function addComment() {
	let commentContent = document.querySelector("#commentContent").value;
	let user_ID = document.querySelector("#hidden_userId").value;
	let board_ID = document.querySelector("#hidden_boardId").value;
	let user_Nickname = document.querySelector("#hidden_userNickname").value;

	event.preventDefault();
	$.ajax({
		type: "Post",
		url: "/comment/write",
		data: {
			boardId: board_ID,
			userId: user_ID,
			userNickname: user_Nickname,
			content: commentContent
		},
		success: function(response) {
			alert(response.message);
			window.location.href = response.redirectUrl;
		},
		error: function(xhr, status, error) {
			var response = JSON.parse(xhr.responseText);
			alert(response.errors.content);
			if (response.errors && Object.keys(response.errors).length > 0) {
				$.each(response.errors, function(field, errorMessage) {
					console.log(errorMessage)
					$("#" + field + "Errors").text(errorMessage);
				});
			} else {
				// 오류가 없으면 빈 문자열로 초기화
				$("#contentErrors").text("");
			}
		}
	});
}

function deleteComment() {
	let commentId = document.querySelector("#delete_commentId").value;
	let boardId = document.querySelector("#delete_boardId").value;
	if (confirm("정말로 삭제하시겠습니까?")) {

		$.ajax({
			type: "POST",
			url: "/comment/delete",
			data: {
				commentId: commentId,
				boardId: boardId
			},
			success: function(response) {
				alert(response.message);
				window.location.href = response.redirectUrl;
			},
			error: function() {
				alert("댓글 삭제 실패");
			}
		});
	}
}


