import http from "@/axios"
import { type User } from "@/domains/user"
import { type SupportedTimezone } from "@/domains/supported-timezone"
import { AxiosResponse } from "axios"

export class UserService {
  constructor() {
  }

  public static find(reqParams: object): Promise<AxiosResponse<Array<User>>> {
    return http.get("api/users", { params: reqParams })
  }

  public static findAll(enabled: boolean | null = null): Promise<AxiosResponse<Array<User>>> {
    return http.get("api/users", { params: { page: 0, size: 2000, enabled: enabled }})
  }

  public static findById(id: string): Promise<AxiosResponse<User>> {
    return http.get("api/users/" + id)
  }

  public static save(model: User): Promise<void> {
    return model.id ? http.put("api/users", model) : http.post("api/users", model)
  }

  public static deleteById(id: string): Promise<void> {
    return http.delete("api/users/" + id)
  }

  public static resetPassword(id: string): Promise<void> {
    return http.put("api/users/reset-password/" + id)
  }

  public static count(): Promise<AxiosResponse<number>> {
    return http.get("api/users/count")
  }

  public static findSupportedTimezones(): Promise<AxiosResponse<Array<SupportedTimezone>>> {
    return http.get("api/users/supported-time-zones")
  }
}