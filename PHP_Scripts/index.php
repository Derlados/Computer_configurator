<?php
    require_once('GoodDownloader.php');
    require_once('Good/Good.php');

    
    if ($_SERVER['REQUEST_METHOD'] != "GET")
    {
        echo "invalid method";
        return;
    }

    $requestUri = explode('/', stristr($_SERVER['REQUEST_URI'] . '?', '?', true));
    array_shift($requestUri); // Первый элемент всегда доменное имя

    if (array_shift($requestUri) == "goods")
    {
        if (empty($requestUri))
        {
            // Извлченеи GET параметров
            $type = $_GET['typeGood'];
            $page = $_GET['page'];
            
            // Выбор подходящего действия в соответствии с получеными параметрами
            $data;
            switch ($type) 
            {   
                case GoodType::CPU:
                    $data = GoodDownloader::downloadGoods(GoodType::CPU, GoodTypeUri::CPU, $page);
                    break;
                case GoodType::GPU:
                    $data = GoodDownloader::downloadGoods(GoodType::GPU, GoodTypeUri::GPU, $page);
                    break;
                case GoodType::HDD:
                    $data = GoodDownloader::downloadGoods(GoodType::HDD, GoodTypeUri::HDD, $page);
                    break;
                case GoodType::SSD:
                    $data = GoodDownloader::downloadGoods(GoodType::SSD, GoodTypeUri::SSD, $page);
                    break; 
                case GoodType::RAM:
                    $data = GoodDownloader::downloadGoods(GoodType::RAM, GoodTypeUri::RAM, $page);
                    break;
                case GoodType::MB:
                    $data = GoodDownloader::downloadGoods(GoodType::MB, GoodTypeUri::MB, $page);
                    break;  
                case GoodType::PS:
                    $data = GoodDownloader::downloadGoods(GoodType::PS, GoodTypeUri::PS, $page);
                    break;                   
                default:
                    // По хорошему надо бы ошибку тут выдавать, не забыть посмотреть 
                    echo "404 not found";
                    return;
            }
            echo $data;
        }
    }   
    else
        echo str_get_html(file_get_contents('test.html'));

?>