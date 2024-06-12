// 유효성검사 효과 
function available_id() {
	let idbox = document.getElementById("id"); //입력된 아이디 
	idbox.className = '';
	
	let getId = idbox.value; 
	
	let validpattern = /^[a-zA-Z0-9]{5,10}$/;

	
	if(validpattern.test(getId)){
		alert('유효한 아이디 입니다.');
		idbox.className.add('change_green')
	} else {
		alert('유효하지 않은 아이디 입니다.')
		idbox.className.add('change_red')
	}
}