import http from "@/axios"
import type { AxiosResponse } from "axios"
import axios from "axios"
import type { AppInfo } from "@/stores/app-info-store"
import appInfoData from "@/data/app-info.json"

export type HealthComponent = {
  status?: "UP" | "DOWN" | "UNKNOWN"
  components?: Record<string, HealthComponent>
  details?: Record<string, unknown>
  error?: string
  [key: string]: unknown
}

export class ManagementService {
  constructor() {
  }

  public static async getAppInfo(): Promise<AppInfo | null> {
    try {
      const res = await axios.get<AppInfo>("/management/info")
      return res.data
    } catch (_error) {
      return appInfoData as AppInfo
    }
  }

  public static getHealth(): Promise<AxiosResponse<{ components: Record<string, HealthComponent> }>> {
    return http.get("management/health", {})
  }
}
