import { useAuthUser } from "@/stores/auth-user-provider"
import { formatDate } from "@/libs/utils"

type Props = {
  value: string | Date
}

export const Date = ({
  value
}: Props) => {
  const { authUser } = useAuthUser()

  return (
    <span >{formatDate(authUser.dateTimeFormatInstance.dateFormat, value, authUser.timeZone)}</span>
  )
}
