import express, { Router } from "express";
import { GoodsController } from "../controllers/goodsController";

const goodsRouter: Router = express.Router();
const goodsController: GoodsController = new GoodsController();

goodsRouter.get('/:id', goodsController.goodsInfo); // Получение всех характеристик комплектующего
goodsRouter.get('/category/:idCategory/page=:numPage', goodsController.goodsList); // Выборка с указанием страницы
goodsRouter.get('/category/:idCategory/page=:numPage/filters=:filters', goodsController.goodsList); // Выборка с указанием фильтров

// Получение максимального количества страниц (отдельный запрос потому что делать выборку комплектующих без лимита не целесообразно)
goodsRouter.get('/category/:idCategory/maxPages', goodsController.getMaxPages);
goodsRouter.get('/category/:idCategory/filters=:filters/maxPages',  goodsController.getMaxPages);

export{goodsRouter};