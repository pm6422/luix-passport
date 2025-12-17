import http from "@/axios"
import { LoginUser } from "@/domains/login-user"
import { AxiosResponse } from "axios"
import { UserLoginCount } from "@/domains/user-login-count"
import { Page } from "@/domains/page"
import { UserAuthEvent } from "@/domains/user-auth-event"

export class UserAuthEventService {
  constructor() {
  }

  public static find(reqParams: object): Promise<AxiosResponse<Page<UserAuthEvent>>> {
    return http.get("/api/user-auth-events", { params: reqParams })
  }

  public static findRecentLoginUsers(): Promise<AxiosResponse<Page<LoginUser>>> {
    return http.get("/api/user-auth-events/recent-login-users", { params: { page: 0, size: 5, sort: 'createdAt,desc' } })
  }

  public static getUserLoginCount(): Promise<AxiosResponse<Array<UserLoginCount>>> {
    return http.get("/api/user-auth-events/user-login-count", {})
  }
}