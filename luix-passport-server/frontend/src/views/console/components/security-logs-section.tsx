import { Card, CardContent } from "@/components/ui/card"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Avatar, AvatarFallback } from "@/components/ui/avatar"
import { Badge } from "@/components/ui/badge"
import { Globe } from "lucide-react"
import { type LoginLog } from "@/domains/login-log"
import { type SecurityAuditLog } from "@/domains/security-audit-log"
import { LoginLogService } from "@/services/login-log-service"
import { SecurityAuditLogService } from "@/services/security-audit-log-service"
import { formatRelativeTime } from "@/lib/time-utils"
import { useEffect, useState } from "react"

export function SecurityLogsSection() {
  const [loading, setLoading] = useState(true)
  const [loginLogs, setLoginLogs] = useState<LoginLog[]>([])
  const [securityLogs, setSecurityLogs] = useState<SecurityAuditLog[]>([])

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [logsRes, securityLogsRes] = await Promise.all([
          LoginLogService.findRecentLogins({ page: 0, size: 5, sort: "loginAt,desc" }),
          SecurityAuditLogService.find({ page: 0, size: 5, sort: "createdAt,desc" })
        ])
        setLoginLogs(logsRes.data.content)
        setSecurityLogs(securityLogsRes.data.content)
      } catch (error) {
        console.error("Failed to load admin logs:", error)
      } finally {
        setLoading(false)
      }
    }
    fetchData()
  }, [])
  return (
    <div className="lg:col-span-2">
      <Tabs defaultValue="logins" className="w-full">
        <div className="flex items-center justify-between mb-4">
          <TabsList className="bg-muted/50 p-1">
            <TabsTrigger value="logins">Recent Logins</TabsTrigger>
            <TabsTrigger value="security">Security Events</TabsTrigger>
          </TabsList>
        </div>

        <TabsContent value="logins" className="mt-0 outline-none">
          <Card className="border border-border/50 shadow-sm bg-card overflow-hidden">
            <CardContent className="p-0">
              <Table>
                <TableHeader>
                  <TableRow className="hover:bg-transparent border-muted/50">
                    <TableHead className="pl-6 h-12 uppercase text-[10px] font-bold tracking-wider text-muted-foreground/70">User Identity</TableHead>
                    <TableHead className="h-12 uppercase text-[10px] font-bold tracking-wider text-muted-foreground/70">Location</TableHead>
                    <TableHead className="h-12 uppercase text-[10px] font-bold tracking-wider text-muted-foreground/70">Device / IP</TableHead>
                    <TableHead className="h-12 uppercase text-[10px] font-bold tracking-wider text-muted-foreground/70">Time</TableHead>
                    <TableHead className="pr-6 h-12 text-right uppercase text-[10px] font-bold tracking-wider text-muted-foreground/70">Status</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {loginLogs.map((log: LoginLog) => (
                    <TableRow key={log.id} className="cursor-default hover:bg-muted/30 transition-colors border-muted/50">
                      <TableCell className="pl-6 py-4">
                        <div className="flex items-center gap-3">
                          <Avatar className="h-8 w-8 border border-primary/10">
                            <AvatarFallback className="text-xs bg-primary/5 text-primary font-bold">
                              {log.username[0].toUpperCase()}
                            </AvatarFallback>
                          </Avatar>
                          <div className="flex flex-col">
                            <span className="text-sm font-semibold text-foreground/90">{log.email || log.username}</span>
                            <span className="text-[10px] text-muted-foreground lg:hidden">{log.ip}</span>
                          </div>
                        </div>
                      </TableCell>
                      <TableCell className="py-4">
                        <div className="flex items-center gap-1.5 text-xs text-muted-foreground">
                          <Globe className="size-3 text-primary/40" />
                          {log.location}
                        </div>
                      </TableCell>
                      <TableCell className="py-4">
                        <div className="flex flex-col gap-0.5 max-w-[150px]">
                          <span className="text-xs font-medium truncate" title={log.device}>{log.device}</span>
                          <span className="text-[10px] text-muted-foreground font-mono">{log.ip}</span>
                        </div>
                      </TableCell>
                      <TableCell className="py-4 text-xs font-medium">{formatRelativeTime(log.loginAt)}</TableCell>
                      <TableCell className="pr-6 py-4 text-right">
                        <Badge variant={log.status === 'success' ? 'default' : 'destructive'} className="text-[10px] uppercase font-bold px-2 py-0.5 rounded-full shadow-none border-none">
                          {log.status}
                        </Badge>
                      </TableCell>
                    </TableRow>
                  ))}
                  {loginLogs.length === 0 && !loading && (
                    <TableRow>
                      <TableCell colSpan={5} className="py-10 text-center text-muted-foreground italic">
                        No recent login logs found.
                      </TableCell>
                    </TableRow>
                  )}
                </TableBody>
              </Table>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="security" className="mt-0 outline-none">
          <Card className="border border-border/50 shadow-sm bg-card overflow-hidden">
            <CardContent className="p-0">
              <Table>
                <TableHeader>
                  <TableRow className="hover:bg-transparent border-muted/50">
                    <TableHead className="pl-6 h-12 w-[100px] uppercase text-[10px] font-bold tracking-wider text-muted-foreground/70">Severity</TableHead>
                    <TableHead className="h-12 uppercase text-[10px] font-bold tracking-wider text-muted-foreground/70">Event</TableHead>
                    <TableHead className="h-12 uppercase text-[10px] font-bold tracking-wider text-muted-foreground/70">Principal</TableHead>
                    <TableHead className="h-12 uppercase text-[10px] font-bold tracking-wider text-muted-foreground/70">Context</TableHead>
                    <TableHead className="pr-6 h-12 text-right uppercase text-[10px] font-bold tracking-wider text-muted-foreground/70">Time</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {securityLogs.map((log: SecurityAuditLog) => (
                    <TableRow key={log.id} className="cursor-default hover:bg-muted/30 transition-colors border-muted/50">
                      <TableCell className="pl-6 py-4">
                        <Badge variant={log.severity === 'CRITICAL' ? 'destructive' : log.severity === 'WARN' ? 'secondary' : 'outline'}
                          className={`text-[10px] uppercase font-bold px-2 py-0.5 rounded-full shadow-none border-none
                                   ${log.severity === 'WARN' ? 'bg-yellow-500/10 text-yellow-600 dark:text-yellow-400' : ''}
                                   ${log.severity === 'INFO' ? 'bg-blue-500/10 text-blue-600 dark:text-blue-400' : ''}
                                 `}>
                          {log.severity}
                        </Badge>
                      </TableCell>
                      <TableCell className="py-4">
                        <div className="flex flex-col">
                          <span className="text-sm font-semibold text-foreground/90">{log.action}</span>
                          <span className="text-[10px] text-muted-foreground truncate max-w-[200px]" title={log.details}>{log.details}</span>
                        </div>
                      </TableCell>
                      <TableCell className="py-4">
                        <div className="flex items-center gap-2">
                          <Avatar className="h-6 w-6 border border-primary/10">
                            <AvatarFallback className="text-[10px] bg-primary/5 text-primary font-bold">
                              {log.principal[0].toUpperCase()}
                            </AvatarFallback>
                          </Avatar>
                          <span className="text-xs font-medium">{log.principal}</span>
                        </div>
                      </TableCell>
                      <TableCell className="py-4">
                        <div className="flex flex-col gap-0.5">
                          <span className="text-xs text-muted-foreground">{log.ip}</span>
                          <span className="text-[10px] text-muted-foreground italic">{log.location}</span>
                        </div>
                      </TableCell>
                      <TableCell className="pr-6 py-4 text-right text-xs text-muted-foreground font-medium">
                        {formatRelativeTime(log.createdAt)}
                      </TableCell>
                    </TableRow>
                  ))}
                  {securityLogs.length === 0 && !loading && (
                    <TableRow>
                      <TableCell colSpan={5} className="py-10 text-center text-muted-foreground italic">
                        No security events recorded.
                      </TableCell>
                    </TableRow>
                  )}
                </TableBody>
              </Table>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  )
}
