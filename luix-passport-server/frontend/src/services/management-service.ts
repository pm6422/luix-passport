import http from "@/axios"
import type { AxiosResponse } from "axios"

export class ManagementService {
  constructor() {
  }

  public static getHealth(): Promise<AxiosResponse<unknown>> {
    return http.get("management/health", {});
  }
}
