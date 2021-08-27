export default class User {
    public id: number;
    public username: string;
    public password?: string;
    public email?: string;
    public secret?: string;
    public googleId?: string;
    public imgUrl?: string;

    constructor(nickname: string, password: string, email: string, secret: string, google: string, photoUrl: string) {
        this.username = nickname;
        this.password = password;
        this.email = email;
        this.secret = secret;
        this.googleId = google;
        this.imgUrl = photoUrl;
    }
}