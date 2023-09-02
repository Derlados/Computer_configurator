import { ValidationPipe } from '@nestjs/common';
import { NestFactory } from '@nestjs/core';
import { DocumentBuilder, SwaggerModule } from '@nestjs/swagger';
import { AppModule } from './app.module';

const PORT = process.env.PORT || 5000;

async function start() {
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
        .setTitle("Flutter PC build - Component picker - documentation")
        .setDescription("Documentation for the REST API of the Flutter application 'PC Builder - Component picker'")
        .setVersion('1.0.0')
        .addTag('TEN_HERALDS')
        .build()
    const doc = SwaggerModule.createDocument(app, config);
    SwaggerModule.setup('/api/docs', app, doc);

    await app.listen(PORT, () => console.log(`Server started on port = ${PORT}`));
}
start();