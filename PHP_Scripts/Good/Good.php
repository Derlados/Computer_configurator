<?php

    class GoodType {
        const CPU = 'CPU';
        const GPU = 'GPU';
        const HDD = 'HDD';
        const SSD = 'SSD';
        const RAM = 'RAM';
        const MB = 'MOTHERBOARD';
        const PS = 'POWER_SUPPLY';
    }

    class Good 
    {
        public $name;
        public $imageUrl;
        public $imageName;
        public $price;
        public $urlFullData;


        public function __construct($name, $imageUrl, $price, $urlFullData)
        {
            $this->name = $name;
            $this->imageUrl = $imageUrl;
            $this->price = $price;
            $this->urlFullData = $urlFullData;

            // Имя является последним параметром URL
            $splitUrl = explode('/', $imageUrl);
            $this->imageName = $splitUrl[count($splitUrl) - 1];
        }
    }
?>