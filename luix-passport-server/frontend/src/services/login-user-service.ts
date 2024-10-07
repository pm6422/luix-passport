import http from "@/axios"
import { LoginUser } from "@/domains/login-user"
import { AxiosResponse } from "axios"

export class LoginUserService {
  constructor() {
  }

  public static findRecentLoginUsers(): Promise<AxiosResponse<Array<LoginUser>>> {
    return http.get("api/user-auth-events/recent-login-users", { params: { page: 0, size: 5, sort: 'createdAt,desc' } })
  }
}