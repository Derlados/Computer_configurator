import axios, { AxiosError } from "axios";
import { axiosInstance, headers } from "..";
import { IProduct } from "../../types/IProduct";
import { ComponentDto } from "./dto/component.dto";
import { CreateComponentDto } from "./dto/create-component.dto";

class ComponentService {
    static readonly API_URL = '/components'

    async getComponents(categoryId: number): Promise<IProduct[]> {
        const { data } = await axiosInstance.get<IProduct[]>(`${ComponentService.API_URL}/category/${categoryId}`);
        return data;
    }

    async createComponent(dto: CreateComponentDto) {
        const { data } = await axiosInstance.post<IProduct>(`${ComponentService.API_URL}`, dto, { headers: headers() });
        return data;
    }

    async updateComponent(id: number, dto: CreateComponentDto) {
        const { data } = await axiosInstance.put<IProduct>(`${ComponentService.API_URL}/${id}`, dto, { headers: headers() })
        return data;

    }
}

export default new ComponentService();