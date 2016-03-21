
<head>
	<meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <meta name="description" content="WhereisMyFriend?">
    <meta name="author" content="">
	
	
    <title>Login</title>
	
    <!-- Bootstrap core CSS -->
    <link href="css/welcss/bootstrap.min.css" rel="stylesheet">
	
    <!-- Custom styles for this template -->
    <link href="css/index/course.css" rel="stylesheet">
	
</head>
<?php
	
	include 'core/init.php';
	
	logged_in_redirect();
	
	if (empty($_POST) === FALSE) {
		$username = $_POST['username'];
		$password = $_POST['password'];
		
		if (empty($username) === TRUE || empty($password) === TRUE) {
			$errors[] = 'You need to enter username and password';        
		} 
		else if (user_exists($username) === FALSE) {
			$errors[] = "We couldn't find the username in our database. Are you sure you are registered? ";       
		}
		else if (user_active($username) === FALSE){
			$errors[] = "it seems that you haven't activated your account yet. Contact the admin ";       
			} else {
            if (strlen($password) > 32) {
			$errors[] = "The entered password is too long";           
            }   
			
            $login = login($username, $password);
            if ($login === FALSE) {
			$count = mysql_query("SELECT login_attempt FROM users WHERE username='$username'");
			$row = mysql_fetch_array($count);
			$realcount= $row['login_attempt'];
			
			$realcount = $realcount + 1;
			            
			mysql_query("UPDATE users SET login_attempt = '$realcount' WHERE username='$username'");
			
			$errors[] = "The username and password doesnt match";
			if ( $realcount > 3 ){
				mysql_query("UPDATE users SET active = 0 WHERE username='$username'");
			}

			
            }
            else {          
			
			//set the user session
			$_SESSION['user_id'] = $login;
			
			// redirect user to home
			header("location: index.php");
			exit();            
            }        
			}
			
			//print_r($errors); 
			
			} else {
			$errors[] = 'no data received';
			
			}
			
			
			
			if (empty($errors) === FALSE) {
			?>
			<body>
				<div class="site-wrapper">
					<div class="cover-container">
						<h2> We tried to log you in, but...</h2>
			<?php
				echo output_errors($errors);
			}
			?>
			<p> Click <a href="log.php" style='color: #43C6DB'> here </a> to go back </p>
					</div>
				</div>
			</body>			