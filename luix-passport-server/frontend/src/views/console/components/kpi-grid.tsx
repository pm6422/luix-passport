import { KpiCard } from "./kpi-card"
import { Users, AppWindow, CircleDot } from "lucide-react"
import { Oauth2ClientService } from "@/services/oauth2-client-service"
import { UserService } from "@/services/user-service"
import { SpringSessionService } from "@/services/spring-session-service"
import { TeamService } from "@/services/team-service"
import { useEffect, useState } from "react"

export function KpiGrid() {
  const [loading, setLoading] = useState(true)
  const [userCount, setUserCount] = useState(0)
  const [stats, setStats] = useState({
    apps: 0,
    sessions: 0,
    teams: 0,
    userTrend: { trend: "+0%", isUp: true },
    appTrend: { trend: "+0%", isUp: true },
    sessionTrend: { trend: "+0%", isUp: true },
    teamTrend: { trend: "+0%", isUp: true }
  })

  useEffect(() => {
    const fetchKpiData = async () => {
      try {
        const yesterday = new Date()
        yesterday.setDate(yesterday.getDate() - 1)
        const since = yesterday.toISOString()
        const sinceMs = yesterday.getTime()

        const [appsCountRes, appsPrevCountRes, usersRes, usersPrevRes, sessionCountRes, sessionPrevRes, teamCountRes, teamPrevCountRes] = await Promise.all([
          Oauth2ClientService.count(),
          Oauth2ClientService.count(since),
          UserService.count(),
          UserService.count(since),
          SpringSessionService.count(),
          SpringSessionService.count(sinceMs),
          TeamService.count(),
          TeamService.count(since)
        ])

        const currentUsers = usersRes.data
        const prevUsers = usersPrevRes.data
        const currentApps = appsCountRes.data
        const prevApps = appsPrevCountRes.data
        const currentSessions = sessionCountRes.data
        const prevSessions = sessionPrevRes.data
        const currentTeams = teamCountRes.data
        const prevTeams = teamPrevCountRes.data

        setUserCount(currentUsers)

        const calculateTrend = (current: number, change: number) => {
          if (current === 0) return { trend: "+0%", isUp: true }
          const percentage = ((change / (current - change)) * 100).toFixed(1)
          return {
            trend: `${change >= 0 ? '+' : ''}${percentage}%`,
            isUp: change >= 0
          }
        }

        setStats({
          apps: currentApps,
          sessions: currentSessions,
          teams: currentTeams,
          userTrend: calculateTrend(currentUsers, currentUsers - prevUsers),
          appTrend: calculateTrend(currentApps, currentApps - prevApps),
          sessionTrend: calculateTrend(currentSessions, currentSessions - prevSessions),
          teamTrend: calculateTrend(currentTeams, currentTeams - prevTeams)
        })
      } catch (error) {
        console.error("Failed to load KPI data:", error)
      } finally {
        setLoading(false)
      }
    }
    fetchKpiData()
  }, [])
  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
      <KpiCard
        title="Total Users"
        value={loading ? "..." : userCount.toLocaleString()}
        trend={stats.userTrend?.trend || "+0%"}
        isUp={stats.userTrend?.isUp ?? true}
        icon={Users}
        description="Total registered users"
        bgClass="bg-card border-border/50"
        iconClass="bg-emerald-500/25 dark:bg-emerald-500/20 text-emerald-700 dark:text-emerald-400"
      />
      <KpiCard
        title="OAuth2 Clients"
        value={loading ? "..." : stats.apps}
        trend={stats.appTrend?.trend || "+0%"}
        isUp={stats.appTrend?.isUp ?? true}
        icon={AppWindow}
        description="Total registered applications"
        bgClass="bg-card border-border/50"
        iconClass="bg-amber-500/25 dark:bg-amber-500/20 text-amber-700 dark:text-amber-400"
      />
      <KpiCard
        title="Active Sessions"
        value={loading ? "..." : stats.sessions}
        trend={stats.sessionTrend?.trend || "+0%"}
        isUp={stats.sessionTrend?.isUp ?? true}
        icon={CircleDot}
        description="Currently active sessions"
        bgClass="bg-card border-border/50"
        iconClass="bg-blue-500/25 dark:bg-blue-500/20 text-blue-700 dark:text-blue-400"
      />
      <KpiCard
        title="Total Teams"
        value={loading ? "..." : stats.teams.toLocaleString()}
        trend={stats.teamTrend?.trend || "+0%"}
        isUp={stats.teamTrend?.isUp ?? true}
        icon={Users}
        description="Total organizations"
        bgClass="bg-card border-border/50"
        iconClass="bg-indigo-500/25 dark:bg-indigo-500/20 text-indigo-700 dark:text-indigo-400"
      />
    </div>
  )
}
