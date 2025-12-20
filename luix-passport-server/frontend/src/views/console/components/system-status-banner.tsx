import { Button } from "@/components/ui/button"
import { formatTime } from "@/lib/time-utils"
import { useLoginUserStore } from "@/stores/login-user-store"
import { useNavigate } from "react-router-dom"
import { useState } from "react"

export function SystemStatusBanner() {
  const navigate = useNavigate()
  const { loginUser } = useLoginUserStore()
  const [apiStatus] = useState<'operational' | 'degraded' | 'offline'>('operational')
  const [lastSync] = useState<string>(new Date().toISOString())
  return (
    <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 bg-card p-6 rounded-2xl border border-border/50 shadow-sm">
      <div>
        <h1 className="text-3xl font-extrabold tracking-tight">Management Console</h1>
        <div className="flex items-center gap-2 mt-1 text-muted-foreground">
          <span className="relative flex h-3 w-3">
            <span className={`animate-ping absolute inline-flex h-full w-full rounded-full opacity-75 ${apiStatus === 'operational' ? 'bg-emerald-400' :
              apiStatus === 'degraded' ? 'bg-amber-400' :
                'bg-rose-400'
              }`}></span>
            <span className={`relative inline-flex rounded-full h-3 w-3 ${apiStatus === 'operational' ? 'bg-emerald-500' :
              apiStatus === 'degraded' ? 'bg-amber-500' :
                'bg-rose-500'
              }`}></span>
          </span>
          <span className="text-sm font-medium">
            System Status: <span className={
              apiStatus === 'operational' ? 'text-emerald-600 dark:text-emerald-400' :
                apiStatus === 'degraded' ? 'text-amber-600 dark:text-amber-400' :
                  'text-rose-600 dark:text-rose-400'
            }>
              {apiStatus === 'operational' ? 'Healthy' : apiStatus === 'degraded' ? 'Issues Detected' : 'Offline'}
            </span>
          </span>
          <span className="mx-2 text-border">|</span>
          <span className="text-sm">Updated at: {formatTime(loginUser.timeFormat, loginUser.timeZoneId, lastSync)}</span>
        </div>
      </div>
      <Button onClick={() => navigate("/teams")}>Create Team</Button>
    </div>
  )
}
