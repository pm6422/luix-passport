import { Button } from "@/components/custom/button"
import { IconBellRinging } from "@tabler/icons-react"

export function NotificationNav() {

  return (
    <Button variant="outline" className="animate-pulse relative size-12 rounded-full p-1 border-0">
      <IconBellRinging className="text-red-500 size-8"></IconBellRinging>
    </Button>
  )
}
