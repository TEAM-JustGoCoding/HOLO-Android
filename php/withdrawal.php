<?php
    $con = mysqli_connect('localhost', 'holo', 'kitce2022*', 'holo');

    mysqli_query($con,'SET NAMES utf8');

    $uid = mysqli_real_escape_string($con, $POST['uid']);

//    $sql = "DELETE FROM user WHERE uid ='".$_POST['uid']."'";
//    $result = mysqli_query($con, $sql);
//    $exist = mysqli_num_rows($result);
//        
//    if($exist>0){
//        echo json_encode(true);
//        exit(0);
//    }
//    else{
//        echo json_encode(false);
//        exit(0);
//    }

    $uid = $_POST["uid"];

    $statement = mysqli_prepare($con, "DELETE FROM user WHERE uid = ?");
    mysqli_stmt_bind_param($statement, "s", $uid);
    mysqli_stmt_execute($statement);

//    $response = array();
//    $response["success"] = true;

    echo json_encode(true);
?>
