import express from "express";
import cors from "cors";
import fileUpload from "express-fileupload";
import { componentRouter } from "./routers/ComponentRouter";
import { userRouter } from "./routers/UserRouter";
import dotenv from 'dotenv';
import { buildRouter } from "./routers/BuildRouter";
dotenv.config();

export const imageRoot = __dirname + '/images';
const PORT = process.env.PORT || 3000;

const app = express();
app.use(cors())
app.use(express.urlencoded({ extended: false }));
app.use(express.json());
app.use(express.static(__dirname));
app.use(fileUpload());

app.use(express.static('/images'));

app.use('/api/builds', buildRouter);
app.use('/api/users/:idUser([0-9]+)/builds', buildRouter);
app.use('/api/users', userRouter);
app.use('/api/components', componentRouter);

app.listen(PORT, () => {
    console.log("START");
});
