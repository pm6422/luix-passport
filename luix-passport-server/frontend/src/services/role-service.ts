import http from "@/axios"
import { type Role } from "@/domains/role"
import {AxiosResponse} from "axios"

export class RoleService {
  constructor() {
  }

  public static findAllIds(): Promise<AxiosResponse<Array<string>>> {
    return http.get("api/roles/ids")
  }

  public static findById(id: string): Promise<AxiosResponse<Role>> {
    return http.get("api/roles/" + id)
  }

  public static save(model: Role): Promise<void> {
    return http.post("api/roles", model)
  }

  public static deleteById(id: string): Promise<void> {
    return http.delete("api/roles/" + id)
  }

  public static upload(formData: FormData): Promise<void> {
    return http.post("api/roles/import", formData)
  }
}