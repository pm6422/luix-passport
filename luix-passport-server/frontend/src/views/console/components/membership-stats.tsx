import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { PieChart, Pie, Cell, ResponsiveContainer, Tooltip as RechartsTooltip } from "recharts"
import { TeamService } from "@/services/team-service"
import { useEffect, useState } from "react"
import colors from "tailwindcss/colors"

interface MembershipDataItem {
  name: string
  value: number
  color: string
}

export function MembershipStats() {
  const [membershipData, setMembershipData] = useState<MembershipDataItem[]>([])

  useEffect(() => {
    const fetchMembershipData = async () => {
      try {
        const res = await TeamService.getMembershipStats()
        const mStats = res.data
        const formattedData = [
          { name: "Free Layer", value: mStats.FREE || 0, color: colors.blue[800] },
          { name: "Pro Plan", value: mStats.PRO || 0, color: colors.yellow[500] },
          { name: "Premium Plan", value: mStats.PREMIUM || 0, color: colors.purple[600] },
        ]
        setMembershipData(formattedData)
      } catch (error) {
        console.error("Failed to load membership stats:", error)
      }
    }
    fetchMembershipData()
  }, [])
  return (
    <Card className="border border-border/50 shadow-sm bg-card">
      <CardHeader>
        <CardTitle>Membership Stats</CardTitle>
        <CardDescription>User tier distribution</CardDescription>
      </CardHeader>
      <CardContent className="flex flex-col items-center">
        <div className="h-[180px] w-full">
          <ResponsiveContainer width="100%" height="100%">
            <PieChart>
              <Pie
                data={membershipData}
                cx="50%"
                cy="50%"
                innerRadius={0}
                outerRadius={85}
                paddingAngle={0}
                dataKey="value"
                stroke="none"
              >
                {membershipData.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={entry.color} />
                ))}
              </Pie>
              <RechartsTooltip />
            </PieChart>
          </ResponsiveContainer>
        </div>
        <div className="w-full space-y-2 mt-2">
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
  )
}
