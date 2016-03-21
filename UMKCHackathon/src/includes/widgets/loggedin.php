
<div class="widget" >
    
    <div class="inner">
                
            <?php
           
            if (isset($_FILES['profile']) ===  TRUE) {
                if (empty($_FILES['profile']['name']) === TRUE) {
                    echo 'please choose a file';                    
                }
                else {
                    //validation extension of file
                    $allowed = array('jpg', 'jpeg', 'gif', 'png');
                    
                    $file_name = $_FILES['profile']['name'];
                    $file_extn = strtolower(end(explode('.', $file_name)));
                    $file_temp = $_FILES['profile']['tmp_name'];
                    
                    if (in_array($file_extn, $allowed) === TRUE) {
                        //uploading the file 
                        change_profile_image($session_user_id, $file_temp, $file_extn);
                        header('location: ' . $current_file);
                        exit();
                    }
                    else {
                        echo 'incorrect file type. Allowed'; 
                        echo implode(', ', $allowed);
                    }                    
                }
            }
            ?>
			
			<?php 
            if (empty($user_data['profile']) === FALSE) {
                echo '<img src="', $user_data['profile'], '" alt="', $user_data['first_name'] , '\'s profile image" style="width:200px;height:260px" >';               
            }           
            ?>
          
        
            
            
		
		
		 <form action="" method="post" enctype="multipart/form-data"> 
                <input type="file" name="profile" > 
				<input type="submit"  style="color:black">
            </form>
           
		</div>
		</div>
                   
    
