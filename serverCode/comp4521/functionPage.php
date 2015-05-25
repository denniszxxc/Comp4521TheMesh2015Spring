<?php
	include_once('coreFunctionPage.php');
	include_once('subFunctionPage.php');	
?>
<?php
	function bookListConfirm($data) {
		$dbConn = createDbConnection();
		$userId = $data->user_id;
		$bookData = $data->book_data; // array
		$arrSize = count($data->book_data);
		$addedTime = $data->added_time;
		$offerType = $data->offer_type;
		
		for($i = 0; $i < $arrSize; $i++) {
			$component = explode("@", $bookData[$i]);
			$name[$i] = $component[0];
			$author[$i] = $component[1];
			$cover[$i] = $component[2];
			$iSBN[$i] = $component[3];
			
			if(!checkBookInfoRecord($dbConn, $iSBN[$i])) {
				$serverBookId[$i] = insertBookInfo($dbConn, $name[$i], $author[$i], $cover[$i], $iSBN[$i]);
				$serverBookId[$i] = insertOwnerBookInfo($dbConn, $serverBookId[$i], $userId, $addedTime, $offerType);
			} else {
				$serverBookId[$i] = findBookInfo($dbConn, $iSBN[$i]);
				if(!checkOwnerBookInfo($dbConn, $serverBookId[$i], $userId, $offerType))
					$serverBookId[$i] = insertOwnerBookInfo($dbConn, $serverBookId[$i], $userId, $addedTime, $offerType);
				else 
					$serverBookId[$i] = updateOwnerBookInfo($dbConn, $serverBookId[$i], $userId, $addedTime, $offerType);
			}
		}
		return $serverBookId;
	}
	
	function userRegister($data) {
		$dbConn = createDbConnection();
		$userId = $data->user_id;
		$phone = $data->phone;
		$userSetting = $data->user_setting;
		$tableName = UserInfo::$DATABASE_TABLE_NAME;
		
		$sql = "SELECT user_id FROM $tableName WHERE user_id = '$userId'";
		$result = $dbConn->query($sql);
		if($result);
			//echo "Succeed for finding the book\n";
		else
			echo "Fail for finding the book\n".mysqli_error($dbConn)."\n";
		$mysqlNumRows = $result->num_rows;
		
		if($mysqlNumRows) {
			echo "Succeed for user register\n";
		} else {
			$sql = "INSERT INTO $tableName VALUES('$userId', '$phone', '$userSetting')";
			$result = $dbConn->query($sql);
			if($result)
				echo "Succeed for user register\n";
			else
				echo "Fail for user register\n".mysqli_error($dbConn)."\n";
		}
	}
	
	function deleteBook($data) {
		$dbConn = createDbConnection();
		$serverBookId = $data->book_id;//need
		$userId = $data->user_id;//need
		$tableName = OwnerBookInfo::$DATABASE_TABLE_NAME;
		
		$sql = "SELECT server_book_id, user_id FROM $tableName WHERE server_book_id = $serverBookId AND user_id = '$userId'";
		$result = $dbConn->query($sql);
		if($result);
			//echo "Succeed for finding the book\n";
		else
			echo "Fail for finding the book\n".mysqli_error($dbConn)."\n";
		$mysqlNumRows = $result->num_rows;
		
		if($mysqlNumRows) {
			$sql = "DELETE FROM $tableName WHERE server_book_id = $serverBookId AND user_id = '$userId'";
			$result = $dbConn->query($sql);
			if($result);
			//echo "Succeed for deleting the book\n";
			else
				echo "Fail for deleting the book\n".mysqli_error($dbConn)."\n";
			echo "Success for deleting the book.\n";
		} else //cannot find the book
			echo "Fail for deleting the book.\n";
	}
	
	function changeUsername($data) {
		$dbConn = createDbConnection();
		$userId = $data->user_id; // need
		$newUserId = $data->new_user_id; // need
		$tableName = UserInfo::$DATABASE_TABLE_NAME;
		
		$sql = "SELECT * FROM $tableName WHERE user_id = '$userId'";
		$result = $dbConn->query($sql);
		if($result);
			//echo "Succeed for finding the user ID\n";
		else
			echo "Fail for finding the user ID\n".mysqli_error($dbConn)."\n";
		$mysqlNumRows = $result->num_rows;
		
		if($mysqlNumRows) {
			$sql = "SELECT * FROM user_info WHERE user_id = '$newUserId'";
			$result = $dbConn->query($sql);
			$mysqlNumRows = $result->num_rows;
			if(!$mysqlNumRows) {
				$sql = "UPDATE user_info SET user_id = '$newUserId' WHERE user_id = '$userId'";
				$result = $dbConn->query($sql);
				
				if($result);
				//echo "Succeed for changing the user ID\n";
				else
					echo "Fail for changing the user ID\n".mysqli_error($dbConn)."\n";
				echo "Success for changing the user ID.\n";
			} else 
				echo "The user ID exists already.\n";
		} else //cannot find the user ID
			echo "Fail for changing the username.\n";
		
	}
	
	function changeBookStatus($data) {
		$dbConn = createDbConnection();
		$userId = $data->lender_user_id;//need
		$serverBookId = $data->book_id;//need
		$targetStatus = $data->target_status;//need
		$tableName = OwnerBookInfo::$DATABASE_TABLE_NAME;
		
		$sql = "SELECT server_book_id, user_id FROM $tableName WHERE server_book_id = $serverBookId AND user_id = '$userId'";
		$result = $dbConn->query($sql);
		if($result);
			//echo "Succeed for finding the book status\n";
		else
			echo "Fail for finding the user ID\n".mysqli_error($dbConn)."\n";
		$mysqlNumRows = $result->num_rows;
		
		if($mysqlNumRows) {
			if($targetStatus == 1) {
				$sql = "UPDATE $tableName SET num_of_book_available = num_of_book_available + 1 WHERE server_book_id = $serverBookId AND user_id = '$userId'";
				$result = $dbConn->query($sql);
			
				if($result);
				//echo "Succeed for changing the book status\n";
				else
					echo "Fail for changing the book status\n".mysqli_error($dbConn)."\n";
				
				echo "Success\n";
			}
			else {// $targetStatus == 0
				$sql = "SELECT num_of_book_available FROM $tableName WHERE server_book_id = $serverBookId AND user_id = '$userId'";
				$result = $dbConn->query($sql);
				$row = $result->fetch_assoc();
				
				if(intval($row['num_of_book_available']) == 0)
					echo "Fail\n";//The book is not available
				else {
					$sql = "UPDATE $tableName SET num_of_book_available = num_of_book_available - 1 WHERE server_book_id = $serverBookId AND user_id = '$userId'";
					$result = $dbConn->query($sql);
					
					if($result);
					//echo "Succeed for changing the book status\n";
					else
						echo "Fail for changing the book status\n".mysqli_error($dbConn)."\n";
					
					echo "Success\n";
				}
				
			}
			
		} else //cannot find the book status
			echo "Fail for changing the book status.\n";
			
	}
	
	function getAListOfBooks($data) {
		$dbConn = createDbConnection();
		$userId = $data->user_id;//need
		
		$sql = "SELECT * FROM book_info, owner_book_info WHERE user_id != '$userId' AND book_info.server_book_id = owner_book_info.server_book_id ORDER BY book_info.name ASC";
		$result = $dbConn->query($sql);
		$i = 0;
		while($row = $result->fetch_assoc()) {
			$serverBookId[$i] = intval($row['server_book_id']);
			$theUserId[$i] = $row['user_id'];
			$numOfBook[$i] = intval($row['num_of_book']);
			$numOfBookAvailable[$i] = intval($row['num_of_book_available']);
			$addedTime[$i] = $row['added_time'];
			$offerType[$i] = $row['offer_type'];
			$name[$i] = $row['name'];
			$author[$i] = $row['author'];
			$cover[$i] = $row['cover'];
			$iSBN[$i] = $row['iSBN'];
			
			$i++;
		}
		
		if($i != 0) {
			 $list = array('server_book_id' => $serverBookId,
									'user_id' => $theUserId,
								'num_of_book'=> $numOfBook,
					'num_of_book_available' => $numOfBookAvailable,
								'added_time' => $addedTime,
								'offer_type' => $offerType,
									'name' => $name,
									'author' => $author,
									'cover' => $cover,
									'isbn' => $iSBN);
					
			$originalStr = backToOriginalString($list);
			return $originalStr;
		} else {
			$list = array();
			//$originalStr = backToOriginalString($list);
			$originalStr = json_encode($list, JSON_FORCE_OBJECT);
			return $originalStr;
		}
	}
	
	function getAListOfUsers($data) {
		$dbConn = createDbConnection();
		$userId = $data->user_id;
		$sql = "SELECT * FROM user_info WHERE user_id != '$userId'";
		$result = $dbConn->query($sql);
		
		$i = 0;
		while($row = $result->fetch_assoc()) {
			$theUserId[$i] = $row['user_id'];
			$phone[$i] = $row['phone'];
			$userSetting[$i] = $row['user_setting'];
			
			$i++;
		}	
		$list = array('user_id' => $theUserId,
						'phone' => $phone,
				 'user_setting' => $userSetting);
				
		return $list = array();
	}
	
	function getAListOfBookFromTheUser($data) {
		$dbConn = createDbConnection();
		$targetUserId = $data->target_user_id;//need
		$sql = "SELECT * FROM book_info, owner_book_info WHERE user_id = '$targetUserId' AND book_info.server_book_id = owner_book_info.server_book_id ORDER BY book_info.name ASC";
		$result = $dbConn->query($sql);
		if($result);
			//echo "Succeed for changing the book status\n";
			else
				echo "Fail for changing the book status\n".mysqli_error($dbConn)."\n";
		$i = 0;
		while($row = $result->fetch_assoc()) {
			$serverBookId[$i] = intval($row['server_book_id']);
			$theUserId[$i] = $row['user_id'];
			$numOfBook[$i] = intval($row['num_of_book']);
			$numOfBookAvailable[$i] = intval($row['num_of_book_available']);
			$addedTime[$i] = $row['added_time'];
			$offerType[$i] = $row['offer_type'];
			$name[$i] = $row['name'];
			$author[$i] = $row['author'];
			$cover[$i] = $row['cover'];
			$iSBN[$i] = $row['iSBN'];
			
			$i++;
		}
		if($i != 0) {
			$list = array('server_book_id' => $serverBookId,
									'user_id' => $theUserId,
								'num_of_book'=> $numOfBook,
					'num_of_book_available' => $numOfBookAvailable,
								'added_time' => $addedTime,
								'offer_type' => $offerType,
									'name' => $name,
									'author' => $author,
									'cover' => $cover,
									'isbn' => $iSBN);
					
			$originalStr = backToOriginalString($list);
			return $originalStr;
		} else {
			$list = array();
			//$originalStr = backToOriginalString($list);
			$originalStr = json_encode($list, JSON_FORCE_OBJECT);
			return $originalStr;
		}
		
	}
	
	function confirmBookBorrow($data) {
		$dbConn = createDbConnection();
		$userId = $data->lender_user_id;
		$borrowerUserId = $data->borrower_user_id;//need
		$serverBookId = $data->book_id;//need
		
		$check = checkConfirmBookBorrowExist($dbConn, $userId, $serverBookId);
		
		if($check) {
			$resultConfirmBookBorrow = array('result' => 'fail');
		} else {// $check->result == 'false'
			$sql = "INSERT INTO borrow_book_info VALUES('$userId', $serverBookId, '$borrowerUserId')";
			$result = $dbConn->query($sql);
			
			$resultConfirmBookBorrow = array('result' => 'success');
		}
		return $resultConfirmBookBorrow;
	}
	
	function checkConfirmBookBorrow($data) {
		$dbConn = createDbConnection();
		$userId = $data->lender_user_id;
		//$borrowerUserId = $data->borrower_user_id;//need
		$serverBookId = $data->book_id;//need
		
		$check = checkConfirmBookBorrowExist($dbConn, $userId, $serverBookId);
		
		if($check)
			$resultCheckConfirmBookBorrow = array('result' => 'true');
		else
			$resultCheckConfirmBookBorrow = array('result' => 'false');
		
		return  $resultCheckConfirmBookBorrow;
	}
	
	function deleteConfirmBookBorrow($data){
		$dbConn = createDbConnection();
		$userId = $data->lender_user_id;
		$borrowerUserId = $data->borrower_user_id;//need
		$serverBookId = $data->book_id;//need
		
		$check = checkConfirmBookBorrowExist($dbConn, $userId, $serverBookId);
		
		if($check){
			$sql = "DELETE FROM borrow_book_info WHERE server_book_id = $serverBookId AND user_id = '$userId' AND borrower_user_id = '$borrowerUserId'";
			$result = $dbConn->query($sql);
			
			$resultDeleteConfirmBookBorrow = array('result' => 'true');
		}
		else
			$resultDeleteConfirmBookBorrow = array('result' => 'false');
		
		return $resultDeleteConfirmBookBorrow;
	}
	
?>