import { Response } from "express";
import { HttpCodes } from "../constants/HttpCodes";
import * as crypto from "crypto";
import User from "../models/data-classes/User";
import UserModel, { UserError } from "../models/UserModel";


export default class UserController {
    private thisModel = this;
    private userModel: UserModel;

    constructor() {
        this.userModel = new UserModel();
    }

    public register = (req: any, res: Response) => {
        const username = req.body.username;
        const email = req.body.email;
        const password = req.body.password;
        const secret = req.body.secret;
        const googleId = req.body.googleId;
        const photoUrl = req.body.photoUrl;
        const user = new User(username, password, email, secret, googleId, photoUrl);

        this.userModel.register(user)
            .then((user: User) => {
                const sendData = this.prepareSendData(user);
                res.status(HttpCodes.OK).send(JSON.stringify(sendData))
            })
            .catch((err: UserError | any) => {
                this.sendError(err, res);
            })
    }

    public login = (req: any, res: Response) => {
        const username = req.body.username;
        const password = req.body.password;
        const secret = req.body.secret;
        const googleId = req.body.googleId;
        const user = new User(username, password, null, secret, googleId, null);

        this.userModel.login(user)
            .then((user: User) => {
                const sendData = this.prepareSendData(user);
                res.status(HttpCodes.OK).send(JSON.stringify(sendData))
            })
            .catch((err: UserError | any) => {
                this.sendError(err, res);
            })
    }

    /**
     * Вход через гугл сервис, если записи не будет в базе данных - будет создана новая
     */
    public googleSignIn = (req: any, res: Response) => {
        const googleId = req.body.googleId;
        const username = req.body.username;
        const photoUrl = req.body.photoUrl;
        const user = new User(username, null, null, null, googleId, photoUrl);


        this.userModel.login(user)
            .then((user: User) => {
                const sendData = this.prepareSendData(user);
                res.status(HttpCodes.OK).send(JSON.stringify(sendData))
            })
            .catch((err: UserError | any) => {
                if (Object.values(UserError).includes(err) && err == UserError.USER_NOT_FOUND) {
                    this.userModel.register(user)
                        .then((user: User) => {
                            const sendData = this.prepareSendData(user);
                            res.status(HttpCodes.OK).send(JSON.stringify(sendData))
                        })
                        .catch((err: UserError | any) => {
                            this.sendError(err, res);
                        })
                } else {
                    this.sendError(err, res);
                }
            })
    }

    public update = (req: any, res: Response) => {

    }

    public removeAccout = (req: any, res: Response) => {

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

    private prepareSendData(user: User): Object {
        const sendData: Object = {
            id: user.id,
            username: user.username,
            photoUrl: user.photoUrl,
            token: this.createKey(user)
        }
        return sendData;
    }

    private sendError(err: UserError | any, res: Response) {
        if (Object.values(UserError).includes(err)) {
            switch (err) {
                case UserError.USER_NOT_FOUND:
                    res.status(HttpCodes.NOT_FOUND).end()
                    break;
                case UserError.DATABASE_ERROR:
                    res.status(HttpCodes.BAD_REQUEST).end()
                    break;
                case UserError.USER_EXIST_USERNAME:
                    res.statusMessage = "username";
                    res.status(HttpCodes.CONFLICT).end();
                    break;
                case UserError.USER_EXIST_EMAIL:
                    res.statusMessage = "email";
                    res.status(HttpCodes.CONFLICT).end();
                    break;
            }


        } else {
            res.status(HttpCodes.INTERNAL_SERVER_ERROR).end()
        }

    }
}