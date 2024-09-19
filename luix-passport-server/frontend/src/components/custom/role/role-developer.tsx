import { useAuthUser } from "@/stores/auth-user-provider"

type Props = {
  children: React.ReactNode
}

export const RoleDeveloper = ({
  children
}: Props) => {
  const { isDeveloper } = useAuthUser()

  return (
    isDeveloper() && children
  )
}
