<?php
    require_once('Good/Good.php');

    class HDD extends Good {

        private $memorySize;
        private $rotationSpeed;
        private $bufferSize;
        private $interface;

        public function __construct($name, $image, $price, $htmlShortStats, $urlFullData)
        {
            parent::__construct($name, $image, $price, $urlFullData);

            // Создание превью данных
            $shortStats = explode(',', $htmlShortStats);

            $this->memorySize = $shortStats[0];
            $this->rotationSpeed = $shortStats[1];
            $this->bufferSize = $shortStats[2];
            $this->interface = $shortStats[3];
        }

        public function toJson()
        {
            $arrayData = parent::toJson();
            $previewData = array('previewData' => [
                'Объем памяти' =>  $this->memorySize,
                'Скорость вращ.' => $this->rotationSpeed,
                'Объем буфера' => $this->bufferSize,
                'Интерфейс' => $this->interface
                
            ]);

            return json_encode(array_merge($arrayData, $previewData));
        }

    }
?>
