<?php 
	include 'core/init.php'; 
?>
<html>
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
	
	<body>
	<?php
					
					if (logged_in() === TRUE) {
					if(user_profile_complete($user_data) === FALSE){
					header("location: init.php");
					exit();
					}
					else {
					header("location: index.php");
					exit();
					
					}
					}
					
					?>
		<div class="site-wrapper">
			
			<div class="cover-container">
				
				<div class="inner cover">
					<h1 >Login</h1>
					<?php
						include 'includes/widgets/login.php';
					?>	
					
				</div>
				
					
			</div>
					
		</div>
					
					<!-- Bootstrap core JavaScript
					================================================== -->
					<!-- Placed at the end of the document so the pages load faster -->
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
		<script src="js/bootstrap.min.js"></script>
	</body>
</html>
										