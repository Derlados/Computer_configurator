import express, { Router } from "express";
import ComponentController from "../controllers/ComponentController";

export const componentRouter: Router = express.Router();
const componentController: ComponentController = new ComponentController();

componentRouter.get('/category=:category', componentController.getComponents);
componentRouter.get('/category=:category/filters', componentController.getFilters);