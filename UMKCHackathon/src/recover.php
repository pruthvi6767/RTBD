<html>
<head>
<meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <meta name="description" content="WhereisMyFriend?">
    <meta name="author" content="">
   

    <title>Recover</title>

    <!-- Bootstrap core CSS -->
    <link href="css/welcss/bootstrap.min.css" rel="stylesheet">

    <!-- Custom styles for this template -->
    <link href="css/index/course.css" rel="stylesheet">
	
</head>

<?php 
include 'core/init.php'; 
logged_in_redirect();

?>
<div class="site-wrapper">
 		
        <div class="cover-container">
					
        <div class="inner cover">

<h2>Recover your password</h2>
<?php
if (isset($_GET['success']) === TRUE && empty($_GET['success']) === TRUE) {
?>
    <p>Thanks,We have emailed you. Please check your email!!</p>
    <p> Click <a href="log.php" style='color: #43C6DB'> here </a> to go back to the Login Page </p>
<?php    
} else {
    $mode_allowed = array('username', 'password');
    if (isset($_GET['mode']) === TRUE && in_array($_GET['mode'], $mode_allowed) ===  TRUE) {
        if (isset($_POST['email']) === TRUE && empty($_POST['email']) === FALSE) {
            if (email_exists($_POST['email']) === TRUE) {
                recover($_GET['mode'], $_POST['email']);
                header('location: recover.php?success');
                exit();
            } else {
                echo '<p>Oops!!!! We couldn\'t find that email address </p>';
            }

        }


    ?>

    <form action="" method="post">
        <ul>
            <li>
                <input type="text" class="form-control" name="email" placeholder="Enter your email address here.">
            </li>
			<br>
            <li><input class="btn btn-lg btn-primary btn-block" type="submit" value="Recover"></li>
        </ul>
    </form>
</div>
</div>
</div>


    <?php   
    } else {
        header('location: index.php');
        exit();

    }
}



?>


    