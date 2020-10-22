<?php
    require_once('lib/simple_html_dom.php');
    require_once('Good/Good.php');
    require_once('GoodsFactory.php');
    
    // Класс констант (некий аналог enum), содержит основные URL интернет магазина на каждый товар 
    class GoodTypeUri {
        const CPU = 'https://brain.com.ua/category/Processory-c1097-128/';
        const GPU = 'https://brain.com.ua/category/Vydeokarty-c1403/';
        const HDD = 'https://brain.com.ua/category/Vynchestery_HDD-c1361-260/';
        const SSD = 'https://brain.com.ua/category/SSD_dysky-c1484/';
        const RAM = 'https://brain.com.ua/category/Moduly_pamyaty-c1334/';
        const MB = 'https://brain.com.ua/category/Systemnye_materynskye_platy-c1264-226/';
        const PS = 'https://brain.com.ua/category/Bloky_pytanyya-c1442-221/';
    }

    class GoodDownloader {

        private static function forHTTPS( $url ) {

            $arrContextOptions=array(
                "ssl"=>array(
                    "verify_peer"=>false,
                    "verify_peer_name"=>false,
                ),
            );  
            $sw=file_get_contents('https://telemart.ua/processor/', false, stream_context_create($arrContextOptions));
            return $sw;
          }

        private static $MAIN_URL = "https://brain.com.ua";

        /* Функция для парсинга страницы сайта и извлечения из нее массива товаров
        * Параметры:
        * $goodType - константа класса GoodType, является URL-ом на нужный катало товаров (тип товара по сути)
        * $page - номер страницы каталога
        * Возврат:
        * json строка, массив товаров полученых со страницы
        */
        public static function downloadGoods($goodType, $goodTypeUrl, int $page) {
             
            // Парсинг страницы и Извлечение товаров
            $html = file_get_html($goodTypeUrl . "page=$page/");
            $goodsHtml = $html->find('div[class="br-pp br-pp-ex goods-block__item br-pcg br-series"]');
           
            // Создаание товаров
            // Функции find всегда возвращают массив, даже если там один элемент, потому нужно брать элемент с индексом 0 
            $goods = array(); 
            $size = count($goodsHtml);
            for ($i = 0; $i < $size; ++$i) 
            {          
                // Получение всех необходимы аттрибутов товаров
                $imgAttrs = $goodsHtml[$i]->find('img[itemprop="image"]');
                $urlStats = $goodsHtml[$i]->find('a[itemprop="url"]')[0]->{'href'}; 
                $prices = $goodsHtml[$i]->find('div[class="br-pp-price br-pp-price-grid"]')[0]
                                        ->find('span=[itemprop="price"]');
                $shortStats = $goodsHtml[$i]->find('div[class="br-pp-i br-pp-i-grid"]');

                //self::downloadStats($urlStats);

                // Добавление товара в массив
                $name = $imgAttrs[0]->{'alt'};
                $img = $imgAttrs[0]->{'data-observe-src'};
                $price = $prices[0]->innertext;
                $goods[] = (GoodsFactory::createGood($goodType, $name, $img, $price, $shortStats[0]->innertext))->toJson();
            }

            $data = json_encode($goods);

            return $data;
        }


        public static function downloadStats($urlStats) 
        {
            $fullUrl = self::$MAIN_URL . $urlStats;
            $html = file_get_html($fullUrl); 

            $mainStats = $html->find('div[class="br-pr-chr-item"]'); 
            echo $mainStats[0];
            echo "<BR><BR>";
        }

        // На случай если надо будет что то делать с изображением, пока что достаточно напрямую скачивать по URL

        /* Функция для скачивание изображения товара
        * Параметры:
        * $url - url по которому скачивается изображение
        * Возврат:
        * ...
        *
        public static function downloadImage($url)
        {
            // Взятие имени изорбражение (формат url https://.../<имя изображения>.jpg)
            $params = explode('/', $url);
            $nameImg = explode('.', $params[count($params) - 1])[0] . '.png'; 
            $imgPath = "images/$nameImg";

            // Если изображение никогда не скачивалось - оно скачивается, иначе быстрее будет взять скаченое 
            if (!file_exists($imgPath))
            {
                $ch = curl_init($url);
                $fp = fopen($imgPath, 'wb');
                curl_setopt($ch, CURLOPT_FILE, $fp);
                curl_setopt($ch, CURLOPT_HEADER, 0);
                curl_exec($ch);
                curl_close($ch);
                fclose($fp);
            }
  
            // Чтение и возврат изображение
            $fp = fopen($imgPath, 'rb');
            header("Content-Type: image/png");
            header("Content-Length: " . filesize($imgPath));
            return fpassthru($fp);
        }
        */
    }


?>