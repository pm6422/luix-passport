import http from "@/axios"
import { AxiosResponse } from "axios"

export class OrgService {
  constructor() {
  }

  public static count(): Promise<AxiosResponse<number>> {
    return http.get("api/orgs/count")
  }
}