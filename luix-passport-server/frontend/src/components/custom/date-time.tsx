import { useStore } from "exome/react"
import { authUserStore } from "@/stores/auth-user-store.ts"
import { formatDateTime } from "@/lib/utils"

type Props = {
  value: string | Date
}

export const DateTime = ({
  value
}: Props) => {
  const { authUser } = useStore(authUserStore)

  return (
    <span >{formatDateTime(authUser.dateTimeFormat, value, authUser.timeZoneId)}</span>
  )
}
