import http from "@/axios"
import { LoginUser } from "@/domains/login-user"
import { AxiosResponse } from "axios"
import {UserLoginCount} from "@/domains/user-login-count.ts";

export class UserAuthEventService {
  constructor() {
  }

  public static findRecentLoginUsers(): Promise<AxiosResponse<Array<LoginUser>>> {
    return http.get("/api/user-auth-events/recent-login-users", { params: { page: 0, size: 5, sort: 'createdAt,desc' } })
  }

  public static getUserLoginCount(): Promise<AxiosResponse<Array<UserLoginCount>>> {
    return http.get("/api/user-auth-events/user-login-count", {})
  }
}