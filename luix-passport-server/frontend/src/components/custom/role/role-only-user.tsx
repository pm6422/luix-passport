import { useStore } from "exome/react"
import { loginUserStore } from "@/stores/login-user-store"

type Props = {
  children: React.ReactNode
}

export const RoleOnlyUser = ({
  children
}: Props) => {
  const { loginUser } = useStore(loginUserStore)

  return (
    loginUser.isOnlyUser && children
  )
}
