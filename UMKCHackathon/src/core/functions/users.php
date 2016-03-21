<?php

$salt = "some-salt-salty-kdfjj545oidf444idfji84f8dsf4";

function change_profile_image($user_id, $file_temp, $file_extn) {
    $file_path = 'images/profile/' . substr(sha1("some-salt-salty-kdfjj545oidf444idfji84f8dsf4" . time()), 0, 10) . '.' . $file_extn; //making the random image name for user
    move_uploaded_file($file_temp, $file_path);
    mysql_query("UPDATE `users`SET `profile` = '" . mysql_real_escape_string($file_path) . "' WHERE `user_id` = " . (int)$user_id);
}

function mail_users($subject, $body) {
    $query = mysql_query("SELECT `email`, `first_name` FROM `users` WHERE `allow_email` = 1");
    while ($row = mysql_fetch_assoc($query) !== FALSE) {       
        
        email($row['email'], $subject, "hello " . $row['first_name'] . ",\n\n" . $body);        
    }
}
function user_login_attempt($username){
	$count = mysql_query("SELECT 'login_attempt' FROM `users` WHERE 'username'='$username'");
	$count = $count + 1;
	mysql_query("UPDATE `users` SET `login_attempt` = '$count' WHERE 'username'='$username'");
	
	if ( $count > 3 ){
		mysql_query("UPDATE `users` SET `active` = 0 WHERE 'username'='$username'");
		}
	
}
function has_access($user_id, $type) {
    $user_id = (int)$user_id;
    $type    = (int)$type;
    
    
    return (mysql_result(mysql_query("SELECT COUNT(`user_id`) FROM `users` WHERE `user_id` = $user_id AND `type` = $type"), 0) == 1) ? TRUE : FALSE;   
}

function process_profile($user_data,$dob,$sex,$nationality,$location,$formatted_address,$univname,$deg,$spec,$hobbies_array,$mobile){
    if(logged_in() === true){
        if($user_data['init'] == 0){
            
            $userid = $user_data['user_id'];
            $address = "";
            $location = str_replace(array( '(', ')' ), '', $location);
            list($ltd,$lng) = explode(",", $location);
            $location = str_replace(" -","%20-",$location);
            echo $formatted_address;
            if($formatted_address == ""){
                
                $output=json_decode(curl_get_contents("http://maps.googleapis.com/maps/api/geocode/json?latlng=$location&sensor=false"), true);
                foreach ($output as $out){
                    foreach ($out as $o){
                        $data = (array)$o;
                        $address = $data['formatted_address'];
                        break;
                    }
                    break;
                }
            }else{
                $address = $formatted_address;
            }     
            if($address == ''){
                return false;
            }
            $address_info = explode(", ",$address );
            if (count($address_info)==4){
                list($street, $city, $state , $country) = array_slice($address_info,-4);
            }elseif (count($address_info)==3){
                list($city, $state , $country) = array_slice($address_info,-3);
                $street = "";
            }
            $state_info = explode(" ", $state);
            if(count($state_info)==2){
                $state = $state_info[0];
                $zip = $state_info[1];
            }elseif(count($state_info)==1){
                $state = $state_info[0];
                $zip = -1;
            }else{
                $zip = trim(str_replace($state_info[0], "", $state));
                $state = $state_info[0];
                
            }
            $nationality = sanitize($nationality);
            $univname = sanitize($univname);
            $deg = sanitize($deg);
            $spec = sanitize($spec);
            array_walk( $hobbies_array, 'array_sanitize');
            $hobbies = implode(",", $hobbies_array);
            $mobile = sanitize($mobile);

            if( mysql_query("INSERT INTO `persons` (User_id,DOB,Sex,Mobile_no,loc_ltd,loc_long,city,state,country,univ_name,degree,specialization,nationality,hobbies,street,zip) VALUES (
               $userid,'$dob','$sex',$mobile,$ltd,$lng,'$city','$state','$country','$univname','$deg','$spec','$nationality','$hobbies','$street',$zip )") === true){
                mysql_query("UPDATE `users` SET `init` = 1 WHERE `user_id` = '$userid'");
                return true;
            }else
                echo "INSERT INTO `persons` (User_id,DOB,Sex,Mobile_no,loc_ltd,loc_long,city,state,country,univ_name,degree,specialization,nationality,hobbies,street,zip) VALUES (
               $userid,'$dob','$sex',$mobile,$ltd,$lng,'$city','$state','$country','$univname','$deg','$spec','$nationality','$hobbies','$street','$zip' )";
                return false;
            
        }   
    }
}

function user_profile_complete($user_data) {
    if(logged_in() === true){
        if($user_data['init'] == 0){
            
            return FALSE;
        }else 
            return TRUE;
    }
    
}

function recover($mode, $email) {
    $mode       = sanitize($mode);
    $email      = sanitize($email);
    
    $user_data = user_data(user_id_from_email($email), 'user_id', 'first_name', 'username');
    
    if ($mode == 'username') {
        //recover username
        email($email, 'your username', "hello " . $user_data['first_name'] . ", \n\n your username is: " . $user_data['username'] . "\n\n ");
        
    } else if ($mode == 'password') {
        //recover password
        $generated_password = substr(sha1("some-salt-salty-kdfjj545oidf444idfji84f8dsf4" . rand(999, 999999)), 0, 8);
        change_password($user_data['user_id'], $generated_password);
        
        update_user($user_data['user_id'], array('password_recover' => '1'));
        
        email($email, 'your password recovery', "hello " . $user_data['first_name'] . ", \n\n your new password is: " . $generated_password . "\n\n ");
        
        
        
    }   
}

function update_user($user_id, $update_data) {	
	$update =  array();
    array_walk($update_data, 'array_sanitize');
	
	foreach ($update_data as $field=>$data) {
		$update[] = '`' . $field . '` = \'' . $data . '\'';
	}
	
	mysql_query("UPDATE `users` SET " . implode(', ', $update) . " WHERE `user_id` = $user_id") or die(mysql_error());
    
    
}




function activate($email, $email_code) {
    $email         = mysql_real_escape_string($email);
    $email_code    = mysql_real_escape_string($email_code);
    
    if (mysql_result(mysql_query("SELECT COUNT(`user_id`) FROM `users` WHERE `email` = '$email' AND `email_code` = '$email_code' AND `active` = 0"), 0) == 1) {
        //query  to updat user active status
        mysql_query("UPDATE `users` SET `active` = 1 WHERE `email` = '$email'");        
    }  else {
        return FALSE;       
    }    
}

function change_password($user_id, $password) {
    $user_id = (int)$user_id;
    $password = sha1("some-salt-salty-kdfjj545oidf444idfji84f8dsf4" . $password);
    
    mysql_query("UPDATE `users` SET `password` = '$password', `password_recover` = 0 WHERE `user_id` = $user_id");    
}

function register_user($register_data) {
    array_walk($register_data, 'array_sanitize');
    $register_data['password'] = sha1("some-salt-salty-kdfjj545oidf444idfji84f8dsf4" .($register_data['password']));
    
    $fields = '`' . implode('`, `', array_keys($register_data)) . '`';
    //echo $data;
    $data  =  '\'' . implode('\', \'', $register_data) . '\'';    
    
    mysql_query("INSERT INTO `users` ($fields) VALUES ($data)");
    email($register_data['email'], 'activate your account', "hello " . $register_data['first_name'] . ",\n\n You need to activate your account, so use the link below: \n\n
			If unable to click the link, please visit it by paste the below link in your address bar.
        http://www.whereismyfriend.site90.com/activate.php?email=" . $register_data['email'] . "&email_code=" . $register_data['email_code'] ."\n\n\n\n\n
            
        -WhereIsmyFriend?
        

        
            
        
        

        ");
    
}



function user_count() {
    //$query = "SELECT COUNT(`user_id`) FROM `users` WHERE `active` = 1";
    return mysql_result(mysql_query("SELECT COUNT(`user_id`) FROM `users` WHERE `active` = 1"), 0);
    
}

function user_data($user_id) {
    $data =  array();
    $user_id = (int)$user_id;
    
    $func_num_args = func_num_args();
    $func_get_args = func_get_args();
    
    if ($func_get_args > 1) {
        unset($func_get_args[0]);
        
        $fields = '`' . implode('`, `', $func_get_args) . '`';      
        $data = mysql_fetch_assoc(mysql_query("SELECT $fields FROM `users` WHERE `user_id` = $user_id"));
        return $data;        
    }
    
}

function privacy_check($user_id){
    return FALSE;
}
function get_profile_data($user_id){
    $data =  array();
    $user_id = (int)$user_id;
    $data = mysql_fetch_assoc(mysql_query("SELECT * FROM `persons` WHERE `user_id` = $user_id"));
    return $data;
    
}
function logged_in() {
    return (isset($_SESSION['user_id'])) ?  TRUE : FALSE;    
}

function email_exists($email) {
    $email = sanitize($email);
    $query = mysql_query("SELECT COUNT(`user_id`) FROM `users` WHERE `email` = '$email'");
    return(mysql_result($query, 0) == 1) ?  TRUE : FALSE;    
}


function user_exists($username) {
    $username = sanitize($username);
    $query = mysql_query("SELECT COUNT(`user_id`) FROM `users` WHERE `username` = '$username'");
    return(mysql_result($query, 0) == 1) ?  TRUE : FALSE;    
}

function user_active($username) {
    $username = sanitize($username);
    $query = mysql_query("SELECT COUNT(`user_id`) FROM `users` WHERE `username` = '$username'  AND `ACTIVE` = 1");
    return(mysql_result($query, 0) == 1) ?  TRUE : FALSE;    
}

function user_id_from_username($username) {
    $username = sanitize($username);
    //$query = mysql_query("SELECT `user_id` FROM `users` WHERE `username` = '$username'");
    return mysql_result(mysql_query("SELECT `user_id` FROM `users` WHERE `username` = '$username'"), 0, 'user_id');    
}

function user_id_from_email($email) {
    $email = sanitize($email);
    //$query = mysql_query("SELECT `user_id` FROM `users` WHERE `username` = '$username'");
    return mysql_result(mysql_query("SELECT `user_id` FROM `users` WHERE `email` = '$email'"), 0, 'user_id');    
}

function login($username, $password) 
{
    $user_id = user_id_from_username($username);    
    $username = sanitize($username);
    $password = sha1("some-salt-salty-kdfjj545oidf444idfji84f8dsf4".$password);   
    //$query = "SELECT COUNT(`user_id`) FROM `users`  WHERE `username` = '$username' AND `password` = '$password'";
    return (mysql_result(mysql_query("SELECT COUNT(`user_id`) FROM `users`  WHERE `username` = '$username' AND `password` = '$password'"), 0) == 1) ? $user_id : FALSE;   
}


// function select countries
function select_country() {
    
}

?>
