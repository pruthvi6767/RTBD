<head>
	<meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content=""> 
    <link href="css/index/bootstrap.min.css" rel="stylesheet">
    <link href="css/index/course.css" rel="stylesheet">
</head>
<body>
	<?php
		include 'core/init.php';
		logged_in_redirect();
		if (isset($_GET['success']) === TRUE && empty($_GET['success']) === true) 
		{
		?>
		<h2>Thanks, You have activated your account ...</h2>
		<p>Now you can log in <a href ="log.php" style="color: #43C6DB"> here </a> </p>
		<?php
		}
		else if (isset($_GET['email'], $_GET['email_code']) === TRUE) 
		{
			$email          = trim($_GET['email']);
			$email_code     = trim($_GET['email_code']);
			if (email_exists($email) === FALSE) 
			{
				$errors[] = "oops, something went wrong, and we couldn't find that email address";        
			} 
			else if (activate($email, $email_code) === FALSE) 
			{
				$errors[] = "we had problems activating your account, please check your emails for activate your account";	
			}
			if (empty($errors) === FALSE) 
			{
			?>
			<h2>Oops...</h2>       
			<?php
				echo output_errors($errors); 
			} 
			else 
			{
				header('location: activate.php?success');
				exit();
			} 
		}
		else 
		{
			header('location: index.php');
		}
		
	?>
</body>