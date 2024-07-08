/*
	댓글작성하기
*/

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
			$.ajax({
				type: "Get",
				url: "/comment/getBoard",
				data: {
					boardId: board_ID
				},
				success: function(response) {
					updateCommentsTable(response.comments, user_ID, board_ID,user_Nickname);
					$("#commentContent").val('');
					$("#contentErrors").text("");
				}
			});
		},
		error: function(xhr, status, error) {
			var response = JSON.parse(xhr.responseText);
			console.log(response);
			alert(response.errors.content);
			if (response.errors && Object.keys(response.errors).length > 0) {
				$.each(response.errors, function(field, errorMessage) {
					$("#" + field + "Errors").text(errorMessage);
				});
			} else {
				// 오류가 없으면 빈 문자열로 초기화
				$("#contentErrors").text("");
			}
		}
	});
}

/*
	댓글쓴후 보여주기
*/
function updateCommentsTable(comments, userId, boardId,user_Nickname) {
	$("#commentsTable tbody").empty();

	// 새로운 댓글 데이터를 추가합니다.
	$.each(comments, function(index, comment) {
		var deleteButton = '';

		// session.user.memberId와 comment.userId를 비교하여 삭제 버튼을 표시합니다.
		if (userId == comment.userId) {
			deleteButton = `
                <td>
                    <input type="hidden" value="${comment.commentId}" />
                    <input id="delete_boardId" type="hidden" value="${comment.boardId}" />
                    <button onclick="deleteComment(this)" data-delete-commentid="${comment.commentId}">삭제하기</button>
                </td>`;
		} else {
			deleteButton = '<td></td>';
		}
		let usercommentmember= comment.userNickname;
		var row = `
            <tr id="${comment.commentId}">
                <td >${comment.userNickname}</td>
                <td >${comment.content}</td>
                <td> 
                	<a onclick="addComment_Reply(this)" id="replyLink_${comment.commentId}"  write-data-commentid1="${comment.commentId}"  write-data-commentid2="${boardId}" write-data-commentid3="${user_Nickname}" write-data-commentid4="${usercommentmember}" style="font-size: small;" > 답글달기</a>
                	<a style="font-size: small;">|</a>
					<a onclick="comment_more(this)" id="comment_more_${comment.commentId}"  data-commentid1="${comment.commentId}" data-commentid2="${boardId}" style="font-size: small;" >더보기 </a><a style="font-size: small;">|</a>
					<a onclick="Comment_fold(this)" id="reply_fold${comment.commentId}" data-commentid1="${comment.commentId}" style="font-size: small;" class="disabled"> 접기 </a>
				</td>
                <td>${comment.commentDate}</td>
                ${deleteButton}
            </tr>
        `;
		$("#commentsTable tbody").append(row);
	});
}
/*
	댓글 삭제
 */
function deleteComment(link) {
	let user_ID = document.querySelector("#hidden_userId").value;
	let commentId = $(link).attr("data-delete-commentid");
	let boardId = document.querySelector("#delete_boardId").value;
	console.log("==============");
	console.log(commentId);
	if (confirm("정말로 삭제하시겠습니까?")) {

		$.ajax({
			type: "POST",
			url: "/comment/delete",
			data: {
				commentId: commentId
			},
			success: function(response) {
				alert(response.message);
				$.ajax({
					type: "Get",
					url: "/comment/delete",
					data: {
						boardId: boardId
					},
					success: function(response) {

						updateCommentsTable(response.comments, user_ID, boardId);
					}
				});
			},
			error: function() {
				alert("댓글 삭제 실패");
			}
		});
	}
}

/*
	대댓글 쓰기
 */
function addComment_Reply(link) {
	$(link).addClass("disabled");
	console.log(link);
	let commentId = $(link).attr("write-data-commentid1");
	let boardId = $(link).attr("write-data-commentid2");
	let memberNickname = $(link).attr("write-data-commentid3");
	let commentmemberNickname = $(link).attr("write-data-commentid4");
	console.log(commentmemberNickname);
	$("#comment_more" + commentId).removeClass("disabled");
	$("#comment_more_" + commentId).removeClass("disabled");
	$(".comment_moreList_" + commentId).remove();
	addReplyRow(link, commentId, boardId, memberNickname,commentmemberNickname);

}

/*
	대댓글 쓰는 폼
 */
function addReplyRow(link, commentId, boardId, memberNickname,commentmemberNickname) {
	console.log("================");
	console.log(commentmemberNickname);
	var replyRow = `
                <tr class="replyRow_${commentId}">
                    <td colspan="5">
                    	<div class="field-error" id="replyTextAreaError">

						</div>
                        <textarea class="replyTextArea" placeholder="${commentmemberNickname}님에게 남길 답글" style="resize: none;width:"100%;height:50px></textarea>
                        <button style="cursor:pointer;padding:5px 10px;" onclick="saveReply(${commentId},${boardId},'${memberNickname}')">저장</button>
                        <button style="cursor:pointer;padding:5px 10px;" data-commentid="${commentId}" onclick="cancelReply(this)">취소</button>
                    </td>
                </tr>
            `;
	// 새로운 답글 행을 현재 클릭된 답글 달기 버튼의 바로 아래에 추가합니다
	$(link).closest("tr").after(replyRow);
}

/*
	대댓글 취소하기
 */
function cancelReply(link) {
	let commentId = $(link).attr("data-commentid");

	$("#replyLink_" + commentId).removeClass("disabled");
	$(".replyRow_" + commentId).remove();
	$(".replyTextArea").val('');
}

/*
	대댓글 저장하기
 */
function saveReply(commentId, boardId, memberNickname) {
	let replyTextArea = document.querySelector(".replyTextArea").value;
	console.log(replyTextArea);
	$.ajax({
		type: "POST",
		url: "/comment/reply",
		data: {
			memberNickname: memberNickname,
			commentId: commentId,
			boardId: boardId,
			replyTextArea: replyTextArea
		},
		success: function(response) {
			alert(response.message);
			$("#replyLink_" + commentId).removeClass("disabled");
			$(".replyRow_" + commentId).remove();
			$(".replyTextArea").val('');
			// 오류가 없으면 빈 문자열로 초기화
			$(".contentErrors").text("");
		},
		error: function(xhr, status, error) {
			var response = JSON.parse(xhr.responseText);
			
			alert(response.errors.replyTextArea);
			if (response.errors && Object.keys(response.errors).length > 0) {
				$.each(response.errors, function(field, errorMessage) {
					$("#" + field + "Error").text(errorMessage);
				});
			} else {
				// 오류가 없으면 빈 문자열로 초기화
				$(".contentErrors").text("");
			}
		}

	})
}

/*
	대댓글 목록보여주기
 */
function comment_more(link) {
	console.log(link);
	$(link).addClass("disabled");
	let commentId = $(link).attr("data-commentid1");
	console.log(commentId);
	console.log("============");
	let boardId = $(link).attr("data-commentid2");
	$("#reply_fold" + commentId).removeClass("disabled");
	$("#replyLink_" + commentId).removeClass("disabled");
	$(".replyRow_" + commentId).remove();
	$(".replyTextArea").val('');
	comment_moreList(link, commentId, boardId);

}

/*
	대댓글 목록가져와서 보여주기
 */
function comment_moreList(link, commentId, boardId) {
	$.ajax({
		type: "Get",
		url: "/comment/more",
		data: {
			commentId: commentId,
			boardId: boardId
		},
		success: function(response) {
			$.each(response.comment_List, function(index, comment_list) {
				let date = formatDate(comment_list.commentDate);

				if (response.sessionNicename == comment_list.memberNickname) {
					deleteButton = `
                <td>
                    <button onclick="deleteaddComment(this,${comment_list.commentReplyId},${comment_list.commentId},${comment_list.boardId})" >삭제하기</button>
                </td>`;
				} else {
					deleteButton = '<td></td>';
				}
				let replyRow = `
				        <tr class="comment_moreList_${comment_list.commentId}">
				            <td style="font-size:20px"></td> 
				            <td colspan="2" style="font-size:20px">
				            	${comment_list.memberNickname} :  &nbsp;&nbsp;&nbsp;
				                ${comment_list.replyTextArea}
				            </td>
				            <td style="font-size:20px">
				            	${date}
				            </td> 
				            ${deleteButton}
				        </tr>
				    `;
				// 새로운 답글 행을 현재 클릭된 답글 달기 버튼의 바로 아래에 추가합니다
				$(link).closest("tr").after(replyRow);
			});
		}
	})

}
/*
대댓글 삭제
*/
function deleteaddComment(link, commentReplyId, commentId, boardId) {

	$.ajax({
		type: "Post",
		url: "/comment/more/delete",
		data: {
			boardId: boardId,
			commentReplyId: commentReplyId
		},
		success: function(response) {
			alert(response.message);
			$(".comment_moreList_" + commentId).remove();
			comment_more_delete_List(commentId, boardId);
		}
	})

}

function comment_more_delete_List(commentId, boardId) {
	console.log(commentId);
	$.ajax({
		type: "Get",
		url: "/comment/more",
		data: {
			commentId: commentId,
			boardId: boardId
		},
		success: function(response) {
			$.each(response.comment_List, function(index, comment_list) {
				let date = formatDate(comment_list.commentDate);

				if (response.sessionNicename == comment_list.memberNickname) {
					deleteButton = `
                <td>
                    <button onclick="deleteaddComment(this,${comment_list.commentReplyId},${comment_list.commentId},${comment_list.boardId})" >삭제하기</button>
                </td>`;
				} else {
					deleteButton = '<td></td>';
				}
				let replyRow = `
				        <tr class="comment_moreList_${comment_list.commentId}">
				            <td style="font-size:20px"></td> 
				            <td colspan="2" style="font-size:20px">
				            	${comment_list.memberNickname} :  &nbsp;&nbsp;&nbsp;
				                ${comment_list.replyTextArea}
				            </td>
				            <td style="font-size:20px">
				            	${date}
				            </td> 
				            ${deleteButton}
				        </tr>
				    `;
				// 새로운 답글 행을 현재 클릭된 답글 달기 버튼의 바로 아래에 추가합니다
				$("#" + commentId).after(replyRow);
			});
		}
	})

}

/*
더보기 접기
*/
function Comment_fold(link) {
	let commentId = $(link).attr("data-commentid1");
	$("#reply_fold" + commentId).addClass("disabled");
	$("#comment_more" + commentId).removeClass("disabled");
	$("#comment_more_" + commentId).removeClass("disabled");
	$(".comment_moreList_" + commentId).remove();

}


function formatDate(dateString) {
	let date = new Date(dateString);
	let year = date.getFullYear();
	let month = ("0" + (date.getMonth() + 1)).slice(-2); // Months are zero-based, so we add 1
	let day = ("0" + date.getDate()).slice(-2);
	return `${year}-${month}-${day}`;
}
