<html>
	<head>
		<meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<meta name="description" content="WhereisMyFriend?">
		<meta name="author" content="">
		
		<title>Login</title>

		<!-- Bootstrap core CSS -->
		<link href="css/welcss/bootstrap.min.css" rel="stylesheet">

		<!-- Custom Designed CSS -->
		<link href="css/index/course.css" rel="stylesheet">
	</head>

	<body>
		<div class="site-wrapper">
			<?php 
				error_reporting(o);
				@ini_set('display_errors', 0);
				include 'core/init.php';
				protect_page();
				if (empty($_POST) === FALSE) 
				{
					$required_fields =  array('first_name', 'email'); 
					foreach($_POST as $key=>$value) 
					{
						if (empty($value) && in_array($key, $required_fields) ===  TRUE) 
						{
							$errors[] = "fields with * are required";
							break 1;            
						}                
					}
	 
					if (empty($errors) === TRUE) 
					{
						if (filter_var($_POST['email'], FILTER_VALIDATE_EMAIL) === FALSE) 
						{
							$errors[] = "A valid email address is required"; 			 
						}
						else if(email_exists($_POST['email']) === TRUE && $user_data['email'] !== $_POST['email']) 
						{
						$errors[] = 'Sorry, the email \'' . $_POST['email'] . '\' is already in use.';
						} 
					}					
					//print_r($errors);	 	
				}
			?>


			<?php
				if (isset($_GET['success']) === TRUE && empty($_GET['success']) === TRUE) 
				{
					echo 'Your details has been updated successfully';
					echo  "<br> To go back click <a href='settings.php' style='color: #43C6DB'>here</a>";
				} 
				else
				{
					//$totPrice = isset($_GET['totP']) ? $_GET['totP'] : 0
					if (empty($_POST) === FALSE && empty($errors)  === TRUE)
					{        
						///update user settings
						$update_data =  array(
						'first_name'    => $_POST['first_name'],
						'last_name'     => $_POST['last_name'],
						'email'     	=> $_POST['email'],
						'allow_email'   => ($_POST['allow_email'] == 'on') ? 1 : 0 ,
						'p1'   => ($_POST['p1'] == 'on') ? 1 : 0 ,
						'p2'   => ($_POST['p2'] == 'on') ? 1 : 0
						);
	
						//print_r($update_data);
	
						update_user($session_user_id, $update_data);
						header('location: accsettings.php?success');
						exit();
	
	
					}
					else if (empty($errors) === FALSE) 
					{
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
			<div class="inner">		
				<div class="cover-container">
					<br><br>
					<div class="inner cover" style="align:center">
						<h2>User Settings</h2>
						<div class="inner" >
							<?php include 'includes/widgets/loggedin.php' ?>		
						</div>		
						<form action="" method="post">
						<ul>
							<li>
								Firstname*:<br>
								<input type="text" name="first_name" class="form-control" value="<?php echo($user_data['first_name']); ?>">
							</li>
							<li>
								Lastname:<br>
								<input type="text" name="last_name" class="form-control" value="<?php echo($user_data['last_name']); ?>">
							</li>
							<li>
								Email*:<br>
								<input type="text" name="email" class="form-control" value="<?php echo($user_data['email']); ?>">
							</li>
							<br>
							<li>        	
								<input type="checkbox" name="p1" 
								<?php if ($user_data['p1'] == 1) {echo 'checked="checked"';}  ?>> Check to share your Personal Details
							</li>
							<li>        	
								<input type="checkbox" name="p2" 
								<?php if ($user_data['p2'] == 1) {echo 'checked="checked"';}  ?>> Check to share your City, State & Country details
							</li>
							<li>  
								<input type="submit" value="update" class="btn btn-lg btn-default">      	
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