<?php
    $con = mysqli_connect('localhost', 'holo', 'kitce2022*', 'holo');

    mysqli_query($con,'SET NAMES utf8');

    $uid = $_GET["uid"];
    
    $statement = mysqli_prepare($con, "SELECT score FROM user WHERE uid = ?");
    mysqli_stmt_bind_param($statement, "s", $uid);
    $exec = mysqli_stmt_execute($statement);
    
    mysqli_stmt_store_result($statement);
    mysqli_stmt_bind_result($statement, $score);

    $response = array();
    $response["success"] = true;
 
    while(mysqli_stmt_fetch($statement)) {
        $response["score"] = $score;
    }
 
    echo json_encode($response, JSON_UNESCAPED_UNICODE);
?>