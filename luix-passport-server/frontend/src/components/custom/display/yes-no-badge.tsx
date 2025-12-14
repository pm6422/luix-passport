import { IconCircleCheckFilled, IconCircleXFilled } from "@tabler/icons-react"
import { cn } from "@/lib/utils"
import { Tooltip, TooltipTrigger, TooltipContent } from "@/components/ui/tooltip"

interface YesNoBadgeProps {
  value?: boolean | null
  className?: string
}

export function YesNoBadge({ value, className }: YesNoBadgeProps) {
  return (
    <Tooltip>
      <TooltipTrigger asChild>
        <span
          className={cn(
            "inline-flex items-center justify-center size-5 rounded-sm border text-muted-foreground",
            className
          )}
          aria-label={value ? "Yes" : "No"}
        >
          {value ? (
            <IconCircleCheckFilled className="size-3 text-green-500 dark:text-green-400" />
          ) : (
            <IconCircleXFilled className="size-3 text-red-500 dark:text-red-400" />
          )}
        </span>
      </TooltipTrigger>
      <TooltipContent side="top">
        {value ? "Yes" : "No"}
      </TooltipContent>
    </Tooltip>
  )
}
