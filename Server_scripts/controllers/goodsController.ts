import { RowDataPacket } from "mysql2";
import { Goods } from "../models/goodsModel/goods";
import { GoodsModel } from "../models/goodsModel/goodsModel";

export class GoodsController {
    private goodsModel: GoodsModel;
 
    constructor() {
        this.goodsModel = new GoodsModel();
    }

    /** Получение всех характеристик комплектующего
     * @param request - строка запроса
     * Параметры запроса:
     * id - id комплектующего (обязательный)
     * @param response - ответ сервера
     */
    public goodsInfo = (request, response): void => {
        let id: number = parseInt(request.params.id);
        this.goodsModel.getFullData(id, (data: Object) => {
            response.send(JSON.stringify(data));
        });
    }

    /** Получение списка товаров
     * @param request - строка запроса
     * Параметры запроса:
     * idCategory - id категории (обязательный)
     * numPage - номера страницы (обязательный)
     * filters - строка фильтров в формате <количество фильтров>;<id атрибута>,... (необязательный)
     * @param response - ответ сервера
     */
    public goodsList = (request, response): void => {
        let idCategory: number = parseInt(request.params.idCategory);
        let numPage: number = request.params.numPage != null ? parseInt(request.params.numPage) : 1;

        this.goodsModel.getGoodsByCategory(idCategory, numPage, function(goodsPreview: RowDataPacket[]) {
            response.send(JSON.stringify(goodsPreview));
        });
    }

    /** Получение максимального количества страниц
     * @param request - строка запроса
     * Параметры запроса:
     * idCategory - id категории (обязательный)
     * filters - строка фильтров в формате <количество фильтров>;<id атрибута>,... (необязательный)
     * @param response 
     */
    public getMaxPages = (request, response): void => {
        let idCategory: number = parseInt(request.params.idCategory);
        this.goodsModel.getMaxPages(idCategory, function(maxPages: number) {
            response.send(maxPages.toString());
        });
    }
}