<?php
    require_once('Good/Good.php');

    class RAM extends Good {

        private $typeMemory;
        private $sizeMemory;
        private $frequency;
        private $timing;

        public function __construct($name, $image, $price, $htmlShortStats)
        {
            parent::__construct($name, $image, $price);

            // Создание превью данных
            $shortStats = explode(',', $htmlShortStats);

            $this->typeMemory = $shortStats[0];
            $this->sizeMemory = $shortStats[1];
            $this->frequency = $shortStats[3];
            $this->timing = $shortStats[4];
        }

        public function toJson()
        {
            $arrayData = parent::toJson();
            $previewData = array('stats' => [
                'Тип памяти' =>  $this->typeMemory,
                'Объем памяти' => $this->sizeMemory,
                'Частота' => $this->frequency,
                'Тайминг' => $this->timing
            ]);

            return json_encode(array_merge($arrayData, $previewData));
        }

    }
?>
