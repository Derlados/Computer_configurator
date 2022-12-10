import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { ComponentsController } from './components.controller';
import { ComponentsService } from './components.service';
import { Attribute } from './models/attribute.model';
import { ComponentAttribute } from './models/component-attribute.model';
import { Component } from './models/component.model';
import { Value } from './models/value.model';

@Module({
  imports: [TypeOrmModule.forFeature([Component, Attribute, Value, ComponentAttribute])],
  controllers: [ComponentsController],
  providers: [ComponentsService]
})
export class ComponentsModule { }
