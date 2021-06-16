import express, { request, Response } from "express";
import cors from "cors";
import fileUpload from "express-fileupload";
// import { goodsRouter as productsRouter } from "./routers/productsRouter";

const app = express();
app.use(cors())
app.use(express.urlencoded({ extended: false }));
app.use(express.json());
app.use(express.static(__dirname));
app.use(fileUpload());

// app.use('/goods', productsRouter);

app.listen(3000, () => {
    console.log("START");
});