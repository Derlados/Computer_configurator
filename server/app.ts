import express from "express";
import cors from "cors";
import fileUpload from "express-fileupload";
import { componentRouter } from "./routers/ComponentRouter";
import { userRouter } from "./routers/UserRouter";
import dotenv from 'dotenv';
dotenv.config();

const PORT = process.env.PORT || 3000;

const app = express();
app.use(cors())
app.use(express.urlencoded({ extended: false }));
app.use(express.json());
app.use(express.static(__dirname));
app.use(fileUpload());

app.use('/api/users', userRouter);
app.use('/api/components', componentRouter);

app.listen(PORT, () => {
    console.log("START");
});