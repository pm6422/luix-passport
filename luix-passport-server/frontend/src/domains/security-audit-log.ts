export interface SecurityAuditLog {
  id: string
  principal: string
  action: string
  severity: "INFO" | "WARN" | "CRITICAL"
  ip?: string
  location?: string
  userAgent?: string
  details?: string
  createdAt: string
}
