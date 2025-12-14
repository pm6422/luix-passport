import { useState } from "react"
import { Badge } from "@/components/ui/badge"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { ScrollArea } from "@/components/ui/scroll-area"
import { cn } from "@/lib/utils"

type Props = {
  items: string[]
  visibleCount?: number
  className?: string
  badgeClassName?: string
  popoverBadgeClassName?: string
}

export function OverflowBadges({
  items,
  visibleCount = 1,
  className,
  badgeClassName,
  popoverBadgeClassName,
}: Props) {
  const visible = items.slice(0, visibleCount)
  const hiddenCount = items.length - visible.length
  const [open, setOpen] = useState(false)

  return (
    <div className={cn(className)}>
      <div className="flex items-center gap-1 whitespace-nowrap overflow-hidden">
        {visible.map((item, index) => (
          <Badge
            key={index}
            variant="secondary"
            className={cn("px-1.5 py-0.5 text-[10px]", badgeClassName)}
          >
            {item}
          </Badge>
        ))}
        {hiddenCount > 0 && (
          <Popover open={open} onOpenChange={setOpen}>
            <PopoverTrigger asChild>
              <Badge
                variant="outline"
                className="px-1.5 py-0.5 text-[10px] cursor-pointer"
                onMouseEnter={() => setOpen(true)}
                onMouseLeave={() => setOpen(false)}
                onClick={() => setOpen((prev) => !prev)}
              >
                +{hiddenCount}
              </Badge>
            </PopoverTrigger>
            <PopoverContent
              align="start"
              className="w-fit p-2"
              onMouseEnter={() => setOpen(true)}
              onMouseLeave={() => setOpen(false)}
            >
              <ScrollArea>
                <div className="flex flex-col gap-1">
                  {items.map((item, index) => (
                    <Badge
                      key={index}
                      variant="secondary"
                      className={cn("px-1.5 py-0.5 text-[10px] w-fit justify-start", popoverBadgeClassName)}
                    >
                      {item}
                    </Badge>
                  ))}
                </div>
              </ScrollArea>
            </PopoverContent>
          </Popover>
        )}
      </div>
    </div>
  )
}
