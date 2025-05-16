import { useStore } from "exome/react"
import { authUserStore } from "@/stores/auth-user-store.ts"
import { cn, formatDate } from '@/lib/utils'

type Props = {
  value: string | Date
  className?: string
}

export const Date = ({
  value,
  className
}: Props) => {
  const { authUser } = useStore(authUserStore)

  return (
    <span className={cn(className)}>
      {formatDate(authUser.dateFormat, value, authUser.timeZoneId)}
    </span>
  )
}
