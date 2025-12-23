import http from "@/axios"
import { type DateTimeFormat } from "@/domains/date-time-format"
import { AxiosResponse } from "axios"

export class DateTimeFormatService {
  constructor() {
  }

  public static findById(id: string): Promise<AxiosResponse<DateTimeFormat>> {
    return http.get("/api/date-time-formats/" + id)
  }
}