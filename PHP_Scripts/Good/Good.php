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

    class Good {
        private $name;
        private $image;
        private $price;


        public function __construct($name, $image, $price)
        {
            $this->name = $name;
            $this->image = $image;
            $this->price = $price;
        }

        public function toJson()
        {
            return array(
                'name' => $this->name,
                'imageUrl' => $this->image,
                'price' => $this->price
            );
        }

        public function fullStatsToJson()
        {
            
        }
    }
?>