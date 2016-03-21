<?php 
	include 'core/init.php';
	
	
	if (logged_in() === TRUE ) {
		
		}else{
		header("location: index.php");
	}
?>

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
	
	<body>
		<div class="site-wrapper">
			<nav>
				<ul class="nav masthead-nav">
					
					
					
					<li ><a href="index.php">Home</a>  
					</li>
					<li>
						<a href="<?php echo($user_data['username']); ?>">Profile</a>
					</li> 
				<li class="active"><a href="search.php">Search</a></li>
				<li >
                <a href="settings.php">Settings</a>
				</li>
				<li>
            	<a href="logout.php">Log out</a>
				</li>                      
				</ul>          
				</nav>
				
				<div class="inner" >
				<h2>Search users nearby</h2>
				<div class="cover-container">
				
				
				
				<div>
				<form method="POST" action="usersnearby.php">
				
				
				<br>
				<select name="J" class="form-control" style="width:20%;margin: 0 auto;" >
				<option value="10">10 miles</option>
				<option value="20">20 miles</option>
				
				</select>
				<br>
				<input type="submit" name="submit" value="Search" style="color:black">
				</div>
				</form>	
				</div>
				</div>
				<?PHP
				if (empty($_POST) === FALSE){
				$extra_details = get_profile_data($user_data['user_id']);
				
				$sql="SELECT User_Id FROM persons where
				( 3959 * acos( cos( radians(".$extra_details['loc_ltd'].") ) * cos( radians( loc_ltd ) ) * 
				cos( radians( loc_long ) - radians(".$extra_details['loc_long'].") ) + sin( radians(".$extra_details['loc_ltd'].") ) * 
				sin( radians( loc_ltd ) ) ) ) < ".$_POST['J'];
				
				
				$result= mysql_query($sql);
				
				if($result === FALSE) { 
				die(mysql_error()); // TODO: better error handling
				}
				$loguser=$user_data['user_id'];
				while($row=mysql_fetch_array($result)){
				
				$user_id=$row['User_Id'];
				if($loguser != $user_id){
				$u_d = user_data($user_id,'username','first_name', 'last_name', 'email');
                
				
                //-display the result of the array
				echo "<ul>\n";
				echo "<li> <a href=/".$u_d['username']."> ".$u_d['first_name'] ."	".$u_d['last_name']." 	" .$u_d['email']." </a></li>\n";
                echo "</ul>";
				
				}
				}
				}
				?>
				</div>
				</body>
				</html>
				
								