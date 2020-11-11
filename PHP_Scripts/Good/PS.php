<?php
    require_once('Good/Good.php');

    class PS extends Good {

        private $power;
        private $motherboard;
        private $cpu;
        private $sizeMm;
        public $previewData;

        public function __construct($name, $image, $price, $htmlShortStats, $urlFullData)
        {
            parent::__construct($name, $image, $price, $urlFullData);

            // Создание превью данных
            $shortStats = explode(',', $htmlShortStats);
            $index = 0;
            if (stripos($shortStats[1], 'Вт') !== false)
                $index = 1;
            $this->power = $shortStats[$index];
            $this->motherboard = $shortStats[$index + 1];
            $this->cpu = $shortStats[$index + 2];
            $this->sizeMm = $shortStats[count($shortStats) - 1]; 

            $this->setPreviewData();
        }

        public function setPreviewData()
        {
            $this->previewData = array([
                'Мощность' =>  $this->power,
                'Подкл. мат. платы' => $this->motherboard,
                'Подкл. процессора' => $this->cpu,
                'Размер' => $this->sizeMm           
            ]);
        }

    }
?>