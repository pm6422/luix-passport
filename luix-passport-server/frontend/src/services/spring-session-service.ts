import http from "@/axios"
import { type SpringSession } from "@/domains/spring-session"
import { AxiosResponse } from "axios"

export class SpringSessionService {
  constructor() {
  }

  public static findAll(): Promise<AxiosResponse<Array<SpringSession>>> {
    return http.get("/api/spring-sessions")
  }
}