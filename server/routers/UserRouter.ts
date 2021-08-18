import express, { Router } from "express";
import UserController from "../controllers/UserController";
import { buildRouter } from "./BuildRouter";

export const userRouter: Router = express.Router();
const userController: UserController = new UserController();

userRouter.get('/:idUser([0-9]+)', userController.checkAuth)
userRouter.post('/reg', userController.register);
userRouter.post('/login', userController.login);
userRouter.post('/google-sign', userController.googleSignIn);
userRouter.put('/:idUser([0-9]+)/update', userController.checkAuth)
userRouter.delete('/:idUser([0-9]+)/delete', userController.checkAuth)
