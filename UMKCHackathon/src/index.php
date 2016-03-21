<html>
	<head>
		<meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		
		<meta name="description" content="">
		<meta name="author" content="">
		
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
		<!-- Bootstrap core CSS -->
		<link href="css/index/bootstrap.min.css" rel="stylesheet">
		<!-- Custom css -->
		<link href="css/index/course.css" rel="stylesheet">
	</head>
	
		<body>   
		<div class="site-wrapper" id="index">
		
		<?php 
		include 'core/init.php';
	?>
	<?php
		if (logged_in() === TRUE) 
		{
			if(user_profile_complete($user_data) === FALSE)
			{
				header("location: init.php");	
				exit();
			}
		}
		if (logged_in() === TRUE) 
		{
		?>
				
				<nav>
					<ul class="nav masthead-nav">
						<li class="active">
							<a href="index.php">Home</a>  
						</li>
						<li>
							<a href="<?php echo($user_data['username']); ?>">Profile</a>
						</li> 
						<li>
							<a id="search_b"> Search </a>
						</li>
						<li>
							<a id="settings_b">Settings</a>
						</li>
						<li>
							<a id="logout_b">Log out</a>
						</li>                      
					</ul>          
				</nav>
				<h2>Hello, <?php echo $user_data['first_name']; ?>!</h2>
				
			
			</div>
				
				<script>
					$(document).ready(function(){
					$("#search_b").click(function(){
					$("#index").load("search.php #search");
						});
					$("#settings_b").click(function(){
					$("#index").load("settings.php #settings");
						});
					$("#logout_b").click(function(){
					$("#index").load("logout.php");
						});
					
			});
		
				</script>
		</body>
	<?php  } ?>
</html>



