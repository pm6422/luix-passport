import http from "@/axios"
import { Page } from "@/domains/page"
import { SecurityAuditLog } from "@/domains/security-audit-log"
import { AxiosResponse } from "axios"

export class SecurityAuditLogService {
  public static find(page: number, size: number): Promise<AxiosResponse<Page<SecurityAuditLog>>> {
    return http.get("/api/security-audit-logs", {
      params: { page, size }
    })
  }
}
