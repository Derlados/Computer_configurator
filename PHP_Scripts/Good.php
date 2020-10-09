<?php

    class Good {
        public $name;
        public $image;
        public $price;

        public function __construct($name, $image, $price)
        {
            $this->name = $name;
            $this->image = $image;
            $this->price = $price;
        }

        public function toJson()
        {
            return json_encode([
                'name' => $this->name,
                'image' => $this->image,
                'price' => $this->price
            ]);
        }
    }
?>