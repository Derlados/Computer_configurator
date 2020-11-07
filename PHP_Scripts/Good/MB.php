<?php
    require_once('Good/Good.php');

    class MB extends Good {

        private $socket;
        private $chipset;
        private $typeMemory;
        private $fromFactor;

        public function __construct($name, $image, $price, $htmlShortStats, $urlFullData)
        {
            parent::__construct($name, $image, $price, $urlFullData);

            // Создание превью данных
            $shortStats = explode(',', $htmlShortStats);

            $this->socket = $shortStats[0];
            $this->chipset = $shortStats[1];
            $this->typeMemory = $shortStats[2];
            if (stripos($shortStats[count($shortStats) - 1], 'BOX') === false)
                $this->fromFactor = $shortStats[count($shortStats) - 1];
            else
                $this->fromFactor = $shortStats[count($shortStats) - 2];
        }

        public function toJson()
        {
            $arrayData = parent::toJson();
            $previewData = array('previewData' => [
                'Сокет' =>  $this->socket,
                'Чипсет' => $this->chipset,
                'Тип памяти' => $this->typeMemory,
                'Форм фактор' => $this->fromFactor
                
            ]);

            return json_encode(array_merge($arrayData, $previewData));
        }

    }
?>
