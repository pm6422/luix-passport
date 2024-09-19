import { Button } from "@/components/custom/button"
import { IconBell } from "@tabler/icons-react"

export function NotificationNav() {

  return (
    <Button variant="outline" className="animate-pulse relative size-12 rounded-full p-1 bg-gray-200 border-0">
      <IconBell className="text-red-500"></IconBell>
    </Button>
  )
}
