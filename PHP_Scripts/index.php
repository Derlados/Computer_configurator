<?php
    require_once('GoodDownloader.php');
    require_once('Good.php');

    //$data = GoodDownloader::downloadGoods(GoodType::CPU, 1);
    $data = GoodDownloader::downloadImage('https://brain.com.ua/static/images/prod_img/5/6/U0356256.jpg');
?>