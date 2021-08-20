import express, { Router } from "express";
import BuildController from "../controllers/BuildController";
import UserController from "../controllers/UserController";

export const buildRouter: Router = express.Router({ mergeParams: true });
const buildController: BuildController = new BuildController();
const userController: UserController = new UserController();

buildRouter.get('/public', buildController.getPublicBuilds);

////////////////////////////// API ПОЛЬЗОВАТЕЛЬСКИХ СБОРОК /////////////////////
buildRouter.get('', userController.checkAuth, buildController.getBuildsByUser);
buildRouter.post('/new', userController.checkAuth, buildController.addBuild);
buildRouter.put('/:idBuild([0-9]+)/status', userController.checkAuth, buildController.updatePublicStatus);
buildRouter.put('/:idBuild([0-9]+)', userController.checkAuth, buildController.updateBuild);

buildRouter.delete('/:idBuild([0-9]+)', userController.checkAuth, buildController.removeBuild);