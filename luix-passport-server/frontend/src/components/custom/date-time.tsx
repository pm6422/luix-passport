import { useStore } from "exome/react"
import { authUserStore } from "@/stores/auth-user-store.ts"
import { formatDateTime } from "@/libs/utils"

type Props = {
  value: string | Date
}

export const DateTime = ({
  value
}: Props) => {
  const { authUser } = useStore(authUserStore)

  return (
    <span >{formatDateTime(authUser.dateTimeFormatInstance.dateTimeFormat, value, authUser.timeZone)}</span>
  )
}
