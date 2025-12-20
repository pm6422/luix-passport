import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import {
  Users,
  AppWindow,
  ArrowUpRight,
  ArrowDownRight,
  ShieldCheck,
  CreditCard,
  Globe,
  CircleDot,
  LogOut,
  Mail,
  Inbox
} from "lucide-react"
import { LayoutBody } from "@/layouts/layout-definitions"
import { useEffect, useState } from "react"
import { TeamService } from "@/services/team-service"
import { type TeamInvitation, type Team } from "@/domains/team"
import { InboxService } from "@/services/inbox-service"
import { type Inbox as InboxType } from "@/domains/inbox"
import { Oauth2ClientService } from "@/services/oauth2-client-service"
import { UserService } from "@/services/user-service"
import { SpringSessionService } from "@/services/spring-session-service"
import { toast } from "sonner"
import { Badge } from "@/components/ui/badge"
import { useLoginUserStore } from "@/stores/login-user-store"
import { useNavigate } from "react-router-dom"
import { LoginLogService, type ActivityData } from "@/services/login-log-service"
import { type LoginLog } from "@/domains/login-log"
import { ROLE_ADMIN } from "@/domains/role"
import { formatRelativeTime, formatTime } from "@/lib/time-utils"
import {
  AreaChart,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell,
  Tooltip as RechartsTooltip,
} from "recharts"
import {
  ChartContainer,
  ChartTooltip,
  ChartTooltipContent,
  type ChartConfig,
} from "@/components/ui/chart"
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Avatar, AvatarFallback } from "@/components/ui/avatar"
import { Progress } from "@/components/ui/progress"
import { motion, AnimatePresence } from "framer-motion"
import { CreateTeamDialog } from "@/views/team/dialog/create-team-dialog"
import { calculateTrend } from "@/lib/utils"

// Data is fetched dynamically in the component

// Membership Pie Chart Config
// Membership Pie Chart Config
// Data is fetched dynamically in the component

const chartConfig = {
  users: {
    label: "Active Users",
    color: "hsl(var(--primary))",
  },
  logins: {
    label: "Auth Requests",
    color: "hsl(var(--destructive))",
  },
} satisfies ChartConfig

export default function ConsolePage() {
  const navigate = useNavigate()
  const { loginUser, hasRole } = useLoginUserStore()
  const [invitations, setInvitations] = useState<TeamInvitation[]>([])
  const [teams, setTeams] = useState<Team[]>([])
  const [messages, setMessages] = useState<InboxType[]>([])
  const [loginLogs, setLoginLogs] = useState<LoginLog[]>([])
  const [userCount, setUserCount] = useState(0)
  const [stats, setStats] = useState({
    teams: 0,
    apps: 0,
    sessions: 0,
    unreadMessages: 0,
    userTrend: { trend: "+0%", isUp: true },
    appTrend: { trend: "+0%", isUp: true },
    sessionTrend: { trend: "+0%", isUp: true },
    teamTrend: { trend: "+0%", isUp: true }
  })
  const [loading, setLoading] = useState(true)
  const [apiStatus, setApiStatus] = useState<'operational' | 'degraded' | 'offline'>('operational')
  const [lastSync, setLastSync] = useState<string>(new Date().toISOString())
  const [createTeamOpen, setCreateTeamOpen] = useState(false)
  const [membershipData, setMembershipData] = useState<any[]>([])
  const [activityData, setActivityData] = useState<ActivityData[]>([])
  const [activityDays, setActivityDays] = useState("7d")

  useEffect(() => {
    fetchDashboardData()
  }, [])

  useEffect(() => {
    if (hasRole(ROLE_ADMIN)) {
      const days = parseInt(activityDays.replace("d", ""))
      LoginLogService.getActivityStats(days).then(({ data }) => setActivityData(data))
    }
  }, [activityDays, hasRole])

  const fetchDashboardData = async () => {
    setLoading(true)
    try {
      const yesterday = new Date()
      yesterday.setDate(yesterday.getDate() - 1)
      const since = yesterday.toISOString()
      const sinceMs = yesterday.getTime()

      const [invitationsRes, teamsRes, unreadRes, appsCountRes, appsPrevCountRes, usersRes, usersPrevRes, sessionCountRes, sessionPrevRes, teamCountRes, teamPrevCountRes, membershipRes, inboxRes, logsRes] = await Promise.all([
        TeamService.getUserInvitations(),
        TeamService.findUserTeams(),
        InboxService.countUnread(),
        hasRole(ROLE_ADMIN) ? Oauth2ClientService.count() : Promise.resolve({ data: 0 }),
        hasRole(ROLE_ADMIN) ? Oauth2ClientService.count(since) : Promise.resolve({ data: 0 }),
        hasRole(ROLE_ADMIN) ? UserService.count() : Promise.resolve({ data: 0 }),
        hasRole(ROLE_ADMIN) ? UserService.count(since) : Promise.resolve({ data: 0 }),
        hasRole(ROLE_ADMIN) ? SpringSessionService.count() : Promise.resolve({ data: 0 }),
        hasRole(ROLE_ADMIN) ? SpringSessionService.count(sinceMs) : Promise.resolve({ data: 0 }),
        hasRole(ROLE_ADMIN) ? TeamService.count() : Promise.resolve({ data: 0 }),
        hasRole(ROLE_ADMIN) ? TeamService.count(since) : Promise.resolve({ data: 0 }),
        hasRole(ROLE_ADMIN) ? TeamService.getMembershipStats() : Promise.resolve({ data: { FREE: 0, PRO: 0, PREMIUM: 0 } }),
        InboxService.find({ page: 0, size: 4 }),
        hasRole(ROLE_ADMIN) ? LoginLogService.findRecentLogins(0, 5) : Promise.resolve({ data: { content: [] } })
      ])

      const currentUsers = usersRes.data
      const prevUsers = usersPrevRes.data
      const currentApps = appsCountRes.data
      const prevApps = appsPrevCountRes.data
      const currentSessions = sessionCountRes.data
      const prevSessions = sessionPrevRes.data
      const currentTeams = teamCountRes.data
      const prevTeams = teamPrevCountRes.data

      setInvitations(invitationsRes.data)
      setTeams(teamsRes.data)
      setUserCount(currentUsers)
      setMessages(inboxRes.data.content)
      setLoginLogs(logsRes.data.content)

      const mStats = membershipRes.data
      const formattedMembershipData = [
        { name: "Free Layer", value: mStats.FREE || 0, color: "#94a3b8" },
        { name: "Pro Plan", value: mStats.PRO || 0, color: "#3b82f6" },
        { name: "Premium Plan", value: mStats.PREMIUM || 0, color: "#8b5cf6" },
      ]
      setMembershipData(formattedMembershipData)
      setMembershipData(formattedMembershipData)

      const userTrendInfo = calculateTrend(currentUsers, currentUsers - prevUsers)
      const appTrendInfo = calculateTrend(currentApps, currentApps - prevApps)
      const sessionTrendInfo = calculateTrend(currentSessions, currentSessions - prevSessions)
      const teamTrendInfo = calculateTrend(currentTeams, currentTeams - prevTeams)

      setStats({
        teams: currentTeams,
        unreadMessages: unreadRes.data,
        apps: currentApps,
        sessions: currentSessions,
        userTrend: userTrendInfo,
        appTrend: appTrendInfo,
        sessionTrend: sessionTrendInfo,
        teamTrend: teamTrendInfo
      })
      setLastSync(new Date().toISOString())
      setApiStatus('operational')
    } catch (error) {
      console.error("Failed to load dashboard data:", error)
      setApiStatus('degraded')
      toast.error("Connected to local server but failed to fetch some metrics")
    } finally {
      setLoading(false)
    }
  }

  return (
    <LayoutBody className="flex flex-col space-y-6 pb-10" fixedHeight={false}>
      {/* Top Banner / System Health */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 bg-secondary/50 dark:bg-gradient-to-r dark:from-primary/10 dark:to-transparent p-6 rounded-2xl border border-border/50 dark:border-primary/10">
        <div>
          <h1 className="text-3xl font-extrabold tracking-tight">Management Console</h1>
          <div className="flex items-center gap-2 mt-1 text-muted-foreground">
            <span className="relative flex h-3 w-3">
              <span className={`animate-ping absolute inline-flex h-full w-full rounded-full opacity-75 ${apiStatus === 'operational' ? 'bg-emerald-400' : apiStatus === 'degraded' ? 'bg-amber-400' : 'bg-rose-400'
                }`}></span>
              <span className={`relative inline-flex rounded-full h-3 w-3 ${apiStatus === 'operational' ? 'bg-emerald-500' : apiStatus === 'degraded' ? 'bg-amber-500' : 'bg-rose-500'
                }`}></span>
            </span>
            <span className="text-sm font-medium">
              System Status: <span className={
                apiStatus === 'operational' ? 'text-emerald-600 dark:text-emerald-400' :
                  apiStatus === 'degraded' ? 'text-amber-600 dark:text-amber-400' : 'text-rose-600 dark:text-rose-400'
              }>
                {apiStatus === 'operational' ? 'Healthy' : apiStatus === 'degraded' ? 'Issues Detected' : 'Offline'}
              </span>
            </span>
            <span className="mx-2 text-border">|</span>
            <span className="text-sm">Updated at: {formatTime(loginUser.timeFormat, loginUser.timeZoneId, lastSync)}</span>
          </div>
        </div>
        <div className="flex items-center gap-2">
          <CreateTeamDialog open={createTeamOpen} onOpenChange={setCreateTeamOpen}>
            <Button size="sm" className="shadow-lg shadow-primary/20" onClick={() => setCreateTeamOpen(true)}>
              <Users className="mr-2 h-4 w-4" />
              Create Team
            </Button>
          </CreateTeamDialog>
        </div>
      </div>

      {/* KPI Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <KpiCard
          title="Total Users"
          value={loading ? "..." : (hasRole(ROLE_ADMIN) ? userCount : "1").toLocaleString()}
          trend={stats.userTrend?.trend || "+0%"}
          isUp={stats.userTrend?.isUp ?? true}
          icon={Users}
          description="Total registered users"
          bgClass="bg-secondary/50 dark:bg-emerald-500/10"
          iconClass="bg-emerald-500/25 dark:bg-emerald-500/20 text-emerald-700 dark:text-emerald-400"
        />
        <KpiCard
          title="OAuth2 Clients"
          value={loading ? "..." : stats.apps}
          trend={stats.appTrend?.trend || "+0%"}
          isUp={stats.appTrend?.isUp ?? true}
          icon={AppWindow}
          description="Total registered applications"
          bgClass="bg-secondary/50 dark:bg-amber-500/10"
          iconClass="bg-amber-500/25 dark:bg-amber-500/20 text-amber-700 dark:text-amber-400"
        />
        <KpiCard
          title="Active Sessions"
          value={loading ? "..." : stats.sessions}
          trend={stats.sessionTrend?.trend || "+0%"}
          isUp={stats.sessionTrend?.isUp ?? true}
          icon={CircleDot}
          description="Currently active sessions"
          bgClass="bg-secondary/50 dark:bg-blue-500/10"
          iconClass="bg-blue-500/25 dark:bg-blue-500/20 text-blue-700 dark:text-blue-400"
        />
        <KpiCard
          title="Total Teams"
          value={loading ? "..." : (hasRole(ROLE_ADMIN) ? stats.teams : teams.length).toLocaleString()}
          trend={stats.teamTrend?.trend || "+0%"}
          isUp={stats.teamTrend?.isUp ?? true}
          icon={Users}
          description="Total organizations"
          bgClass="bg-secondary/50 dark:bg-indigo-500/10"
          iconClass="bg-indigo-500/25 dark:bg-indigo-500/20 text-indigo-700 dark:text-indigo-400"
        />
      </div>

      {/* Analytics Section */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Main Chart */}
        <Card className="lg:col-span-2 overflow-hidden border-none shadow-sm bg-secondary/50 dark:bg-violet-600/10 backdrop-blur-xl">
          <CardHeader className="flex flex-row items-center justify-between pb-8">
            <div>
              <CardTitle>Login Activity</CardTitle>
              <CardDescription>Authentication trends & active users (Last {activityDays.replace("d", "")} Days)</CardDescription>
            </div>
            <Tabs defaultValue="7d" className="w-[180px]" value={activityDays} onValueChange={setActivityDays}>
              <TabsList className="grid w-full grid-cols-2">
                <TabsTrigger value="7d">7 Days</TabsTrigger>
                <TabsTrigger value="30d">30 Days</TabsTrigger>
              </TabsList>
            </Tabs>
          </CardHeader>
          <CardContent className="px-2">
            <ChartContainer config={chartConfig} className="h-[300px] w-full">
              <AreaChart data={activityData} margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
                <defs>
                  <linearGradient id="colorUsers" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#8b5cf6" stopOpacity={0.6} />
                    <stop offset="95%" stopColor="#8b5cf6" stopOpacity={0.2} />
                  </linearGradient>
                  <linearGradient id="colorLogins" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#10b981" stopOpacity={0.4} />
                    <stop offset="95%" stopColor="#10b981" stopOpacity={0.1} />
                  </linearGradient>
                </defs>
                <CartesianGrid vertical={false} strokeDasharray="3 3" />
                <XAxis
                  dataKey="name"
                  axisLine={false}
                  tickLine={false}
                  tickMargin={10}
                />
                <YAxis
                  axisLine={false}
                  tickLine={false}
                />
                <ChartTooltip content={<ChartTooltipContent />} />
                <Area
                  type="monotone"
                  dataKey="users"
                  stroke="#8b5cf6"
                  fillOpacity={1}
                  fill="url(#colorUsers)"
                  strokeWidth={2}
                />
                <Area
                  type="monotone"
                  dataKey="logins"
                  stroke="#10b981"
                  fillOpacity={1}
                  fill="url(#colorLogins)"
                  strokeWidth={2}
                  strokeDasharray="4 4"
                />
              </AreaChart>
            </ChartContainer>
          </CardContent>
        </Card>

        {/* Membership Distribution */}
        <Card className="border-none shadow-sm bg-secondary/50 dark:bg-rose-500/5 backdrop-blur-xl">
          <CardHeader>
            <CardTitle>Membership Stats</CardTitle>
            <CardDescription>User tier distribution</CardDescription>
          </CardHeader>
          <CardContent className="flex flex-col items-center">
            <div className="h-[200px] w-full">
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie
                    data={membershipData}
                    cx="50%"
                    cy="50%"
                    innerRadius={60}
                    outerRadius={80}
                    paddingAngle={5}
                    dataKey="value"
                  >
                    {membershipData.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={entry.color} />
                    ))}
                  </Pie>
                  <RechartsTooltip />
                </PieChart>
              </ResponsiveContainer>
            </div>
            <div className="w-full space-y-3 mt-4">
              {membershipData.map((item) => {
                const total = membershipData.reduce((acc: number, curr: any) => acc + curr.value, 0)
                const percentage = total > 0 ? Math.round((item.value / total) * 100) : 0
                return (
                  <div key={item.name} className="flex items-center justify-between text-sm">
                    <div className="flex items-center gap-2">
                      <div className="h-2 w-2 rounded-full" style={{ backgroundColor: item.color }} />
                      <span className="text-muted-foreground">{item.name}</span>
                    </div>
                    <div className="flex items-center gap-2">
                      <span className="font-mono text-[10px] text-muted-foreground">{item.value}</span>
                      <span className="font-medium">{percentage}%</span>
                    </div>
                  </div>
                )
              })}
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Tabs Section for Deep Data */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2">
          <Tabs defaultValue="logins" className="w-full">
            <div className="flex items-center justify-between mb-4">
              <TabsList className="bg-muted/50 p-1">
                <TabsTrigger value="logins">Recent Logins</TabsTrigger>
                <TabsTrigger value="teams">Team Activities</TabsTrigger>
                <TabsTrigger value="security">Security Events</TabsTrigger>
              </TabsList>
            </div>

            <TabsContent value="logins" className="mt-0 outline-none">
              <Card className="border-none shadow-sm bg-secondary/50 dark:bg-sky-500/5 backdrop-blur-xl overflow-hidden">
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

            <TabsContent value="teams">
              <Card className="p-12 text-center border-dashed border-2 bg-secondary/50">
                <div className="mx-auto w-12 h-12 rounded-full bg-muted flex items-center justify-center mb-4">
                  <Users className="h-6 w-6 text-muted-foreground text-opacity-40" />
                </div>
                <h3 className="font-semibold text-lg">No recent team activity</h3>
                <p className="text-muted-foreground text-sm max-w-xs mx-auto mt-2">
                  Team member actions and role changes will appear here as they happen.
                </p>
                <Button variant="outline" size="sm" className="mt-4" onClick={() => navigate("/teams")}>
                  Manage Teams
                </Button>
              </Card>
            </TabsContent>
          </Tabs>
        </div>

        {/* Sidebar Widgets */}
        <div className="space-y-6">
          {/* Recent Notifications Widget */}
          <Card className="border-none shadow-sm overflow-hidden text-clip bg-secondary/50 dark:bg-indigo-500/5 backdrop-blur-xl">
            <CardHeader className="pb-3 flex flex-row items-center justify-between border-b border-border/50">
              <div>
                <CardTitle className="text-sm font-bold flex items-center">
                  <Inbox className="mr-2 h-4 w-4 text-primary" />
                  Recent Notifications
                </CardTitle>
              </div>
              {stats.unreadMessages > 0 && (
                <Badge variant="outline" className="text-[10px] bg-primary/5 text-primary border-primary/20">
                  {stats.unreadMessages} New
                </Badge>
              )}
            </CardHeader>
            <CardContent className="p-0">
              <div className="divide-y divide-border/50">
                {loading ? (
                  [1, 2, 3].map(i => <div key={i} className="p-4 space-y-2"><div className="h-4 w-3/4 bg-muted animate-pulse rounded" /><div className="h-3 w-1/2 bg-muted/60 animate-pulse rounded" /></div>)
                ) : messages.length > 0 ? (
                  messages.map((msg: InboxType) => (
                    <div key={msg.id} className={`p-4 hover:bg-muted/30 transition-colors cursor-pointer group ${msg.status === 'unread' ? 'bg-primary/[0.02]' : ''}`} onClick={() => navigate("/inbox")}>
                      <div className="flex justify-between items-start gap-2">
                        <h4 className={`text-xs font-semibold truncate ${msg.status === 'unread' ? 'text-foreground' : 'text-muted-foreground'}`}>{msg.title}</h4>
                        <span className="text-[10px] text-muted-foreground whitespace-nowrap">{msg.createdAt.split('T')[0]}</span>
                      </div>
                      <p className="text-[10px] text-muted-foreground line-clamp-1 mt-1 group-hover:text-foreground/70 transition-colors">
                        {msg.content}
                      </p>
                    </div>
                  ))
                ) : (
                  <div className="p-8 text-center text-clip">
                    <p className="text-[10px] text-muted-foreground italic">No messages found.</p>
                  </div>
                )}
              </div>
              <Button variant="ghost" className="w-full rounded-none h-10 text-xs border-t border-border/50 hover:bg-muted/50" onClick={() => navigate("/inbox")}>
                View All Messages
              </Button>
            </CardContent>
          </Card>

          {/* Quick Stats Widget */}
          <Card className="border-none shadow-sm bg-secondary/50 dark:bg-blue-500/5 backdrop-blur-xl">
            <CardHeader className="pb-2">
              <CardTitle className="text-sm font-bold flex items-center">
                <ShieldCheck className="mr-2 h-4 w-4 text-primary" />
                Security Health
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <div className="flex justify-between text-xs font-medium">
                  <span>MFA Adoption</span>
                  <span>78%</span>
                </div>
                <Progress value={78} className="h-1.5" />
              </div>
              <div className="space-y-2">
                <div className="flex justify-between text-xs font-medium">
                  <span>API Key Rotation</span>
                  <span>42%</span>
                </div>
                <Progress value={42} className="h-1.5" />
              </div>
              <div className="pt-2">
                <Button variant="link" className="p-0 h-auto text-xs text-primary" onClick={() => navigate("/account/settings")}>
                  Strengthen Security Settings →
                </Button>
              </div>
            </CardContent>
          </Card>

          {/* Pending Invitations (Refined) */}
          <AnimatePresence>
            {invitations.length > 0 && (
              <motion.div
                initial={{ opacity: 0, scale: 0.95 }}
                animate={{ opacity: 1, scale: 1 }}
                exit={{ opacity: 0, scale: 0.95 }}
              >
                <Card className="border-none shadow-lg shadow-primary/10 bg-secondary/50 dark:bg-fuchsia-500/5 backdrop-blur-xl">
                  <CardHeader className="pb-3 border-b border-border/50">
                    <div className="flex items-center justify-between">
                      <CardTitle className="text-md">Team Requests</CardTitle>
                      <Badge variant="destructive" className="rounded-full h-5 w-5 p-0 flex items-center justify-center text-[10px]">
                        {invitations.length}
                      </Badge>
                    </div>
                  </CardHeader>
                  <CardContent className="p-4 space-y-4">
                    {invitations.map((inv: TeamInvitation) => (
                      <div key={inv.id} className="p-3 bg-muted/30 rounded-lg flex flex-col gap-3">
                        <div className="flex items-start gap-3">
                          <div className="h-8 w-8 rounded-full bg-blue-500/10 flex items-center justify-center">
                            <Mail className="h-4 w-4 text-blue-600" />
                          </div>
                          <div className="flex-1 overflow-hidden">
                            <p className="text-xs font-semibold truncate italic text-muted-foreground">{inv.inviteeRoles}</p>
                            <p className="text-[10px] text-muted-foreground">Expires {new Date(inv.expiresAt).toLocaleDateString()}</p>
                          </div>
                        </div>
                        <div className="flex gap-2">
                          <Button size="sm" className="flex-1 h-7 text-[10px]" onClick={() => navigate(`/teams`)}>Accept</Button>
                          <Button size="sm" variant="ghost" className="flex-1 h-7 text-[10px]">Ignore</Button>
                        </div>
                      </div>
                    ))}
                  </CardContent>
                </Card>
              </motion.div>
            )}
          </AnimatePresence>

          {/* Quick Links Overlay */}
          <Card className="border-none shadow-sm mt-4 bg-secondary/50 dark:bg-slate-500/5 backdrop-blur-xl">
            <div className="p-2 grid grid-cols-2 gap-2">
              <QuickLink icon={AppWindow} label="Apps" onClick={() => navigate("/management/oauth2-clients")} />
              <QuickLink icon={ShieldCheck} label="Permissions" onClick={() => navigate("/management/permissions")} />
              <QuickLink icon={CreditCard} label="Billing" />
              <QuickLink icon={LogOut} label="Sign Out" onClick={() => navigate("/logout")} color="text-destructive" />
            </div>
          </Card>
        </div>
      </div>
    </LayoutBody>
  )
}

function KpiCard({ title, value, trend, isUp, icon: Icon, description, bgClass, iconClass }: any) {
  return (
    <Card className={`border-none shadow-sm hover:shadow-md transition-all duration-300 ${bgClass || 'bg-background/50'} backdrop-blur-xl`}>
      <CardContent className="p-5">
        <div className="flex justify-between items-start">
          <div className={`size-14 rounded-2xl ${iconClass || 'bg-primary/10 text-primary'} flex items-center justify-center mb-4 shadow-sm`}>
            <Icon className="size-8" />
          </div>
          <div className={`flex items-center text-xs font-bold ${isUp ? 'text-emerald-500' : 'text-rose-500'} bg-transparent p-1`}>
            {isUp ? <ArrowUpRight className="size-3 mr-0.5" /> : <ArrowDownRight className="size-3 mr-0.5" />}
            {trend}
          </div>
        </div>
        <div>
          <h3 className="text-2xl font-black tracking-tight">{value}</h3>
          <p className="text-xs font-bold text-muted-foreground uppercase opacity-70 mb-1">{title}</p>
          <p className="text-[10px] text-muted-foreground italic font-medium">{description}</p>
        </div>
      </CardContent>
    </Card>
  )
}

function QuickLink({ icon: Icon, label, onClick, color }: any) {
  return (
    <Button
      variant="ghost"
      className={`h-auto py-3 px-2 flex flex-col items-center justify-center gap-1.5 hover:bg-muted/50 rounded-xl ${color || 'text-foreground'}`}
      onClick={onClick}
    >
      <Icon className="h-4 w-4" />
      <span className="text-[10px] font-semibold">{label}</span>
    </Button>
  )
}
