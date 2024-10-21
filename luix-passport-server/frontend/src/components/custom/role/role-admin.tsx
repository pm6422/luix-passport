import { useStore } from "exome/react"
import { authUserStore } from "@/stores/auth-user-store.ts"

type Props = {
  children: React.ReactNode
}

export const RoleAdmin = ({
  children
}: Props) => {
  const { authUser } = useStore(authUserStore)

  return (
    authUser.isAdmin && children
  )
}
