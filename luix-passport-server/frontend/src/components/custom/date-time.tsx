import { useStore } from "exome/react"
import { authUserStore } from "@/stores/auth-user-store.ts"
import { formatDateTime } from "@/lib/utils"
import { cn } from "@/lib/utils" // Assuming you have a cn utility for classNames

type Props = {
  value: string | Date
  className?: string
}

export const DateTime = ({
                           value,
                           className
                         }: Props) => {
  const { authUser } = useStore(authUserStore)

  return (
    <span className={cn(className)}>
      {formatDateTime(authUser.dateTimeFormat, value, authUser.timeZoneId)}
    </span>
  )
}