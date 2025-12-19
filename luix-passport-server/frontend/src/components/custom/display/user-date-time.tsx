import { useLoginUserStore } from '@/stores/login-user-store'
import { formatDate, formatDateTime, formatTime } from '@/lib/time-utils'
import { cn } from "@/lib/utils"

type Props = {
  value?: string | null
  type?: 'date' | 'time' | 'datetime'
  className?: string
  twoLines?: boolean
  align?: 'start' | 'center' | 'end'
}

export const UserDateTime = ({ value, type = 'datetime', className, twoLines = false, align = 'center' }: Props) => {
  const { loginUser } = useLoginUserStore()

  if (!value) return null

  if (twoLines && type === 'datetime') {
    const dateStr = formatDate(loginUser.dateFormat, loginUser.timeZoneId, value)
    const timeStr = formatTime(loginUser.timeFormat, loginUser.timeZoneId, value)

    const alignClass = align === 'start' ? 'items-start' : align === 'end' ? 'items-end' : 'items-center'

    return (
      <span className={cn("flex flex-col leading-snug", alignClass, className)}>
        <span>{dateStr}</span>
        <span className="text-muted-foreground opacity-80 text-[0.9em]">{timeStr}</span>
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
