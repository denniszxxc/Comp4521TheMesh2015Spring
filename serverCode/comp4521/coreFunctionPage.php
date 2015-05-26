<?php
	include_once('mySQLDbSchema.php');
?>
<?php
	function createDbConnection() { // create a database connection
		$servername = "localhost";
		$username = "root";
		$password = "";//can be changed
		$dbname = "library";
		$dBconn = new mysqli($servername, $username, $password, $dbname); // Create connection
		mysqli_set_charset($dBconn, "utf8");
		if ($dBconn->connect_error) // Check connection
			die("Connection failed: " . $dBconn->connect_error);
		return $dBconn;
	}
		
	function specialCharacterHandler($str) {
		$changedStr = str_replace ("'","$$$$$", $str); 
		//$newStr = htmlentities($_POST["json"]);
		//$newStr = htmlspecialchars($_POST["json"]);
		return $changedStr;
	}
		
	function backToOriginalString($data) {
		$originalStr = json_encode($data);
		$originalStr = str_replace ("$$$$$","'", $originalStr);
		$originalStr = str_replace ("^","&", $originalStr);
		$originalStr = str_replace ("\/","/", $originalStr);
		return $originalStr;
	}
	
	function checkCreatedTable() {
		$link = mysql_connect('localhost', 'root', '');
		$databaseName = "library";
		$sql = "CREATE DATABASE IF NOT EXISTS $databaseName CHARACTER SET utf8 COLLATE utf8_general_ci";
		$result = mysql_query($sql, $link);
		if($result);
			//echo "Succeed for createTable<br>";
		else
			echo "Fail for createDatabase<br>".mysqli_error($dbConn)."<br>";
		
		createTableBookInfo();
		createTableUserInfo();
		createTableOwnerBookInfo();
		createTableBorrowBookInfo();
	}
	
	function createTableBookInfo() {
		$dbConn = createDbConnection();
		$tableName = BookInfo::$DATABASE_TABLE_NAME;
		$serverBookId = BookInfo::$COLUMN_SERVER_BOOK_ID;
		$name = BookInfo::$COLUMN_NAME;
		$author = BookInfo::$COLUMN_AUTHOR;
		$cover = BookInfo::$COLUMN_COVER;
		$iSBN = BookInfo::$COLUMN_ISBN;
		
		$sql = "CREATE TABLE IF NOT EXISTS $tableName (
				$serverBookId INT(255) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
				$name VARCHAR(1024),
				$author VARCHAR(1024),
				$cover VARCHAR(1024),
				$iSBN VARCHAR(1024))";
				
		$result = $dbConn->query($sql);
		if($result);
			//echo "Succeed for $tableName<br>";
		else
			echo "Fail for $tableName<br>".mysqli_error($dbConn)."<br>";
	}
	
	function createTableUserInfo() {
		$dbConn = createDbConnection();
		$tableName = UserInfo::$DATABASE_TABLE_NAME;
		$userId = UserInfo::$COLUMN_USER_ID;
		
		$phone = UserInfo::$COLUMN_PHONE;
		$userSetting = UserInfo::$COLUMN_USER_SETTING;
		
		$sql = "CREATE TABLE IF NOT EXISTS $tableName (
				$userId VARCHAR(255) PRIMARY KEY,
				
				$phone INT(255),
				$userSetting TEXT)";
				
		$result = $dbConn->query($sql);
		if($result);
			//echo "Succeed for $tableName<br>";
		else
			echo "Fail for $tableName<br>".mysqli_error($dbConn)."<br>";
	}
	
	function createTableOwnerBookInfo() {
		$dbConn = createDbConnection();
		$tableName = OwnerBookInfo::$DATABASE_TABLE_NAME;
		$serverBookId = OwnerBookInfo::$COLUMN_SERVER_BOOK_ID;
		$userId = OwnerBookInfo::$COLUMN_USER_ID;
		$numOfBook = OwnerBookInfo::$COLUMN_NUM_OF_BOOK;
		$numOfBookAvailable = OwnerBookInfo::$COLUMN_NUM_OF_BOOK_AVAILABLE;
		$addedTime = OwnerBookInfo::$COLUMN_ADDED_TIME;
		$offerType = OwnerBookInfo::$COLUMN_OFFER_TYPE;
		
		$refServerBookIdTableName = BookInfo::$DATABASE_TABLE_NAME;
		$refServerBookId = BookInfo::$COLUMN_SERVER_BOOK_ID;
		$refUserIdTableName = UserInfo::$DATABASE_TABLE_NAME;
		$refUserId = UserInfo::$COLUMN_USER_ID;
		
		$sql = "CREATE TABLE IF NOT EXISTS $tableName (
				$serverBookId INT(255) UNSIGNED AUTO_INCREMENT,
				$userId VARCHAR(255),
				$numOfBook INT(255),
				$numOfBookAvailable INT(255),
				$addedTime TEXT,
				$offerType TEXT,
				PRIMARY KEY($serverBookId, $userId),
				FOREIGN KEY ($serverBookId) REFERENCES $refServerBookIdTableName($refServerBookId),
				FOREIGN KEY ($userId) REFERENCES $refUserIdTableName($refUserId) ON UPDATE CASCADE)";
				
		$result = $dbConn->query($sql);
		if($result);
			//echo "Succeed for $tableName<br>";
		else
			echo "Fail for $tableName<br>".mysqli_error($dbConn)."<br>";
	}
	
	function createTableBorrowBookInfo() {
		$dbConn = createDbConnection();
		$tableName = BorrowBookInfo::$DATABASE_TABLE_NAME;
		$lenderUserId = BorrowBookInfo::$COLUMN_LENDER_USER_ID;
		$serverBookId = BorrowBookInfo::$COLUMN_SERVER_BOOK_ID;
		$borrowerUserId = BorrowBookInfo::$COLUMN_BORROWER_USER_ID;
		
		$refServerBookIdTableName = BookInfo::$DATABASE_TABLE_NAME;
		$refServerBookId = BookInfo::$COLUMN_SERVER_BOOK_ID;
		$refUserIdTableName = UserInfo::$DATABASE_TABLE_NAME;
		$refUserId = UserInfo::$COLUMN_USER_ID;
		
		$sql = "CREATE TABLE IF NOT EXISTS $tableName (
				$lenderUserId VARCHAR(255),
				$serverBookId INT(255) UNSIGNED AUTO_INCREMENT,
				$borrowerUserId VARCHAR(255),
				PRIMARY KEY($lenderUserId, $serverBookId, $borrowerUserId),
				FOREIGN KEY ($serverBookId) REFERENCES $refServerBookIdTableName($refServerBookId),
				FOREIGN KEY ($lenderUserId) REFERENCES $refUserIdTableName($refUserId) ON UPDATE CASCADE,
				FOREIGN KEY ($borrowerUserId) REFERENCES $refUserIdTableName($refUserId) ON UPDATE CASCADE)";
				
		$result = $dbConn->query($sql);
		if($result);
			//echo "Succeed for $tableName<br>";
		else
			echo "Fail for $tableName<br>".mysqli_error($dbConn)."<br>";
	}
?>