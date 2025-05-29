import { useEffect, useState } from "react"
import { UserLoginCount } from "@/domains/user-login-count"
import { UserAuthEventService } from "@/services/user-auth-event-service"
import { Area, AreaChart, CartesianGrid, XAxis } from "recharts"
import {
  ChartConfig,
  ChartContainer,
  ChartTooltip,
  ChartTooltipContent,
} from "@/components/ui/chart"

export function SevenDaysUserLoginCount() {
  const [ userLoginCount, setUserLoginCount ] = useState([] as Array<UserLoginCount>)
  useEffect(() => {
    UserAuthEventService.getUserLoginCount().then((resp) => {
        setUserLoginCount(resp.data)
    })
  }, [])

  const chartConfig = {
    loginCount: {
        label: "Login Count",
        color: "hsl(var(--chart-1))",
    },
  } satisfies ChartConfig

  return (
    <ChartContainer config={chartConfig}>
        <AreaChart
            accessibilityLayer
            data={userLoginCount}
            margin={{
              left: 12,
              right: 12,
            }}
        >
          <CartesianGrid vertical={false} />
          <XAxis
              dataKey="calculatedAt"
              tickLine={false}
              axisLine={false}
              tickMargin={8}
              tickFormatter={(value) => value.slice(0, 10)}
          />
          <ChartTooltip
              cursor={false}
              content={<ChartTooltipContent indicator="line" />}
          />
          <Area
              dataKey="loginCount"
              type="natural"
              fill="var(--color-loginCount)"
              fillOpacity={0.4}
              stroke="var(--color-loginCount)"
          />
        </AreaChart>
      </ChartContainer>
  )
}
