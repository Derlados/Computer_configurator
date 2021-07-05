import { Response } from "express";
import { HttpCodes } from "../constants/HttpCodes";
import ComponentModel from "../models/ComponentModel";

export default class ComponentController {


    private componentModel: ComponentModel;

    constructor() {
        this.componentModel = new ComponentModel();
    }

    public getComponents = (req: any, res: Response): void => {
        const category: string = req.params.category;
        const offset: number = req.params.block;

        this.componentModel.getComponents(category, offset)
            .then(data => {
                res.status(HttpCodes.OK).send(JSON.stringify(data));
            })
            .catch(err => {
                res.status(HttpCodes.INTERNAL_SERVER_ERROR).send(err.message);
            })
    }

    public getMaxBlocks = (req: any, res: Response): void => {
        const category: string = req.params.category;

        this.componentModel.getMaxBlocks(category)
            .then(data => {
                res.status(HttpCodes.OK).send(JSON.stringify(data));
            })
            .catch(err => {
                res.status(HttpCodes.INTERNAL_SERVER_ERROR).send(err.message);
            })
    }

    public getFilters = (req: any, res: Response): void => {
        const category: string = req.params.category;

        this.componentModel.getFilters(category)
            .then(data => {

                const result: any = {}
                data.forEach((value, key) => {
                    result[key] = value
                })

                res.status(HttpCodes.OK).send(JSON.stringify(result));
            })
            .catch(err => {
                res.status(HttpCodes.INTERNAL_SERVER_ERROR).send(err.message);
            })
    }
}