import { Card, CardContent } from "@/components/ui/card"
import { LucideIcon } from "lucide-react"
import { ArrowUpRight, ArrowDownRight } from "lucide-react"

interface KpiCardProps {
  title: string
  value: string | number
  trend: string
  isUp: boolean
  icon: LucideIcon
  description: string
  bgClass?: string
  iconClass?: string
}

export function KpiCard({ title, value, trend, isUp, icon: Icon, description, bgClass, iconClass }: KpiCardProps) {
  return (
    <Card className={`border-none shadow-sm hover:shadow-md transition-all duration-300 ${bgClass || 'bg-background/50'} backdrop-blur-xl`}>
      <CardContent className="p-5 flex items-center justify-between">
        <div className="flex flex-col gap-0.5">
          <p className="text-[10px] font-bold text-muted-foreground uppercase opacity-70">{title}</p>
          <h3 className="text-2xl font-black tracking-tight leading-none my-0.5">{value}</h3>
          <div className="flex items-center gap-2">
            <div className={`flex items-center text-[10px] font-bold ${isUp ? 'text-emerald-500' : 'text-rose-500'} bg-transparent`}>
              {isUp ? <ArrowUpRight className="size-3 mr-0.5" /> : <ArrowDownRight className="size-3 mr-0.5" />}
              {trend}
            </div>
            <p className="text-[10px] text-muted-foreground italic opacity-60 truncate max-w-[150px] hidden xl:block">{description}</p>
          </div>
        </div>
        <div className={`size-12 rounded-xl ${iconClass || 'bg-primary/10 text-primary'} flex items-center justify-center shadow-sm ml-2`}>
          <Icon className="size-6" />
        </div>
      </CardContent>
    </Card>
  )
}
