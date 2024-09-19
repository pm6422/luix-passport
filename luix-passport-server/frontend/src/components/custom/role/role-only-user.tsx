import { useAuthUser } from "@/stores/auth-user-provider"

type Props = {
  children: React.ReactNode
}

export const RoleOnlyUser = ({
  children
}: Props) => {
  const { isOnlyUser } = useAuthUser()

  return (
    isOnlyUser() && children
  )
}
