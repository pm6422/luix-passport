import http from "@/axios"
import { type Permission } from "@/domains/permission"
import {AxiosResponse} from "axios"

export class PermissionService {
  constructor() {
  }

  public static find(reqParams: object): Promise<AxiosResponse<Array<Permission>>> {
    return http.get("api/permissions", { params: reqParams })
  }

  public static findById(id: string): Promise<AxiosResponse<Permission>> {
    return http.get("api/permissions/" + id)
  }

  public static save(model: Permission): Promise<void> {
    return http.post("api/permissions", model)
  }

  public static deleteById(id: string): Promise<void> {
    return http.delete("api/permissions/" + id)
  }

  public static upload(formData: FormData): Promise<void> {
    return http.post("api/permissions/import", formData)
  }
}