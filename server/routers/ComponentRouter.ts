import express, { Router } from "express";
import ComponentController from "../controllers/ComponentController";

export const componentRouter: Router = express.Router();
const componentController: ComponentController = new ComponentController();

componentRouter.get('/category=:category/block=:block([0-9]+)', componentController.getComponents);
componentRouter.get('/category=:category/max-blocks', componentController.getComponents);