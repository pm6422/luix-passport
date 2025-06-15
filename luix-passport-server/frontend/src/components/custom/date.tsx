import { useStore } from 'exome/react'
import { loginUserStore } from '@/stores/login-user-store'
import { formatDate } from '@/lib/time-utils'
import { cn } from '@/lib/utils'

type Props = {
  value?: string | null
  className?: string
}

export const Date = ({ value, className }: Props) => {
  const { loginUser } = useStore(loginUserStore)

  return (
    <span className={cn(className)}>
      {formatDate(loginUser.dateFormat, loginUser.timeZoneId, value)}
    </span>
  )
}
