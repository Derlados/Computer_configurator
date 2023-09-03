import { CanActivate, ExecutionContext, Injectable } from '@nestjs/common';
import * as admin from 'firebase-admin';

@Injectable()
export class FirebaseGuard implements CanActivate {
    async canActivate(context: ExecutionContext): Promise<boolean> {
        const request = context.switchToHttp().getRequest();
        const authToken = request.headers['authorization'];
        if (!authToken) {
            return false;
        }

        try {
            const decodedToken = await admin.auth().verifyIdToken(authToken);

            if (decodedToken) {
                request.user = decodedToken;
                return true;
            } else {
                return false;
            }
        } catch (error) {
            console.error('Firebase token verification failed:', error);
            return false;
        }
    }
}