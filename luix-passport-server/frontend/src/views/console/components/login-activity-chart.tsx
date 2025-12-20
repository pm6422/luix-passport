import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Tabs, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { ChartContainer, ChartTooltip, ChartTooltipContent, type ChartConfig } from "@/components/ui/chart"
import { Area, AreaChart, CartesianGrid, XAxis, YAxis } from "recharts"
import { LoginLogService } from "@/services/login-log-service"
import { useEffect, useState } from "react"

interface ActivityData {
  name: string
  users: number
  logins: number
}

const chartConfig = {
  users: {
    label: "Active Users",
  },
  logins: {
    label: "Auth Requests",
  },
} satisfies ChartConfig

export function LoginActivityChart() {
  const [activityData, setActivityData] = useState<ActivityData[]>([])
  const [activityDays, setActivityDays] = useState("7d")

  useEffect(() => {
    const fetchActivityData = async () => {
      try {
        const days = parseInt(activityDays.replace("d", ""))
        const { data } = await LoginLogService.getActivityStats(days)
        setActivityData(data)
      } catch (error) {
        console.error("Failed to load activity stats:", error)
      }
    }
    fetchActivityData()
  }, [activityDays])
  return (
    <Card className="lg:col-span-2 overflow-hidden border border-border/50 shadow-sm bg-card">
      <CardHeader className="flex flex-row items-center justify-between pb-4">
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
        <ChartContainer config={chartConfig} className="h-[250px] w-full">
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
  )
}
