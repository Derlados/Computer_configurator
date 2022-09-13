import { ValidationPipe } from '@nestjs/common';
import { NestFactory } from '@nestjs/core';
import { DocumentBuilder, SwaggerModule } from '@nestjs/swagger';
import { AppModule } from './app.module';

async function start() {
    const PORT = process.env.PORT || 5000;
    const app = await NestFactory.create(AppModule)
    app.enableCors();
    app.setGlobalPrefix('api')
    app.useGlobalPipes(
        new ValidationPipe({
            whitelist: true,
            transform: true
        }),
    );

    const config = new DocumentBuilder()
        .setTitle("Android конфигуратор ПК - документация")
        .setDescription("Документация к REST API Android приложения 'Конфигуратор ПК'")
        .setVersion('1.0.0')
        .addTag('BSL')
        .build()
    const doc = SwaggerModule.createDocument(app, config);
    SwaggerModule.setup('/api/docs', app, doc);

    await app.listen(PORT, () => console.log(`Server started on port = ${PORT}`));
}
start();