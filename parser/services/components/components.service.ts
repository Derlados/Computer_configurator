import axios from "axios";
import { IProduct } from "../../types/IProduct";
import { ComponentDto } from "./dto/component.dto";
import { CreateComponentDto } from "./dto/create-component.dto";

class ComponentService {
    static readonly API_URL = 'http://localhost:5000/api/components'

    async getComponents(category: string): Promise<IProduct[]> {
        const { data } = await axios.get<IProduct[]>(`${ComponentService.API_URL}/category=${category}`)
        return data;
    }

    async createComponent(dto: CreateComponentDto) {
        console.log(dto);
    }

    async updateComponent(id: number, dto: ComponentDto) {
        console.log(dto);
    }
}

export default new ComponentService();