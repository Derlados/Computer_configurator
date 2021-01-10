<?php
    require_once('Good/Good.php');

    class CompCase extends Good {

        private $formFactor  = "";
        private $coolers = "";
        private $size = "";
        private $class = "";
        public $previewData;

        public function __construct($name, $image, $price, $htmlShortStats, $urlFullData)
        {
            parent::__construct($name, $image, $price, $urlFullData);

            // Создание превью данных
            $shortStats = explode(',', $htmlShortStats);
            $this->class = $shortStats[0];

            $size = count($shortStats);
            for ($i = 1; $i < $size; ++$i)
                if (stripos($shortStats[$i], 'Вентиляторы') !== false) {
                    $temp = explode(' ', $shortStats[$i]);
                    for ($j = 3; $j < count($temp); ++$j)
                        $this->coolers .= $temp[$j] . ' ';
                    break;
                }

            $count = 0;
            for ($i = 1; $i < $size; ++$i)
                if (stripos($shortStats[$i], 'TX') !== false || stripos($shortStats[$i], 'CEB') !== false) {
                    if ($count > 0)
                        $this->formFactor .= ',';
                     $this->formFactor .= $shortStats[$i];
                     ++$count;          
                }

            for ($i = $size - 1; $i >= 1; --$i)
                if (stripos($shortStats[$i], 'мм') !== false) {
                    $this->size = $shortStats[$i];
                    break;
                }

            $this->setPreviewData();
        }

        public function setPreviewData()
        {
            $this->previewData = array([
                "Форм-фактор" => $this->formFactor,
                "Размер" => $this->size,
                "Класс" => $this->class,
                "Вентиляторы" => $this->coolers
            ]);
        }

    }
?>