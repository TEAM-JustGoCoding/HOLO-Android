<?php
    $con = mysqli_connect('localhost', 'holo', 'kitce2022*', 'holo');
    /*
    if(!$con){
        echo 'DB에 연결하지 못했습니다.'. mysqli_connect_error();
    } else{
        echo 'DB에 접속했습니다';
    }
    */

    $nick_name = mysqli_real_escape_string($con, $POST['nick_name']);
    print_r($nick_name);

    $sql = "SELECT nick_name FROM user WHERE nick_name ='".$_POST['nick_name']."'";
    $result = mysqli_query($con, $sql);
    $exist = mysqli_num_rows($result);
        
    if($exist>0){
        echo json_encode(false);
        exit(0);
    }
    else{
        echo json_encode(true);
        exit(0);
    }
    
    /*
    
    mysqli_stmt_bind_param($statement, "s", $uid);
    mysqli_stmt_execute($statement);
    mysqli_stmt_store_result($statement);
    mysqli_stmt_bind_result($statement, $userID);

    $response = array();
    $response["success"] = true;

    while(mysqli_stmt_fetch($statement)){
      $response["success"] = false;
      $response["uid"] = $uid;
    }

    echo json_encode($response);
    */
?>
