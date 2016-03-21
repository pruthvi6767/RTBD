<html>
	<head>
		<meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<meta name="description" content="WhereisMyFriend?">
		<meta name="author" content="">
		<!-- Bootstrap core CSS  --> 
		<link href="css/index/bootstrap.min.css" rel="stylesheet">
		
		<!--Custom styles for this template -->
		<link href="css/index/course.css" rel="stylesheet">
		
	</head>
	
	
	<?php 
		
		
		
		include 'core/init.php';
		logged_in_redirect();
		
		
		if (empty($_POST) === FALSE) {
			$required_fields =  array('username', 'password', 'password_again', 'first_name', 'email'); 
			foreach($_POST as $key=>$value) {
				if (empty($value) && in_array($key, $required_fields) ===  TRUE) {
					$errors[] = "fields with * are required";
					break 1;            
				}                
			}
			if (empty($errors) === TRUE) {
				if (user_exists($_POST['username']) === TRUE) {
					$errors[] = 'Sorry, the username \'' . $_POST['username'] . '\' is already taken.';             
				}
				if (preg_match("/\\s/", $_POST['username']) == TRUE) { 
					$errors[] = "your username  must not contain any spaces";             
				}
				
			if (strlen($_POST['password']) < 6) {
			$errors[] = "your password must be at least 6 characters long";             
			}
			if ($_POST['password'] !== $_POST['password_again']) {
			$errors[] = "your passwords doesn't match";            
			}
			if (filter_var($_POST['email'], FILTER_VALIDATE_EMAIL) === FALSE) {
			$errors[] = "A valid email address is required";             
			}
			if (email_exists($_POST['email']) === TRUE) {
			$errors[] = 'Sorry, the email \'' . $_POST['email'] . '\' is already in use.';          
			}
			}
			}
			
			//print_r($errors);
			
			
			?>
			
			
			
			<?php
			
			if (isset($_GET['success']) && empty($_GET['success'])) {
			echo ' You have been successfully registered with WhereisMyFriend? , Please follow the instructions sent in the mail to activate your account';
			echo  "<br> To go to login page click <a href='log.php' style='color: #43C6DB'>here</a>";
			
			} else {   
			
			if (empty($_POST) === FALSE && empty($errors) === TRUE) {
			//register user
			$register_data = array(
            'username'     => $_POST['username'],
            'password'     => $_POST['password'],
            'first_name'   => $_POST['first_name'],
            'last_name'    => $_POST['last_name'],            
            'email'        => $_POST['email'],
            'email_code'   => md5($_POST['username'] + microtime())
			);
			register_user($register_data);
			//redirect user
			header('location: register.php?success');
			exit();
			
			
			
			}  else if (empty($errors) === FALSE) {
			//output errors
			echo output_errors($errors);    
			}
			
			
			
			?>
			
		    <div class="site-wrapper">
			
			<div class="site-wrapper-inner">
			
			<div class="cover-container">
			
			<div class="masthead clearfix">
			
			
			<div class="inner">
			<h2>WhereisMyFriend?</h2>
			
			
			
			<h2>Register</h2>
			<form action="" method="post">
			<ul>
            <li>
			<br>
			<input type="username" id="inputusername" name="username" class="form-control" placeholder="Username" required>
            </li>
            <li>
			<br>
			<input type="password" id="inputpassword" name="password" class="form-control" placeholder="Password" required>
		    </li>
            <li>
			<br>
			<input type="password" id="inputpassword" name="password_again" class="form-control" placeholder="Retype Password" required>
			
            </li>
            <li>
			<br>
			<input type="firstname" id="inputfirstname" name="first_name" class="form-control" placeholder="First Name" required>
			
            </li>
            <li>
			<br>
			<input type="lastname" id="inputlastname" name="last_name" class="form-control" placeholder="Last Name" required> 
			
            </li>
            <li>
			<br>
			<input type="email" id="inputemail" name="email" class="form-control" placeholder="Email" required>
			
            </li>
            <li>    
			<br>
			<button class="btn btn-lg btn-primary btn-block" type="submit">Register</button>
            </li> 
			<li>
			<br>
			<p> Click <a href="log.php" style='color: #43C6DB'> here </a> to login </p>
			</li>
			</ul>    
			</form>
			</div>
			</div>
			</div>
			</div>
			</div>
			
			<?php 
			}
			?>
			</html>			