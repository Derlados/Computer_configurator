export default class User {
    public id: number;
    public nickname: string;
    public password?: string;
    public email?: string;
    public secret?: string;
    public google?: string;

    constructor(nickname: string, password: string, email: string, secret: string, google: string) {
        this.nickname = nickname;
        this.password = password;
        this.email = email;
        this.secret = secret;
        this.google = google;
    }
}