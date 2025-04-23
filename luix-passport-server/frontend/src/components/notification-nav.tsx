import { Button } from "@/components/custom/button"
import { IconBellRinging } from "@tabler/icons-react"

export function NotificationNav() {

  return (
    <Button variant="outline" className="animate-pulse relative size-12 rounded-full p-1 bg-gray-200 border-0">
      <IconBellRinging className="text-red-500"></IconBellRinging>
    </Button>
  )
}
