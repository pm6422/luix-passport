import http from "@/axios"
import { type Schedule } from "@/domains/schedule"
import {AxiosResponse} from "axios"

export class ScheduleService {
  constructor() {
  }

  public static find(reqParams: object): Promise<AxiosResponse<Array<Schedule>>> {
    return http.get("/api/schedules", { params: reqParams })
  }

  public static findById(id: string): Promise<AxiosResponse<Schedule>> {
    return http.get("/api/schedules/" + id)
  }

  public static deleteById(id: string): Promise<void> {
    return http.delete("/api/schedules/" + id)
  }
}