<?php
   
   // Для Фабрики нужно подключить все классы товаров (да, php не может это сделать кроме как перебором прямо в файлах)
    foreach (scandir('Good') as $filename) {
        $path = 'Good/' . $filename;
        if (is_file($path))
            require_once($path);
    }

    // Фабрика для создание комплектующих
    // Необходима по причине того, что невозможно заранее знать какой именно объект будет парсится в GoodsDownloader
    class GoodsFactory 
    {
        public static function createGood($typeGood, $name, $image, $price, $htmlShortStats, $urlFullData) 
        {
            switch ($typeGood)
            {
                case GoodType::CPU:
                    return new CPU($name, $image, $price, $htmlShortStats, $urlFullData);
                case GoodType::GPU:
                    return new GPU($name, $image, $price, $htmlShortStats, $urlFullData);
                case GoodType::HDD:
                    return new HDD($name, $image, $price, $htmlShortStats, $urlFullData);
                case GoodType::SSD:
                    return new SSD($name, $image, $price, $htmlShortStats, $urlFullData);
                case GoodType::RAM:
                    return new RAM($name, $image, $price, $htmlShortStats, $urlFullData);
                case GoodType::MB:
                    return new MB($name, $image, $price, $htmlShortStats, $urlFullData);
                case GoodType::PS:
                    return new PS($name, $image, $price, $htmlShortStats, $urlFullData);               
            }
        }
    }
?>