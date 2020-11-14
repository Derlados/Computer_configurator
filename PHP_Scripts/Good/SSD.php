<?php
    require_once('Good/Good.php');

    class SSD extends Good {

        private $memorySize = "";
        private $flash = "";
        private $form = "";
        private $readWritespeed = "";
        public $previewData;

        public function __construct($name, $image, $price, $htmlShortStats, $urlFullData)
        {
            parent::__construct($name, $image, $price, $urlFullData);

            // Создание превью данных
            $shortStats = explode(',', $htmlShortStats);
            $index = 0;
            if (stripos($shortStats[0], 'Серия') !== false)
                $index = 1;
            $this->memorySize = $shortStats[$index];
            $this->flash = $shortStats[$index + 1];
            $this->form = $shortStats[$index + 2];
            $this->readWritespeed = $shortStats[$index + 5] .  $shortStats[$index + 7]; 

            $this->setPreviewData();
        }

        public function setPreviewData()
        {
            $this->previewData = array([
                'Объем памяти' =>  $this->memorySize,
                'Флеш память' => $this->flash,
                'Форм фактор' => $this->form,
                'Чтение/запись' => $this->readWritespeed
                
            ]);
        }

    }
?>