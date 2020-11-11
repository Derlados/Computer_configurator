<?php
    require_once('Good/Good.php');

    class HDD extends Good {

        private $memorySize;
        private $rotationSpeed;
        private $bufferSize;
        private $interface;
        public $previewData;

        public function __construct($name, $image, $price, $htmlShortStats, $urlFullData)
        {
            parent::__construct($name, $image, $price, $urlFullData);

            // Создание превью данных
            $shortStats = explode(',', $htmlShortStats);

            $this->memorySize = $shortStats[0];
            $this->rotationSpeed = $shortStats[1];
            $this->bufferSize = $shortStats[2];
            $this->interface = $shortStats[3];

            $this->setPreviewData();
        }

        public function setPreviewData()
        {
            $this->previewData = array([
                'Объем памяти' =>  $this->memorySize,
                'Скорость вращ.' => $this->rotationSpeed,
                'Объем буфера' => $this->bufferSize,
                'Интерфейс' => $this->interface
            ]);
        }

    }
?>
