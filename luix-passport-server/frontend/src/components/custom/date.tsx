import { useStore } from "exome/react"
import { authUserStore } from "@/stores/auth-user-store.ts"
import { formatDate } from "@/libs/utils"

type Props = {
  value: string | Date
}

export const Date = ({
  value
}: Props) => {
  const { authUser } = useStore(authUserStore)

  return (
    <span >{formatDate(authUser.dateTimeFormatInstance.dateFormat, value, authUser.timeZone)}</span>
  )
}
