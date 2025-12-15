import { useLoginUserStore } from '@/stores/login-user-store'
import { formatDate, formatDateTime, formatTime } from '@/lib/time-utils'
import { cn } from "@/lib/utils"

type Props = {
  value?: string | null
  type?: 'date' | 'time' | 'datetime'
  className?: string
}

export const UserDateTime = ({ value, type = 'datetime', className }: Props) => {
  const { loginUser } = useLoginUserStore()

  if (!value) return null

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
