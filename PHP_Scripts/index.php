<?php
    require_once('GoodDownloader.php');
    require_once('Good.php');

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
            $type = $_GET['type'];
            $page = $_GET['page'];
            
            // Выбор подходящего действия в соответствии с получеными параметрами
            $data;
            switch ($type) 
            {   
                case "CPU":
                    $data = GoodDownloader::downloadGoods(GoodType::CPU, $page);
                    break;
                case "GS":
                    $data = GoodDownloader::downloadGoods(GoodType::GS, $page);
                    break;
                case "HDD":
                    $data = GoodDownloader::downloadGoods(GoodType::HDD, $page);
                    break;
                case "SSD":
                    $data = GoodDownloader::downloadGoods(GoodType::SSD, $page);
                    break; 
                case "RAM":
                    $data = GoodDownloader::downloadGoods(GoodType::RAM, $page);
                    break;
                case "MB":
                    $data = GoodDownloader::downloadGoods(GoodType::MB, $page);
                    break;  
                case "PS":
                    $data = GoodDownloader::downloadGoods(GoodType::PS, $page);
                    break;                   
                default:
                    // По хорошему надо бы ошибку тут выдавать, не забыть посмотреть 
                    echo "404 not found";
                    return;
            
            }

            echo $data;
        }
        else if (array_shift($requestUri) == "image")
        {
            $imageUrl = $_GET['imageUrl'];
            $data = GoodDownloader::downloadImage($imageUrl);
            echo $data;
        }
        else
            echo "invalid method";
    }
?>