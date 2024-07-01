function selectAll() {
	const headerCheckbox = document.getElementById("header_checkbox");
    const isChecked = headerCheckbox.checked;
    const allCheckboxes = document.querySelectorAll('.row_checkbox');
    allCheckboxes.forEach(checkbox => checkbox.checked = isChecked);
}
function submitForm() {

       const checkboxes = document.querySelectorAll('.row_checkbox');
       const selectedBoardsData = [];


	   checkboxes.forEach(checkbox => {
	           if (checkbox.checked) {
	               const boardDataElement = checkbox.parentNode.parentNode.querySelectorAll('td'); // 체크된 체크박스의 부모(tr)에서 데이터를 선택
				   console.log(boardDataElement)
	               const boardData = {
	                   boardId: boardDataElement[1].textContent.trim(), // 각 열의 데이터를 선택하여 boardData 객체에 추가
	                   boardTitle: boardDataElement[2].textContent.trim(),
	                   boardWriter: boardDataElement[3].textContent.trim(),
	                   boardDate: boardDataElement[4].textContent.trim(),
	                   // 필요한 다른 필드들을 추가
	               };
	               selectedBoardsData.push(boardData); // 선택된 게시물의 데이터를 배열에 추가
	           }
	       });

	       document.getElementById('selectedBoardsData').value = JSON.stringify(selectedBoardsData);
	       document.querySelector('form').submit();
   }