import express, { Router } from "express";
import BuildController from "../controllers/BuildController";
import UserController from "../controllers/UserController";

export const buildRouter: Router = express.Router({ mergeParams: true });
const buildController: BuildController = new BuildController();
const userController: UserController = new UserController();

//TODO Спорное решение управления комментариями через сущность сборок, возможно необходимо перенести в отдельный лкасс

buildRouter.get('/public', buildController.getPublicBuilds);
buildRouter.get('/:idBuild([0-9]+)/comments', buildController.getComments)

////////////////////////////// API ПОЛЬЗОВАТЕЛЬСКИХ СБОРОК /////////////////////
buildRouter.get('', userController.checkAuth, buildController.getBuildsByUser);

buildRouter.post('/new', userController.checkAuth, buildController.addBuild);
buildRouter.post('/:idBuild([0-9]+)/comments', userController.checkAuth, buildController.addComment);
buildRouter.post('/:idBuild([0-9]+)/comments/:idParentComment([0-9]+)/answer', userController.checkAuth, buildController.addComment);

buildRouter.put('/:idBuild([0-9]+)/status', userController.checkAuth, buildController.updatePublicStatus);
buildRouter.put('/:idBuild([0-9]+)', userController.checkAuth, buildController.updateBuild);

buildRouter.delete('/:idBuild([0-9]+)', userController.checkAuth, buildController.removeBuild);