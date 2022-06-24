<?php
    $con = mysqli_connect('localhost', 'holo', 'kitce2022*', 'holo');
/*
    if(!$con){
        echo 'DB에 연결하지 못했습니다.'. mysqli_connect_error();
    } else{
        echo 'DB에 접속했습니다';
    }
*/
    mysqli_query($con,'SET NAMES utf8');

    $uid = $_GET["uid"];
  
    $statement = mysqli_prepare($con, "SELECT nick_name, token FROM user WHERE uid = ?");
    mysqli_stmt_bind_param($statement, "s", $uid);
    $exec = mysqli_stmt_execute($statement);
    
    mysqli_stmt_store_result($statement);
    mysqli_stmt_bind_result($statement, $nick_name, $token);

    $response = array();
    $response["success"] = true;
 
    while(mysqli_stmt_fetch($statement)) {
        $response["success"] = true;
        $response["uid"] = $uid;
        $response["nick_name"] = $nick_name;
        $response["token"] = $token;
    }
 
    echo json_encode($response, JSON_UNESCAPED_UNICODE);
?>
