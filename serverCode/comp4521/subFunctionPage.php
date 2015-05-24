<?php
	header('Content-Type: text/html; charset=utf-8');
	/**
		Sub-functions for bookListConfirm function
	*/	
	function checkBookInfoRecord($dbConn, $iSBN) {
		$sql = "SELECT iSBN FROM book_info WHERE iSBN = '$iSBN'";
		$result = $dbConn->query($sql);
		$mysqlNumRows = $result->num_rows;
		
		if($result);
			//echo "Succeed for checkBookInfoRecord\n";
		else
			echo "Fail for checkBookInfoRecord\n".mysqli_error($dbConn)."\n";
	  
		return $mysqlNumRows;
	}
	
	function insertBookInfo($dbConn, $name, $author, $cover, $iSBN) {
		$bookInfoSql = "INSERT INTO book_info VALUES(null,'$name','$author','$cover','$iSBN')";
		$result = $dbConn->query($bookInfoSql);
		$serverBookId = $dbConn->insert_id;
		
		if($result);
			//echo "Succeed for insertBookInfo\n";
		else
			echo "Fail for insertBookInfo\n".mysqli_error($dbConn)."\n";
		
		return $serverBookId;
	}
	
	function insertOwnerBookInfo($dbConn, $serverBookId, $userId, $addedTime, $offerType) {
		$ownerBookInfoSql = "INSERT INTO owner_book_info VALUES($serverBookId, '$userId', 1, 1, '$addedTime', '$offerType')";
		$result = $dbConn->query($ownerBookInfoSql);
		
		if($result);
			//echo "Succeed for insertOwnerBookInfo\n";
		else
			echo "Fail for insertOwnerBookInfo\n".mysqli_error($dbConn)."\n";

		return $serverBookId;
	}
	
	function checkOwnerBookInfo($dbConn, $serverBookId, $userId, $offerType){
		$sql = "SELECT server_book_id, user_id, offer_type FROM owner_book_info WHERE server_book_id = $serverBookId AND user_id = '$userId' AND offer_type = '$offerType'";
		$result = $dbConn->query($sql);
		$mysqlNumRows = $result->num_rows;
		
		if($result);
			//echo "Succeed for checkOwnerBookInfo\n";
		else
			echo "Fail for checkOwnerBookInfo\n".mysqli_error($dbConn)."\n";
		
		return $mysqlNumRows;
	}
	
	function findBookInfo($dbConn, $iSBN) {
		$sql = "SELECT server_book_id FROM book_info WHERE iSBN = '$iSBN'";
		$result = $dbConn->query($sql);
		if($result);
			//echo "Succeed for findBookInfo\n";
		else
			echo "Fail for findBookInfo\n".mysqli_error($dbConn)."\n";
		$row = $result->fetch_assoc();
		$serverBookId = intval($row['server_book_id']);
		
		return $serverBookId;
	}
	
	function updateOwnerBookInfo($dbConn, $serverBookId, $userId, $addedTime, $offerType) {
		$sql = "UPDATE owner_book_info SET num_of_book = num_of_book + 1, num_of_book_available = num_of_book_available + 1, added_time = '$addedTime'
				WHERE server_book_id = $serverBookId AND user_id = '$userId' AND offer_type = '$offerType'"; 
		$result = $dbConn->query($sql);
		if($result);
			//echo "Succeed for updateOwnerBookInfo\n";
		else
			echo "Fail for updateOwnerBookInfo\n".mysqli_error($dbConn)."\n";
		
		return $serverBookId;
	}
?>