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
 
    $uid = $_POST["uid"];
    //print_r($uid);
    $real_name = $_POST["real_name"];
    $nick_name = $_POST["nick_name"];
    //print_r($_POST);
 
    $statement = mysqli_prepare($con, "INSERT INTO user (uid, real_name, nick_name) VALUES (?,?,?)");
    mysqli_stmt_bind_param($statement, "sss", $uid, $real_name, $nick_name);
    mysqli_stmt_execute($statement);
 
    echo json_encode(true);
    exit(0);
?>
