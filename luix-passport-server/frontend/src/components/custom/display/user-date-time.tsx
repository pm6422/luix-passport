import { useLoginUserStore } from '@/stores/login-user-store'
import { formatDate, formatDateTime, formatTime } from '@/lib/time-utils'
import { cn } from "@/lib/utils"

type Props = {
  value?: string | null
  type?: 'date' | 'time' | 'datetime'
  className?: string
  twoLines?: boolean
}

export const UserDateTime = ({ value, type = 'datetime', className, twoLines = false }: Props) => {
  const { loginUser } = useLoginUserStore()

  if (!value) return null

  if (twoLines && type === 'datetime') {
    const dateStr = formatDate(loginUser.dateFormat, loginUser.timeZoneId, value)
    const timeStr = formatTime(loginUser.timeFormat, loginUser.timeZoneId, value)

    return (
      <span className={cn("flex flex-col items-center leading-snug", className)}>
        <span>{dateStr}</span>
        <span className="text-muted-foreground text-xs">{timeStr}</span>
      </span>
    )
  }

  let formattedValue = ''

  switch (type) {
    case 'date':
      formattedValue = formatDate(loginUser.dateFormat, loginUser.timeZoneId, value)
      break
    case 'time':
      formattedValue = formatTime(loginUser.timeFormat, loginUser.timeZoneId, value)
      break
    case 'datetime':
    default:
      formattedValue = formatDateTime(loginUser.dateTimeFormat, loginUser.timeZoneId, value)
      break
  }

  return (
    <span className={cn(className)}>
      {formattedValue}
    </span>
  )
}
