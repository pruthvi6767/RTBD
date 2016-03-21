<html>
	<head>
		<meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<meta name="description" content="WhereisMyFriend?">
		<meta name="author" content="">
		<title>WhereisMyFriend?- Profile Setup</title>
		
		<!-- Bootstrap core CSS -->
		<link href="css/index/bootstrap.min.css" rel="stylesheet">
		
		<!-- Custom styles for this template -->
		<link href="css/index/course.css" rel="stylesheet">
		
	</head>
	<?php 
		include 'core/init.php';
		if (logged_in() === TRUE) 
		{
			if(user_profile_complete($user_data) === TRUE)
			{
				header("location: index.php");
				exit();
			}
		}
		else
		{
			header("location: index.php");
		}
		if (empty($_POST) === FALSE) 
		{
			$dob = $_POST['dob'];
			$sex = $_POST['sex'];
			$nationality = $_POST['nationality'];
			$refn= $_POST['refn'];
			if(strpos($refn,$nationality) === false)
			{
				mysql_query("INSERT INTO `nationality` (`Nationality_Name`) VALUES ('$nationality')");
			}
			$location = $_POST['location_google'];
			$formatted_address = $_POST['formatted_address'];
			$univname = $_POST['univname'];
			$refuniv= $_POST['refuniv'];
			if(strpos($refuniv,$univname) === false)
			{
				mysql_query("INSERT INTO `university` (`U_Name`) VALUES ('$univname')");
			}
			
			$deg = $_POST['deg'];
			$spec = $_POST['spec'];
			$refs= $_POST['refs'];
			if(strpos($refs,$univname) === false)
			{
				mysql_query("INSERT INTO `specialization` (`S_Name`) VALUES ('$spec')");
			}
			$hobbies_array = $_POST['hobbies'];
			$mobile = $_POST['mobile'];
			if ( process_profile($user_data,$dob,$sex,$nationality,$location,$formatted_address,$univname,$deg,$spec,$hobbies_array,$mobile) === true )
			{
				$user_data['init'] = 1;
				header("location: index.php");
			}
			else
			{
			?>
			<h3>
				<?php echo "Please enter valid details";
				?>
			</h3>
			<?php  
			}
		}
	?>
	<script type="text/javascript">
		function validate(frm)
		{
			var ele = frm.elements['hobbies[]'];
			if (! ele.length)
			{
				alert(ele.value);
			}
			for(var i=0; i<ele.length; i++)
			{
				alert(ele[i].value);
			}
			return true;
		}
		function add_feed()
		{
			var div1 = document.createElement('div');
			// Get template data
			div1.innerHTML = document.getElementById('newlinktpl').innerHTML;
			// append to our form, so that template data
			//become part of form
			document.getElementById('newlink').appendChild(div1);
		}
	</script>
	<style>
		.feed {padding: 5px 0}
	</style>
	<div style="align:center">
		<h2><?php echo $user_data['first_name'].", ". $user_data['last_name']; ?></h2>
		<h2>Profile Setup</h2>
		<form action="init.php" method="post">
			<ul id="login">
				<li>
					<br>
					Date of Birth:<br>
					<input type="date" id="dob" name="dob" style="color:black" required>
				</li>
                <li>
					<br>
					Sex:         
                    <input type="radio" name="sex" value="M">Male
                    <input type="radio" name="sex" value="F">Female
				</li> 
                <li>
					<br>
					<input type="tel" id="mobileno" name="mobile" class="form-control" style="width:50%;margin: 0 auto;" placeholder="Mobile No." >
				</li>
				<li>
					<br>
					<input type="text" id="nationality" name="nationality" class="form-control" style="width:50%;margin: 0 auto;" list="nationality" placeholder="Nationality" >
					
				<datalist id="nationality">
				
				<?PHP
				$sql3="select Nationality_Name from nationality";
				$str1=" ";
				$result3=mysql_query($sql3);
				while($row3=mysql_fetch_array($result3)){
				
				$nationalityname=$row3['Nationality_Name'];
				$str1=$str1.$nationality." ";
				echo "<option value=".$nationalityname."> ".$nationalityname."</option>";
				}
				?>
				</datalist>
				<input name="refn" type="hidden" value="<?php echo $str1;?>"
				
				</li>
				
				
				<li>Mark your location or Enter Zipcode : <input type="text" name="zip" id="zip" class="form-control" style="width:50%;margin: 0 auto;" onblur="getLocation()">
				<input type="hidden" name="location_google" id="location_google" value="">
				<input type="hidden" name="formatted_address" id="formatted_address" value="">
				
				
				<script src="https://maps.googleapis.com/maps/api/js?v=3.exp&signed_in=true"></script>
				
				<script type="text/javascript">
				
				function getLocation(){
				getAddressInfoByZip(document.forms[0].zip.value);
				
				}
				
				function response(obj){
				console.log(obj);
				}
				function getAddressInfoByZip(zip){
				if(zip.length >= 5 && typeof google != 'undefined'){
				var addr = {};
				var geocoder = new google.maps.Geocoder();
				geocoder.geocode({ 'address': zip }, function(results, status){
				if (status == google.maps.GeocoderStatus.OK){
				if (results.length >= 1) {
				addr.lat = results[0].geometry.location.A;
				addr.long = results[0].geometry.location.F;
				addr.formatted_address = results[0].formatted_address;
				addr.success = true;
				document.getElementById('formatted_address').value = addr.formatted_address;
				document.getElementById('location_google').value = "("+addr.lat+", "+addr.long+")";
				addMarker(results[0].geometry.location);
				map.setZoom(15);
				response(addr);
				} else {
				response({success:false});
				}
				} else {
				clearMarkers();
				map.setZoom(5);
				var haightAshbury = new google.maps.LatLng(40.716994, -100.674765);
				map.panTo(haightAshbury);
				alert("Invalid Zip Code");
				document.getElementById('formatted_address').value = "";
				document.getElementById('location_google').value = "";
				response({success:false});
				}
				});
				} else {
				response({success:false});
				}
				}
				
				
				
				
				var map;
				var markers = [];
				
				function initialize() {
				var haightAshbury = new google.maps.LatLng(40.716994, -100.674765);
				var mapOptions = {
				zoom: 5,
				center: haightAshbury,
				mapTypeId: google.maps.MapTypeId.ROADMAP
				};
				map = new google.maps.Map(document.getElementById('map-canvas'),
				mapOptions);
				
				
				
				// This event listener will call addMarker() when the map is clicked.
				google.maps.event.addListener(map, 'click', function(event) {
				clearMarkers();
				addMarker(event.latLng);
				map.setZoom(7);
				alert(JSON.stringify(event.latLng));
				document.getElementById('location_google').value = event.latLng;
				document.getElementById('formatted_address').value = "";
				
				});
				
				}
				
				// Add a marker to the map and push to the array.
				function addMarker(location) {
				clearMarkers();
				var marker = new google.maps.Marker({
				position: location,
				map: map
				});
				//var center = new google.maps.LatLng(location.k, location.D);
				// using global variable:
				map.setCenter(location);
				markers.push(marker);
				}
				
				// Sets the map on all markers in the array.
				function setAllMap(map) {
				for (var i = 0; i < markers.length; i++) {
				markers[i].setMap(map);
				}
				}
				
				// Removes the markers from the map, but keeps them in the array.
				function clearMarkers() {
				setAllMap(null);
				}
				
				// Shows any markers currently in the array.
				function showMarkers() {
				setAllMap(map);
				}
				
				// Deletes all markers in the array by removing references to them.
				function deleteMarkers() {
				clearMarkers();
				markers = [];
				}
				
				google.maps.event.addDomListener(window, 'load', initialize);
				</script>
				
				<br>
				
				<div style="height: 50%;width:30%;margin: 0 auto;  position: relative; overflow: hidden; transform: translateZ(0px); background-color: rgb(229, 227, 223);" id="map-canvas"></div>
				</li>	
				
				
				<li>
				<br>
				<div style="width:50%;margin: 0 auto;">
				<input type="text" id="univ" name="univname" class="form-control" placeholder="University" list="univname" >
				</div>
				<datalist id="univname">
				
				
				<?PHP
				$sql1="select U_Name from university";
				$str="";
				$result1=mysql_query($sql1);
				while($row1=mysql_fetch_array($result1)){
				$universityname=$row1['U_Name'];
				$str=$str.$univname." ";
				echo "<option value=\"".$universityname."\"> ".$universityname."</option>";
				}
				?>
				</datalist>
				<input name="refuniv" type="hidden" value="<?PHP echo $str;?>"
				
				
				</li>
				<li>
				<br>
				<input type="text" id="degree" name="deg" class="form-control"  style="width:50%;margin: 0 auto;" placeholder="Degree" list="deg" >
				
				<datalist id="deg">
				<?PHP
				$sql2="select Degree_Name from degree";
				
				$result2=mysql_query($sql2);
				while($row2=mysql_fetch_array($result2)){
				
				$degreename=$row2['Degree_Name'];
				
				echo "<option value=\"".$degreename."\">".$degreename."</option>";
				}
				?>
				</datalist>
				
				</li>
				<li>
				<br>
				<input type="text" id="specialization" name="spec" class="form-control" style="width:50%;margin: 0 auto;" placeholder="Specialization" list="spec" >
				
				<datalist id="spec">
				<?PHP
				$sql4="select S_Name from specialization";
				$str3="";
				$result4=mysql_query($sql4);
				while($row4=mysql_fetch_array($result4)){
				
				$sname=$row4['S_Name'];
				$str3=$str3.$spec." ";
				echo "<option value=\"".$sname."\">".$sname."</option>";
				}
				?>
				</datalist>
				<input name="refs" type="hidden" value="<?PHP echo $str3;?>"
				</li>
				<li>
				
				
				<div id="newlink">
				<div class="feed">
				<br>
				<input type="text" id="hobby" name="hobbies[]" class="form-control" style="width:50%;margin: 0 auto;" placeholder="Hobby" value="" size="50" >
				<span id="addnew">
				<a href="javascript:add_feed()"> Add New </a>
				</span>
				
				</div>
				</div>
				<li>    
				<br>
				
				<button class="btn btn-lg btn-primary btn-block" style="width:50%;margin: 0 auto;" type="submit">Submit</button>
				<br>
				<a href="logout.php"> LogOut </a>  
				<br>
				</li>  
				
				</li>
				
				</ul> 
				</form> 
				
				<div id="newlinktpl" style="display:none">
				<div class="feed">
				<input type="text" name="hobbies[]" class="form-control" style="width:50%;margin: 0 auto;" placeholder="Hobby"  size="50">
				</div>
				</div>
				
				</div>
				
</html>								