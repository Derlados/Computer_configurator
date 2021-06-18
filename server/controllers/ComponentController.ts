import { Response } from "express";
import { HttpCodes } from "../constants/HttpCodes";
import ComponentModel from "../models/ComponentModel";

export default class ComponentController {

    private readonly BLOCK_SIZE: number = 100;
    private componentModel: ComponentModel;

    constructor() {
        this.componentModel = new ComponentModel();
    }

    public getComponents = (req: any, res: Response): void => {
        const category: string = req.params.category;
        const offset: number = req.params.block * this.BLOCK_SIZE;

        this.componentModel.getComponents(category, offset)
            .then(data => {
                res.status(HttpCodes.OK).send(JSON.stringify(data));
            })
    }

    public getComponentById = (req: any, res: Response): void => {

    }

    public getMaxPages = (req: any, res: Response): void => {

    }
}