<head>
	<meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    
    <meta name="description" content="">
    <meta name="author" content="">
    

    <!-- Bootstrap core CSS -->
    <link href="css/index/bootstrap.min.css" rel="stylesheet">
    <link href="css/index/course.css" rel="stylesheet">
    <!-- Custom styles for this template -->
    

</head>
	<?php 
	include 'core/init.php'; 
	if (logged_in() === TRUE) {
	?>
<header>   
       <nav>
		<ul class="nav masthead-nav">
			<li ><a href="index.php">Home</a>  
            </li>
            <li class="active">
                <a href="<?php echo($user_data['username']); ?>">Profile</a>
            </li> 
			<li><a href="search.php">Search</a></li>
            <li>
                <a href="settings.php">Settings</a>
            </li>
            <li>
            	<a href="logout.php">Log out</a>
            </li>                      
        </ul>          
    </nav>
</header>
	
<?php	
}else{
    header("location: log.php");
}

if (isset($_GET['username']) === TRUE && empty($_GET['username']) === FALSE) {
    $username       = $_GET['username'];
    
    if (user_exists($username) ===  TRUE) {   
    $user_id        = user_id_from_username($username);  
    
    $profile_data   = user_data($user_id, 'first_name', 'last_name', "email",'p1','p2','profile');
    
    
   ?>
    
    <h1><?php echo $profile_data['first_name']; ?>'s profile</h1>   
		  <?php if (empty($profile_data['profile']) === FALSE) {
                echo '<img src="', $profile_data['profile'], '" alt="', $profile_data['first_name'] , '\'s profile image" style="width:200px;height:260px" >';               
            } ?>
        <p>First Name: <?php echo $profile_data['first_name']; ?></p>
        <p>Last Name: <?php echo $profile_data['last_name']; ?></p>
        <p>Email: <?php echo $profile_data['email']; ?></p>
       
        



    <?php
        if($user_data['username'] == $username){
            $extra_details = get_profile_data($user_id);
			
            ?>  
                    <p>Sex: <?php echo $extra_details['Sex']; ?></p>
                    <p><u><b>Location</b></u><br>
                    
                    City: <?php echo $extra_details['city']; ?><br>
                    State: <?php echo $extra_details['state']; ?><br>
                    Country: <?php echo $extra_details['country']; ?><br>
                    Zip: <?php echo $extra_details['zip']; ?></p>
                    <p><u><b>Education</b></u><br>
                    University: <?php echo $extra_details['univ_name']; ?><br>
                    Degree: <?php echo $extra_details['degree']; ?><br>
                    Specialization: <?php echo $extra_details['specialization']; ?></p>
                    
                    <p>Hobbies: <?php echo $extra_details['hobbies']; ?></p>
                <?php
            
        }else{
			$extra_details = get_profile_data($user_id);
			
            if($profile_data['p2']==1){
				
               ?>
                                  <p>Sex: <?php echo $extra_details['Sex']; ?></p>
                                  <p><u><b>Location</b></u><br>
                                  <?php if($profile_data['p1']==1) {?>
                                      
                                    
                                        
                                  <?php }?>
                                  City: <?php echo $extra_details['city']; ?><br>
                                  State: <?php echo $extra_details['state']; ?><br>
                                  Country: <?php echo $extra_details['country']; ?><br>
                                  <?php if($profile_data['p1']==1) {?>
                                  Zip: <?php echo $extra_details['zip']; ?>
								  
                                   <?php }?></p>
                                    <?php if($profile_data['p1']==1) {?>
                                  <p><u><b>Education</b></u><br>
                                  University: <?php echo $extra_details['univ_name']; ?><br>
                                  Degree: <?php echo $extra_details['degree']; ?><br>
                                  Specialization: <?php echo $extra_details['specialization']; ?></p>
                                  
                                  <p>Hobbies: <?php echo $extra_details['hobbies']; ?></p>
                                  <?php }?>
                 <?php
			}
        }
    } else {
        echo 'sorry that username doesn\'t exist';       
    }   
    
} else {
    header('location: index.php');
    exit();
}

 
?>
    