import http from "@/axios"
import { type ScheduleExecutionLog } from "@/domains/schedule-execution-log.ts"
import {AxiosResponse} from "axios"

export class ScheduleExecutionLogService {
  constructor() {
  }

  public static find(reqParams: object): Promise<AxiosResponse<Array<ScheduleExecutionLog>>> {
    return http.get("/api/schedule-execution-logs", { params: reqParams })
  }

  public static findById(id: string): Promise<AxiosResponse<ScheduleExecutionLog>> {
    return http.get("/api/schedule-execution-logs/" + id)
  }

  public static deleteById(id: string): Promise<void> {
    return http.delete("/api/schedule-execution-logs/" + id)
  }
}