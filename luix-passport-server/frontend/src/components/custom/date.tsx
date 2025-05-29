import { useStore } from 'exome/react'
import { loginUserStore } from '@/stores/login-user-store'
import { formatDate } from '@/lib/time-utils'
import { cn } from '@/lib/utils'

type Props = {
  value: string | Date
  className?: string
}

export const Date = ({ value, className }: Props) => {
  const { loginUser } = useStore(loginUserStore)

  return (
    <span className={cn(className)}>
      {formatDate(loginUser.dateFormat, value, loginUser.timeZoneId)}
    </span>
  )
}
