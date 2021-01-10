import express from "express";
import {goodsRouter} from "./routers/goodsRouter";

const app = express();

app.use('/goods', goodsRouter);

app.listen(3000, () => {
    console.log("START");
});