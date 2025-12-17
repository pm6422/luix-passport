import http from "@/axios"
import { type ScheduleLog } from "@/domains/schedule-log"
import { AxiosResponse } from "axios"
import { Page } from "@/domains/page"

export class ScheduleLogService {
  constructor() {
  }

  public static find(reqParams: object): Promise<AxiosResponse<Page<ScheduleLog>>> {
    return http.get("/api/schedule-logs", { params: reqParams })
  }

  public static findById(id: string): Promise<AxiosResponse<ScheduleLog>> {
    return http.get("/api/schedule-logs/" + id)
  }

  public static deleteById(id: string): Promise<void> {
    return http.delete("/api/schedule-logs/" + id)
  }
}
