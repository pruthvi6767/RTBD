<html>
	<head>
		<meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<meta name="description" content="WhereisMyFriend?">
		<meta name="author" content="">
		<title>ChangePassWord</title>	
		
		<!-- Bootstrap core CSS -->
		<link href="css/index/bootstrap.min.css" rel="stylesheet">
		
		<!-- Custom styles for this template -->	
		<link href="css/index/course.css" rel="stylesheet">
	</head>
	<body>
		<div class="site-wrapper">			
			<?php 
				include 'core/init.php';
				protect_page();
				
				/*$salt = "some-salt-salty-kdfjj545oidf444idfji84f8dsf4"; */
				if (empty($_POST) ===  FALSE) 
				{
					$required_fields =  array('current_password', 'password', 'password_again');
					foreach($_POST as $key=>$value) 
					{
						if (empty($value) && in_array($key, $required_fields) ===  TRUE) 
						{
							$errors[] = "fields with * are required";
							break 1;            
						}                
					}
					if (sha1("some-salt-salty-kdfjj545oidf444idfji84f8dsf4".$_POST['current_password']) === $user_data['password']) 
					{
						if (trim($_POST['password']) !== trim($_POST['password_again'])) 
						{
							$errors[] = "your new passwords do not match";             
						}
						elseif (strlen($_POST['password']) < 6) 
						{
							$errors[] = "your password must be at least 6 characters long";            
						}	
						
					}
					else 
					{
						$errors[] = "your current password is incorrect";
					}
					//print_r($errors);
				}
			?>
			
			
			<?php
				if (isset($_GET['success']) === TRUE && empty($_GET['success']) === TRUE) 
				{
					echo 'your password has been changed!';	
					echo  "<br> To go to home page click <a href='index.php' style='color: #43C6DB'>here</a>";
					
				} 
				else 
				{ 
					if (isset($_GET['force']) === TRUE && empty($_GET['force']) === TRUE) 
					{
						
					?>
					
					<p>Please change your password now.</p>
					
					<?php 
						
					}
					
					if (empty($_POST) === FALSE && empty($errors) === TRUE) 
					{
						//change password posted the form and no errors !!
						change_password($session_user_id, $_POST['password']);
						header('location: changepassword.php?success');
					}		
					
					else if (empty($errors) === FALSE) 
					{
						//output errors
						echo output_errors($errors);    
					}
					
				?>
				<nav>
					<ul class="nav masthead-nav">
						<li >
							<a href="index.php">Home</a>  
						</li>
						<li>
							<a href="<?php echo($user_data['username']); ?>">Profile</a>
						</li> 
						<li >
							<a href="search.php">Search</a>
						</li>
						<li class="active">
							<a href="settings.php">Settings</a>
						</li>
						<li>
							<a href="logout.php">Log out</a>
						</li>                      
					</ul>          
				</nav>
				<h1>Change password</h1>
				<div class="cover-container">
					<div class="inner cover">
						<div class="inner">
							<form action="" method="post">
								<ul>
									<li>
										<br>
										<input type="password" id="CurrentPassword" name="current_password" 
										class="form-control" placeholder="Current Password" >
										
									</li>
									<li>
										<br>
										<input type="password" id="CurrentPassword" name="password" 
										class="form-control" placeholder="New Password" >
									</li>
									<li>
										<br>
										<input type="password" id="CurrentPassword" name="password_again" 
										class="form-control" placeholder="Re-Enter Password" >
									</li>
									<li>
										<br>
										<button class="btn btn-lg btn-primary btn-block" type="submit">Submit</button>	
									</li>
								</ul>
							</form>
						</div>
					</div>
				</div>
			</div>
		<?php } ?>
	</body>
</html>
