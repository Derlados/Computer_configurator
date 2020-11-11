<?php
    require_once('Good/Good.php');

    class RAM extends Good {

        private $typeMemory;
        private $sizeMemory;
        private $frequency;
        private $timing;
        public $previewData;

        public function __construct($name, $image, $price, $htmlShortStats, $urlFullData)
        {
            parent::__construct($name, $image, $price, $urlFullData);

            // Создание превью данных
            $shortStats = explode(',', $htmlShortStats);

            $this->typeMemory = $shortStats[0];
            $this->sizeMemory = $shortStats[1];
            $this->frequency = $shortStats[3];
            $this->timing = $shortStats[4];

            $this->setPreviewData();
        }

        public function setPreviewData()
        {
            $this->previewData = array([
                'Тип памяти' =>  $this->typeMemory,
                'Объем памяти' => $this->sizeMemory,
                'Частота' => $this->frequency,
                'Тайминг' => $this->timing
            ]);
        }

    }
?>
