import { BadRequestException, CallHandler, ExecutionContext, ForbiddenException, Injectable, NestInterceptor, NestMiddleware } from "@nestjs/common";
import { NextFunction, Request } from "express";
import { Observable } from "rxjs";

@Injectable()
export class ChekAccessInterceptor implements NestInterceptor {
    intercept(context: ExecutionContext, next: CallHandler): Observable<any> {
        const request = context.switchToHttp().getRequest();
        if (request.params.id && request.user.id && Number(request.params.id) !== Number(request.user.id)) {
            throw new ForbiddenException("Access denied")
        }

        return next.handle()
    }
}