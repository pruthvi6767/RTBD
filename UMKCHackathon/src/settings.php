<html>
	<head>
		<meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<meta name="description" content="WhereisMyFriend?">
		<meta name="author" content="">
		<!-- <link rel="icon" href="../../favicon.ico"> -->
		
		<title>WhereisMyFriend?</title>
		
		<!-- Bootstrap core CSS -->
		<link href="css/index/bootstrap.min.css" rel="stylesheet">
		
		<!-- Custom styles for this template -->
	<link href="css/index/course.css" rel="stylesheet"></head>
			<body>
			<div class="site-wrapper"  id="settings">
			<?php 
		include 'core/init.php';
		protect_page();
		
		
		
		if (empty($_POST) === FALSE) {
			$required_fields =  array('first_name', 'email'); 
			foreach($_POST as $key=>$value) {
				if (empty($value) && in_array($key, $required_fields) ===  TRUE) {
					$errors[] = "fields with * are required";
					break 1;            
				}                
			}
			
			if (empty($errors) === TRUE) {
				if (filter_var($_POST['email'], FILTER_VALIDATE_EMAIL) === FALSE) {
					$errors[] = "A valid email address is required"; 			 
				}
				else if(email_exists($_POST['email']) === TRUE && $user_data['email'] !== $_POST['email']) {
					$errors[] = 'Sorry, the email \'' . $_POST['email'] . '\' is already in use.';
				} 
				
			
			}	
			//print_r($errors);	 	
			}
			
			
			?>		
			<nav>
			<ul class="nav masthead-nav">
			
			<li ><a id="home_i">Home</a>  
            </li>
            <li>
			<a href="<?php echo($user_data['username']); ?>">Profile</a>
            </li> 
			<li><a href="search.php">Search</a></li>
            <li class="active">
			<a href="settings.php">Settings</a>
            </li>
            <li>
			<a href="logout.php">Log out</a>
            </li>                      
			</ul>          
			</nav>
			
			<div class="inner">		
			<h2>Settings</h2>
			<br>
			<br>
			<div class="inner cover">
			<p class="lead">
			<a href="accsettings.php" class="btn btn-lg btn-default">User Settings</a>
			<br><br>
			<a href="changepassword.php" class="btn btn-lg btn-default">Change Password</a>
			</p>
			</div>
			</form>
			</div>
				<script>
					$(document).ready(function(){
					$("#search_i").click(function(){
					$("#index").load("search.php #search");
						});
					$("#home_i").click(function(){
					$("#settings").load("index.php #index");
						});
					$("#logout_i").click(function(){
					$("#index").load("logout.php");
						});
					
					});
		
				</script>
			</div>
			
			
			</body>
			
			</html>			