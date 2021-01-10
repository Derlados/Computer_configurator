<?php
    require_once('GoodDownloader.php');
    require_once('Good/Good.php');

    /* API:
    * GET <домен>/goods - получение всех товаров на конкретной странице. Тип товара и страница передаются в GET запросе
    * GET <домен>/goods/fullData - получение полной информации о комплектующем. В гет запросе передается ссылка на страницу (без домена)
    */
    
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
            $search = $_GET['search'];


            // Выбор подходящего действия в соответствии с получеными параметрами
            $data = GoodDownloader::downloadGoods($type, $page, $search);
            echo $data;
        }
        else if ($requestUri[0] == "fullData")
        {
            $urlFullData = $_GET['urlFullData'];
            echo GoodDownloader::downloadFullData($urlFullData);
        }   
        else if ($requestUri[0] == "maxPages") 
        {
            $goodType = $_GET['typeGood'];
            $search = $_GET['search'];
            echo GoodDownloader::getMaxPages($goodType, $search);
        }
        else
            http_response_code(404);
    }   
    else
        http_response_code(404);

?>