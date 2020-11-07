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
        private $name;
        private $imageUrl;
        private $imageName;
        private $price;
        private $urlFullData;


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

        public function toJson()
        {
            return array(
                'name' => $this->name,
                'imageUrl' => $this->imageUrl,
                'imageName' => $this->imageName,
                'price' => $this->price,
                'urlFullData' => $this->urlFullData
            );
        }
    }
?>