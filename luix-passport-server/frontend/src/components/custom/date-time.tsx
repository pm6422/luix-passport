import { useStore } from "exome/react"
import { loginUserStore } from "@/stores/login-user-store"
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
  const { loginUser } = useStore(loginUserStore)

  return (
    <span className={cn(className)}>
      {formatDateTime(loginUser.dateTimeFormat, value, loginUser.timeZoneId)}
    </span>
  )
}