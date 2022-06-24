<?php
    $con = mysqli_connect('localhost', 'holo', 'kitce2022*', 'holo');
    mysqli_query($con,'SET NAMES utf8');
    /*
    if(!$con){
        echo 'DB에 연결하지 못했습니다.'. mysqli_connect_error();
    } else{
        echo 'DB에 접속했습니다';
    }
     */
    $token = $_POST["token"];
    $uid = $_POST["uid"];
    
    $statement = mysqli_prepare($con, "UPDATE user SET token = ? WHERE uid = ?");
    mysqli_stmt_bind_param($statement, "ss", $token, $uid);
    mysqli_stmt_execute($statement);
    //print_r($_POST);
    echo json_encode(true);
    exit(0);
?>
