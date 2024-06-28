// My_page.html에서 닉네임 빠르게 수정하는 코드입니다.
function editNickname() {
    // 닉네임 셀을 가져옵니다.
    var nicknameCell = document.getElementById('nicknameCell');

    // 닉네임 셀의 현재 텍스트를 저장합니다.
    var currentNickname = nicknameCell.innerText;

    // 닉네임 셀의 내용을 인풋 필드로 바꿉니다.
    nicknameCell.innerHTML = '<input type="text" id="nicknameInput" value="' + currentNickname + '">';

    // 인풋 필드에 포커스를 줍니다.	
    document.getElementById('nicknameInput').focus();
}




//페이지 로그인 입니다
// function login_carini() {
//     if(아이디강 ㅇ){

//     }
// }


// 마이페이지 -> password.html -> edit_info.html
function change_info() {
    // 비밀번호 확인을 위한 팝업 창 열기
    var passwordWindow = window.open('password.html', '_blank', 'width=400,height=300');

    // 팝업 창에서 비밀번호가 확인된 후 호출될 함수
    window.verifyPasswordCallback = function(isVerified) {
        if (isVerified) {
            // 비밀번호가 확인되면 edit_info.html로 이동
            window.location.href = 'edit_info.html';
        } else {
            alert("비밀번호나 아이디를 확인해주세요");
        }
    };
}




// 소셜마이페이지 -> password.html -> social_edit_info.html
function social_change_info() {
    // 비밀번호 확인을 위한 팝업 창 열기
    var passwordWindow = window.open('password.html', '_blank', 'width=400,height=300');

    // 팝업 창에서 비밀번호가 확인된 후 호출될 함수
    window.verifyPasswordCallback = function(isVerified) {
        if (isVerified) {
            // 비밀번호가 확인되면 edit_info.html로 이동
            window.location.href = 'social_edit_info.html';
        } else {
            alert("비밀번호나 아이디를 확인해주세요");
        }
    };
}



// password.html 에서 비번확인 -> 
function verifyPassword() {
    var enteredPassword = document.querySelector('input[name="user_password"]').value;
 
    var storedPassword = "1234"; // db에서 가져온 회원 비번

    if (enteredPassword === storedPassword) {
        // 부모 창의 콜백 함수를 호출하여 비밀번호 확인 결과 전달
        window.opener.verifyPasswordCallback(true);
        window.close();
    } else {
        alert("비밀번호가 일치하지 않습니다.");
        window.opener.verifyPasswordCallback(false);
    }
}

//(그냥마이페이지) 수정된 모든 내역을 db로 전송 
function send_edit_info() {
    // db로 수정정보 보내기
    window.location.href = 'My_page.html';
} 


//(소셯) 수정된 모든 내역을 db로 전송 
function send_social_info() {
    // db로 수정정보 보내기
    window.location.href = 'Social_My_page.html';
} 


// 탈퇴하기 
function quit_member() {
    if(confirm('정말 탈퇴 하시겠습니까?')){
        alert('회원탈퇴가 완료되었습니다.')
        // 회원정보 탈퇴 쿼리문 전송
    }
    else{
        alert('회원탈퇴가 취소되었습니다.')
    }
}


// (보류) '수정하기' 버튼을 누르면 '수정확인 버튼으로 변경'
// function changeButton() {
//     var button = document.querySelector("#edit_save_td .edit_info");
//     if(button.innerHTML === "수정하기") {
//         button.innerHTML = "확인";
//         button.style.backgroundColor = "blue";
//         button.removeEventListener("click", changeButton);
//     }
// }




// 비밀번호 보이기 아이콘
$(document).ready(function(){
    $('.password-input-container i').on('click',function(){
        $(this).toggleClass('fa-eye fa-eye-slash');
        var input = $(this).prev('input');
        if(input.attr('type') === 'password'){
            input.attr('type', 'text');
        } else {
            input.attr('type', 'password');
        }
    });
});


// getboard list 게시판 스크립트 코드입니다. 

 // 변수 초기화
const itemsPerPage = 5; // 페이지당 항목 수
let currentPage = 1; // 현재 페이지



// 페이지를 변경하는 함수
function changePage(action) {
    const boardItems = document.querySelectorAll('.my_board_list');
    const totalPages = Math.ceil((boardItems.length - 1) / itemsPerPage); // 헤더 제외

    if (action === 'first') {
        currentPage = 1;
    } else if (action === 'prev' && currentPage > 1) {
        currentPage--;
    } else if (action === 'next' && currentPage < totalPages) {
        currentPage++;
    } else if (action === 'last') {
        currentPage = totalPages;
    }
    renderBoard();
}

// 게시판 항목을 렌더링하는 함수
function renderBoard() {
    const boardItems = document.querySelectorAll('.my_board_list');
    const start = (currentPage - 1) * itemsPerPage + 1; // 헤더 제외
    const end = start + itemsPerPage;

    // 모든 게시판 항목 숨기기
    boardItems.forEach((item, index) => {
        if (index === 0) return; // 헤더는 항상 보이게
        item.style.display = 'none';
    });

    // 현재 페이지에 해당하는 항목만 보이게 하기
    boardItems.forEach((item, index) => {
        if (index >= start && index < end) {
            item.style.display = 'flex';
        }
    });

    // 페이지 번호 업데이트
    renderPageNumbers();
}

// 페이지 번호를 렌더링하는 함수
function renderPageNumbers() {
    const boardItems = document.querySelectorAll('.my_board_list');
    const totalPages = Math.ceil((boardItems.length - 1) / itemsPerPage); // 헤더 제외
    const pageNumbers = document.getElementById('page_numbers');
    pageNumbers.innerHTML = '';

    for (let i = 1; i <= totalPages; i++) {
        const pageNumber = document.createElement('span');
        pageNumber.className = 'page-number';
        pageNumber.textContent = i;
        pageNumber.onclick = () => goToPage(i);

        if (i === currentPage) {
            pageNumber.classList.add('active-page'); // 현재 페이지를 볼드 처리
        }

        pageNumbers.appendChild(pageNumber);
    }
}

// 특정 페이지로 이동하는 함수
function goToPage(pageNumber) {
    currentPage = pageNumber;
    renderBoard();
}

// 초기 렌더링
document.addEventListener('DOMContentLoaded', renderBoard);
