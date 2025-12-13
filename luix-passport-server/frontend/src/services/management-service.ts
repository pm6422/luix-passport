import http from "@/axios"
import type { AxiosResponse } from "axios"

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

  public static getHealth(): Promise<AxiosResponse<{ components: Record<string, HealthComponent> }>> {
    return http.get("management/health", {})
  }
}
