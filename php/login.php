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
    
    $statement = mysqli_prepare($con, "SELECT * FROM user WHERE uid = ?");
    mysqli_stmt_bind_param($statement, "s", $uid);
    $exec = mysqli_stmt_execute($statement);
    
    mysqli_stmt_store_result($statement);
    mysqli_stmt_bind_result($statement, $id, $uid, $password, $real_name, $nick_name, $join_date, $score, $deal_count, $token);

    $response = array();
    $response["success"] = true;
 
    while(mysqli_stmt_fetch($statement)) {
        $response["success"] = true;
        $response["id"] = $id;
        $response["uid"] = $uid;
        $response["password"] = $password;
        $response["real_name"] = $real_name;
        $response["nick_name"] = $nick_name;
        $response["join_date"] = $join_date;
        $response["score"] = $score;
        $response["deal_count"] = $deal_count;
        $response["token"] = $token;
    }
 
    echo json_encode($response, JSON_UNESCAPED_UNICODE);
?>