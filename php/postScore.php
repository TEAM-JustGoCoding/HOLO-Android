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
    $uid = $_POST["uid"];
    $star = $_POST["star"];
    
    $statement = mysqli_prepare($con, "UPDATE user SET deal_count = deal_count+1 WHERE uid = ?");
    mysqli_stmt_bind_param($statement, "s", $uid);
    $exec = mysqli_stmt_execute($statement);

    $statement = mysqli_prepare($con, "SELECT deal_count FROM user WHERE uid = ?");
    mysqli_stmt_bind_param($statement, "s", $uid);
    $exec = mysqli_stmt_execute($statement);
    mysqli_stmt_store_result($statement);
    mysqli_stmt_bind_result($statement, $deal_count);

    $response = array();
    while(mysqli_stmt_fetch($statement)) {
        $response["deal_count"] = $deal_count;
    }

    if($deal_count == 1){
        $result = $star;
    }else{
        $statement = mysqli_prepare($con, "SELECT score FROM user WHERE uid = ?");
        mysqli_stmt_bind_param($statement, "s", $uid);
        $exec = mysqli_stmt_execute($statement);
        mysqli_stmt_store_result($statement);
        mysqli_stmt_bind_result($statement, $score);
        
        while(mysqli_stmt_fetch($statement)) {
            $response["score"] = $score;
        }
        $result = ($response["score"] * ($response["deal_count"] - 1) + $star) / $response["deal_count"];
        $result = sprintf("%0.1f", $result);
    }
    print_r($response);
    //$result = ($score * ($deal_count - 1) + $star) / $deal_count;
    
    $statement = mysqli_prepare($con, "UPDATE user SET score = ? WHERE uid = ?");
    mysqli_stmt_bind_param($statement, "ss", $result, $uid);
    $exec = mysqli_stmt_execute($statement);

    print_r($result);
    echo json_encode(true);
    exit(0);
?>
