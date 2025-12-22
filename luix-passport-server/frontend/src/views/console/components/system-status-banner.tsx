import { Button } from "@/components/ui/button"
import { useEffect, useState } from "react"
import { CreateTeamDialog } from "@/views/team/dialog/create-team-dialog"
import { UserDateTime } from "@/components/custom/display/user-date-time"
import { ManagementService, type HealthResponse } from "@/services/management-service"

export function SystemStatusBanner() {
  const [apiStatus, setApiStatus] = useState<HealthResponse['status']>('UP')
  const [lastSync, setLastSync] = useState<string>(new Date().toISOString())
  const [createTeamOpen, setCreateTeamOpen] = useState(false)

  useEffect(() => {
    const checkHealth = async () => {
      try {
        const { data } = await ManagementService.getHealth()
        setApiStatus(data.status)
      } catch (error) {
        console.error(error)
        setApiStatus('DOWN')
      } finally {
        setLastSync(new Date().toISOString())
      }
    }
    checkHealth()
    const interval = setInterval(checkHealth, 60000)
    return () => clearInterval(interval)
  }, [])
  return (
    <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 bg-card p-6 rounded-2xl border border-border/50 shadow-sm">
      <div>
        <h1 className="text-3xl font-extrabold tracking-tight">Management Console</h1>
        <div className="flex items-center gap-2 mt-1 text-muted-foreground">
          <span className="relative flex h-3 w-3">
            <span className={`animate-ping absolute inline-flex h-full w-full rounded-full opacity-75 ${apiStatus === 'UP' ? 'bg-emerald-400' :
              apiStatus === 'UNKNOWN' || apiStatus === 'OUT_OF_SERVICE' ? 'bg-amber-400' :
                'bg-rose-400'
              }`}></span>
            <span className={`relative inline-flex rounded-full h-3 w-3 ${apiStatus === 'UP' ? 'bg-emerald-500' :
              apiStatus === 'UNKNOWN' || apiStatus === 'OUT_OF_SERVICE' ? 'bg-amber-500' :
                'bg-rose-500'
              }`}></span>
          </span>
          <span className="text-sm font-medium">
            System Status: <span className={
              apiStatus === 'UP' ? 'text-emerald-600 dark:text-emerald-400' :
                apiStatus === 'UNKNOWN' || apiStatus === 'OUT_OF_SERVICE' ? 'text-amber-600 dark:text-amber-400' :
                  'text-rose-600 dark:text-rose-400'
            }>
              {apiStatus === 'UP' ? 'Healthy' : apiStatus === 'DOWN' ? 'Offline' : 'Issues Detected'}
            </span>
          </span>
          <span className="mx-2 text-border">|</span>
          <span className="text-sm">Updated at: <UserDateTime value={lastSync} type="time" /></span>
        </div>
      </div>
      <Button onClick={() => setCreateTeamOpen(true)}>Create Team</Button>
      <CreateTeamDialog open={createTeamOpen} onOpenChange={setCreateTeamOpen} />
    </div>
  )
}
