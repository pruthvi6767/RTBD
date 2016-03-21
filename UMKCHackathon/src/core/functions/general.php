<?php

function email($to , $subject, $body) {
    mail($to, $subject, $body, 'from: whereismyfriend@outlook.com');   
    
}

function logged_in_redirect() {
    if (logged_in() === TRUE) {
        header('location: index.php');
        exit();        
    }    
}

function admin_protect() {
    global $user_data;
    if (has_access($user_data['user_id'], 1) === FALSE) {
        header('location: index.php');
        exit();
        
    }
    
}


function curl_get_contents($url,$posts="")
{
$ch = curl_init();
curl_setopt($ch, CURLOPT_URL, $url);
curl_setopt($ch, CURLOPT_HEADER, "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.1.4322)");
curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1); 
curl_setopt($ch, CURLOPT_POST, $posts ? 0 :1); 
curl_setopt($ch, CURLOPT_POSTFIELDS,$posts);
$icerik = curl_exec($ch);
return $icerik;
curl_close($ch);
}

function protect_page() {
    if (logged_in() === false) {
        header('location: protected.php');
        exit();        
    }    
}

function array_sanitize(&$item) {
    $item = htmlentities(strip_tags(mysql_real_escape_string($item)));
    
}

function sanitize($data) {
    return htmlentities(strip_tags(mysql_real_escape_string($data)));    
}

function output_errors($errors) {    
    return '<ul><li>' . implode('</li><li>', $errors) .  '</li></ul>';
    
    
}

?>
