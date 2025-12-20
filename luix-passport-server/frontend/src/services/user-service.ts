import http from "@/axios"
import { type User } from "@/domains/user"
import { AxiosResponse } from "axios"
import { Page } from "@/domains/page"

export class UserService {
  constructor() {
  }

  public static find(reqParams: object): Promise<AxiosResponse<Page<User>>> {
    return http.get("/api/users", { params: reqParams })
  }

  public static findAll(enabled: boolean | null = null): Promise<AxiosResponse<Page<User>>> {
    return http.get("/api/users", { params: { page: 0, size: 2000, enabled: enabled } })
  }

  public static findById(username: string): Promise<AxiosResponse<User>> {
    return http.get("/api/users/" + username)
  }

  public static save(model: User): Promise<void> {
    return model.username ? http.put("/api/users", model) : http.post("/api/users", model)
  }

  public static deleteById(username: string): Promise<void> {
    return http.delete("/api/users/" + username)
  }

  public static resetPassword(username: string): Promise<void> {
    return http.put("/api/users/reset-password/" + username)
  }

  public static count(since?: string): Promise<AxiosResponse<number>> {
    return http.get("/api/users/count", { params: { since } })
  }

  public static getMembershipStats(): Promise<AxiosResponse<Record<string, number>>> {
    return http.get("/api/users/membership-stats")
  }
}