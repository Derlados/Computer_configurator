"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
Object.defineProperty(exports, "__esModule", { value: true });
const __1 = require("..");
class ComponentService {
    getComponents(categoryId) {
        return __awaiter(this, void 0, void 0, function* () {
            const { data } = yield __1.axiosInstance.get(`${ComponentService.API_URL}/category/${categoryId}`);
            return data;
        });
    }
    createComponent(dto) {
        return __awaiter(this, void 0, void 0, function* () {
            const { data } = yield __1.axiosInstance.post(`${ComponentService.API_URL}`, dto, { headers: (0, __1.headers)() });
            return data;
        });
    }
    updateComponent(id, dto) {
        return __awaiter(this, void 0, void 0, function* () {
            const { data } = yield __1.axiosInstance.put(`${ComponentService.API_URL}/${id}`, dto, { headers: (0, __1.headers)() });
            return data;
        });
    }
}
ComponentService.API_URL = '/components';
exports.default = new ComponentService();
