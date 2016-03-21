<?php
error_reporting(E_ALL ^ E_DEPRECATED);
$connect_error = "sorry, we\'re experiening connection problems.";

mysql_connect('localhost', 'root', '') or die($connect_error);
mysql_select_db('dataset') or die($connect_error);

?>
