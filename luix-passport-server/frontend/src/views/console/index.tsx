import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import {
  Users,
  AppWindow,
  Activity,
  ArrowUpRight,
  ArrowDownRight,
  ShieldCheck,
  CreditCard,
  Globe,
  CircleDot,
  LogOut,
  UserPlus,
  Mail,
  Inbox
} from "lucide-react"
import { LayoutBody } from "@/layouts/layout-definitions"
import { useEffect, useState } from "react"
import { TeamService, TeamInvitation, Team } from "@/services/team-service"
import { InboxService } from "@/services/inbox-service"
import { type Inbox as InboxType } from "@/domains/inbox"
import { Oauth2ClientService } from "@/services/oauth2-client-service"
import { UserService } from "@/services/user-service"
import { toast } from "sonner"
import { Badge } from "@/components/ui/badge"
import { useLoginUserStore } from "@/stores/login-user-store"
import { useNavigate } from "react-router-dom"
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

// Mock Data for Analytics
const activityData = [
  { name: "Mon", users: 400, logins: 240 },
  { name: "Tue", users: 300, logins: 139 },
  { name: "Wed", users: 200, logins: 980 },
  { name: "Thu", users: 278, logins: 390 },
  { name: "Fri", users: 189, logins: 480 },
  { name: "Sat", users: 239, logins: 380 },
  { name: "Sun", users: 349, logins: 430 },
]

const membershipData = [
  { name: "Free Layer", value: 45, color: "#94a3b8" },
  { name: "Pro Plan", value: 30, color: "#3b82f6" },
  { name: "Enterprise", value: 25, color: "#8b5cf6" },
]

const recentLogins = [
  { id: 1, user: "admin@luixtech.cn", ip: "192.168.1.1", location: "Beijing, CN", device: "Chrome / macOS", time: "2 mins ago", status: "success" },
  { id: 2, user: "jack.dorsey@twitter.com", ip: "45.22.11.102", location: "San Francisco, US", device: "Safari / iOS", time: "15 mins ago", status: "success" },
  { id: 3, user: "unknown", ip: "103.11.2.45", location: "Moscow, RU", device: "Unknown / Linux", time: "1 hour ago", status: "failed" },
  { id: 4, user: "lisa@gmail.com", ip: "210.34.12.8", location: "London, UK", device: "Firefox / Windows", time: "2 hours ago", status: "success" },
]

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
  const [userCount, setUserCount] = useState(0)
  const [stats, setStats] = useState({
    teams: 0,
    apps: 0,
    unreadMessages: 0
  })
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetchDashboardData()
  }, [])

  const fetchDashboardData = async () => {
    setLoading(true)
    try {
      const [invitationsRes, teamsRes, unreadRes, appsCountRes, usersRes, inboxRes] = await Promise.all([
        TeamService.getUserInvitations(),
        TeamService.findUserTeams(),
        InboxService.countUnread(),
        hasRole("ROLE_ADMIN") ? Oauth2ClientService.count() : Promise.resolve({ data: 0 }),
        hasRole("ROLE_ADMIN") ? UserService.count() : Promise.resolve({ data: 0 }),
        InboxService.find({ page: 0, size: 4 })
      ])

      setInvitations(invitationsRes.data)
      setTeams(teamsRes.data)
      setUserCount(usersRes.data)
      setMessages(inboxRes.data.content)
      setStats({
        teams: teamsRes.data.length,
        unreadMessages: unreadRes.data,
        apps: appsCountRes.data
      })
    } catch (error) {
      console.error("Failed to load dashboard data:", error)
      toast.error("Connected to local server but failed to fetch some metrics")
    } finally {
      setLoading(false)
    }
  }

  return (
    <LayoutBody className="flex flex-col space-y-6 pb-10" fixedHeight={false}>
      {/* Top Banner / System Health */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 bg-gradient-to-r from-primary/5 to-transparent p-6 rounded-2xl border border-primary/10">
        <div>
          <h1 className="text-3xl font-extrabold tracking-tight">Passport Control</h1>
          <div className="flex items-center gap-2 mt-1 text-muted-foreground">
            <span className="relative flex h-3 w-3">
              <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
              <span className="relative inline-flex rounded-full h-3 w-3 bg-emerald-500"></span>
            </span>
            <span className="text-sm font-medium">System API status: Operational</span>
            <span className="mx-2 text-border">|</span>
            <span className="text-sm">Last Sync: {new Date().toLocaleTimeString()}</span>
          </div>
        </div>
        <div className="flex items-center gap-2">
          <Button variant="outline" size="sm" className="hidden sm:flex">
            <Activity className="mr-2 h-4 w-4" />
            Full Logs
          </Button>
          <Button size="sm" className="shadow-lg shadow-primary/20">
            <UserPlus className="mr-2 h-4 w-4" />
            Invite Member
          </Button>
        </div>
      </div>

      {/* KPI Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <KpiCard
          title="Total Users"
          value={loading ? "..." : (hasRole("ROLE_ADMIN") ? userCount : "1").toLocaleString()}
          trend="+12.5%"
          isUp={true}
          icon={Users}
          description="Total registered identifiers"
        />
        <KpiCard
          title="Active Sessions"
          value="128"
          trend="+4.2%"
          isUp={true}
          icon={CircleDot}
          description="Concurrent auth tokens"
        />
        <KpiCard
          title="OAuth2 Clients"
          value={loading ? "..." : stats.apps}
          trend="-2"
          isUp={false}
          icon={AppWindow}
          description="Applications connected"
        />
        <KpiCard
          title="Monthly Requests"
          value="42.5k"
          trend="+18%"
          isUp={true}
          icon={Globe}
          description="API bridge throughput"
        />
      </div>

      {/* Analytics Section */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Main Chart */}
        <Card className="lg:col-span-2 overflow-hidden border-none shadow-sm bg-background/50 backdrop-blur-sm">
          <CardHeader className="flex flex-row items-center justify-between pb-8">
            <div>
              <CardTitle>Security Activity</CardTitle>
              <CardDescription>Authentication & Authorization trends (7 Days)</CardDescription>
            </div>
            <Tabs defaultValue="7d" className="w-[180px]">
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
                    <stop offset="5%" stopColor="var(--color-users)" stopOpacity={0.3} />
                    <stop offset="95%" stopColor="var(--color-users)" stopOpacity={0} />
                  </linearGradient>
                  <linearGradient id="colorLogins" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="var(--color-logins)" stopOpacity={0.1} />
                    <stop offset="95%" stopColor="var(--color-logins)" stopOpacity={0} />
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
                  stroke="var(--color-users)"
                  fillOpacity={1}
                  fill="url(#colorUsers)"
                  strokeWidth={2}
                />
                <Area
                  type="monotone"
                  dataKey="logins"
                  stroke="var(--color-logins)"
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
        <Card className="border-none shadow-sm bg-background/50 backdrop-blur-sm">
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
              {membershipData.map((item) => (
                <div key={item.name} className="flex items-center justify-between text-sm">
                  <div className="flex items-center gap-2">
                    <div className="h-2 w-2 rounded-full" style={{ backgroundColor: item.color }} />
                    <span className="text-muted-foreground">{item.name}</span>
                  </div>
                  <span className="font-medium">{item.value}%</span>
                </div>
              ))}
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
              <Button variant="ghost" size="sm" className="text-xs">Export CSV</Button>
            </div>

            <TabsContent value="logins" className="mt-0">
              <Card className="border-none shadow-sm">
                <Table>
                  <TableHeader>
                    <TableRow className="hover:bg-transparent">
                      <TableHead>User Identity</TableHead>
                      <TableHead>Location</TableHead>
                      <TableHead>Device / IP</TableHead>
                      <TableHead>Time</TableHead>
                      <TableHead className="text-right">Status</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {recentLogins.map((login) => (
                      <TableRow key={login.id} className="cursor-default">
                        <TableCell>
                          <div className="flex items-center gap-2">
                            <Avatar className="h-7 w-7">
                              <AvatarFallback className="text-[10px] bg-primary/10 text-primary">
                                {login.user[0].toUpperCase()}
                              </AvatarFallback>
                            </Avatar>
                            <span className="text-sm font-medium">{login.user}</span>
                          </div>
                        </TableCell>
                        <TableCell className="text-xs text-muted-foreground">{login.location}</TableCell>
                        <TableCell>
                          <div className="flex flex-col">
                            <span className="text-xs">{login.device}</span>
                            <span className="text-[10px] text-muted-foreground">{login.ip}</span>
                          </div>
                        </TableCell>
                        <TableCell className="text-xs">{login.time}</TableCell>
                        <TableCell className="text-right">
                          <Badge variant={login.status === 'success' ? 'default' : 'destructive'} className="text-[10px] px-1.5 py-0">
                            {login.status}
                          </Badge>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </Card>
            </TabsContent>

            <TabsContent value="teams">
              <Card className="p-12 text-center border-dashed border-2">
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
          <Card className="border-none shadow-sm overflow-hidden text-clip">
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
                  messages.map((msg) => (
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
          <Card className="border-none shadow-sm bg-primary/5">
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
                <Card className="border-none shadow-lg shadow-primary/10">
                  <CardHeader className="pb-3 border-b border-border/50">
                    <div className="flex items-center justify-between">
                      <CardTitle className="text-md">Team Requests</CardTitle>
                      <Badge variant="destructive" className="rounded-full h-5 w-5 p-0 flex items-center justify-center text-[10px]">
                        {invitations.length}
                      </Badge>
                    </div>
                  </CardHeader>
                  <CardContent className="p-4 space-y-4">
                    {invitations.map((inv) => (
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
          <Card className="border-none shadow-sm mt-4">
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

function KpiCard({ title, value, trend, isUp, icon: Icon, description }: any) {
  return (
    <Card className="border-none shadow-sm hover:shadow-md transition-shadow">
      <CardContent className="p-5">
        <div className="flex justify-between items-start">
          <div className="w-10 h-10 rounded-xl bg-primary/10 flex items-center justify-center text-primary mb-4">
            <Icon className="h-5 w-5" />
          </div>
          <div className={`flex items-center text-xs font-bold ${isUp ? 'text-emerald-500' : 'text-rose-500'} bg-transparent p-1`}>
            {isUp ? <ArrowUpRight className="h-3 w-3 mr-0.5" /> : <ArrowDownRight className="h-3 w-3 mr-0.5" />}
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
