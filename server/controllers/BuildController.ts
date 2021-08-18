import { Response } from "express";
import { HttpCodes } from "../constants/HttpCodes";
import BuildModel from "../models/BuildModel";
import { Build } from "../types/Build";

export default class BuildController {
    private buildModel: BuildModel;

    constructor() {
        this.buildModel = new BuildModel();
    }

    public getBuildsByUser = (req: any, res: Response) => {
        const idUser = req.params.idUser;
        this.buildModel.getBuilds(idUser)
            .then((builds: Build[]) => res.status(HttpCodes.OK).json(builds))
            .catch((err) => {
                console.error(err);
                res.status(HttpCodes.INTERNAL_SERVER_ERROR).end()
            });
    }

    public getPublicBuilds = (req: any, res: Response) => {
        this.buildModel.getBuilds()
            .then((builds: Build[]) => res.status(HttpCodes.OK).json(builds))
            .catch((err) => res.status(HttpCodes.INTERNAL_SERVER_ERROR).end());
    }

    public addBuild = (req: any, res: Response) => {
        const build = new Build();
        build.idUser = req.params.idUser;
        build.name = req.body.name;
        build.description = req.body.desc;
        build.isPublic = req.body.isPublic;
        const idComponents = req.body.idComponents;

        this.buildModel.addBuild(build, idComponents)
            .then(() => res.status(HttpCodes.OK).end())
            .catch(() => res.status(HttpCodes.INTERNAL_SERVER_ERROR).end());
    }

    public updateBuild = (req: any, res: Response) => {
        const idBuild = req.params.idBuild;
        const idUser = req.params.idUser;
        const build = new Build();
        build.name = req.body.name;
        build.description = req.body.desc;
        build.isPublic = req.body.isPublic;;
        const idComponents = req.body.idComponents;

        this.buildModel.updateBuild(idUser, idBuild, build, idComponents)
            .then(() => res.status(HttpCodes.OK).end())
            .catch(() => res.status(HttpCodes.INTERNAL_SERVER_ERROR).end());
    }

    public removeBuild = (req: any, res: Response) => {
        const idBuild = req.params.idBuild;

        this.buildModel.deleteBuild(idBuild)
            .then(() => res.status(HttpCodes.OK).end())
            .catch(() => res.status(HttpCodes.INTERNAL_SERVER_ERROR).end());
    }
}