import { useStore } from "exome/react"
import { loginUserStore } from "@/stores/login-user-store"

type Props = {
  children: React.ReactNode
}

export const RoleAdmin = ({
  children
}: Props) => {
  const { loginUser } = useStore(loginUserStore)

  return (
    loginUser.isAdmin && children
  )
}
