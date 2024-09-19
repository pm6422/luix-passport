import { useAuthUser } from "@/stores/auth-user-provider"

type Props = {
  children: React.ReactNode
}

export const RoleAdmin = ({
  children
}: Props) => {
  const { isAdmin } = useAuthUser()

  return (
    isAdmin() && children
  )
}
