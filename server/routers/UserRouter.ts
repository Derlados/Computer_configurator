import express, { Router } from "express";
import ComponentController from "../controllers/ComponentController";
import UserController from "../controllers/UserController";

export const userRouter: Router = express.Router();
const userController: UserController = new UserController();

userRouter.post('/reg', userController.register);
userRouter.post('/login', userController.login);