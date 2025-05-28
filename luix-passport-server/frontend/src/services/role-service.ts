import http from "@/axios"
import { type Role } from "@/domains/role"
import {AxiosResponse} from "axios"

export class RoleService {
  constructor() {
  }

  public static find(reqParams: object): Promise<AxiosResponse<Array<Role>>> {
    return http.get("api/roles", { params: reqParams })
  }

  public static findAllIds(): Promise<AxiosResponse<Array<string>>> {
    return http.get("api/roles/ids")
  }

  public static findById(id: string): Promise<AxiosResponse<Role>> {
    return http.get("api/roles/" + id)
  }

  public static save(model: Role): Promise<void> {
    return model.id ? http.put("api/roles", model) : http.post("api/roles", model)
  }

  public static deleteById(id: string): Promise<void> {
    return http.delete("api/roles/" + id)
  }

  public static upload(formData: FormData): Promise<void> {
    return http.post("api/roles/import", formData)
  }
}