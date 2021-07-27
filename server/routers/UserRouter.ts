import express, { Router } from "express";
import UserController from "../controllers/UserController";

export const userRouter: Router = express.Router();
const userController: UserController = new UserController();

userRouter.post('/reg', userController.register);
userRouter.post('/login', userController.login);
userRouter.post('/google-sign', userController.googleSignIn);
userRouter.put('/:id/update', userController.checkAuth)
userRouter.put('/:id/remove', userController.checkAuth)
userRouter.get('/:id', userController.checkAuth)
