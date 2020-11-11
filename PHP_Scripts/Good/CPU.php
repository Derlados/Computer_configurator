<?php
    require_once('Good/Good.php');

    class CPU extends Good {

        private $socket;
        private $cores;
        private $threads;
        private $frequency;
        private $TDP;
        public $previewData;

        public function __construct($name, $image, $price, $htmlShortStats, $urlFullData)
        {
            parent::__construct($name, $image, $price, $urlFullData);

            // Создание превью данных
            $shortStats = explode(',', $htmlShortStats);
            $this->socket = $shortStats[0];
            $this->cores = explode(' ', $shortStats[1])[1];
            if (stripos($shortStats[2], 'поток') === false) 
            {
                $this->threads = '-';
                $this->frequency = $shortStats[2];
            }
            else
            {
                $this->threads =  explode(' ', $shortStats[2])[1];
                $this->frequency = $shortStats[3];
            }

            $size = count($shortStats);
            for ($i = 3; $i < $size; ++$i)
                if (stripos($shortStats[$i], 'TDP') !== false)
                {
                    $this->TDP = explode(' ', $shortStats[$i])[3];
                    break;
                }

            $this->setPreviewData();
        }

        public function setPreviewData()
        {
            $this->previewData = array([
                'Сокет' => $this->socket,
                'Ядер(потоков)' => ($this->cores . '(' . $this->threads . ')'),
                'Частота' => ($this->frequency . ' ГГц'),
                'TDP' => ($this->TDP)
            ]);
        }
    }
?>