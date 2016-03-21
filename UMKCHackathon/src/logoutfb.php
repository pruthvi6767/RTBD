<?php
session_start();
unset($_SESSION['fb_token']);
header('Location: index.php');
?>