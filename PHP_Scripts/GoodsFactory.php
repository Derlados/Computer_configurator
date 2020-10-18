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
        public static function createGood($typeGood, $name, $image, $price, $htmlShortStats) 
        {
            switch ($typeGood)
            {
                case GoodType::CPU:
                    return new CPU($name, $image, $price, $htmlShortStats);
                case GoodType::GPU:
                    break;
                case GoodType::HDD:
                    break;
                case GoodType::SSD:
                    break; 
                case GoodType::RAM:
                    break;
                case GoodType::MB:
                    break;  
                case GoodType::PS:
                    break;                   
            }
        }
    }
?>