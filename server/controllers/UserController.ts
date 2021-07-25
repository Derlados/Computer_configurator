import { Response } from "express";
import { HttpCodes } from "../constants/HttpCodes";
import * as crypto from "crypto";
import User from "../models/data-classes/User";
import UserModel, { UserError } from "../models/UserModel";


export default class UserController {
    private userModel: UserModel;

    constructor() {
        this.userModel = new UserModel();
    }

    public register = (req: any, res: Response) => {
        const nickname = req.body.nickname;
        const email = req.body.email;
        const password = req.body.password;
        const secret = req.body.secret;
        const google = req.body.google;
        const user = new User(nickname, password, email, secret, google);

        this.userModel.register(user)
            .then((id: number) => {
                user.id = id;
                const token = this.createKey(user);

                res.status(HttpCodes.OK).send(JSON.stringify(token))
            })
            .catch((err: UserError | any) => {
                if (Object.values(UserError).includes(err)) {
                    if (err == UserError.USER_EXIST) {
                        res.status(HttpCodes.CONFLICT).send(JSON.stringify("User already exists"))
                    }
                } else {
                    res.status(HttpCodes.INTERNAL_SERVER_ERROR).send(JSON.stringify(err))
                }
            })
    }

    public login = (req: any, res: Response) => {
        const nickname = req.body.nickname;
        const email = req.body.email;
        const password = req.body.password;
        const secret = req.body.secret;
        const google = req.body.google;
        const user = new User(nickname, password, email, secret, google);

        this.userModel.login(user)
            .then((id: number) => {
                user.id = id;
                const token = this.createKey(user);
                res.status(HttpCodes.OK).send(JSON.stringify(token))
            })
            .catch((err: UserError | any) => {
                console.error(err);
                if (Object.values(UserError).includes(err)) {
                    if (err == UserError.USER_NOT_FOUND) {
                        res.status(HttpCodes.NOT_FOUND).send(JSON.stringify(err))
                    } else if (err == UserError.DATABASE_ERROR) {
                        res.status(HttpCodes.BAD_REQUEST).send(JSON.stringify(err))
                    }
                } else {
                    res.status(HttpCodes.INTERNAL_SERVER_ERROR).send(JSON.stringify(err))
                }

            })
    }

    public checkAuth = (req: any, res: Response, next) => {
        const token: string = req.headers.auth;
        const tokenParts: string[] = token.split('.');
        const signature: string = crypto.createHmac('SHA256', process.env.AUTH_TOKEN_KEY).update(`${tokenParts[0]}.${tokenParts[1]}`).digest('base64');
        if (tokenParts[2] == signature) {
            next();
        } else {
            res.status(HttpCodes.UNAUTHORIZED).send('not authorized');
        }
    }

    private createKey(user: User): String {
        let header: string;
        let body: string = "";
        let signature: string = "";
        let token: string = "";

        header = JSON.stringify({
            typ: "JWT",
            alg: "HS256"
        });
        header = Buffer.from(header).toString('base64');

        body = JSON.stringify({
            iss: "10_HERALDS",
            sub: 'authorization',
            id: user.id,
            time: new Date().toString()
        });
        body = Buffer.from(body).toString('base64');

        signature = crypto.createHmac('SHA256', process.env.AUTH_TOKEN_KEY).update(`${header}.${body}`).digest('base64');
        token = `${header}.${body}.${signature}`;

        return token;

    }
}