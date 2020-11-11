<?php
    require_once('Good/Good.php');

    class GPU extends Good {

        private $interface;
        private $memorySize;
        private $typeMemory;
        private $memoryBus;
        public $previewData;

        public function __construct($name, $image, $price, $htmlShortStats, $urlFullData)
        {
            parent::__construct($name, $image, $price, $urlFullData);

            // Создание превью данных
            $shortStats = explode(',', $htmlShortStats);
            $this->interface = $shortStats[0];
            $this->memorySize = $shortStats[1];
            $this->typeMemory = $shortStats[2];
            $this->memoryBus = $shortStats[3];

            $this->setPreviewData();
        }

        public function setPreviewData()
        {
            $this->previewData = array([
                'Интерфейс' =>  $this->interface,
                'Размер памяти' => $this->memorySize,
                'Тип памяти' => $this->typeMemory,
                'Шина памяти' => $this->memoryBus
            ]);
        }

    }
?>