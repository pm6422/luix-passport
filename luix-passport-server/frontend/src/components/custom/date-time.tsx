import { useAuthUser } from "@/stores/auth-user-provider"
import { formatDateTime } from "@/libs/utils"

type Props = {
  value: string | Date
}

export const DateTime = ({
  value
}: Props) => {
  const { authUser } = useAuthUser()

  return (
    <span >{formatDateTime(authUser.dateTimeFormatInstance.dateTimeFormat, value, authUser.timeZone)}</span>
  )
}
