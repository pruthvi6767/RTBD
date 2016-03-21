<?php 
	include 'core/init.php';
	
	
	if (logged_in() === TRUE ) {
		
		}else{
		header("location: index.php");
	}
?>
<!DOCTYPE html>
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
		<div class="site-wrapper" id="search">
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
				
				<div class="inner">
				<h2>Search</h2>
				<div class="cover-container">
				
				<div class="inner cover">
				<p> Please select a field you want to search on </p>
				
				
				<form method="POST" action="search.php" id="searchform">
				
				<select name="J" class="form-control">
				<option value="username">Name</option>
				<option value="nationality">Nationality</option>
				<option value="hobby">Hobby</option>
				</select>
				
				<br>
				<p> Please type the search input </p>
				<br>
				<input type="text" name="name" class="form-control" placeholder="Search Input">
				<br>
				<button class="btn btn-lg btn-primary btn-block" type="submit" name="submit">Submit</button>
				
				<br>
				
				</form>
				<p> Or Click <a href="usersnearby.php" style="color: #43C6DB"> here </a> to find people who are near by your location  </p>
				</div>
				</div>
				</div>
				
				
				
				<?PHP
				if (empty($_POST) === FALSE){
				if($_POST['J']=== 'username'){
				
				$n = sanitize($_POST['name']);
				$sql="SELECT * FROM users WHERE first_name LIKE '%" . $n . "%' or last_name LIKE '%" . $n . "%' ";
				
				
				$result=mysql_query($sql);
				$loguser=$user_data['user_id'];
				
				
				while($row=mysql_fetch_array($result)){
				if($loguser != $row['user_id']){
                $name =$row['first_name'];
                $email=$row['email'];
                $id_user=$row['user_id'];
                $username=$row['username'];
                //-display the result of the array
				
                echo "<ul>\n";
                echo "<li> <a href=".$username."> ".$name ."        -        " . $email . "</a></li>\n";
                echo "</ul>";
				}
				}
				}
				
				if($_POST['J']=== 'nationality'){
				
				$n = sanitize($_POST['name']);
				$sql="SELECT * FROM persons inner join nationality on persons.nationality=nationality.Nationality_Name inner join users users on persons.user_id=users.user_id WHERE nationality LIKE '%" . $n . "%' ";
				
				//  -run the query against the mysql query function
				$result=mysql_query($sql);
				
				$loguser=$user_data['user_id'];
				//-create while loop and loop through result set
				while($row=mysql_fetch_array($result)){
				if($loguser != $row['user_id']) {
                $name =$row['first_name'];
                $email=$row['email'];
                $id_user=$row['user_id'];
                $username=$row['username'];
                //-display the result of the array
				
                echo "<ul>\n";
                echo "<li> <a href=".$username."> ".$name ."        -        " . $email . "</a></li>\n";
                echo "</ul>";
				}
				}
				}
				
				if($_POST['J']=== 'hobby'){
				$n = sanitize($_POST['name']);
				$sql="SELECT * FROM persons inner join users on persons.user_id=users.user_id WHERE hobbies LIKE '%" . $n . "%' ";
				
				//  -run the query against the mysql query function
				$result=mysql_query($sql);
				$loguser=$user_data['user_id'];
				
				//-create while loop and loop through result set
				while($row=mysql_fetch_array($result)){
				if($loguser != $row['user_id']){
                $name =$row['first_name'];
                $email=$row['email'];
                $id_user=$row['user_id'];
                $username=$row['username'];
                //-display the result of the array
				
                echo "<ul>\n";
                echo "<li> <a href=".$username."> ".$name ."        -        " . $email . "</a></li>\n";
                echo "</ul>";
				}
				}
				}
				}
				
				?>
				
				</div>
				
				</body>
				</html>
				
								