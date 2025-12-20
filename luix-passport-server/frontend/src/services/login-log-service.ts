import http from "@/axios"
import { Page } from "@/domains/page"
import { AxiosResponse } from "axios"
import { type LoginLog } from "@/domains/login-log"

export class LoginLogService {
  constructor() {
  }

  public static findRecentLogins(reqParams: object): Promise<AxiosResponse<Page<LoginLog>>> {
    return http.get("/api/login-logs/recent", { params: reqParams })
  }

  public static getActivityStats(days?: number): Promise<AxiosResponse<Array<ActivityData>>> {
    return http.get("/api/login-logs/activity", {
      params: { days }
    })
  }
}

export interface ActivityData {
  name: string
  users: number
  logins: number
}
