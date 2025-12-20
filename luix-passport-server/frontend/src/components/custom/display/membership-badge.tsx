import { Badge } from "@/components/ui/badge"
import { IconDiamond, IconStar } from "@tabler/icons-react"

interface MembershipBadgeProps {
  type: string
  className?: string
}

export function MembershipBadge({ type, className }: MembershipBadgeProps) {
  if (!type) return null

  // Removing "ROLE_MEMBER_" based on observed usage
  const displayType = type.replace("ROLE_MEMBER_", "")
  const isPremium = type.includes("PREMIUM")

  return (
    <Badge
      className={`
        px-1.5 py-0 h-5 text-[10px] font-normal border-none shadow-none flex items-center
        ${isPremium
          ? "bg-primary/10 text-primary hover:bg-primary/20"
          : "bg-yellow-500/10 text-yellow-600 hover:bg-yellow-500/20"
        }
        ${className || ""}
      `}
    >
      {isPremium ? (
        <IconDiamond className="size-3 mr-1" />
      ) : (
        <IconStar className="size-3 mr-1" />
      )}
      {displayType}
    </Badge>
  )
}
