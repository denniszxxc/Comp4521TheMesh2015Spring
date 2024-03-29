<?php
	include_once('coreFunctionPage.php');
	include_once('functionPage.php');
?>
<?php 
    checkCreatedTable();
	if(isset($_POST["json"])) {
		$newStr = specialCharacterHandler($_POST["json"]); // handle the special characters of the JSON string
		$data = json_decode($newStr);
		$serverBookId;

		if($data->handle_method == "BookListConfirm") {
			$serverBookId = bookListConfirm($data);
			$jsonBookIds = json_encode(array('book_ids' => $serverBookId));
			echo $jsonBookIds."\n";
		} else if($data->handle_method == "UserRegister") 
			userRegister($data);
		else if($data->handle_method == "DeleteBook")
			deleteBook($data);
		else if($data->handle_method == "ChangeUsername")
			changeUsername($data);
		else if($data->handle_method == "ChangeBookStatus") 
			changeBookStatus($data);
		else if($data->handle_method == "GetAListOfBooks"){
			$list = getAListOfBooks($data);
			echo $list."\n";
		} else if($data->handle_method == "GetAListOfUsers") {
			$list = getAListOfUsers($data);
			$jsonList = json_encode($list);
			echo $jsonList."\n";
		} else if($data->handle_method == "GetAListOfBookFromTheUser") {
			$jsonList = getAListOfBookFromTheUser($data);
			echo $jsonList."\n";
		}else if($data->handle_method == "confirmBookBorrow") {
			$result = confirmBookBorrow($data);
			$jsonResult = json_encode($result);
			echo $jsonResult;
		} else if($data->handle_method == "CheckConfirmBookBorrow") {
			$result = checkConfirmBookBorrow($data);
			$jsonResult = json_encode($result);
			echo $jsonResult;
		} else if($data->handle_method == "deleteConfirmBookBorrow") {
			$result = deleteConfirmBookBorrow($data);
			$jsonResult = json_encode($result);
			echo $jsonResult;
		} else
			echo "Error!.\n";
	} else {
		echo "Fail!\n";
	}
?>