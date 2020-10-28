<?php

    class GoodType {
        const CPU = 'CPU';
        const GPU = 'GPU';
        const HDD = 'HDD';
        const SSD = 'SSD';
        const RAM = 'RAM';
        const MB = 'MB';
        const PS = 'PS';
    }

    class Good 
    {
        private $name;
        private $image;
        private $price;
        private $urlFullData;


        public function __construct($name, $image, $price, $urlFullData)
        {
            $this->name = $name;
            $this->image = $image;
            $this->price = $price;
            $this->$urlFullData = $urlFullData;
        }

        public function toJson()
        {
            return array(
                'name' => $this->name,
                'imageUrl' => $this->image,
                'price' => $this->price,
                'urlFullData' => $this->urlFullData
            );
        }
    }
?>