import { Response } from "express";
import { HttpCodes } from "../constants/HttpCodes";
import BuildModel, { BuildError } from "../models/BuildModel";
import { Build } from "../models/Build";
import { BuildComponent } from "../types/BuildComponent";
import { Pair } from "../types/Pair";

export default class BuildController {
    private buildModel: BuildModel;

    constructor() {
        this.buildModel = new BuildModel();
    }

    public getBuildsByUser = (req: any, res: Response) => {
        const idUser = req.params.idUser;
        this.buildModel.getBuilds(idUser)
            .then((builds: Build[]) => res.status(HttpCodes.OK).json(this.getJsonFromatBuilds(builds)))
            .catch((err) => this.sendError(err, res));
    }

    public getPublicBuilds = (req: any, res: Response) => {
        this.buildModel.getBuilds()
            .then((builds: Build[]) => res.status(HttpCodes.OK).json(this.getJsonFromatBuilds(builds)))
            .catch((err) => this.sendError(err, res));
    }

    public addBuild = (req: any, res: Response) => {
        const idUser = req.params.idUser;
        const build = new Build();
        build.name = req.body.name;
        build.description = req.body.desc;
        build.isPublic = req.body.isPublic;
        const components = Array<Pair>();
        for (const component of req.body.components) {
            components.push(new Pair(component.first, component.second))
        }

        this.buildModel.addBuild(idUser, build, components)
            .then((serverId) => res.status(HttpCodes.OK).json(serverId))
            .catch((err) => this.sendError(err, res));
    }

    public updateBuild = (req: any, res: Response) => {
        const idBuild = req.params.idBuild;
        const idUser = req.params.idUser;
        const build = new Build();
        build.name = req.body.name;
        build.description = req.body.desc;
        const components = Array<Pair>();
        for (const component of req.body.components) {
            components.push(new Pair(component.first, component.second))
        }

        this.buildModel.updateBuild(idUser, idBuild, build, components)
            .then(() => res.status(HttpCodes.OK).end())
            .catch((err) => this.sendError(err, res));
    }

    public updatePublicStatus = (req: any, res: Response) => {
        const idUser = req.params.idUser;
        const idBuild = req.params.idBuild;
        const isPublic: boolean = JSON.parse(req.body.isPublic);

        this.buildModel.updatePublicStatus(idUser, idBuild, isPublic)
            .then((newStatus) => res.status(HttpCodes.OK).json(newStatus))
            .catch((err) => this.sendError(err, res));
    }

    public removeBuild = (req: any, res: Response) => {
        const idUser = req.params.idUser;
        const idBuild = req.params.idBuild;

        this.buildModel.deleteBuild(idUser, idBuild)
            .then(() => res.status(HttpCodes.OK).end())
            .catch((err) => this.sendError(err, res));
    }

    private getJsonFromatBuilds(builds: Build[]): any {
        const responseData: any = JSON.parse(JSON.stringify(builds));

        for (let i = 0; i < responseData.length; i++) {
            responseData[i].components = Object.fromEntries(builds[i].components);
        }

        return responseData;
    }

    private sendError(err: BuildError | any, res: Response) {
        console.log(err);
        if (Object.values(BuildError).includes(err)) {
            switch (err) {
                case BuildError.BUILD_NOT_FOUND:
                    res.status(HttpCodes.NOT_FOUND).end();
                    break;
            }
        } else {
            res.status(HttpCodes.INTERNAL_SERVER_ERROR).end()
        }
    }
}