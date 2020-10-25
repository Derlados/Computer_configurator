<?php
    require_once('Good/Good.php');

    class GPU extends Good {

        private $interface;
        private $memorySize;
        private $typeMemory;
        private $memoryBus;

        public function __construct($name, $image, $price, $htmlShortStats, $urlFullData)
        {
            parent::__construct($name, $image, $price, $urlFullData);

            // Создание превью данных
            $shortStats = explode(',', $htmlShortStats);
            $this->interface = $shortStats[0];
            $this->memorySize = $shortStats[1];
            $this->typeMemory = $shortStats[2];
            $this->memoryBus = $shortStats[3];
        }

        public function toJson()
        {
            $arrayData = parent::toJson();
            $previewData = array('stats' => [
                'Интерфейс' =>  $this->interface,
                'Размер памяти' => $this->memorySize,
                'Тип памяти' => $this->typeMemory,
                'Шина памяти' => $this->memoryBus
                
            ]);

            return json_encode(array_merge($arrayData, $previewData));
        }

    }
?>