import http from "@/axios"
import { type SupportedDateTimeFormat } from "@/domains/supported-date-time-format"
import { AxiosResponse } from "axios"

export class SupportedDateTimeFormatService {
  constructor() {
  }

  public static findById(id: string): Promise<AxiosResponse<SupportedDateTimeFormat>> {
    return http.get("api/supported-date-time-formats/" + id)
  }
}