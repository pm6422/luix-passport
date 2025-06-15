import { useStore } from 'exome/react'
import { loginUserStore } from '@/stores/login-user-store'
import { formatDateTime } from '@/lib/time-utils'
import { cn } from '@/lib/utils'

type Props = {
  value?: string | null
  className?: string
}

export const DateTime = ({ value, className }: Props) => {
  const { loginUser } = useStore(loginUserStore)

  return (
    <span className={cn(className)}>
      {formatDateTime(loginUser.dateTimeFormat, loginUser.timeZoneId, value)}
    </span>
  )
}
