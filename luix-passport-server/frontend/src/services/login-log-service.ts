import http from "@/axios"
import { Page } from "@/domains/page"
import { AxiosResponse } from "axios"
import { type LoginLog } from "@/domains/login-log"

export class LoginLogService {
  constructor() {
  }

  public static findRecentLogins(page: number, size: number, username?: string): Promise<AxiosResponse<Page<LoginLog>>> {
    return http.get("/api/recent-logins", {
      params: { page, size, username }
    })
  }
}
